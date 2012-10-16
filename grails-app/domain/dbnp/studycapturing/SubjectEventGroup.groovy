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

class SubjectEventGroup extends Identity {

	long startTime // this property is accessed as a RelTime object, which stores long values

	static belongsTo = [parent : Study]
	static hasMany = [
			eventGroups: EventGroup,
			subjectGroups: SubjectGroup
	]
	// In general, we expect in most cases the SubjectEventGroup to have one eventGroup,
	// which is subsequently linked to several subjectGroups at particular times
	// However, this model provides flexibility to extend the model with e.g. groups of event groups

	static constraints = {
	}

	/**
	 * return the domain fields for this domain class
	 * @return List<TemplateField>
	 */
	static List<TemplateField> giveDomainFields() { return SubjectEventGroup.domainFields }

	// To improve performance, the domain fields list is stored as a static final variable in the class.
	static final List<TemplateField> domainFields = [
			new TemplateField(
					name: 'startTime',
					type: TemplateFieldType.RELTIME,
					comment: "Please enter the start time as a relative time from study start date."+RelTime.getHelpText(),
					required: true)
	]

}
