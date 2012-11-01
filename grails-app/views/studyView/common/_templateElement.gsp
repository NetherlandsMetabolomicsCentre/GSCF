<element identifier="${entity.giveUUID()}" type="${entityType}" class="vertical">
	<description>
		${field.name[0].toUpperCase()}${field.name.substring(1)}
	</description>

	<g:if test="${fieldType == 'template'}">
		<g:render template="types/template" model="['entity':entity, 'field':field, 'value': field.value, 'css': 'vertical', 'canRead': canRead, 'canWrite': canWrite, 'horizontal': false, 'templates': templates]" />
	</g:if>
	<g:else>
		<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name), 'css': 'vertical', 'canRead': canRead, 'canWrite': canWrite, 'horizontal': false]" />
	</g:else>

	<debug>
		<g:if test="${field.required}"><b>*</b></g:if>
		${fieldType}
	</debug>
</element>
