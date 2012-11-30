package dbnp.studycapturing
import org.dbnp.gdt.Identity
import org.dbnp.gdt.RelTime
import org.dbnp.gdt.TemplateField
import org.dbnp.gdt.TemplateFieldType

/**
 * SubjectEventGroup Domain Class
 *
 * Models the fact that a certain subject group undergoes the (sampling) events in an eventGroup
 * at a certain time point in the study
 */

class SubjectEventGroup {

	long startTime // this property is accessed as a RelTime object, which stores long values
    EventGroup eventGroup
    SubjectGroup subjectGroup

    static belongsTo = [parent : Study]

	static constraints = {
	}

}
