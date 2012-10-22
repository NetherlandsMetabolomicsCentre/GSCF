<html>
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'studyView.css')}"/>
    <script type="text/javascript">
	    var canRead = ${canRead};
	    var canWrite = ${canWrite};

	    $('document').ready(function () {
		    <g:if test="${canWrite}">
		    // (current and future) event handlers
		    $(document).on('hover blur focus', '.editable', function(event) {
			    var t = $(this);
			    if (event.type == "mouseenter" || event.type == "mouseleave") {
				    t.toggleClass('highlight');
			    } else if (event.type == 'focusin') {
					// start editting class
				    t.toggleClass('editting');

				    // handle tabbed scrolling
				    var p   = t.parent();                   // row element
				    var sl  = p.prop('scrollLeft');         // scroll position of this row
				    var pp  = p.parent()                    // block element enclosing the rows
				    // remembered scroll position of all rows in this block
				    var cl  = jQuery.data(pp[0], 'data') ? jQuery.data(pp[0], 'data').left : 0;

				    // do we need to handle scrolling?
				    if (sl != cl) {
					    // yes, the row has scrolled and we need to scroll the
					    // other rows as well. Iterate through other rows
					    pp.children().each(function () {
						    // scroll row to the new position
						    var e = $(this);
						    if (e[0] != p[0]) e.prop('scrollLeft', sl);
					    });
				    }

					// remember current scroll position in order of being able to compare
				    // to the scroll position (sl) of the row to determine if we need to
				    // change the scroll position of all rows or not (to save resources)
				    jQuery.data(pp[0], 'data', { left: sl });
			    } else if (event.type == "focusout") {
				    // stop editting
				    t.toggleClass('editting');
			    }
		    });
		    </g:if>

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
