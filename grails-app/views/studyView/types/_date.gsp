<g:if test="${value != null}">
	<g:if test="${value.getHours() == 0 && value.getMinutes() == 0}">
		${String.format('%td/%<tm/%<tY', value)}
	</g:if>
	<g:else>
		${String.format('%td/%<tm/%<tY %<tH:%<tM', value)}
	</g:else>
</g:if>
