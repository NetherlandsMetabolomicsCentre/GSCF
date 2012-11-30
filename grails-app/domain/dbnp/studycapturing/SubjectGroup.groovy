package dbnp.studycapturing
import org.dbnp.gdt.Identity
import org.dbnp.gdt.TemplateEntity

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

	/**
	 * Calculate all events which occur in this event group
	 */
	List<Event> giveEvents() {
		List<Event> events = new ArrayList<Event>()
		parent.subjectEventGroups.each {
			if (this.id == it.subjectGroup.id) {
				it.eventGroup.events.each { event ->
					if (event) events << event
				}
			}
		}
		events
	}

	/**
	 * Calculate all sampling events which occur in this event group
	 */
	List<SamplingEvent> giveSamplingEvents() {
		List<SamplingEvent> events = new ArrayList<SamplingEvent>()
		parent.subjectEventGroups.each {
			if (this.id == it.subjectGroup.id) {
				it.eventGroup.samplingEvents.each { event ->
					if (event) events << event
				}
			}
		}
		events
	}
}
