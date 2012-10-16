package dbnp.studycapturing
import org.dbnp.gdt.*

/**
 * EventGroup Domain Class
 * Models a group of events that can be re-used in the study design
 * Is referred to by the SubjectEventGroup class
 */
class EventGroup extends Identity {
	String name

	static belongsTo = [parent : Study]
	static hasMany = [
			events: Event,
			samplingEvents: SamplingEvent
	]

	static constraints = {
		// Ensure that the event group name is unique within the study
		name(unique:['parent'])
	}
}
