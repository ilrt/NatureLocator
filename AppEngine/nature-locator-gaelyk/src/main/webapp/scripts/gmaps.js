/*
 * University of Bristol
 */

gmaps = new Object();

// define rough size of lat/lng square in uk
gmaps.oneDegreeLng = 111; // km (not quite accurate but good enough)
gmaps.oneDegreeLat = 68; // km (not quite accurate but good enough)

gmaps.windowSizeLarge = 40; // in km
gmaps.windowSizeMedium = 20; // in km
gmaps.windowSizeSmall = 10; // in km
gmaps.windowSize = gmaps.windowSizeLarge; // init starting pos but will be reset anyway

// define uk co-ordinates
gmaps.ukBottom = 49.9565 * 1e6;
gmaps.ukTop = 59.2244 * 1e6;
gmaps.ukLeft = -10.689697 * 1e6;
gmaps.ukRight = 1.768799 * 1e6;

gmaps.maxRow = 0;
gmaps.maxColumn = 0;
gmaps.arrayGrid = [];
gmaps.stepLat;
gmaps.stepLng;
gmaps.currentZoomLevel;
gmaps.gridSize = 1;

gmaps.iconurl = "http://chart.apis.google.com/";

gmaps.damageColours = [];
gmaps.damageColours[-1]="b3b3b3";
gmaps.damageColours[0]="ffffff";
gmaps.damageColours[1]="ffd700";
gmaps.damageColours[2]="ff8c00";
gmaps.damageColours[3]="ff0000";
gmaps.damageColours[4]="a52a2a";

gmaps.initMap = function()
{
    if (jQuery("#map").length == 1)
    {
	var center = new google.maps.LatLng(LATITUDE,LONGITUDE);

	gmaps.currentZoomLevel = 6;
        gmaps.map = new google.maps.Map(jQuery("#map")[0], {
			zoom:gmaps.currentZoomLevel,
			center:center,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		});
	}
	
	google.maps.event.addListener(gmaps.map, 'zoom_changed', function() {
		gmaps.currentZoomLevel = gmaps.map.getZoom();
		var newSize = calculateWindowSize();
		if (newSize != gmaps.windowSize) gmaps.applyMarkers();
	  });
	
	gmaps.manager = new MarkerManager(gmaps.map);
	google.maps.event.addListener(gmaps.manager, 'loaded', function(){
		gmaps.applyMarkers();
	});
	
	// add legend
	for (var i in gmaps.damageColours)
	{
		$(".damage_"+i).prepend("<img src='"+gmaps.iconurl + "chart?chst=d_map_pin_letter&chld=|"+gmaps.damageColours[i]+"|000000'/>");
	}
}

gmaps.applyMarkers = function()
{
        var len = locations.length;

	var start = jQuery('#startDate option:selected').val();
	var end = jQuery('#startDate option:selected').next().val();
	if (!end)
	{
		// view the next 14 days
		end = parseInt(start) +(1000*60*60*24*14) + "";
	}

	var markers = [];

	if (gmaps.manager) gmaps.manager.clearMarkers();

	// reset grid;
	initGrid();
	
	for (var i=0; i<len; i++)
        {
		if (dates[i] > start && dates[i] < end)
		{
			var points = locations[i].split(",");
			var row = lngToGridPos(parseFloat(points[0]));
			var column =  latToGridPos(parseFloat(points[1]));
						
			if (column != -1 && row != -1)
			{
				gmaps.arrayGrid[row][column].total++;
				
				var maxCurrDam = gmaps.arrayGrid[row][column].maxdamage;
				if (parseInt(damage[i]) > maxCurrDam)
				{
					gmaps.arrayGrid[row][column].maxdamage = parseInt(damage[i]);
				}
			}
		}
	}

	// we now have all grid values set;

	for (var row=0; row<gmaps.maxRow; row++)
	{
		for (var column=0; column<gmaps.maxColumn; column++)
		{
			var dmg = gmaps.arrayGrid[row][column].maxdamage;
			var total = gmaps.arrayGrid[row][column].total;
			if (total > 0)
			{
				var latLng = new google.maps.LatLng((gmaps.ukBottom+(row*gmaps.stepLng)+(gmaps.stepLng/2))/1e6, (gmaps.ukLeft+(column*gmaps.stepLat)+(gmaps.stepLat/2))/1e6);

				var marker = new google.maps.Marker({
					icon: gmaps.iconurl + "chart?chst=d_map_pin_letter&chld="+total+"|"+gmaps.damageColours[dmg]+"|000000",
					position: latLng
				});
				markers.push(marker);
			}
		}
        }

	gmaps.manager.addMarkers(markers,0);
	gmaps.manager.refresh();
}

