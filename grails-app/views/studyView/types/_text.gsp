<g:if test="${canWrite}">
	<value class="${css}">
		<g:if test="${horizontal}">
			<input type="text" name="${field.name}" class="editable" required="${field.required}" value="${value}"/>
		</g:if>
		<g:else>
			<textarea name="${field.name}" class="editable" required="${field.required}">${value}</textarea>
		</g:else>
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>