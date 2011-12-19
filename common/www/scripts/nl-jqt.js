// Functions specific to the jqtouch version of NL
var jQT = new $.jQTouch({
	icon : 'icon-app-57px.png',
	icon4 : 'icon-app-114px.png',
	statusBar : 'black',
	backSelector : '.back, .goback',
	addGlossToIcon : false,
	startupScreen : 'nl-startup.png',
	useAnimations : false,
	//debug: true,
	preloadImages : [ 'themes/naturelocator/img/btn-green-active.png',
			'themes/naturelocator/img/btn-black-active.png',
			'themes/naturelocator/img/btn-lightbrown-active.png',
			'themes/naturelocator/img/loader-large.gif',
			'themes/naturelocator/img/icon-transmit-large.png',
			'themes/naturelocator/img/icon-transmit-medium.png',
			'themes/naturelocator/img/icon-qmark-large.png',
			'themes/naturelocator/img/icon-tick.png',
			'themes/naturelocator/img/icon-tick-large.png' ]
});

var allowClick = true;
function preventGhostClick() {
	allowClick = false;
	setTimeout(function() {
		allowClick = true;
	}, 500);
}

// jquery ready
$(document).ready(function() {

	updatePendingCount();

	// call pending check on index anim in
	$('#home').bind('pageAnimationStart', function(event, info) {
		if (info.direction == 'in')
			updatePendingCount();
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	// location anim in start
	$('#location').bind('pageAnimationStart', function(event, info) {
		if (info.direction == 'in') {
			$('#location-gps').hide();
		}
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	// location anim out start
	$('#location').bind('pageAnimationEnd', function(event, info) {
		if (info.direction == 'in') {
			getLocation();
		}
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	$('#location-gps').bind('pageAnimationEnd', function(event, info) {
		if (info.direction == 'in') {
			getLocation();
		}
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	// update archive list on my records anim in
	$('#archive').bind('pageAnimationStart', function(event, info) {
		if (info.direction == 'in') {
			updateArchiveList();
		}
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	// update email, check send button on new anim in
	$('#new').bind('pageAnimationEnd', function(event, info) {
		if (info.direction == 'in') {
			updateEmail();
			checkSend();
		}
		$(this).data('referrer'); // return the link which triggered the
									// animation, if possible
	});

	// buttons
	// Switched off
//	$('#archive-actions-send').bind('tap', function() {
//		if (!allowClick)
//			return false;
//		preventGhostClick();
//		sendPending();
//	});

	// Switched off
//	$('#archive-actions-unsent-delete').bind('tap', function() {
//		if (!allowClick)
//			return false;
//		preventGhostClick();
//		deletePending();
//	});

	// Switched off
//	$('#archive-actions-sent-delete').bind('tap', function() {
//		if (!allowClick)
//			return false;
//		preventGhostClick();
//		clearSent();
//	});

	$('#archive-actions-delete').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		clearList();
	});

	$('#new-cancel').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		reset();
	});

	$('#new-send').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		send();
// Send now/later option switched off
//		moveDownToPage('#send');
	});

// Send now/later option switched off
//	$('#send-now').bind('tap', function() {
//		if (!allowClick)
//			return false;
//		preventGhostClick();
//		send();
//	});
//
//	$('#send-later').bind('tap', function() {
//		if (!allowClick)
//			return false;
//		preventGhostClick();
//		save();
//	});

	$('#location-gps-set').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		setLocationGPS();
		moveBackToPage('#new');
	});

	$('#location-manual-set').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		setLocationManual();
		moveBackToPage('#new');
	});

	$('#locating-cancel').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		cancelLocating();
		moveToPage('#location-manual');
	});

	$('#sending-cancel').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		cancelSending();
	});

	$('#photo-take').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		takePic(true);
	});

	$('#photo-roll').bind('tap', function() {
		if (!allowClick)
			return false;
		preventGhostClick();
		takePic(false);
	});

	$('.can-tap').bind('tap', function() {
		var id = $(this).attr('id');
		if (!allowClick) {
			log('can-tap disallowed. id: ' + id);
			return false;
		}
		preventGhostClick();

		if (typeof id == 'undefined') {
			return;
		}

		var pageTo;
		var back = true;

		log(id + " tapped");

		if (id.lastIndexOf('litter-select', 0) === 0) {
			var page = '#new';
			setLitter(id);
			pageTo = page;
		} else if (id.lastIndexOf('damage-select', 0) === 0) {
			var page = '#new';
			if (id == 'damage-select-0') {
				setDamage(0);
				pageTo = page;
			}
			if (id == 'damage-select-1') {
				setDamage(1);
				pageTo = page;
			}
			if (id == 'damage-select-2') {
				setDamage(2);
				pageTo = page;
			}
			if (id == 'damage-select-3') {
				setDamage(3);
				pageTo = page;
			}
			if (id == 'damage-select-4') {
				setDamage(4);
				pageTo = page;
			}
			if (id == 'damage-select-unknown') {
				setDamage(DAMAGE_UNKNOWN);
				pageTo = page;
			}
		} else if (id.lastIndexOf('location', 0) === 0) {
			if (id == 'location-search-toggle') {
				$("#location-search-form").slideToggle();
			}
			if (id == 'location-set') {
				setLocation();
				pageTo = '#new';
			}
        } else if (id.lastIndexOf('help', 0) === 0) {
                       var target = null;
                       if (id == 'help-home') {
                    target = '#home';
                       } else if (id == 'help-new') {
                       target = '#new';
                       } else if (id == 'help-damage') {
                       target = '#damage';
                       }
                       if (target != null) {
                        $(".help-back").attr('href', target);
                       }
		}

		if (typeof pageTo != 'undefined') {
			if (back) {
				moveBackToPage(pageTo);
			} else {
				moveToPage(pageTo);
			}
		}

	});

});

function checkClickSendPending(id) {
	if (!allowClick)
		return false;
	preventGhostClick();
	sendPending(id);
}

function moveBack() {
	if (onHome())
		device.exitApp();

	jQT.goBack();
}

function moveToPage(ref) {
	jQT.goTo(ref, 'slideleft');
}

function moveBackToPage(ref) {
	jQT.goTo(ref, 'slideright');
}

function moveDownToPage(ref) {
	jQT.goTo(ref, 'slideup');
}

function moveStraightToPage(ref) {
	jQT.goTo(ref);
}

function fadeToPage(ref) {
	jQT.goTo(ref, 'fade');
}

function onHome() {
	return $("#home.current").length > 0;
}
