<g:if test="${canWrite}">
	<value class="${css}">
		<sv:ontologyterm name="${field.name}" type="${field.type}" class="editable" required="${field.required}" from="${field.ontologies}" value="${value}" />
		%{--<g:select name="${field.name}" type="${field.type}" class="editable" required="${field.required}" from="${field.ontologies}" value="${value}" />--}%
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>

<%/**

 <value class="${css}">
 [term:${value}]
 </value>



 case 'ONTOLOGYTERM':
 // @see http://www.bioontology.org/wiki/index.php/NCBO_Widgets#Term-selection_field_on_a_form
 // @see ontology-chooser.js
 inputElement = (renderType == 'element') ? 'termElement' : 'termSelect'

 // override addDummy to always add the dummy...
 addDummy = true

 if (templateField.ontologies) {
 out << "$inputElement"(
 description: ucName,
 name: prependName + templateField.escapedName(),
 value: fieldValue.toString(),
 ontologies: templateField.ontologies,
 addDummy: addDummy,
 required: templateField.isRequired()
 ) {helpText}
 } else {
 out << "$inputElement"(
 description: ucName,
 name: prependName + templateField.escapedName(),
 value: fieldValue.toString(),
 addDummy: addDummy,
 required: templateField.isRequired()
 ) {helpText}
 }
 **/%>