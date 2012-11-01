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

	def view = {
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0

		SecUser user = authenticationService.getLoggedInUser()
		Study study = studyViewService.fetchStudyForCurrentUserWithId(id)

		// got a study?
		if (study) {
			// yes, render the study view page
			render(view: "view", model: [
					study   : study,
					canRead : study.canRead(user),
					canWrite: study.canWrite(user)
			])
		} else {
			// no user and/or no study. As only users can create
			// a new study show the 401 page
			render(view: "/error/_401")
		}
	}

	def ajaxTimeline = {
		SecUser user = authenticationService.getLoggedInUser()
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/timeline", model: [study: study, summary: summary, canRead: study.canRead(user), canWrite: study.canWrite(user)])
		})
	}

	def ajaxDetails = {
		SecUser user = authenticationService.getLoggedInUser()
		Integer cleanupInDays = RemoveExpiredStudiesJob.studyExpiry
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/details", model: [
					study: study,
					summary: summary,
					canRead: study.canRead(user),
					canWrite: study.canWrite(user),
					cleanupInDays: cleanupInDays
			])
		})
	}

	def ajaxSubjects = {
		SecUser user = authenticationService.getLoggedInUser()
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/subjects", model: [subjects: study.subjects, canRead: study.canRead(user), canWrite: study.canWrite(user)])
		})
	}

	def ajaxUpdateStudy = {
		println "update study: ${params}"

		SecUser user = authenticationService.getLoggedInUser()
		String name = (params.containsKey('name')) ? params.get('name') : ''
		String value = (params.containsKey('value')) ? params.get('value') : ''
		String uuid = (params.containsKey('identifier')) ? params.get('identifier') : ''
		Map result = [:]

		// fetch study
		Study study = Study.findWhere(UUID: uuid)

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
				if (study.save()) {
					response.status = 200
				} else {
					response.status = 409

					def error = study.errors.getFieldError(name)
					String fieldType    = study.giveFieldType(name).toString().toLowerCase()
					String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
					String rejectedValue= (error) ? error?.rejectedValue : value
					String comment      = study.getField(name)?.comment

					result = [
							error: errorMessage,
							rejectedValue: rejectedValue,
							comment: comment
					]
				}
			} else {
				response.status = 412

				def error = study.errors.getFieldError(name)
				String fieldType    = study.giveFieldType(name).toString().toLowerCase()
				String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
				String rejectedValue= (error) ? error?.rejectedValue : value
				String comment      = study.getField(name)?.comment

				result = [
						error: errorMessage,
						rejectedValue: rejectedValue,
						comment: comment
				]
			}
		} else {
			response.status = 401
		}

		// set output headers
		response.contentType = 'application/json;charset=UTF-8'

		if (params.containsKey('callback')) {
			render "${params.callback}(${result as JSON})"
		} else {
			render result as JSON
		}
	}

	def ajaxUpdateSubject = {
		println params

		SecUser user = authenticationService.getLoggedInUser()
		String name = (params.containsKey('name')) ? params.get('name') : ''
		String value = (params.containsKey('value')) ? params.get('value') : ''
		String uuid = (params.containsKey('identifier')) ? params.get('identifier') : ''
		Map result = [:]

		Subject subject = Subject.findWhere(UUID: uuid)
		if (subject) {
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
					if (subject.save()) {
						response.status = 200
					} else {
						response.status = 409

						def error = subject.errors.getFieldError(name)
						String fieldType    = subject.giveFieldType(name).toString().toLowerCase()
						String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
						String rejectedValue= (error) ? error?.rejectedValue : value
						String comment      = subject.getField(name)?.comment

						result = [
								error: errorMessage,
								rejectedValue: rejectedValue,
								comment: comment
						]
					}
				} else {
					response.status = 412

					def error = subject.errors.getFieldError(name)
					String fieldType    = subject.giveFieldType(name).toString().toLowerCase()
					String errorMessage = (error) ? g.message(error: error) : g.message(code: "templateEntity.typeMismatch.${fieldType}", args: [name, value])
					String rejectedValue= (error) ? error?.rejectedValue : value
					String comment      = subject.getField(name)?.comment

					result = [
							error: errorMessage,
							rejectedValue: rejectedValue,
							comment: comment
					]
				}
			} else {
				response.status = 401
			}
		} else {
			response.status = 401
		}

		// set output headers
		response.contentType = 'application/json;charset=UTF-8'

		if (params.containsKey('callback')) {
			render "${params.callback}(${result as JSON})"
		} else {
			render result as JSON
		}
	}
}
