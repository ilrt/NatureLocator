<%
  def validator = System.getProperty('naturelocator.validator')
%>

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

					<title>Conker Tree Science: Leaf Watch &mdash; Validate</title>
					<meta name="description" content="Help scientists at the Universities of Bristol and Hull monitor what's happening with the UK's conker trees as they are attacked by a species of non-native moth.">
					<meta name="author" content="University of Bristol">

					<meta name="viewport" content="width=device-width,initial-scale=1">

					<link rel="stylesheet" href="css/style.css">
					<link rel="stylesheet" type="text/css" href="scripts/fancybox/jquery.fancybox-1.3.4.css" media="screen" />
					<link rel="icon" type="image/png" href="images/favicon.png" />
				</head>

				<body class="validate2">

					<div id="container">
						<div id="header">
							<a id="logo" href="."><img src="images/logo.png" alt="Conker Tree Science: Leaf Watch" width="256" height="98" /></a>

							<ul>
								<li id="overview"><a href=".">Overview</a></li>
								<li id="validate"><a class="current" href="validate">Help validate data</a></li>
								<li id="results"><a href="results">Results</a></li>
								<li id="blog"><a href="http://naturelocator.ilrt.bris.ac.uk/">Blog</a></li>
							</ul>

						</div><!-- /header -->

						<div id="content">

							<div id="main" role="main">

<form id="form" action="validate" method="POST">

							<% if(request.report) { %>
							
								<div class="widget">
									<h1>Choose a damage score for this leaf <span>(<a href="#help">Get help with this</a>)</span></h1>

	<% if(request.showGuide) { %>
		<div id="first-time">
			First time here? Have a look at the <strong><a href="guide.html">leaf identification guide and example images</a></strong> before you start.
		</div>
	<% } %>

	<input type="hidden" name="reportKeyId" value="${request.report.key.id}"/>

								<div class="controls">

								<ul class="score">

									<input id='score-value' type='hidden' name='score' value=''/>

									<li><a href='#' class='score-item' id='score_0' title="The leaf is completely free from leaf mines. No evidence of moth attack."><img src="images/icon-score-0.png" alt="The leaf is completely free from leaf mines. No evidence of moth attack." width="56" height="56" /></a><span>0</span></li>
									<li><a href='#' class='score-item' id='score_1' title="Just a couple of leaf mines on the leaf."><img src="images/icon-score-1.png" alt="Just a couple of leaf mines on the leaf." width="56" height="56" /></a><span>1</span></li>
									<li><a href='#' class='score-item' id='score_2' title="Less than half of the leaf is covered with leaf mines."><img src="images/icon-score-2.png" alt="Less than half of the leaf is covered with leaf mines." width="56" height="56" /></a><span>2</span></li>
									<li><a href='#' class='score-item' id='score_3' title="It's about half leaf mines. It's difficult to decide whether the area covered with leaf mines is larger or smaller than the area not covered with leaf mines."><img src="images/icon-score-3.png" alt="It's about half leaf mines. It's difficult to decide whether the area covered with leaf mines is larger or smaller than the area not covered with leaf mines." width="56" height="56" /></a><span>3</span></li>
									<li><a href='#' class='score-item' id='score_4' title="The leaf mines definitely cover more than half the leaf. They may cover all the leaf."><img src="images/icon-score-4.png" alt="The leaf mines definitely cover more than half the leaf. They may cover all the leaf." width="56" height="56" /></a><span>4</span></li>
									<li><a href='#' class='score-item' id='score_-1' title="The whitish/brown leaf mines are possibly present, but you are not certain enough to give it a damage score."><img src="images/icon-score-notsure.png" alt="The whitish/brown leaf mines are possibly present, but you are not certain enough to give it a damage score." width="56" height="56" /></a><span>Not sure</span></li>
									<li><a href='#' class='score-item' id='score_99' title="This is not a photo of a horse-chestnut leaf."><img src="images/icon-score-invalid.png" alt="This is not a photo of a horse-chestnut leaf." width="56" height="56" /></a><span>Invalid</span></li>
								</ul>
								<a id="nextinactive" class="button" href="#">Next &raquo;</a>
								<a id="next" class="button" href="#">Next &raquo;</a>

								</div><!-- /controls -->

								<div class="dataimage">
								
									<p class="loading"><img src="images/ajax-loader.gif" width="16" height="16" alt=""> Loading image</p>
								
									<div class="dataimage-wrapper">
										<a rel="enlargelink" href="image/${request.report.key.id}" title="View larger version of this image">
											<img id="leaf-image" src="image/${request.report.key.id}" alt="Leaf Watch leaf record photo"/>
										</a>
									</div>
									
									<span id="image-controls">
										<a id="rotate-clockwise" href="#">Rotate image</a>
										|
										<a rel="enlargelink2" href="image/${request.report.key.id}" class="enlarge">View larger version</a>
									</span>
								</div><!-- /dataimage -->

								</div><!-- /widget -->

								<h2 id="help">How to rate the damage</h2>
								<ol class="help">

									<li><strong>0</strong>: The leaf is completely free from leaf mines. No evidence of moth attack.</li>
									<li><strong>1</strong>: Just a couple of leaf mines on the leaf.</li>
									<li><strong>2</strong>: Less than half of the leaf is covered with leaf mines.</li>
									<li><strong>3</strong>: It's about half leaf mines. It's difficult to decide whether the area covered with leaf mines is larger or smaller than the area not covered with leaf mines.</li>
									<li><strong>4</strong>: The leaf mines definitely cover more than half the leaf. They may cover all the leaf.</li>
									<li><strong>Not sure</strong>: The whitish/brown leaf mines are possibly present, but you are not certain enough to give it a damage score.</li>
									<li><strong>Invalid</strong>: This is not a photo of a horse-chestnut leaf.</li></ol>
									
								<p>
									<strong>Need more information?</strong> See the <strong><a href="guide.html">leaf identification guide and example images</a></strong>.
								</p>

	<% } else if(request.missingKey) { %>
	
		<input type="hidden" name="reportKeyId" value="${request.missingKey.id}"/>
								<h1 class="message">Sorry, something has gone wrong!</h1>
								<div class="highlight">
									<div class="inner">
										<p>This image seems to be missing. Please skip ahead. If you are finding that this problem is re-occuring frequently please let <a href="mailto:conker-science@bristol.ac.uk">us know</a>.</p>
										<a id="skip" class="button" href="#">Skip &raquo;</a>
									</div><!-- /inner -->
								</div><!-- /highlight -->


	<% } else { %>
								<h1 class="message">Thank you!</h1>
								<div class="highlight">
									<div class="inner">
										<p>You have validated all the available images. Thank you for your commitment and invaluable contribution to this project.</p>
									</div><!-- /inner -->
								</div><!-- /highlight -->
	
	<% } %>

	   <input type="hidden" name="v" value="${validator}"/>

