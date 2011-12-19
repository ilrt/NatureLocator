function showError(msg)
{
    msg = i18n.render(msg);

    if (arguments.length > 1)
    {
        arguments[0] = msg;
        msg = i18n.sprintf(arguments);
    }

    if (navigator && navigator.notification) navigator.notification.alert(msg, function(){}, "Error", "Ok");
    else alert(msg);
}

function showWarning(msg)
{
    msg = i18n.render(msg);

    if (arguments.length > 1)
    {
        arguments[0] = msg;
        msg = i18n.sprintf(arguments);
    }
    
    if (navigator && navigator.notification) navigator.notification.alert(msg, function(){}, "Warning", "Ok");
    else alert(msg);
}

var month = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

function getPrettyDate(unixtime) {

	var date = new Date(unixtime);
	
	var hours = date.getHours();
	hours = hours + (date.getTimezoneOffset()/60);
	if(hours < 10) hours = '0' + hours;
	
	var mins = date.getMinutes();
	if(mins < 10) mins = '0' + mins;
	
	return hours + ":" + mins + " " + date.getDate() + " " + month[date.getMonth()];

}


function log(msg)
{
	if (console && console.log) console.log(msg);
	else
	{
		if ($("#log").length == 0)
		{
			var snippet = "<div id='log'/>";
			$("body").append(snippet);
		}
		
		$("#log").append(msg+"<br/>");
	}
}
