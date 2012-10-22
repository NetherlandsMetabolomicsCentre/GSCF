<element identifier="${entity.giveUUID()}" class="vertical">
	<description>
		${field.name[0].toUpperCase()}${field.name.substring(1)}
	</description>

	<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name), 'css': 'vertical', 'canRead': canRead, 'canWrite': canWrite]" />
	%{--<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" />--}%

	%{--<g:if test="${canWrite}">--}%
		%{--<value contenteditable="true" class="editable vertical"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>--}%
	%{--</g:if>--}%
	%{--<g:else>--}%
		%{--<value class="vertical"><g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name)]" /></value>--}%
	%{--</g:else>--}%



	<debug>
		${fieldType}
	</debug>
</element>
