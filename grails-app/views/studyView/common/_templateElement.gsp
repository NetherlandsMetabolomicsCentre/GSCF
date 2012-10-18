<element>
	<description>
		${field.name[0].toUpperCase()}${field.name.substring(1)}
	</description>
	<value contenteditable="true" class="editable">
		<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" />
	</value>
	<debug>
		${fieldType}
	</debug>
</element>
