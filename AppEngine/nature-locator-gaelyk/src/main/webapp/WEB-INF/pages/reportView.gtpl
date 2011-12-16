<% include '/WEB-INF/includes/header.gtpl' %>

<%
  def report = request.getAttribute("report")
  def apikey = System.getProperty("gmaps.key")
%>

<h1>View Report</h1>

<dl>
	<dt>Report ID</dt>
	<dd>${report.key.id}</dd>

	<dt>User ID</dt>
	<dd>${report.uid}</dd>

	<dt>Report timestamp</dt>
	<dd>${report.created}</dd>

	<dt>Time user created report</dt>
	<dd>${report.displayTime}</dd>

	<dt>Email</dt>
	<dd>${report.email ? report.email  : 'Not provided'}</dd>

	<dt>Photo</dt>
	<dd><a href="/image/${report.key.id}"><img src="/image/thumb/${report.key.id}"/></a></dd>

	<dt>Damage score</dt>
	<dd>${report.score}</dd>

	<dt>User damage validations</dt>

<table border="1">
	<thead>
		 <tr>
			<th>Damage</th>
			<th>Date</th>
			<th>Nickname</th>
		 </tr>
	</thead>
	<tbody>
	 <% request.validations.each { v -> %>
		<tr>
			<td>${v.score}</td> 
			<td>${v.created}</td>
			<td>${v.nickname}</td>
		</tr>
	 <% } %>
	</tbody>
</table>
<p></p>

	<dt>Leaf litter</dt>
	<dd>${report.litter}</dd>

	<dt>Location (geocoded)</dt>
	<dd>
		<a href="http://maps.google.com/maps/api/staticmap?center=${report.location}&zoom=13&size=800x800&maptype=roadmap&key=${apikey}&sensor=true&markers=color:red|${report.location}"><img src="http://maps.google.com/maps/api/staticmap?center=${report.location}&zoom=13&size=150x150&maptype=roadmap&key=${apikey}&sensor=true&markers=color:red|${report.location}"/></a>
		<br/>
		${report.location}
	</dd>

	<dt>Location (original)</dt>
	<dd>${report.originallocation}</dd>

</dl>

<% include '/WEB-INF/includes/footer.gtpl' %>
