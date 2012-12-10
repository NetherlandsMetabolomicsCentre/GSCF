<g:if test="${study.cleanup}">
	<warning>
		Created a new study. Note that this study will be cleaned up in ${cleanupInDays} days if not modified.
	</warning>
</g:if>
<sv:vertical entity="${study}" canRead="${canRead}" canWrite="${canWrite}" />

<auditLink>[slick this to show audit trail]</auditLink>
