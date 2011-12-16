<% include '/WEB-INF/includes/header.gtpl' %>

<%
  def report = request.getAttribute("report")
  def validator = System.getProperty('naturelocator.validator')
  boolean existingKey = report?.key
  String action = !existingKey ? 'Add' : 'Update'
%>

<h1>${action} Report</h1>

<form action="/reports/${!existingKey ? 'insert' : 'update'}" method="POST" ${!existingKey ? 'enctype="multipart/form-data"' : ''}>
	<% if(existingKey) { %>
		<input type="hidden" name="id" value="${report.key.id}"/>
	<% } %>

	<p><label>UID <input type="text" name="uid" required value="${report?.uid ? report.uid : ''}"/></label></p>

	<p>
		<% if (existingKey) { %>
			<img src="/image/thumb/${report.key.id}"/>
		<% } else { %>
			<label>Photo: <input type="file" name="photo" required></label>
		<% } %>
	</p>

	<fieldset>
		<legend>Damage score:</legend>

<%
def createInput(report, value, property)
{
	println "<p><label><input type='radio' name='${property}' value='${value}' required ${report && report[property] == value ? "checked='checked'" : ""}/> ${value}</label></p>";
}

createInput(report, "0", "score");
createInput(report, "1", "score");
createInput(report, "2", "score");
createInput(report, "3", "score");
createInput(report, "4", "score");
createInput(report, "99", "score");
%>

	</fieldset>

	<p>
		<label>Location: <input type="text" name="location" required value="${report?.location ? report.location : ''}"/></label>
		<span class="help">Suported formats: Place names (e.g. Bath) or lat & lng (e.g. 	51.385030,-2.361324)</span>
	</p>

	<p>
		<label>Time: <input type="datetime" value="${report?.displayTime ? report.displayTime : ''}" name="created"/></label>
		<span class="help">Supported formats: 10/05/2011, 1305043265246, 2011-05-10T17:01:05.246+01:00</span>
	 </p>

	<fieldset>
		<legend>Leaf litter:</legend>
		<%
			createInput(report, "bare", "litter");
			createInput(report, "short", "litter");
			createInput(report, "dead", "litter");
			createInput(report, "long", "litter");
			createInput(report, "scrub", "litter");
			createInput(report, "stone", "litter");
			createInput(report, "various", "litter");
			createInput(report, "unlisted", "litter");
		%>
	</fieldset>

	<p>
	   <input type="submit" value="${action}"/>
	   <input type="hidden" name="v" value="${validator}"/>
	   <input type="button" value="Cancel" onclick="javascript:document.location.href = '/reports';"/>
	</p>

</form>

<% include '/WEB-INF/includes/footer.gtpl' %>
