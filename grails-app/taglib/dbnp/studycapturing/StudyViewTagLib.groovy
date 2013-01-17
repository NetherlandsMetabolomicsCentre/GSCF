/**
 * StudyViewTagLib Tag Library
 *
 * Description
 */
package dbnp.studycapturing

import org.dbnp.gdt.TemplateEntity
import org.dbnp.gdt.Term
import org.dbnp.gdt.Template

class StudyViewTagLib {
	// define the tag namespace (e.g.: <foo:action ... />
	static namespace = "sv"

    // inject the studyViewService
	def studyViewService

    /**
     * render a domain class and it's template fields vertically. Used for rendering
     * and editing in more detail.
     * (e.g. a study, a subject, a sample, etcetera).
     */
	def vertical = { attrs, body ->
		def entity = (attrs.containsKey('entity') && attrs.get('entity') instanceof TemplateEntity) ? attrs.get('entity') : null
		Boolean canRead = (attrs.containsKey('canRead')) ? attrs.get('canRead').toString().toBoolean() : false
		Boolean canWrite = (attrs.containsKey('canWrite')) ? attrs.get('canWrite').toString().toBoolean() : false
		String entityType = entity.class.toString().split(/\./).last()

//		// add template field
//		def templates = Template.findAllByEntity(entity.class)
//		out << render(template: "common/templateElement", model: [
//				entity: entity,
//				entityType: entityType,
//				field: [
//					name    : 'template',
//					value   : entity.template,
//					required: true
//				],
//				fieldType: 'template',
//				canRead: canRead,
//				canWrite: canWrite,
//				templates   : Template.findAllByEntity(entity.class)
//		])

		// iterate through entity fields
		entity.giveFields().each { field ->
			out << render(template: "common/templateElement", model: [
					entity: entity,
					entityType: entityType,
					field: field,
					fieldType:field.type.toString().toLowerCase(),
					canRead: canRead,
					canWrite: canWrite
			])
		}
	}

    /**
     * render a domain class and its template fields horizontally. Generally used
     * for rendering more than one instances of the same domain class in a limited
     * browser space (less detailed than rendering horizontally, e.g. many subjects)
     */
	def horizontal = { attrs, body ->
		def entity = (attrs.containsKey('entity') && attrs.get('entity') instanceof TemplateEntity) ? attrs.get('entity') : null
		Boolean canRead = (attrs.containsKey('canRead')) ? attrs.get('canRead').toString().toBoolean() : false
		Boolean canWrite = (attrs.containsKey('canWrite')) ? attrs.get('canWrite').toString().toBoolean() : false
		String entityType = entity.class.toString().split(/\./).last()

//		def templates = Template.findAllByEntity(entity.class)

		out << render(template: "common/templateFields", model: [
				entity: entity,
				entityType: entityType,
				fields: entity.giveFields(),
				canRead: canRead,
				canWrite: canWrite
//				templates: templates
		])
	}

    /**
     * render the header for a domain class. Used in horizontal rendering
     */
	def header = { attrs, body ->
		def entity = (attrs.entity instanceof List) ? attrs.entity[0] : attrs.entity
		String entityType = entity.class.toString().split(/\./).last()

		out << render(template: "common/templateFieldHeaders", model: [
		        entity: entity,
				entityType: entityType,
				fields: entity.giveFields()
		])
	}

    /**
     * render an ontologyterm field
     */
	def ontologyterm = { attrs, body ->
		HashSet ontologies = attrs.get('from')

		def terms = studyViewService.termsForOntologies(ontologies)

		attrs.from = terms
		attrs.optionKey = "id"
		attrs.value = attrs?.value?.id

		out << select(attrs)
	}

    /**
     * render a template field
     */
	def template = { attrs, body ->
		def entity = attrs.get('entity')
		def templates = Template.findAllByEntity(entity.class)

		attrs.from = templates
		attrs.optionKey = "id"
		attrs.values = attrs.value.id

		out << select(attrs)
	}
}