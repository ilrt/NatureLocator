<% include '/WEB-INF/includes/header.gtpl' %>

<h1>White Listed Reports</h1>

<p>These reports were selected for crowd validation. They are always presented to users in this order.</p>

<%
  def apikey = System.getProperty("gmaps.key")
%>

<table border="1">
	<thead>
		 <tr>
			<th>ID</th>
			<th>Photo</th>
			<th>Leaf litter</th>
			<th>Date of sighting</th>
			<th>Action</th>
			<th>Validations</th>
		 </tr>
	</thead>
	<tbody>
	 <% request.reports.each { report -> %>
		<tr>
			<td>${report.getKey().getId()}</td>
			<td><a href="image/${report.key.id}"><img width="300" src="image/${report.key.id}"/></a></td>
			<td>${report.litter}</td>
			<td>${report.displayTime}</td>
			<td><a href="/reports/${report.key.id}">View</a></td>
			<td>${report.validationCount}</td>
		</tr>
	 <% } %>
	</tbody>
</table>

<p>Total:${request.total}</p>

<% def pageSize=request.pageSize %>
<form>
<p>
	Results:
	<select name="size">
		<option value="10" ${pageSize == 10 ? "selected='selected'" : ''}>10</option>
		<option value="20" ${pageSize == 20 ? "selected='selected'" : ''}>20</option>
		<option value="50" ${pageSize == 50 ? "selected='selected'" : ''}>50</option>
	</select>
	Page: 
	<select name="page">
	<%
		def page=0
		while ( (page*pageSize) < request.total )
		{
			page++
			println "<option value='"+page+"' " + (request.currentPage == page ? "selected='selected'" : '')+">"+page+"</option>\n"
		}
		%>
	</select>
	<input type="submit" value="go"/>
</p>
</form>
<% include '/WEB-INF/includes/footer.gtpl' %>
