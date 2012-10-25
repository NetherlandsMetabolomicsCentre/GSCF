<value class="${css}">
	<g:if test="${value != null}">
		<% /** g:if test="${value.getHours() == 0 && value.getMinutes() == 0}" **/ %>
		<input type="date" value="${String.format('%td/%<tm/%<tY', value)}">
	</g:if>
</value>
