<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>

<g:if test="${studies.size()}">
    <h1>List of studies:</h1>

    <g:each in="${studies}" var="study">
        <li>
            <g:link controller="studyView" action="view" params="[id:study.id]">
                ${study}
            </g:link>
        </li>
    </g:each>
</g:if>
<g:else>
    <h1>No studies</h1>

    <p>
        At this moment you do not yet own any studies, nor are you a reader or a writer to a study owned by someone else.
    </p>
    <p>
        Please proceed by <g:link controller="studyView" action="view">creating</g:link> your first study.
    </p>
</g:else>

<img src='${fam.icon(name: 'add')}'/> <g:link controller="studyView" action="view">create a new study</g:link><br/>

</body>
</html>
