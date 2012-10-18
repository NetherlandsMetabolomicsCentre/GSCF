/**
 * StudyViewController
 *
 * This controller provides the possibility to view
 * and modify a study
 */
package dbnp.studycapturing

import dbnp.authentication.SecUser

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
		println params
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/timeline", model: [study: study, summary: summary])
		})
	}

	def ajaxDetails = {
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/details", model: [study: study, summary: summary])
		})
	}

	def ajaxSubjects = {
		studyViewService.wrap(params, { study, summary ->
			render(view: "elements/subjects", model: [subjects: study.subjects])
		})
	}
}
