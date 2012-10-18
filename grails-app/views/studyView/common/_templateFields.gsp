<element>
	<g:each in="${fields}" var="field">
		<g:set var="fieldType" value="${field.type.toString().toLowerCase()}"/>
		<field>
			<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" />
		</field>
	</g:each>
</element>
