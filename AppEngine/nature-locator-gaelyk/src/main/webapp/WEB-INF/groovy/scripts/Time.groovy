package scripts

import org.apache.commons.lang.StringUtils
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

/**
 *
 * @author cmcpb
 */
class Time {
	static DateTimeFormatter inputFmt = ISODateTimeFormat.dateTime()
	static DateTimeFormatter displayFmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
	
	static Long parse(def inputStr)
	{
		if (!StringUtils.isEmpty(inputStr)) 
		{
			try
			{
				DateTime dt = inputFmt.parseDateTime(inputStr)
				return dt.getMillis()
			}
			catch (Exception e0)
			{
				try
				{
					DateTime dt = displayFmt.parseDateTime(inputStr)
					return dt.getMillis()
				}
				catch (Exception e1)
				{					
					try
					{
						// attempt to parse as long
						DateTime dt = new DateTime(Long.parseLong(inputStr))
						return dt.getMillis()
					}
					catch (Exception e2) {}
				}
			}
		}

		return new DateTime().getMillis()
	}

	static String display(def timeinMilliSecs)
	{
		try
		{
			return displayFmt.print(new DateTime(timeinMilliSecs));
		}
		catch (Exception e) 
		{
			return timeinMilliSecs
		}
	}
}

