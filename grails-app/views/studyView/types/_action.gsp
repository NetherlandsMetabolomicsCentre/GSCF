<g:if test="${canWrite}">
    <g:actionSubmitImage action="delete" value="delete" src="${fam.icon(name: 'delete')}" alt="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
</g:if>
<g:else>
    <img src='${fam.icon(name: 'cross')}'/>
</g:else>