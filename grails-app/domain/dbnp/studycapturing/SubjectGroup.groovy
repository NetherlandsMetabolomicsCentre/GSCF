package dbnp.studycapturing
import org.dbnp.gdt.Identity

/**
 * SubjectGroup Domain Class
 *
 * Models a group of subjects, which can be used to design your study
 * Belongs to the Study class
 */

class SubjectGroup extends Identity {

	String name

	static belongsTo = [parent : Study]
	static hasMany = [
			subjects: Subject
	]

	static constraints = {
		// Ensure that the event group name is unique within the study
		name(unique:['parent'])
	}

}
