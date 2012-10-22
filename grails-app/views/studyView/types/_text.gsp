<g:if test="${canWrite}">
	<value contenteditable="true" class="editable ${css}" name="${field.name}">${value}</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>