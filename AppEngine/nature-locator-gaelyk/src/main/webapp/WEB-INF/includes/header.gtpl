<%
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
%>
<!DOCTYPE html>
<html>
	<head>
		<title>Nature Locator Storage Service</title>
		
		<link rel="shortcut icon" href="/images/gaelyk-small-favicon.png" type="image/png">
		<link rel="icon" href="/images/gaelyk-small-favicon.png" type="image/png">
		<link rel="stylesheet" type="text/css" href="/css/main.css"/>
	</head>
	<body>
		<div class="header">

		<ul>
			<li><a href="/reports">List Reports</a></li>
			<li><a href="/reportswl">White List Reports</a></li>
			<li><a href="/reports/map">View Reports on Map</a></li>
			<li><a href="/reports/add">Add New Report</a></li>
			<li><a href="/reports/export">Export Results</a></li>
			<li><a href="/validate">Validate Results</a></li>
			<li><a href="/users">Users</a></li>
			<li>
<%
     UserService userService = UserServiceFactory.getUserService();
     if (!userService.isUserLoggedIn()) {
%>
	<a href="${userService.createLoginURL(request.getRequestURI())}">log in</a>
<% } else { %>
      ${userService.getCurrentUser().getNickname()} <a href="${userService.createLogoutURL("/")}">log out</a>
<%
   }
%>			
		</li>
		</ul>
		</div>

		<div class="content">
