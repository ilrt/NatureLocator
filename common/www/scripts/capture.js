var loc = null;

var navhistory = new Array();

var state = new Object();
state.hasGPS = false;
state.hasInternet = false;
state.currentPosition = null;

var deviceReady = false;

var gpsCancelled = false;
var sendingCancelled = false;
var sendingCancelCB = null;

var locatingMinimumDone = false;
var locatingCallback = null;
var locatingPosition = null;

var selectedArchiveId = null;

// jquery ready
$(document).ready(function() {
	
	initializeDB();

	// phonegap listener
	document.addEventListener("deviceready", onDeviceReady, false);

	document.addEventListener("backbutton", function(){
		moveBack();
	}, true);
	
});

function onDeviceReady() {
	deviceReady = true;
	getLocationBackground();
}

// this is to kick GPS - we throw away result
function getLocationBackground() {
	navigator.geolocation.getCurrentPosition(locationSuccessBackgroundCallback,
			function(e) { log(e); }, {
				maximumAge : 300000,
				timeout : 120000,
				enableHighAccuracy : isOnMobile()
			});
}

function getLocation() {
	locating(true);
	if(deviceReady)	navigator.network.isReachable('maps.google.com', reachableCallback);
	navigator.geolocation.getCurrentPosition(locationSuccessCallback,
			locationFailureCallback, {
				maximumAge : 300000,
				timeout : 120000,
				enableHighAccuracy : isOnMobile()
			});
}

function locationSuccessBackgroundCallback(position) {
	log("Background GPS successful");
}

function locationSuccessCallback(position) {
	log("GPS successful");

	locating(false, locatingDismissCallback, position);

}

function locatingDismissCallback(position) {

    if(gpsCancelled) return;
    
	// Grab coordinates object from the Position object passed into success
	// callback.
	loc = position.coords;
	state.currentPosition = position;
	state.hasGPS = true;
	if (state.hasInternet)
	{
		var m_str = "http://maps.google.com/maps/api/staticmap?center=" + loc.latitude + "," + loc.longitude + "&zoom=13&size=260x150&sensor=true&markers=color:red%7C" + loc.latitude + "," + loc.longitude;
		$("#location-static-map").attr("src", m_str);

		$("#location-map").show();
		$("#location-no-map").hide();
	}
	else
	{
		// no internet
		$("#location-no-map").show();
		$("#location-map").hide();
	}
	$("#location-coords").text(loc.latitude.toFixed(2) + " lat, " + loc.longitude.toFixed(2) + " long");
	$("#location-gps").show();

}

function isOnMobile() {
    // always return false for speed
    return false;
//	var useragent = navigator.userAgent;
//	if (useragent.indexOf('iPhone') != -1 || useragent.indexOf('Android') != -1)
//		return true;
//	return false;
}

function locationFailureCallback(e) {

	locating(false, locatingDismissFailureCallback, e);

}

function locatingDismissFailureCallback(e) {
//	if (e)
//		showWarning("naturelocator.capture.locationnotfounderror");

	state.hasGPS = false;
	
	moveStraightToPage('#location-manual');
	
}

function reachableCallback(reachability) {
	// There is no consistency on the format of reachability
	var networkState = reachability.code || reachability;

	var states = {};
	states[NetworkStatus.NOT_REACHABLE] = 'No network connection';
	states[NetworkStatus.REACHABLE_VIA_CARRIER_DATA_NETWORK] = 'Carrier data connection';
	states[NetworkStatus.REACHABLE_VIA_WIFI_NETWORK] = 'WiFi connection';

	log('Connection type: ' + states[networkState]);

	state.hasInternet = false;
	if (networkState == NetworkStatus.REACHABLE_VIA_CARRIER_DATA_NETWORK || networkState == NetworkStatus.REACHABLE_VIA_WIFI_NETWORK)
	{
		state.hasInternet = true;
	}
	
}

function updateLocationManualButton() {
	var placename = jQuery.trim($("#placename").val());
	if(placename.length > 0) {
		$('#location-manual-set-inactive').hide();
		$('#location-manual-set').show();
	} else {
		$('#location-manual-set-inactive').show();
		$('#location-manual-set').hide();
	}
}

