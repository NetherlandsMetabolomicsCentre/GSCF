%{--<g:if test="${study.cleanup}">--}%
	%{--<warning>--}%
		%{--Created a new study. Note that this study will be cleaned up in ${cleanupInDays} days if not modified.--}%
	%{--</warning>--}%
%{--</g:if>--}%

<sv:vertical entity="${study}" canRead="${canRead}" canWrite="${canWrite}" />

<g:if test="${canWrite && study.audits.size()}">
    <auditLink><img src='${fam.icon(name: 'zoom')}'/> show audit trail</auditLink>
</g:if>
