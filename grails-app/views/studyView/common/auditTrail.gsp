<auditTrail>
<g:each in="${study.audits}" var="entry">
	<entry>
		<g:if test="${entry.entityType}">
			<icon class="${entry.entityType?.toLowerCase()}" uuid="${entry.entityUUID}"/>
		</g:if>
		<g:else>
			<icon class="study" uuid="${entry.study.giveUUID()}" />
		</g:else>
		<g:if test="${user == entry.user}">
			<who class="you">you</who>
		</g:if>
		<g:else>
			<who class="you">you</who>
		</g:else>
		<icon class="${entry.fieldType} ${entry.fieldName?.toLowerCase()}"/>
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
