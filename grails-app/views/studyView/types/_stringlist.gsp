<g:if test="${canWrite}">
	<value class="${css}">
		<input type="text" name="${field.name}" class="editable" required="${field.required}" value="${value}"/>
	</value>
</g:if>
<g:else>
	<value class="${css}">${value}</value>
</g:else>

<% /**
 ${value}${field.listEntries}<input list="browsers" name="browser" value="${value}">
 <datalist id="browsers">
 <option value="Internet Explorer">
 <option value="Firefox">
 <option value="Chrome">
 <option value="Opera">
 <option value="Safari">
 </datalist>
 * */ %>
