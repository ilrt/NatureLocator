<!doctype html>
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]> <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]> <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!--[if IE 9]> <html class="no-js ie9" lang="en"> <![endif]--> 
<!--[if gt IE 9]><!--> 
<html class="no-js" lang="en"> 
<!--<![endif]-->
	<head>
		<meta charset="utf-8">

		<title>Conker Tree Science: Leaf Watch &mdash; Results</title>
		<meta name="description" content="Help scientists at the Universities of Bristol and Hull monitor what's happening with the UK's conker trees as they are attacked by a species of non-native moth.">
		<meta name="author" content="University of Bristol">

		<meta name="viewport" content="width=device-width,initial-scale=1">

		<link rel="stylesheet" href="css/style.css">
		<link rel="icon" type="image/png" href="images/favicon.png" />

		<%import java.text.SimpleDateFormat%>
		<%import java.util.Calendar%>

		<%
			def apikey = System.getProperty("gmaps.key")
		%>
		<script src="http://maps.google.com/maps/api/js?sensor=false" type="text/javascript"></script>
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js" type="text/javascript"></script>
		<script src="/scripts/gmaps.js" type="text/javascript"></script>
		<script src="/scripts/jquery-ui-1.7.3.custom.min.js" type="text/javascript"></script>
		<script src="/scripts/selectToUISlider.jQuery.js" type="text/javascript"></script>


<script src="https://google-maps-utility-library-v3.googlecode.com/svn/trunk/markermanager/src/markermanager_packed.js"></script>
<!--
		<script src="http://gmaps-utility-library.googlecode.com/svn/trunk/mapiconmaker/release/src/mapiconmaker.js" type="text/javascript"></script>
		<script src="http://google-maps-utility-library-v3.googlecode.com/svn/tags/markerclustererplus/2.0.4/src/markerclusterer_packed.js"></script>
-->
		<link rel="stylesheet" href="/css/redmond/jquery-ui-1.7.3.custom.css" type="text/css" />
		<link rel="Stylesheet" href="/css/ui.slider.extras.css" type="text/css" />

		<style>
/*maps */
.mapcontrols
{
	width:600px;
	margin-top:2em;
	margin-bottom:3em;
}

.ui-slider-scale .ui-slider-label, .mapcontrols select, .mapcontrols label{
	display:none !important;
}

.ui-slider-scale dt span
{
	background-color:transparent;
}

.ui-slider-scale dt
{
	top:0px !important;
	border-bottom:none;
}

/* Loading indicator styles on map page */
#progressbar .ui-progressbar-value { background-image: url(/css/redmond/images/pbar-ani.gif); }

#progressbar
{
	height:20px;
}

.window
{
	position:absolute;
	top:94px;
	margin-left:50px;
	width:800px;
	align:center;
}

.window p
{
	margin:0px;
}

.dialog
{
	-moz-border-radius: 5px;
	border-radius: 5px;
	width:50%;
	background-color:#e1dfd6;
	padding-top:0.3em;
	padding-left:0.5em;
	padding-right:0.5em;
	padding-bottom:0.5em;
	margin-left:auto;
	margin-right:auto;
}
			</style>
	</head>

	<body class="results">

		<div id="container">
			<div id="header">
				<a id="logo" href="."><img src="images/logo.png" alt="Conker Tree Science: Leaf Watch" width="256" height="98" /></a>

				<ul>
					<li id="overview"><a href=".">Overview</a></li>
					<li id="validate"><a href="validate">Help validate data</a></li>
					<li id="results"><a class="current" href="results">Results</a></li>
					<li id="blog"><a href="http://naturelocator.ilrt.bris.ac.uk/">Blog</a></li>
				</ul>

			</div><!-- /header -->

			<div id="content">

				<div id="main" role="main">
					<h1>Results for 2011</h1>

					<p>The map shows data uploaded from the Conker Tree Science app during each fortnight from mid July 2011. 
Drag the bar at the bottom of the map to see how the pattern of results changes over time. The pins show the damage that was reported
 and we can now validate the photographic records (<a href="/validate">with your help</a>) to see how the horse-chestnut leaf-mining moth
 continues to spread. Last year the moth was confirmed in Newcastle-on-Tyne, but has it got further north this year? We will publish a map 
of verified records in a couple of months’ time.</p>

<p>The map shows how the damage changes over the season and across the country. We expected that levels of damage increased through
 the season, but this pattern is not very clear from the map. The moth has spread from London since its discovery there in 2002 and we 
expected that damage would be least where the moth has only just arrived (at the west and north of its distribution). This appears to be 
the case. When zooming into the map, it also appears that the most records come from large cities, which probably says more about the 
distribution of smartphones than the distribution of the leaf-mining moth!</p>

					<div class="mapContainer">
						<div id="map" style="width:600px;height:700px;"></div>
					</div>

					<div class="window">
						<div class="dialog">
							<p>Loading data...</p>
							<div id="progressbar"></div>
						</div>
					</div><!-- End demo -->

