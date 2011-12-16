<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Export Results</h1>

<form action="/reports/export" method="GET">
	<p>Reports: 
		<select name="qty">
			<option>All</option>
			<option>50</option>
			<option>500</option>
			<option>1000</option>
			<option>2000</option>
		</select>

		&nbsp;
		<label>Offset:&nbsp;<input type="text" value="0" name="offset" style="width:30px"/></label>
		&nbsp;
		<label><input type="checkbox" name="images"/>Include images?</label>
		<input type="submit" name="submit" value="Download"/>
	</p>
</form>

<form action="/validationcsvdump" method="GET">
	<p>Validations: 
		<select name="size">
			<option>20</option>
			<option>50</option>
			<option>500</option>
			<option>1000</option>
			<option>2000</option>
		</select>

		&nbsp;
		<label>Page:&nbsp;<input type="text" value="1" name="page" style="width:30px"/></label>
		&nbsp;
		<input type="submit" name="submit" value="Download"/>
	</p>
</form>

<% include '/WEB-INF/includes/footer.gtpl' %>
