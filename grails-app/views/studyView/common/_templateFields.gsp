<element identifier="${entity.giveUUID()}" class="horizontal">
	<g:each in="${fields}" var="field">
		<g:set var="fieldType" value="${field.type.toString().toLowerCase()}"/>
		<g:if test="${canWrite}">
			<value contenteditable="true" class="editable horizontal"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>
		</g:if>
		<g:else>
			<value class="horizontal"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>
		</g:else>
	</g:each>
</element>