<script type="text/javascript">
// center map
var LATITUDE = 54.5;
var LONGITUDE = -2.5;
var DISPLAY_DATE_FORMAT = 'd MMM yy';
var INPUT_DATE_FORMAT = 'dd/MM/yyyy';
var HEIGHT_FROM_GROUND = 6;
var pageSize = 500;
var totalResults = <% out << request.getAttribute("total") %>;
var dates = new Array();
var locations = new Array();
var descriptions = new Array();
var damage = new Array();
var litter = new Array();
</script>

<div class="mapcontrols">
	<label for='startDate'>Start Date:</label>
	<select name='startDate' id='startDate'>
	<%
	SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
	SimpleDateFormat dayFormatter = new SimpleDateFormat("MMM dd");
	Calendar rightNow = Calendar.getInstance();
	int year = rightNow.get(Calendar.YEAR);

	(6..11).each {
		def month = it
		rightNow.set(Calendar.MONTH, month)
		out << "  <optgroup label='"+monthFormatter.format(rightNow.getTime())+"'>\n"

		// add 2 options per month (start and mid)
		Calendar start = Calendar.getInstance();
		start.set(year, month, 1, 0, 0, 0)
		out << "	<option value='"+start.getTimeInMillis()+"'>"+dayFormatter.format(start.getTime())+"</option>\n" 

		Calendar mid = Calendar.getInstance();
		mid.set(year, month, (int)(rightNow.getActualMaximum(Calendar.DAY_OF_MONTH)/2), 0, 0, 0)
		out << "	<option value='"+mid.getTimeInMillis()+"'>"+dayFormatter.format(mid.getTime())+"</option>\n" 
		}
	%>
	</select>
</div>

<div class='legend'>
	<h3>Legend</h3>
	<p>The number in the pin represents the number of results submitted in the nearby area. The colour of the pin represents the maximum amount of damage reported amongst these results.</p>
	<ul style='list-style-type:none'>
		<li class="damage_-1"> Unknown</li>
		<li class="damage_0"> No damage (damage score = 0)</li>
		<li class="damage_1"> Very light (damage score = 1)</li>
		<li class="damage_2"> Light (damage score = 2)</li>
		<li class="damage_3"> Moderate (damage score = 3)</li>
		<li class="damage_4"> Heavy (damage score = 4)</li>
	</ul>
</div>
			</div><!-- END #main -->

			<div id="sidebar">
				<div class="section">
				<h2 class="tick">Help us check the results</h2>
				<p>We've had a great response from our volunteers around the UK who have sent us valuable data. As with all scientific endeavour, though, we want to be 100% positive that our data are as accurate as they can be.</p>
				<a class="button" id="validatenow" href="validate">Help validate data</a>
			</div><!-- /section -->

				<div class="section">
				<h2>Download the app</h2>
				<img id="smallphones" src="images/phones-small.png" alt="" width="191" height="212" />
				<div id="download">

				<ul>
					<li><a title="Get the app from the iTunes App Store" href="http://itunes.apple.com/gb/app/conker-tree-science-leaf-watch/id445371129"><img src="images/logo-itunes-small.png" alt="Available on the iPhone App Store" width="121" height="39" /></a></li>
					<li><a title="Get the app from the Android Market" href="https://market.android.com/details?id=uk.ac.bris.ilrt.leafwatch"><img src="images/logo-android-small.png" alt="Available in Android Market" width="104" height="45" /></a></li>
				</ul>
			</div><!-- /download -->
			</div><!-- /section -->
			</div><!-- /sidebar -->

		</div><!-- /content -->


		<div id="footer">
			<ul>
				<li><a href="http://naturelocator.ilrt.bristol.ac.uk"><img src="images/logo-nature-locator.png" alt="Nature Locator" width="165" height="39" /></a></li>
				<li><a href="http://www.jisc.ac.uk"><img src="images/logo-jisc.png" alt="JISC" width="65" height="38" /></a></li>
				<li><a href="http://www.bristol.ac.uk"><img src="images/logo-bristol.png" alt="University of Bristol" width="108" height="33" /></a></li>
				<li><a href="http://www.hull.ac.uk"><img src="images/logo-hull.png" alt="University of Hull" width="134" height="33" /></a></li>
			</ul>
			<p class="legal">Copyright &copy;2011 University of Bristol.<br />
			Funded by <a href="http://www.jisc.ac.uk">JISC</a>. Built by <a href="http://www.ilrt.bristol.ac.uk">ILRT</a>.
			<a href="mailto:conker-science@bristol.ac.uk">Contact us</a>.</p>

		</div><!-- /footer -->

	</div> <!--! end of #container -->

	</body>
</html>