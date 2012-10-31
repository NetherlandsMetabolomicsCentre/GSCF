<g:if test="${canWrite}">
	<value class="${css}">
		<sv:ontologyterm name="${field.name}" type="${field.type.toString().toLowerCase()}" class="editable" required="${field.required}" from="${field.ontologies}" value="${value}" />
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>