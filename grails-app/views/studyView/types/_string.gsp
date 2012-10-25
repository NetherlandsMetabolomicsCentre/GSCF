<g:if test="${canWrite}">
	%{--<value contenteditable="true" class="editable ${css}" name="${field.name}">${value}</value>--}%
	<value class="${css}">
		<input type="text" name="${field.name}" class="editable" required="${field.required}" value="${value}"/>
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>