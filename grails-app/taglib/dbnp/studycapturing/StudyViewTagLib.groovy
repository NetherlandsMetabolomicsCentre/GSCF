/**
 * StudyViewTagLib Tag Library
 *
 * Description
 */
package dbnp.studycapturing

import org.dbnp.gdt.TemplateEntity

class StudyViewTagLib {
	// define the tag namespace (e.g.: <foo:action ... />
	static namespace = "sv"

	/**
	 * bar tag (e.g. <foo:bar ... />
	 * @param Map attributes
	 * @param Closure
	 */
	def foo = { attrs, body ->
		// render bar
		out << "bla"
	}

	def vertical = { attrs, body ->
		def entity = (attrs.containsKey('entity') && attrs.get('entity') instanceof TemplateEntity) ? attrs.get('entity') : null

		// iterate through entity fields
		entity.giveFields().each { field ->
			out << render(template: "common/templateElement", model: [entity: entity, field: field, fieldType:field.type.toString().toLowerCase()])
		}
	}

	def horizontal = { attrs, body ->
		def entity = (attrs.containsKey('entity') && attrs.get('entity') instanceof TemplateEntity) ? attrs.get('entity') : null

		out << render(template: "common/templateFields", model: [entity: entity, fields: entity.giveFields()])
	}
}