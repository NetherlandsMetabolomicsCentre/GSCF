/**
 * StudyViewController
 *
 * This controller provides the possibility to view
 * and modify a study
 */
package dbnp.studycapturing

import dbnp.authentication.SecUser
import grails.converters.JSON
import org.dbnp.gdt.TemplateFieldType
import org.dbnp.gdt.Term
import org.dbnp.gdt.Template
import dbnp.generic.Audit

class StudyViewController {
	def authenticationService
	def studyViewService

	/**
	 * list the studies where the viewer has access to
	 */
	def index = {
		SecUser user = authenticationService.getLoggedInUser()
		def studies = Study.giveReadableStudies( user );

		render(view: "list", model: [studies: studies])
	}

    /**
     * view a study
     */
	def view = {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0

		SecUser user = authenticationService.getLoggedInUser()
		Study study = studyViewService.fetchStudyForCurrentUserWithId(id)

		// got a study?
		if (study) {
			// yes, did we have a study id?
            if (id) {
                // yes, render the study view page
                render(view: "view", model: [
                        study   : study,
                        canRead : study.canRead(user),
                        canWrite: study.canWrite(user)
                ])
            } else {
                // no, redirect to view with newly generated study id. This
                // is done because if the page is rendered (as we do above)
                // and the user refreshes the page, a new empty study is
                // created each time the users does so. By redirecting to the
                // same logic with the new study id we can make sure the study
                // is always in edit mode...
                redirect(action: 'view', id: study.id)
            }
		} else {
			// no user and/or no study. As only users can create
			// a new study show the 401 page
			render(view: "/error/_401")
		}
	}

    /**
     * render the partial containing the timeline
     */
    def ajaxTimeline = {
		SecUser user = authenticationService.getLoggedInUser()
		studyViewService.wrap(response, params, { study, summary ->
			render(view: "elements/timeline", model: [study: study, summary: summary, canRead: study.canRead(user), canWrite: study.canWrite(user)])
		})
	}

    /**
     * render the partial containing the study details
     */
    def ajaxDetails = {
		SecUser user = authenticationService.getLoggedInUser()
		Integer cleanupInDays = RemoveExpiredStudiesJob.studyExpiry
		studyViewService.wrap(response, params, { study, summary ->
			render(view: "elements/details", model: [
					study: study,
					summary: summary,
					canRead: study.canRead(user),
					canWrite: study.canWrite(user),
					cleanupInDays: cleanupInDays
			])
		})
	}

    /**
     * render the partial containing the study subjects
     */
	def ajaxSubjects = {
		SecUser user = authenticationService.getLoggedInUser()
		studyViewService.wrap(response, params, { study, summary ->
			render(view: "elements/subjects", model: [
					subjects: study.subjects,
					canRead: study.canRead(user),
					canWrite: study.canWrite(user)
			])
		})
	}

    /**
     * ajax call to update study domain fields
     */
	def ajaxUpdateStudy = {
		SecUser user = authenticationService.getLoggedInUser()
		String name = (params.containsKey('name')) ? params.get('name') : ''
		String value = (params.containsKey('value')) ? params.get('value') : ''
		String uuid = (params.containsKey('identifier')) ? params.get('identifier') : ''
		Map result = [:]

		// fetch study
		Study study = Study.findWhere(UUID: uuid)

        // do we have a study, and does the logged in user have write access to it?
		if (study && study.canWrite(user)) {
			study.cleanup = false

			// update field
			if (name == "template") {
				def template = Template.findByName(value)
				study.template = template
			} else {
				study.setFieldValue(name, value)
			}

			// validate instance
			if (study.validate()) {
                // the study validates
				String fieldType = study.giveFieldType(name).toString().toLowerCase()

                // save the study
				if (study.save()) {
                    // all went well
					response.status = 200

					// add to audit trail
					studyViewService.addToAuditTrail(study, fieldType, name, value)
				} else {
                    // something went wrong trying to save the study
					response.status = 409

                    // get errors
					def error = study.errors.getFieldError(name)
					String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
					String rejectedValue= (error) ? error?.rejectedValue : value
					String comment      = study.getField(name)?.comment

                    // define result map containing feedback to return as JSON to the client
					result = [
							error: errorMessage,
							rejectedValue: rejectedValue,
							comment: comment
					]
				}
			} else {
                // the study does not validate
				response.status = 412

                // get domain errors
				def error = study.errors.getFieldError(name)
				String fieldType    = study.giveFieldType(name).toString().toLowerCase()
				String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
				String rejectedValue= (error) ? error?.rejectedValue : value
				String comment      = study.getField(name)?.comment

                // define the result map containing feedback to return as JSON to the client
				result = [
						error: errorMessage,
						rejectedValue: rejectedValue,
						comment: comment
				]
			}
		} else {
            // no user logged in, or the user has no write access
			response.status = 401
		}

		// set output headers
		response.contentType = 'application/json;charset=UTF-8'

        // render JSON
		if (params.containsKey('callback')) {
			render "${params.callback}(${result as JSON})"
		} else {
			render result as JSON
		}
	}

