<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>

<h1>list of studies:</h1>

<g:each in="${studies}" var="study">
    <li>
        <g:link controller="studyView" action="view" params="[id:study.id]">
            ${study}
        </g:link>
    </li>
</g:each>

<g:link controller="studyView" action="view">create a new study</g:link><br/>
<g:link controller="studyView" action="view" params="[id:2000]">dummy edit study with id:2000</g:link> (should show 401)<br/>

</body>
</html>
