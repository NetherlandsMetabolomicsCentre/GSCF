<html>
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'studyView.css')}"/>
    <script type="text/javascript">
	    $('document').ready(function () {
		    // populate all elements
		    $('div.studyView').each(function () {
			    var that = this;

			    // fire ajax call to populate element
			    $.ajax({
				    url:"<g:resource/>/studyView/ajax" + this.id.charAt(0).toUpperCase() + this.id.slice(1),
				    context:document.body
			    }).done(function (msg) {
					$(that).html(msg);
				});
		    });
	    });
    </script>
</head>
<body>

<div id="timeline" class="studyView"></div>

<div id="details" class="studyView"></div>

<p>
    study view / edit / create dashboard page
</p>

<p>
study id: ${studyData.study.id}<br/>
study title: ${studyData.study.title}<br/>
owner: ${studyData.study.owner}<br/>
canRead: ${studyData.canRead}<br/>
canWrite: ${studyData.canWrite}<br/>
modified: ${studyData.modified}
</p>

</body>
</html>
