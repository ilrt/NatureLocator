var i18n = {
    thousands_sep: ',',

    "naturelocator.capture.locationnotfound" : "Unable to determine your location",
    "naturelocator.capture.locationnotfounderror" : "Unable to determine your location.",
    "naturelocator.capture.invalidvalue" : "Invalid %s value",
    "naturelocator.database.fatal" : "Sorry, there was a database error",
    "naturelocator.database.notsupported" : "Incorrect platform. Database not supported",
    "naturelocator.email.invalid" : "Invalid email address"
};

i18n.render = function(s)
{
    if (typeof(i18n) !== "undefined" && i18n[s])
    {
        return i18n[s];
    }
    return s;
};

/*
 * Version of C's sprintf function. Currently only supports %d and %s replacement
 * From: http://24ways.org/2007/javascript-internationalisation
 */
i18n.sprintf = function(s)
{
    var bits = s[0].split('%');
    var out = bits[0];
    var re = /^([ds])(.*)$/;
    for (var i=1; i<bits.length; i++)
    {
        p = re.exec(bits[i]);
        if (!p || s[i]==null) continue;
        if (p[1] == 'd')
        {
            out += parseInt(s[i], 10);
        }
        else if (p[1] == 's')
        {
            out += s[i];
        }
        out += p[2];
    }
    return out;
};