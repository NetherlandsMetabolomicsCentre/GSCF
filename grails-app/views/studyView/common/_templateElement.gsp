<element identifier="${entity.giveUUID()}">
	<description>
		${field.name[0].toUpperCase()}${field.name.substring(1)}
	</description>
	<g:if test="${canWrite}">
		<value contenteditable="true" class="editable"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>
	</g:if>
	<g:else>
		<value><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>
	</g:else>
	<debug>
		${fieldType}
	</debug>
</element>
