<g:if test="${canWrite}">
	<value class="${css}">
		<sv:template entity="${entity}" name="${field.name}" type="template" class="editable" required="${field.required}" value="${value}" />
		%{--<g:select name="${field.name}" type="template" class="editable" required="${field.required}" from="${templates}" value="${value}" />--}%
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>