    /**
     * ajax call to update a subject that is part of a study
     */
	def ajaxUpdateSubject = {
		SecUser user = authenticationService.getLoggedInUser()
		String name = (params.containsKey('name')) ? params.get('name') : ''
		String value = (params.containsKey('value')) ? params.get('value') : ''
		String uuid = (params.containsKey('identifier')) ? params.get('identifier') : ''
		Map result = [:]

		// does the subject exist?
        Subject subject = Subject.findWhere(UUID: uuid)
		if (subject) {
            // yes, check if the user has write access to this study
            Study study = subject.parent
            if (study.canWrite(user)) {
				// update field
				if (name == "template") {
					def template = Template.findByName(value)
					subject.template = template
				} else {
					// do we need to do something special?
					switch (subject.giveFieldType(name)) {
						case TemplateFieldType.ONTOLOGYTERM:
							value = Term.findById(value)
							break
					}

					// update the subject
					subject.setFieldValue(name, value)
				}

				// validate subject
				if (subject.validate()) {
					String fieldType    = subject.giveFieldType(name).toString().toLowerCase()

					// save subject
                    if (subject.save()) {
						response.status = 200

						// add to audit trail
						studyViewService.addToAuditTrail(subject, fieldType, name, value)
					} else {
                        // something went wrong while saving the subject
						response.status = 409

                        // get the errors
						def error = subject.errors.getFieldError(name)
						String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
						String rejectedValue= (error) ? error?.rejectedValue : value
						String comment      = subject.getField(name)?.comment

                        // define a result map containing user feedback
						result = [
								error: errorMessage,
								rejectedValue: rejectedValue,
								comment: comment
						]
					}
				} else {
                    // the subject does not validate
					response.status = 412

                    // get error messages
					def error = subject.errors.getFieldError(name)
					String fieldType    = subject.giveFieldType(name).toString().toLowerCase()
					String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
					String rejectedValue= (error) ? error?.rejectedValue : value
					String comment      = subject.getField(name)?.comment

                    // and build a result map containing user feedback
					result = [
							error: errorMessage,
							rejectedValue: rejectedValue,
							comment: comment
					]
				}
			} else {
                // the user is not allowed to write to this study
				response.status = 401
			}
		} else {
            // the subject does not exist
			response.status = 401
		}

		// set output headers
		response.contentType = 'application/json;charset=UTF-8'

        // render as JSON
		if (params.containsKey('callback')) {
			render "${params.callback}(${result as JSON})"
		} else {
			render result as JSON
		}
	}

    /**
     * load audit trail for a study
     */
	def ajaxAuditTrail = {
		SecUser user= authenticationService.getLoggedInUser()
		String uuid = (params.containsKey('identifier')) ? params.get('identifier') : ''
		Study study = Study.findWhere(UUID: uuid)
		Date today = new Date()

		response.contentType = "text/plain"
		response.characterEncoding = "UTF-8"

		// check if user is allowed to access the audit log
		if (study && study.canWrite(user)) {
            // user is allowed
			render(view: "common/auditTrail", model: [
					study: study,
					user: user,
					today: today
			])
		} else {
            // the user may not view the audit trail
			render(view: "errors/invalid")
		}
	}
}