function updateLocationManualInput() {
	var placename = jQuery.trim($("#placename").val());
	if(placename.length == 0) {
		$("#placename").val('');
	}
}

function setLocationManual() {
	log("setting location from manual");
	var placename = jQuery.trim($("#placename").val());
	if(placename.length > 0) {
		storeLocation(placename);
		if(placename.length > 20) {
			placename = placename.substring(0,20) + '...';
		}
		$("#location-text-latlng").text(placename);
		$("#location-text-latlng").show();
		$("#location-text").hide();
	}
}

function setLocationGPS() {
	log("setting location from gps");
	
	var lat = null;
	var lng = null;
	if (state.hasGPS)
	{
		log("Using last known gps position");
		lat = state.currentPosition.coords.latitude;
		lng = state.currentPosition.coords.longitude;
	}
	
	if (lat != null && lng != null)
	{
		storeLocation(lat + "," + lng);
		$("#location-text-latlng").text(lat.toFixed(2) + ", " + lng.toFixed(2));
		$("#location-text-latlng").show();
		$("#location-text").hide();
	}
}

function resetLocation() {
	storeLocation(null);
	$("#location-text-latlng").hide();
	$("#location-text").show();
	// $("#location-image").attr("src", "images/qm.png");
}

function takePic(useCamera) {
	if (!navigator.camera) {
		savePic("no-camera-test.png");
	} else {
		if (useCamera) {
			navigator.camera.getPicture(savePic, unableToTakePhoto, {
				quality : QUALITY,
				destinationType : Camera.DestinationType.FILE_URI,
				sourceType : Camera.PictureSourceType.CAMERA
			});
		} else {
			navigator.camera.getPicture(savePic, unableToFetchPhoto, {
				quality : QUALITY,
				destinationType : Camera.DestinationType.FILE_URI,
				sourceType : Camera.PictureSourceType.PHOTOLIBRARY
			});
		}
	}
}

function savePic(uri) {
	// log(uri);
	// document.getElementById("photo-image").src = "data:image/jpeg;base64," +
	// data;
    
    
    
	storePhoto(uri);
	$("#photo-image-span").html('<img class="photo-image" id="photo-image" />')
	$("#photo-image").attr("src", uri);
	$('#photo-image').cropresize({
	      width:57,
	      height:57,
	      vertical:"center",
	      horizontal:"center"
	    });  
	$("#photo-image-blank").hide();
	$("#photo-image").show();
	moveBackToPage("#new");
	$("#photo-text").hide();
	$("#photo-image-text").show();
}

function resetPhoto() {
	storePhoto(null);
	$("#photo-image-blank").show();
	$("#photo-image-span").empty()
	$("#photo-text").show();
	$("#photo-image-text").hide();
}

function unableToTakePhoto() {
	// called from camera
	// note: called on 'cancel'
}

function unableToFetchPhoto() {
	// called from camera roll
	// note: called on 'cancel'
}

function setLitter(litterState) {
	if (litterState == null) storeLitter(null);
	else storeLitter(litterState.substring(14)); // litter-state-short => short
	$(".litter-label").hide();
	$("."+litterState).show();
}

function setDamageChoice(damageChoice) {
	switch (damageChoice) {
	case 0: // diagram
		$("#damage-0").attr("src", "images/damage-diagram-0.png");
		$("#damage-1").attr("src", "images/damage-diagram-1.png");
		$("#damage-2").attr("src", "images/damage-diagram-2.png");
		$("#damage-3").attr("src", "images/damage-diagram-3.png");
		$("#damage-4").attr("src", "images/damage-diagram-4.png");
		break;
	case 1: // photo
		$("#damage-0").attr("src", "images/damage-photo-0.png");
		$("#damage-1").attr("src", "images/damage-photo-1.png");
		$("#damage-2").attr("src", "images/damage-photo-2.png");
		$("#damage-3").attr("src", "images/damage-photo-3.png");
		$("#damage-4").attr("src", "images/damage-photo-4.png");
		break;
	}
}

