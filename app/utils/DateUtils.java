package utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static Date getInitNextDay(Date date) {
		Date nextDay;
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MINUTE);
		nextDay = cal.getTime();
		
		return nextDay;
	}
	
	public static Date getFinalOfTheDay(Date date) {
		Date finalOfTheDay = getInitNextDay(date);
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(finalOfTheDay);
		
		cal.add(Calendar.SECOND, -1);
		
		finalOfTheDay = cal.getTime();
		
		return finalOfTheDay;
	}
	
}
