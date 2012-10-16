<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>

<p>
    study view / edit / create dashboard page
</p>

<p>
study id: ${study.id}<br/>
study title: ${study.title}<br/>
owner: ${study.owner}<br/>
canRead: ${study.canRead(user)}<br/>
canWrite: ${study.canWrite(user)}<br/>
</p>

</body>
</html>
