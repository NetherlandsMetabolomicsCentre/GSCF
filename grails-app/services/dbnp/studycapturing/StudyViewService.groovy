/**
 * StudyViewService Service
 * 
 * Description
 */
package dbnp.studycapturing

import dbnp.studycapturing.Study
import dbnp.authentication.SecUser
import org.dbnp.gdt.Term

class StudyViewService {
    static transactional = true
	def authenticationService

    def fetchStudyForCurrentUserWithId(long id) {
	    SecUser user = authenticationService.getLoggedInUser()
	    return fetchStudyWithIdAndUser(id, user)
    }
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

	def fetchStudy(params) {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0
		return fetchStudyForCurrentUserWithId(id)
	}

	def wrap(params,Closure block) {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0

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

	def termsForOntologies(HashSet ontologies) {
		def terms = []
		ontologies.each { ontology ->
			terms += Term.findAllByOntology(ontology)
		}

		return terms.sort{ it.name }
	}
}