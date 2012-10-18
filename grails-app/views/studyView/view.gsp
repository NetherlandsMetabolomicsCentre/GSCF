<html>
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'studyView.css')}"/>
    <script type="text/javascript">
	    var canRead = ${canRead};
	    var canWrite = ${canWrite};

	    $('document').ready(function () {
		    // (current and future) event handlers
		    $(document).on('hover blur focus', '.editable', function(event) {
			    var t = $(this);
			    if (event.type == "mouseenter" || event.type == "mouseleave") {
				    t.toggleClass('highlight');
			    } else if (event.type == 'focusin' || event.type == "focusout") {
				    t.toggleClass('editting');
			    }
		    });

		    // populate all elements
		    $('div#studyView > div.box').each(function () {
			    var element = $(this);
			    element.addClass('waitForLoad');

			    // fire ajax call to populate element
			    $.ajax({
				    url:"<g:resource/>/studyView/ajax" + this.id.charAt(0).toUpperCase() + this.id.slice(1),
				    context:document.body,
				    data: {
					    id: "${study.id}",
					    info: this.id,
				        summary: true
				    }
			    }).done(function (msg) {
						    element.removeClass('waitForLoad');
							element.html(msg);
						    element.animate({ height: element.prop('scrollHeight') }, 500);
				});
		    });
	    });
    </script>
</head>
<body>

<div id="studyView">
	<h1>canRead: ${canRead}, canWrite: ${canWrite}</h1>

	<div id="timeline" class="box"></div>

	<div id="details" class="box"></div>

	<div id="subjects" class="box"></div>
</div>

</body>
</html>
