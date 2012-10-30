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
				    p.addClass('editting');

				    // remember current value
				    var v = t.val();
				    jQuery.data(t[0], 'data', { previousValue: (v) ? v.trim() : null });

				    // handle tabbed scrolling
				    // remembered scroll position of all rows in this block
				    var cl  = jQuery.data(pp[0], 'data') ? jQuery.data(pp[0], 'data').left : 0;
				    var sl  = (cl == -100) ? 0 : r.scrollLeft();    // scroll position of this row

				    // sometimes the left focus becomes 1 pixels, if so we want it to be zero to
				    // show the left pixel of the row as well
				    var toZero = (sl < 10);
				    if (toZero) sl = 0;

				    // do we need to handle scrolling?
				    if (sl != cl) {
					    // yes, the row has scrolled and we need to scroll the
					    // other rows as well. Iterate through other rows
					    pp.children().each(function () {
						    // scroll row to the new position
						    var e = $(this);
						    if (e[0] != p[0] || toZero) e.scrollLeft(sl);
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
						    break;
					    case 'stringlist':
						    handleStringListField(t);
						    break;
					    default:
							console.log('no special stuff for ' + t.attr('type'));
							console.log(t);
						    break;
				    }
			    } else if (event.type == "focusout" || (event.type == 'change' && t.attr('type') == 'checkbox')) {
				    // stop editting
				    p.removeClass('editting');

				    var previousData    = jQuery.data(t[0], 'data');
				    var previousValue   = (previousData) ? previousData.previousValue : null;
				    var v = t.val();
				    var newValue        = (t.attr('type') == 'checkbox') ? t.is(':checked') : ((v) ? v.trim() : null);

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
			    blurOnEnter(element);

			    // launch datepicker
			    element.datepicker({
				    duration: 500,
				    showTime: false,
				    constrainInput:false,
				    dateFormat: 'dd/mm/yy',
				    onSelect: function() {
					    // focus the next element
					    focusNextElement(element);
				    },
				    onClose: function() {
					    // remove date picker
					    element.datepicker("destroy");
				    }
			    }, 'dd/mm/yy', element.val());
		    }

		    function handleTextField(element) {
			    blurOnEnter(element);
		    }

		    function handleNumberField(element) {
			    blurOnEnter(element);
		    }

		    function handleStringListField(element) {
			    blurOnEnter(element);
		    }

		    /**
		     * Skip to the next input field if an enter key is pressed
		     * @param element
		     */
		    function blurOnEnter(element) {
				element.bind('change keyup',function(event) {
					if (event.keyCode == 13) {
						// unbind event handler and blur input element
						element.unbind('keyup');
						var nextElement = focusNextElement(element);
						moveCursorToEnd(nextElement);
					}
				});
		    }

		    function focusNextElement(element) {
			    var row  = element.parent().parent();
			    var block = (row.hasClass('horizontal')) ? row : row.parent();
			    var elements = $('.editable', block);
				var count = elements.length;
			    var current = 0;
			    var nextElement = [];

			    // blur element
			    element.blur();

				// find current input element
			    for (var c=0;c<count;c++) {
				    if (elements[c] == element[0]) {
					    current = c;
					    break;
				    }
			    }

			    // was this the last input element in this block?
			    var jumpRow = false;
			    if (current == (count-1)) {
				    // yes, get the next block
				    var nextBlock = block.next();
				    var check = true;
				    var ne = false;

				    while (nextBlock.length == 1 && check) {
					    var elements = $('.editable', nextBlock);
					    if (elements.length > 0) {
							ne = $(elements[0]);
						    jumpRow = true;
						    check = false;
					    } else {
						    nextBlock = nextBlock.next();
					    }
				    }

				    // got a next element?
				    nextElement = (ne) ? ne : element;
			    } else {
				    nextElement = $(elements[c+1]);
			    }

			    // there seems to be an issue where scrollLeft first triggers the onLeft event
			    // and only updated the scrollLeft value afterwards. This causes the logic unable
			    // to detect scrolling, and rows get miss aligned.
			    if (jumpRow) {
				    // 1. force the cache to expire to make sure the rows get updated:
				    jQuery.data(block.parent()[0], 'data', { left: -100 });
			    }

				// is the nextElement in view?
			    var width = block.width();
			    var right = nextElement.position().left + nextElement.width() + block.scrollLeft();
			    var scroll= ((right - width) < 0) ? 0 : (right - width);
			    var left  = block.scrollLeft();
			    if (scroll > left) {
				    // 2. force scroll to make sure that scrollLeft is known in the 'scrollin' event
				    //    handler when we focus
				    block.scrollLeft(scroll);
			    }

			    // 3. focus the next element and trigger the 'focusin' event handler
			    nextElement.focus();

			    // and return it
			    return nextElement;
		    }

		    function moveCursorToEnd(element) {
			    if (element.is('select')) return;

			    var v = element.val();
			    if (v) {
				    var c = v.replace(/ *$/, '').length;
				    element[0].setSelectionRange(c, c);
			    }
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
