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
		    $(document).on('hover blur focus change', '.editable', function(event) {
			    var t = $(this);        // input element
			    var p = t.parent();     // value element
			    var r   = p.parent();   // row element
			    var pp  = r.parent();   // block element enclosing the rows
			    var entityType  = t.parent().parent().attr('type');

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

				    switch (t.attr('type')) {
					    case 'date':
						    handleDateField(t);
						    break;
					    case 'text':
						    handleTextField(t);
						    break;
					    case 'number':
						    handleNumberField(t);
					    default:
							console.log('no special stuff for ' + t.attr('type'));
						    break;
				    }
			    } else if (event.type == "focusout" || (event.type == 'change' && t.attr('type') == 'checkbox')) {
				    // stop editting
				    p.toggleClass('editting');

				    var previousData    = jQuery.data(t[0], 'data');
				    var previousValue   = (previousData) ? previousData.previousValue : null;
				    var newValue        = (t.attr('type') == 'checkbox') ? t.is(':checked') : t.val().trim();

				    // did the value change?
				    if (!previousData || (previousData && newValue != previousValue)) {
					    var identifier  = t.parent().parent().attr('identifier');
					    var name        = t.attr('name');

//console.log(t.attr('name') + ' :: ' + previousValue + ' > ' + newValue);

					    updateValue(t, entityType, identifier, name, newValue);
				    }
			    }
		    });

		    function handleDateField(element) {
			    element.datepicker({
				    duration: '',
				    showTime: false,
				    constrainInput:false,
				    dateFormat: 'dd/mm/yy',
				    onClose: function() {
					    element.blur();
				    }
			    }, 'dd/mm/yy', element.val());
		    }

		    function handleTextField(element) {
			    blurOnEnter(element);
		    }

		    function handleNumberField(element) {
			    blurOnEnter(element);
		    }

		    /**
		     * Skip to the next input field if an enter key is pressed
		     * @param element
		     */
		    function blurOnEnter(element) {
				element.bind('keyup',function(event) {
					if (event.keyCode == 13) {
						// unbind event handler and blur input element
						element.unbind('keyup');
						element.blur();

						// find next input element and focus it
						var nextElement = $('.editable', element.parent().next());
						if (nextElement.length != 1) {
							nextElement = $('.editable', element.parent().parent().next());
						}
						nextElement.focus();

						// and move the cursor to the end (and ignore trailing spaces)
						var v = nextElement.val();
						if (v) {
							var c = v.replace(/ *$/, '').length;
							nextElement[0].setSelectionRange(c, c);
						}
					}
				});
		    }

		    function updateValue(element, entityType, identifier, name, newValue) {
//console.log('ajax update a '+entityType+' with uuid:'+identifier+', name:'+name+', value:'+newValue);
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

					    // remove previous tooltip
					    parentElement.unbind('hover');

						// animate
					    parentElement.removeClass('updating');
					    parentElement.css({ 'background-color': '#e8503e' });
					    parentElement.animate({ 'background-color': '#fee8e5' }, 400);

						// add error message tooltip
					    parentElement.tipTip({
						    content: obj.error
					    });
				    },
				    success: function() {
					    // remove previous tooltip
					    parentElement.unbind('hover');

						// animate
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
