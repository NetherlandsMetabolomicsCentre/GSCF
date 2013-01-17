/**
 * StudyViewService Service
 * 
 * Description
 */
package dbnp.studycapturing

import dbnp.studycapturing.Study
import dbnp.authentication.SecUser
import org.dbnp.gdt.Term
import org.dbnp.gdt.Template
import dbnp.generic.Audit

class StudyViewService {
    static transactional = true

    // inject the authentication service
    def authenticationService

    /**
     * fetch study for the currently logged in user, or create a
     * new study if the study does not exist
     * @param id
     * @return Study
     */
    def fetchStudyForCurrentUserWithId(long id) {
	    SecUser user = authenticationService.getLoggedInUser()
	    return fetchStudyWithIdAndUser(id, user)
    }

    /**
     * fetch the study for a particular user, or create a new study
     * if it does not yet exists
     * @param id
     * @param user
     * @return Study
     */
	def fetchStudyWithIdAndUser(long id, SecUser user) {
	    Study study
	    def criteria = Study.createCriteria()

	    // check if we need to create or edit/view a study
	    if (user != null && id > 0) {
		    // is this user an administrator?
		    if (user && user.hasAdminRights()) {
			    study = Study.findById(id)
		    } else if (user) {
			    List studies = criteria.list {
				    and {
					    eq("id", id)
					    and {
						    or {
							    eq("owner", user)
							    writers {
								    eq("id", user.id)
							    }
							    readers {
								    eq("id", user.id)
							    }
						    }
					    }
				    }
			    }
			    study = (studies.size()>0) ? studies.first() : null
		    }
	    } else if (user == null && id > 0) {
            // nobody is logged in, check to see if this is
            // a public study which may be viewed by the world
		    List studies = criteria.list {
			    and {
				    eq("id", id)
				    eq("publicstudy", true)
			    }
		    }
		    study = (studies.size()>0) ? studies.first() : null
	    } else if (user != null) {
		    // create a new study with this user as owner
		    study = new Study()
		    study.owner = user
		    study.cleanup = true
		    study.description = "Study description by ${user.email}"
		    study.startDate = new Date()

		    // fetch first template to use as initial template
			def templates = Template.findAllByEntity(study.class)
		    if (templates) {
			    study.template = templates[0]
		    }

			// make sure the title is unique
		    String title = "New study by ${user.username}"
			Integer count = 0

			study.title = title
		    while (!study.validate()) {
				count++
			    study.title = "${title} #${count}"
			}

		    if (!study.save()) {
			    log.error "Could not create a new study on demand. Did the Study domain change?"
			    study.errors.each { error ->
				    log.error error
			    }
		    }
	    }

		return study
    }

    /**
     * shortcut method to fetchStudyForCurrentUserWithId
     * @param params
     * @return
     */
	def fetchStudy(params) {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0
		return fetchStudyForCurrentUserWithId(id)
	}

    /**
     * wrapper method to perform some common checks and validations. It
     * accepts a closure to perform some specific logic
     * @param response
     * @param params
     * @param block
     * @return
     */
	def wrap(response, params,Closure block) {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"

		try {
			Study study = fetchStudyForCurrentUserWithId(id)
			Boolean summary = (params.containsKey('summary')) ? (params.get('summary') as Boolean) : false

			if (study) {
				block(study, summary)
			} else {
				render(view: "errors/invalid")
			}
		} catch (Exception e) {
			render(view: "errors/exception")
		}
	}

    /**
     * returns a list of terms for a set of ontologies. Used by taglib
     * @param ontologies
     * @return
     */
	def termsForOntologies(HashSet ontologies) {
		def terms = []
		ontologies.each { ontology ->
			terms += Term.findAllByOntology(ontology)
		}

		return terms.sort{ it.name }
	}

    /**
     * add a record to the audit trail of a study to keep track of changes
     * @param entity
     * @param fieldType
     * @param fieldName
     * @param fieldValue
     * @return
     */
	def addToAuditTrail(entity, String fieldType, String fieldName, fieldValue) {
		SecUser user = authenticationService.getLoggedInUser()
		Study study

		// instantiate Audit
		Audit audit = new Audit(user: user, fieldName: fieldName, fieldValue: fieldValue, fieldType: fieldType.toLowerCase())

		// set entity information
		if (entity instanceof Study) {
			study = entity
		} else {
			def matches = entity.getClass().toString() =~ /\.([a-zA-Z]+)$/
			String entityName = matches[0][1]

			audit.entityType = entityName
			audit.entityUUID = entity.giveUUID()

			study = entity.parent
		}

		// add audit entry to study
		study.addToAudits(audit)
	}
}