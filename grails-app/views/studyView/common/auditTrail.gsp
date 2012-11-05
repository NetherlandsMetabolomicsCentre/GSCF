<auditTrail>
<g:each in="${study.audits}" var="entry">
	<entry>
		<g:if test="${user == entry.user}">
			<who<g:if test="${user == entry.user}"> class="you"</g:if>>you</who>
		</g:if>
		<name>${entry.fieldName[0].toUpperCase()}${entry.fieldName.substring(1)}</name>
		<value>${entry.fieldValue}</value>
		<g:if test="${entry.dateCreated.dateString == today.dateString}">
			<date class="today">today</date>
		</g:if>
		<g:else>
			<date>${entry.dateCreated.dateString}</date>
		</g:else>
		<time><g:formatDate date="${entry.dateCreated}" type="time" style="SHORT"/></time>
	</entry>
</g:each>
</auditTrail>