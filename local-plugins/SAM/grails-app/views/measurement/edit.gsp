<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="sammain"/>
        <title>Edit measurement for ${module}</title>
    </head>

    <body>
        <content tag="contextmenu">
            <g:render template="contextmenu" />
        </content>
        <g:hasErrors bean="${measurementInstance}">
            <div class="errors">
                <g:renderErrors bean="${measurementInstance}" as="list"/>
            </div>
        </g:hasErrors>
        <h1>Edit measurement</h1>

        <div class="data">
            <g:form method="post">
                <g:hiddenField name="id" value="${measurementInstance?.id}"/>
                <g:hiddenField name="version" value="${measurementInstance?.version}"/>
                <g:hiddenField name="module" value="${module}"/>
                <div class="dialog">
                    <table>
                        <tbody>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="value"><g:message code="measurement.value.label" default="Value"/></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: measurementInstance, field: 'value', 'errors')}">
                                <g:textField name="value" value="${measurementInstance.value}"/>
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="operator"><g:message code="measurement.operator.label" default="Operator"/></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: measurementInstance, field: 'operator', 'errors')}">
                                <g:textField name="operator" value="${measurementInstance?.operator}"/>
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="comments"><g:message code="measurement.comments.label" default="Comments"/></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: measurementInstance, field: 'comments', 'errors')}">
                                <g:textField name="comments" value="${measurementInstance?.comments}"/>
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="sample"><g:message code="measurement.sample.label" default="Sample"/></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: measurementInstance, field: 'sample', 'errors')}">
                                <g:select name="sample.id" from="${org.dbxp.sam.SAMSample.list()}" optionKey="id"
                                          value="${measurementInstance?.sample?.id}"/>
                            </td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="feature"><g:message code="measurement.feature.label" default="Feature"/></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: measurementInstance, field: 'feature', 'errors')}">
                                <g:select name="feature.id" from="${org.dbxp.sam.Feature.list()}" optionKey="id"
                                          value="${measurementInstance?.feature?.id}"/>
                            </td>
                        </tr>

                        </tbody>
                    </table>
                </div>

                <ul class="data_nav buttons">
                    <li><g:actionSubmit class="save" action="update" value="Update"/></li>
                    <li><g:actionSubmit class="delete" action="delete" value="Delete" onclick="return confirm('Are you sure?');"/></li>
                    <li><g:link controller="measurement" action="list" class="cancel" params="${[module: module]}">Cancel</g:link></li>
                </ul>

            </g:form>
        </div>
    </body>
</html>