</form>



							</div><!-- /main -->

							<div id="sidebar">
								<div class="section">
									<p>Logged in as <strong>${request.username}</strong> (<a class="logout" href="/logout">Log out</a>)</p>
							</div><!-- /section -->

								<div class="section">

									<div class="highlight">
										<div class="inner">

	 <% if(request.current.validated > 0) { %>
										<p class="large top">You have validated <strong>${request.current.validated} ${request.current.validated == 1? "record": "records"}</strong> (thank you!)</p>
	 <% 	if(request.position) { %>
										<p>You are ranked <strong>#${request.position}</strong> in our table of top validators:</p>
	 <% 	}
	 	} else { %>
										<p>Our table of top validators:</p>
	 <% } %>

										<table>
	 <% 
	 request.leaders.each { p ->
	 %>
		<tr>
			<th>${p.nickname}:</th>
			<td>${p.validated} ${p.validated == 1? "record": "records"}</td>
		</tr>
	 <% } %>

	<% if(request.current.validated > 0 && !request.inLeaders) { %>
											<tr><th>&hellip;</th><td></td>
		<tr>
			<th>${request.current.nickname}:</th>
			<td>${request.current.validated} ${request.current.validated == 1? "record": "records"}</td>
		</tr>
	<% } %>

										</table>
									</div><!-- /inner -->
									</div><!-- /highlight -->


							</div><!-- /section -->

							<div class="section">
								<h2>See the results so far</h2>
								<ul class="objects">
									<li><a class="thumb" href="results"><img src="images/map-thumb.png" alt="Map of data" width="80" height="80" /></a>
									<p>Take a look at the results from 2011 so far. A range of maps shows how the miner leaf moth is spreading across the UK.</p>
									<p class="more"><a href="results">See more</a></p>
									</li>
								</ul>
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


					<!-- JavaScript at the bottom for fast page loading -->

					<script src="scripts/jquery-1.6.4.min.js"></script>
					<script src="scripts/jQueryRotateCompressed.2.1.js"></script>
					<script type="text/javascript" src="scripts/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
					
					<script>
						var angle = 0;

						\$(document).ready(function() {

							\$("a[rel='enlargelink']").fancybox({'type':'image', 'titleShow':false});
							\$("a[rel='enlargelink2']").fancybox({'type':'image'});
						
							\$('#next').click(function() {
								\$('#next').hide();
								\$('#nextinactive').show();
    							\$('#form').submit();
							});

							\$('#skip').click(function() {
    							\$('#form').submit();
							});
							
							\$('#rotate-clockwise').click(function() {
								angle += 90;
         						\$("#leaf-image").rotate({animateTo:angle});
        					});

							\$('.score-item').click(function() {
								var id = \$(this).attr('id');
								\$('.score-item').removeClass('selected');
								\$('#' + id).addClass('selected');
								var score = id.substr(id.indexOf('_')+1);
								\$('#score-value').val(score);
								\$('#nextinactive').hide();
								\$('#next').show();
							});
							
							\$('#leaf-image').one('load', function() {
        						var maxWidth = 373; // Max width for the image
        						var maxHeight = 372;    // Max height for the image
        						var ratio = 0;  // Used for aspect ratio
        						var width = \$(this).width();    // Current image width
        						var height = \$(this).height();  // Current image height

        						// Check if the current width is larger than the max
        						if(width > maxWidth){
            						ratio = maxWidth / width;   // get ratio for scaling image
            						\$(this).css("width", maxWidth); // Set new width
            						\$(this).css("height", height * ratio);  // Scale height based on ratio
            						height = height * ratio;    // Reset height to match scaled image
            						width = width * ratio;    // Reset width to match scaled image
        						}

        						// Check if current height is larger than max
        						if(height > maxHeight){
            						ratio = maxHeight / height; // get ratio for scaling image
            						\$(this).css("height", maxHeight);   // Set new height
            						\$(this).css("width", width * ratio);    // Scale width based on ratio
            						width = width * ratio;    // Reset width to match scaled image
        						}

        						if(width > height){
        							angle += 90;
        							\$(this).rotate(angle);
        							\$(this).css('margin-top',(width-height)/2);
        							\$('.dataimage-wrapper').height(width + ((width-height)/2));
        						}
        						
           						\$(".loading").hide();
           						\$("#leaf-image").show();
           						\$("span#image-controls").show();
        					}).each(function() {
								if(this.complete) {
									\$(this).load();
								}
							});

						});				
					</script>
					</body>
				</html>
