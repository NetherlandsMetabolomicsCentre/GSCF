<elementheader>
	<g:each in="${fields}" var="field">
		<g:if test="${field.comment}">
			<value comment="${field.comment}">${field.name[0].toUpperCase()}${field.name.substring(1)}</value>
		</g:if>
		<g:else>
			<value>${field.name[0].toUpperCase()}${field.name.substring(1)}</value>
		</g:else>
	</g:each>
</elementheader>