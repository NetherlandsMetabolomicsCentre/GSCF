<g:if test="${canWrite}">
	<value class="${css}">
		<g:select name="${field.name}" type="stringlist" class="editable" required="${field.required}" from="${field.listEntries}" value="${value}" />
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>