var currentPage = 0;

gmaps.loadData = function()
{
	currentPage++;
	$.ajax({
		url: "/csvdump/"+pageSize+"/"+currentPage,
		context: document.body,
		dataType: "json",
		success: function(data){
			for  (row in data)
			{
				dates.push(data[row].date);
				locations.push(data[row].location);
				descriptions.push(data[row].description);
				damage.push(data[row].score);
				litter.push(data[row].litter);
			}
			
			var percentage = ((dates.length/totalResults) * 100);
			if (totalResults > 0)
			{
				$( "#progressbar" ).progressbar("value", percentage);
			}
				
			if (dates.length <= totalResults && currentPage*pageSize < totalResults) gmaps.loadData();
			else gmaps.finishLoading();
		},
		error: function(jqXHR) { 
			alert("error");
			console.log(jqXHR);
			$( ".dialog" ).slideUp();
		}
	});
}

gmaps.finishLoading = function()
{
	$( ".dialog" ).slideUp();
	gmaps.applyMarkers();
}

jQuery(document).ready(function(){

	$( ".dialog" ).show();
	$( "#progressbar" ).progressbar();

	gmaps.initMap();

	jQuery('#endDate option:last').attr("selected","selected");
	
	jQuery('.mapcontrols select').selectToUISlider({
			labels: 12,
			sliderOptions: {
			change:function(e, ui) {
				gmaps.applyMarkers();
			}
		}
	});
	
	gmaps.loadData();
});

function calculateWindowSize()
{
	if (gmaps.currentZoomLevel >= 8) return gmaps.windowSizeSmall;
	else if (gmaps.currentZoomLevel >= 7) return gmaps.windowSizeMedium;
	else return gmaps.windowSizeLarge;
}
function initGrid()
{	
	gmaps.windowSize = calculateWindowSize();
	
	gmaps.stepLat = parseInt((gmaps.windowSize/gmaps.oneDegreeLat) * 1e6);
	gmaps.stepLng = parseInt((gmaps.windowSize/gmaps.oneDegreeLng) * 1e6);
	
	gmaps.arrayGrid = [];
	
	// init the grid
	var row = 0;
	for (var j=gmaps.ukBottom; j<=gmaps.ukTop; j = j+gmaps.stepLng)
	{
		var colArray = new Array();

		var column = 0;		
		for (var i=gmaps.ukLeft; i<=gmaps.ukRight; i = i+gmaps.stepLat)
		{
			colArray[column] = new Object()
			colArray[column].maxdamage = -1; // default damage
			colArray[column].total = 0; // default total
			column++;
		}
		gmaps.arrayGrid[row] = colArray;
		gmaps.maxColumn = column - 1;
		row++;
	}
	
	gmaps.maxRow = row - 1;
}

function latToGridPos(lat)
{
	lat = lat * 1e6;
	if (lat < gmaps.ukLeft || lat > gmaps.ukRight) return -1;
	var count = 0;
	for (var i=gmaps.ukLeft; i<gmaps.ukRight; i = i+gmaps.stepLat)
	{
		if (lat >=i && lat < i+gmaps.stepLat) return count;
		count++
	}
	return count;
}

function lngToGridPos(lng)
{
	lng = lng * 1e6;
	if (lng < gmaps.ukBottom || lng > gmaps.ukTop) return -1;
	var count = 0;
	for (var j=gmaps.ukBottom; j<gmaps.ukTop; j = j+gmaps.stepLng)
	{
		if (lng >=j && lng < j+gmaps.stepLng) return count;
		count++
	}
	return count;
}

if (typeof console == "undefined")
{
	console = new Object();
	console.log = function (msg) { alert(msg); };
}