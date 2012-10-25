<html>
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'studyView.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'tipTip.css')}"/>
	<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.tipTip.minified.js')}"></script>
    <script type="text/javascript">
	    var canRead = ${canRead};
	    var canWrite = ${canWrite};

	    $('document').ready(function () {
		    <g:if test="${canWrite}">
		    // (current and future) event handlers
		    $(document).on('hover blur focus', '.editable', function(event) {
			    var t = $(this);        // input element
			    var p = t.parent();     // value element
			    var r   = p.parent();   // row element
			    var pp  = r.parent();   // block element enclosing the rows

			    if (event.type == "mouseenter" || event.type == "mouseleave") {
				    p.toggleClass('highlight');
			    } else if (event.type == 'focusin') {
					// start editting class
				    p.toggleClass('editting');

				    // remember current value
				    jQuery.data(t[0], 'data', { previousValue: t.val().trim() });

				    // handle tabbed scrolling
				    var sl  = r.prop('scrollLeft');         // scroll position of this row
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

				    // handle special types
				    if (t.attr('type') == 'date') {
					    handleDateField(t);
				    }
			    } else if (event.type == "focusout") {
				    // stop editting
				    p.toggleClass('editting');

				    var previousData    = jQuery.data(t[0], 'data');
				    var previousValue   = (previousData) ? previousData.previousValue : null;
				    var newValue        = t.val().trim();

				    // did the value change?
				    if (!previousData || (previousData && newValue != previousValue)) {
					    var identifier  = t.parent().parent().attr('identifier');
					    var entityType  = t.parent().parent().attr('type');
					    var name        = t.attr('name');

					    updateValue(t, entityType, identifier, name, newValue);
				    }
			    }
		    });

		    function handleDateField(element) {
			    console.log(element.val());
			    element.datepicker({
				    duration: '',
				    showTime: false,
				    constrainInput:false,
				    dateFormat: 'dd/mm/yy',
				    onClose: function() {
					    console.log('closseeeee');
					    element.blur();
				    }
			    }, 'dd/mm/yy', element.val());
		    }

		    function updateValue(element, entityType, identifier, name, newValue) {
				console.log('ajax update a '+entityType+' with uuid:'+identifier+', name:'+name+', value:'+newValue);
			    var parentElement = element.parent();
			    parentElement.addClass('updating');

			    // perform ajax call
			    $.ajax({
				    url:"<g:resource/>/studyView/ajaxUpdate" + entityType,
				    dataType: "json",
				    context: document.body,
				    data: {
					    identifier: identifier,
					    name: name,
					    value: newValue
				    },
				    error: function(msg) {
					    var obj = jQuery.parseJSON(msg.responseText);
					    parentElement.removeClass('updating');
					    parentElement.css({ 'background-color': '#e8503e' });
					    parentElement.animate({ 'background-color': '#fee8e5' }, 400);
					    parentElement.tipTip({
						    content: obj.error
					    });
				    },
				    success: function() {
					    console.log('unbind tiptip! tipTip does not yet support this, so we need to hack it... TODO');
					    parentElement.removeClass('updating');
					    parentElement.css({ 'background-color': '#bbe094' });
					    parentElement.animate({ 'background-color': '#f2ffe4' }, 400);
				    }
			    });
		    }
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
