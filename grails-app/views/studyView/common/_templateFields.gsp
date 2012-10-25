<element identifier="${entity.giveUUID()}" type="${entityType}" class="horizontal">
	<g:each in="${fields}" var="field">
		<g:set var="fieldType" value="${field.type.toString().toLowerCase()}"/>
		<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name), 'css': 'horizontal', 'canRead': canRead, 'canWrite': canWrite, 'horizontal': true]" />
	</g:each>
</element>
