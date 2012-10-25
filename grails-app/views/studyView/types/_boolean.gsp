<g:if test="${canWrite}">
<value class="${css}">
	<g:if test="${value}">
		<input type="checkbox" name="name" class="editable" checked />
	</g:if>
	<g:else>
		<input type="checkbox" name="name" class="editable" />
	</g:else>
</value>
</g:if>
<g:else>
	<value class="${css}">
		<g:if test="${value}">
			<img src='${fam.icon(name: 'tick')}'/>
		</g:if>
		<g:else>
			<img src='${fam.icon(name: 'cross')}'/>
		</g:else>
	</value>
</g:else>