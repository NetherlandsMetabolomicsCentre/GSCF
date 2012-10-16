<html>
<head>
    <meta name="layout" content="main"/>
    <style type="text/css">
    h1 {
        font-size: 100pt;
        font-family: Haettenschweiler;
        text-shadow: 0 3px 3px rgba(0, 0, 0, 0.68);
        color: #ffd1ca;
        display: inline-block;
        width: 200px;
    }
    h2 {
        font-size: 22pt;
        display: inline-block;
        color: #ff1610;
        font-family: "HelveticaNeue-UltraLight", "Helvetica Neue Light", Verdana;
        width: 700px;
    }
    </style>
</head>
<body>
<h1>401</h1>
<h2>
    You are not authorized to view the page you are looking for.
    <sec:ifLoggedIn>
        <sec:ifAllGranted roles="ROLE_ADMIN">
            As you are administrator and should be able to view everything, the most
            likely cause is that the content you tried to request does not exist.
        </sec:ifAllGranted>
        <sec:ifNotGranted roles="ROLE_ADMIN">
        This means that your account has insufficient rights to view the content
        you were trying to request.
        </sec:ifNotGranted>
    </sec:ifLoggedIn><sec:ifNotLoggedIn>
        You should probably either log in or create an account if you have none.
    </sec:ifNotLoggedIn>
</h2>
</body>
</html>
