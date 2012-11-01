<g:if test="${subjects}">
	<sv:header entity="${subjects}"/>
	<g:each in="${subjects}" var="subject">
		<sv:horizontal entity="${subject}" canRead="${canRead}" canWrite="${canWrite}"/>
	</g:each>
</g:if>
