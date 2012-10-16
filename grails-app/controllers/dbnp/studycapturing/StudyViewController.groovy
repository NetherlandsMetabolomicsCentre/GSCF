/**
 * StudyViewController
 *
 * This controller provides the possibility to view
 * and modify a study
 */
package dbnp.studycapturing

import dbnp.authentication.SecUser
import org.hibernate.Criteria

class StudyViewController {
	def authenticationService

	/**
	 * list the studies where the viewer has access to
	 */
	def index = {
		SecUser user = authenticationService.getLoggedInUser()
		def studies = Study.giveReadableStudies( user );

		render(view: "list", model: [studies: studies])
	}

	/**
	 * view, create or modify a study (if applicable)
	 */
	def view = {
		SecUser user = authenticationService.getLoggedInUser()
		Long id = (params.containsKey('id') && (params.get('id').toLong()) > 0) ? params.get('id').toLong() : 0
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
		}

		println user
		println study

		// got a study?
		if (study) {
			// yes, render the study view page
			render(view: "view", model: [study: study, user: user] )
		} else {
			// no user and/or no study. As only users can create
			// a new study show the 401 page
			render(view: "/error/_401")
		}
	}
}
