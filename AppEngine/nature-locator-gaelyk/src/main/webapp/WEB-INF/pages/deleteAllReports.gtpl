<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Delete Results</h1>
<% 
def success = request.getAttribute("success")
def totalreports = request.getAttribute("totalReports")
def totalphotos = request.getAttribute("totalPhotos")

if (success) { %>

    <p>${totalreports}/${totalphotos} records deleted</p>

<% } else { %>

    <form action="/reports/deleteall" method="GET">
        <p>There are currently ${totalreports} reports and ${totalphotos} photos uploaded. Are you sure you want to delete all reports?</p>
        <input type="submit" name="confirm" value="Confirm"/>

    </form>

<% } %>

<% include '/WEB-INF/includes/footer.gtpl' %>
