package scripts;

import net.sf.json.*;
import java.net.*;
import java.io.*;

class GeoCode
{
	static String baseGoogle = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";

	static String get(String address)
	{
		return getYahoo(address)
	}

	static String getGoogle(String address)
	{
		def url = new URL(baseGoogle + URLEncoder.encode(address, "UTF-8"))

		def response = url.getText()
		def json = JSONObject.fromObject(response)

		if (json['results'].size() == 0) return ""

		def loc = json['results']['geometry']['location'][0]
		return loc['lat']+","+loc['lng']
	}

	static String yahooKey = System.getProperty("yahoo.key")
	static String baseYahoo = "http://where.yahooapis.com/geocode?flags=J&appid="+yahooKey+"&q="
	static String getYahoo(String address)
	{
		def url = new URL(baseYahoo + URLEncoder.encode(address, "UTF-8"))

		def response = url.getText()

		def json = JSONObject.fromObject(response)

		if (json['ResultSet']['Found'] == 0) return ""

		def loc = json['ResultSet']['Results'][0]
		return loc['latitude']+","+loc['longitude']
	}
}