function setDamage(damageState) {
	storeDamage(damageState);

	$("#damage-text").hide();
	$("#damage-text-0").hide();
	$("#damage-text-1").hide();
	$("#damage-text-2").hide();
	$("#damage-text-3").hide();
	$("#damage-text-4").hide();
	$("#damage-text-unknown").hide();
	switch (damageState) {
	case 0:
		$("#damage-text-0").show();
		// $("#damage-image").attr("src", "images/damage-diagram-0.png");
		break;
	case 1:
		$("#damage-text-1").show();
		// $("#damage-image").attr("src", "images/damage-diagram-1.png");
		break;
	case 2:
		$("#damage-text-2").show();
		// $("#damage-image").attr("src", "images/damage-diagram-2.png");
		break;
	case 3:
		$("#damage-text-3").show();
		// $("#damage-image").attr("src", "images/damage-diagram-3.png");
		break;
	case 4:
		$("#damage-text-4").show();
		// $("#damage-image").attr("src", "images/damage-diagram-4.png");
		break;
	case DAMAGE_UNKNOWN:
		$("#damage-text-unknown").show();
		// $("#damage-image").attr("src", "images/damage-unknown.png");
		break;
	default: // reset
		$("#damage-text").show();
		// $("#damage-image").attr("src", "images/qm.png");
	}
}

function updatePendingCount() {
	getPendingCount();
}

function updatePendingCountCB(count) {
	if(count > 0) {
		$("#pending-count").html(" (" + count + " unsent record" + (count == 1?"":"s") + ")");
	} else {
		$("#pending-count").html("");
	}
}

function updateArchiveList() {
	getArchiveList(updateArchiveListCB);
}

function updateArchiveListCB(html) {
	if(html.length > 0) {
		$("#archive-list").html(html);
		$('.archive-photo-image').cropresize({
			width:57,
			height:57,
			vertical:"center",
			horizontal:"center"
	    });
		$("#archive-list").show();
		$("#archive-empty-text").hide();
        $("#archive-actions-buttons").show();
	} else {
		$("#archive-list").hide();
		$("#archive-actions-buttons").hide();
		$("#archive-empty-text").show();
	}
}

function saveEmail() {
	var email = jQuery.trim($("#email").val());
	if(email.length == 0 || EMAIL_REGEX.test(email)) {
		storeEmail($("#email").val());
		updateEmail();
	} else {
		showError("naturelocator.email.invalid");
	}
}

function updateEmail() {
	var email = getEmail();
	if(email != null) {
		$("#email").val(email);
	} else {
		// triggers placeholder
		$("#email").val('');
	}
}

function reset() {
	setDamage(null);
	setLitter(null);
	resetLocation();
	resetPhoto();
	checkSend();
}

function checkSend() {
	log("checking send button");
	getCurrent(checkSendCB);
}

function checkSendCB(data) {
	if(data.damage == null || data.location == null || data.litter == null || data.photoURI == null) {
		setSendButtonActive(false);
	} else {
		setSendButtonActive(true);
	}
}

function setSendButtonActive(isActive) {
	if(isActive) {
		$("#new-send-inactive").hide();
		$("#new-send").show();
	} else {
		$("#new-send-inactive").show();
		$("#new-send").hide();
	}
}

function sending(isSending) {
	if(isSending) {
        sendingCancelled = false;
        sendingCancelCB = null;
		$("#sending").show();
	} else {
		$("#sending").hide();
	}
}

function cancelSending() {
    sendingCancelled = true;
    $("#sending").hide();
    if(sendingCancelCB != null) {
        sendingCancelCB();
    }
}

function locating(isLocating, callback, position) {
    gpsCancelled = false;
	if(isLocating) {
		$("#locating").show();
        locatingMinimumDone = false;
        locatingCallback = null;
        locatingPosition = null;
        window.setTimeout(function() { log('locating min completed'); locatingMinimumDone = true; checkLocatingDone(); }, 2000);
	} else {
        log('locating completed'); 
        locatingCallback = callback;
        locatingPosition = position;
        checkLocatingDone();
	}
}

function checkLocatingDone() {
    if(locatingMinimumDone && locatingCallback != null && locatingPosition != null) {
        $("#locating").hide();
        locatingCallback(locatingPosition);
    }
}

function cancelLocating() {
    gpsCancelled = true;
    $("#locating").hide();
}

