<g:if test="${canWrite}">
	%{--<value contenteditable="true" class="editable ${css}" name="${field.name}">${value}</value>--}%
	<value contenteditable="true" class="${css}">
		<textarea name="${field.name}" class="editable" required="${field.required}">${value}</textarea>
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>