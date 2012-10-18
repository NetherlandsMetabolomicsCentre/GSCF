<element identifier="${entity.giveUUID()}">
	<g:each in="${fields}" var="field">
		<g:set var="fieldType" value="${field.type.toString().toLowerCase()}"/>
		<g:if test="${canWrite}">
			<field contenteditable="true" class="editable"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></field>
		</g:if>
		<g:else>
			<field><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></field>
		</g:else>
	</g:each>
</element>
