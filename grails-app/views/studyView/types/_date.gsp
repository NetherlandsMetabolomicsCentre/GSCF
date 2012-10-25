<g:if test="${canWrite}">
	<value class="${css}">
		<g:if test="${value != null}">
			<input type="date" name="${field.name}" class="editable" required="${field.required}" value="${String.format('%td/%<tm/%<tY', value)}" placeholder="dd/mm/yyyy"/>
		</g:if>
		<g:else>
			<input type="date" name="${field.name}" class="editable" required="${field.required}" placeholder="dd/mm/yyyy"/>
		</g:else>
	</value>
</g:if>
<g:else>
	<g:if test="${value != null}">
		<value class="${css}">${String.format('%td/%<tm/%<tY', value)}</value>
	</g:if>
	<g:else>
		<value class="${css}">-</value>
	</g:else>
</g:else>
