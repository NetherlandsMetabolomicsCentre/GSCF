<g:if test="${studyList*.samples.flatten()?.size()==0}">
  No samples in the selected studies
</g:if>
<g:else>
  <table>
	<thead>
	  <tr>
		<g:if test="${multipleStudies}">
		  <th></th>
		</g:if>

		  <th>Parent Subject</th>
		  <th>Parent Sampling Event</th>


		<g:each in="${new dbnp.studycapturing.Sample().giveDomainFields()}" var="field">
		  <th>${field}</th>
		</g:each>

		<%
		  // Determine a union of the fields for all different
		  // samples in all studies. In order to show a proper list.
		  // We want every field to appear just once,
		  // so the list is filtered for unique values
		  sampleTemplates = studyList*.giveSampleTemplates().flatten().unique()

		  if( !sampleTemplates ) {
			sampleTemplates = [];
			sampleFields = [];
			showSampleFields = [];
		  } else {
			sampleFields = sampleTemplates*.fields.flatten().unique()
			if( !sampleFields ) {
			  sampleFields = [];
			  showSampleFields = [];
			} else {
			  // Filter out all fields that are left blank for all samples
			  allSamples = studyList*.samples.flatten()

			  showSampleFields = [];
			  sampleFields.each { sampleField ->
				for( sample in allSamples )
				{
				  // If the field is filled for this subject, we have to
				  // show the field and should not check any other
				  // samples (hence the break)
				  if( sample.fieldExists( sampleField.name ) && sample.getFieldValue( sampleField.name ) ) {
					showSampleFields << sampleField;
					break;
				  }
				}
			  }
			}
		  }
		%>

		<g:each in="${showSampleFields}" var="field">
		  <th>${field}</th>
		</g:each>

	  </tr>
	</thead>

	<g:set var="i" value="${1}" />

	<g:each in="${studyList}" var="studyInstance">
	  <%
		// Sort samples by name
		samples = studyInstance.samples;
		sortedSamples = samples.sort( { a, b -> a.name <=> b.name } as Comparator )
	  %>

	  <g:each in="${sortedSamples}" var="sample" status="j">
		<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		  <g:if test="${multipleStudies && j==0}">
			<td class="studytitle" rowspan="${sortedSamples?.size()}">
			  ${studyInstance.title}
			</td>
		  </g:if>
			<td>${sample.parentSubject?.name}</td>
			<td>${sample.parentEvent?.template?.name} at ${sample.parentEvent?.getStartTimeString()}</td>
		  <g:each in="${sample.giveDomainFields()}" var="field">
			<td><wizard:showTemplateField field="${field}" entity="${sample}" /></td>
		  </g:each>

		  <g:each in="${showSampleFields}" var="field">
			<td>
			  <g:if test="${sample.fieldExists(field.name)}">
				<wizard:showTemplateField field="${field}" entity="${sample}" />
			  </g:if>
			  <g:else>
				N/A
			  </g:else>
			</td>
		  </g:each>

		</tr>
		<g:set var="i" value="${i + 1}" />
	  </g:each>
	</g:each>

  </table>
</g:else>