/**
 * jquery plugin to handle a timeline
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author		Jeroen Wesbeek
 * @since		20130114
 * @requires	jquery, jquery-ui, jquery.xcolor.js
 */
(function($){
    // plugin methods
    var methods = {
        // retun the startTime of the latest eventGroup. Note that
        // this is relative to the beginning of the timeline / start
        // of the study
        getStartTimeOfLastEventGroup: function(data,options) {
            var startTime = 0;
            $('eventGroup', data.me).each(function() {
                var relTime = parseInt($(this).attr('startTime'));
                if (relTime > startTime) startTime = relTime;
            });
            return startTime;
        },

        // position all event groups based on their relative start time
        // to the beginning of the study / timeline
        initEventGroups: function(data, options) {
            $('eventGroup', data.me).each(function() {
                // position this event group
                methods.positionEventGroup(this, data, options);

                // bind on click event
                $(this).bind('click', function() {
                    methods.showDetails(this, data, options);
                });
            });
        },

        detailsAnimateSlide: function(eg, slideOut, color, sg, data, options) {
            console.log('slide out');

            // add slideOut to DOM
            slideOut.insertAfter(sg);

            // slide out...
            slideOut.animate({
                'height': '+=200px'
            }, options.slideDuration, function() {
                console.log('height animation done');
                // ...create and slide up the top arrow...
                var topArrow = $('<toparrow></toparrow>');
                topArrow.css({
                    'border-bottom-color'   :color,
                    'top'   :slideOut.offset().top + 'px',
                    'left'  :eg.offset().left + 'px'
                });
                slideOut.append(topArrow);
                topArrow.animate({ 'top':'-=10px' });
            });

            var above = (slideOut.nextAll('slideout').length > 0);
            var plus = (above) ? '+=200px' : '+=10px'

            // and slide other open sliders in...
            $('slideout', data.ME).each(function() {
                var s = $(this);
                if (s.get(0) != slideOut.get(0)) {
                    // slide in arrow
                    var a = $('toparrow', s);
                    a.animate({
                        'top': plus
                    }, options.slideDuration / options.slideDurationSpeedup, function() {
                        a.remove();
                    });

                    // slide in and remove element...
                    s.animate({
                        'height': '0px'
                    }, options.slideDuration, function() {
                        s.remove();
                    });
                }
            });
        },

        detailsClose: function(slideOut, data, options) {
            // slide slider in...
            slideOut.animate({
                'height': '0px'
            }, options.slideDuration, function() {
                slideOut.remove();
            });

            // ...and slide arrow down
            var t = $('toparrow', slideOut);
            t.animate({
                'top': '+=10px'
            }, options.slideDuration / options.slideDurationSpeedup, function() {
                t.remove();
            });
        },

        detailsAnimateTransition: function(eg, slideOut, color, ne, data, options) {
            console.log('transition');

            // add slideOut to DOM
            slideOut.insertAfter(ne);
            slideOut.css({
                'height': '+=200px',
                'margin-top': '-200px',
                'opacity' : '0'
            });

            // ...create the top arrow...
            var topArrow = $('<toparrow></toparrow>');
            topArrow.css({
                'border-bottom-color'   :color,
                'top'   :slideOut.offset().top + 'px',
                'left'  :eg.offset().left + 'px'
            });
            slideOut.append(topArrow);

            // fade in new slideOut...
            slideOut.animate({
                'opacity'   : '1'
            }, options.slideDuration, function() {
                console.log('done!');
                slideOut.css({ 'margin-top': '0px'});
                ne.remove();
                topArrow.animate({ 'top':'-=10px' }, options.slideDuration);
            });

            // ...and arrow...
            $('toparrow', ne).animate({ 'top': '+=10px' }, options.slideDuration / options.slideDurationSpeedup);
        },

        showDetails: function(eg, data, options) {
            var EG          = $(eg);
            var SG          = EG.parent().parent();
            var NE          = SG.next();
            var color       = EG.css('background-color');
            var lightColor  = $.xcolor.opacity(color, '#ffffff', 0.8).getRGB();
//			var lightColor  = $.xcolor.opacity(color, '#f2f2f2', 0.99).getRGB();

            // add slideout element
            var slideOut = $('<slideout><close></close><content>this should be rendered using an ajax call for eventGroup uuid ' + EG.attr('uuid') + '</content></slideout>');
            slideOut.css({'background-color': lightColor});

            // fetch details using ajax
            console.log('perform ajax call to fill details div with eventGroup information (uuid: '+EG.attr('uuid')+')');

            // when done, the following should be executed:
            // is the next element also a slideout element?
            if (NE && NE.is('slideout')) {
                // transition
                methods.detailsAnimateTransition(EG, slideOut, lightColor, NE, data, options);
            } else {
                // slide out
                methods.detailsAnimateSlide(EG, slideOut, lightColor, SG, data, options);
            }
        },

        // position one eventGroup based on it's relative start time
        // and set a background color if defined
        positionEventGroup: function(eg, data, options) {
            //console.log('position eventGroup:');
            var EG = $(eg);
            var startTime = parseInt(EG.attr('startTime'));
            var left = (((data.width - data.left) / data.latestRelativeTime) * startTime) + data.left;

            // position element
            EG.css({ 'left': left });

            // does this eventGroup has a color defined?
            var c = EG.attr('color');
            if (c) EG.css({ 'background-color': '#'+c });
        },

        // get the max width of the SubjectGroup details
        getMaxSubjectGroupDetailsWidth: function(data,options) {
            var width = 0;
            var outerWidth = 0;

            // determine max width
            $('subjectGroup > details', data.EM).each(function() {
                var w = $(this).width();
                var ow = $(this).outerWidth();
                if (w > width) width = w;
                if (ow > outerWidth) outerWidth = ow;
            });

            // resize all details elements to this max width
            $('subjectGroup > details', data.EM).each(function() {
                var e = $(this);
                if (e.width() < width) {
                    e.css( { 'width': width + 'px' } );
                }
            });

            return outerWidth;
        },

        // get the right position of the SubjectGroup's details, and
        // hence the left position/beginning of the timeline
        getLeftPosition: function(data,options) {
            return $('subjectGroup > details', data.EM).first().outerWidth();
        },

        bindEventHandlers: function(me, data, options) {
            me.on('click', 'close', function () {
                methods.detailsClose($(this).parent(), data, options);
            });
        },

        // initialize a timeline
        init: function(me,data,options) {
            //console.log('* initializing timeline');
            //console.log(me);

            // get me, myself and I
            var ME	= $(me);

            // reference myself
            data.me	= me;
            data.ME	= ME;

            // determine start and stop time
            data.startTime		= parseInt(ME.attr('startTime'));
            data.latestRelativeTime	= methods.getStartTimeOfLastEventGroup(data,options);
            data.endTime		= data.startTime + data.latestRelativeTime;

            // determine and set timeline width
            data.width		= ME.width();
            data.left		= methods.getMaxSubjectGroupDetailsWidth(data,options);

            // position eventGroups
            methods.initEventGroups(data, options);

            // bind event handlers
            methods.bindEventHandlers(ME, data, options);
        }
    };

    // define the jquery plugin code
    $.fn.timeline = function(options) {
        // default settings
        var defaults = {
            'slideDuration'	        : 500,
            'slideDurationSpeedup'  : 2
        };

        // data
        var data = {
        };

        // extend the jQuery options with the defaults
        var options = $.extend(defaults, options);

        // iterate through DOM elements and initialize them
        return this.each(function() {
            methods.init(this,data,options);

            console.log('options:');
            console.log(options);
            console.log('data:');
            console.log(data);
        });
    };
})(jQuery);