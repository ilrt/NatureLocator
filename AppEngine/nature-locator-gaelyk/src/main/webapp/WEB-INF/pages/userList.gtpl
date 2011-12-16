<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Users</h1>

<table border="1">
	<thead>
		 <tr>
			<th>Key Name</th>
			<th>Created date</th>
			<th>UID</th>
			<th>Nickname</th>
			<th>Viewed</th>
			<th>Validated</th>
			<th>Actions</th>
		 </tr>
	</thead>
	<tbody>
	 <% request.people.each { person -> %>
		<tr>
			<td>${person.key.name}</td>
			<td>${person.displayTime}</td>
			<td>${person.uid}</td>
			<td>${person.nickname}</td>
			<td>${person.viewed}</td>
			<td>${person.validated}</td>
			<td>
				<a href="/users/deletevalidations/${person.key.name}" onclick="return confirm('Are you sure?');">Reset viewed and delete validations</a>
				<a href="/users/delete/${person.key.name}" onclick="return confirm('This will delete person ${person.key.name}. Are you sure?');">Delete person</a>
			</td>
 		</tr>
	 <% } %>
	</tbody>
</table>

<p>Total:${request.total}</p>

<% def pageSize=request.pageSize %>
<form>
<p>
	Users:
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