//Switched off
//function save() {
//	getCurrent(saveCB);
//}

//Switched off
//function saveCB(data) {
//    pend();
//    moveStraightToPage('#send-later-success');
//}

function send() {
	getCurrent(sendCB);
}

function sendCB(data) {
	__send(data, sendSuccessCB, sendFailureCB, sendCancelCB);
}

function sendSuccessCB(r) {
    
    if(sendingCancelled) return;
    
	log("Code = " + r.responseCode);
	log("Response = " + r.response);
	log("Sent = " + r.bytesSent);
//	alert("Got response:" + r.response);
	sending(false);
	if(r.response == "SUCCESS") {
	    archive();
	    moveStraightToPage('#success');
	} else {
		moveStraightToPage('#error');
	}
}

function sendFailureCB(error) {

    if(sendingCancelled) return;

    //	alert("An error has occurred: Code = " + error.code);
	sending(false);
    pend();
	moveStraightToPage('#failure');
}

function sendCancelCB() {
    pend();
    moveStraightToPage('#send-cancel');
}

//Switched off
//function clearSent() {
//    deleteSent();
//    moveStraightToPage('#sent-delete-success');
//}

function clearList() {
    deleteAllArchive();
    moveStraightToPage('#delete-success');
}

//Switched off
//function checkSendPending(id) {
//    selectedArchiveId = id;
//    moveToPage('#archive-actions');
//}

//Switched off
//function deletePending() {
//    deleteArchive(selectedArchiveId);
//    moveStraightToPage('#pending-delete-success');
//}

function sendPending(id) {
	// Use selectedArchiveId if inserting checkSendPending step
	getArchive(id, function(data) {
		__send(data, function(r) {
               
            if(sendingCancelled) return;

			log("Code = " + r.responseCode);
			log("Response = " + r.response);
			log("Sent = " + r.bytesSent);
//			alert("Got response:" + r.response);
			sending(false);
			if(r.response == "SUCCESS") {
				archivePending(id);
				moveStraightToPage('#pending-success');
			} else {
				moveStraightToPage('#pending-error');
			}
		}, pendFailureCB, pendCancelCB);
	});
}


function pendFailureCB(error) {

    if(sendingCancelled) return;

//	alert("An error has occurred: Code = " + error.code);
	sending(false);
	moveStraightToPage('#pending-failure');
}

function pendCancelCB() {
	moveBackToPage('#archive');
}

function __send(data, successCB, failureCB, cancelCB) {
	var score;
	if (typeof data.damage == 'number' && data.damage >= -1 && data.damage <= 4) {
		score = data.damage + "";
	} else {
		showError("naturelocator.capture.invalidvalue", "damage");
		return;
	}

	var litter;
	if (!data.litter) {
		showError("naturelocator.capture.invalidvalue", "litter");
		return;
	} else {
		litter = data.litter + "";
	}

	var location;
	if (!data.location) {
		showError("naturelocator.capture.invalidvalue", "location");
		return;
	} else {
		location = data.location + "";
	}

	var timestamp;
	if (!data.timestamp) {
		var date = new Date();
		timestamp = (date.getTime()/1000) + ""; // secs
	} else {
		timestamp = data.timestamp + "";
	}

	var uuid = "";
	if (navigator.device) {
		uuid = navigator.device.uuid + "";
	}

	var photoURI;
	if (!data.photoURI) {
		showError("naturelocator.capture.invalidvalue", "photo");
		return;
	}
	photoURI = data.photoURI + "";

	try {
		var options = new FileUploadOptions();
		options.fileKey = "photo";
		options.fileName = photoURI.substr(photoURI.lastIndexOf('/') + 1);
		options.mimeType = "image/jpeg";

		var params = new Object();
		params.uid = uuid;
		params.v = VALIDATION;
		params.score = score;
		params.litter = litter;
		params.location = location;
		params.time = timestamp;
		params.device = "mobile";
		// params.name
		params.email = getEmail();

		options.params = params;

		sending(true);
        sendingCancelCB = cancelCB;

		var ft = new FileTransfer();
		ft.upload(photoURI, UPLOAD_ENDPOINT, successCB,	failureCB, options);

	} catch (e) {
		failureCB(e);
	}

}

