/**
 * Quartz job to remove expired un-editted studies
 *
 * As of the refactoring of October 2012, the new studyViewController
 * allows users to create new studies on the fly. However, as we do not
 * want to fill the database with garbage studies, the 'cleanup' property
 * is automatically set to 'true' when a study is created on the fly. When
 * a user edits the study, the property is set to false.
 *
 * This quartz job will remove all studies which have the cleanup property
 * set to true and were created over X days ago.
 */

package dbnp.studycapturing

class RemoveExpiredStudiesJob {
	// number of days after which cleanup studies are to be removed
	static Integer studyExpiry = 5

	// the maximum number of expired accounts to delete per job run
	static Integer maxDeletionsPerBatch = 100

    // define job trigger(s)
	static triggers = {
        // cronjob that runs every whole hour
        cron name: 'removeExpiredStudies', cronExpression: "0 0 * * * ?"
    }

	// job logic
	def execute() {
		// fetch expired garbage studies
		def criteria = Study.createCriteria()
		def studies = criteria.list(max: maxDeletionsPerBatch) {
			and {
				eq("cleanup", true)
				lt("dateCreated", new Date() - studyExpiry)
			}
		}

		// if we have studies, delete them
		if (studies.size()) {
			log.info "removing ${studies.size()} studies marked for cleanup and over ${studyExpiry} days old"

			// remove studies
			studies.each { study ->
				study.delete()
			}
		}
	}
}
