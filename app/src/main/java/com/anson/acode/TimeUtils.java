package com.anson.acode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	public static long ONEDAY = 24 * 60 * 60 * 1000;
	public static long ONEWEEK = 7 * ONEDAY;
	public static long ONEMONTHS = 30 * ONEDAY;
	public static long ONEMONTHB = 31 * ONEDAY;
	public static long HALFYEAR = 182 * ONEDAY;
	public static long ONEYEAR = 365 * ONEDAY;
	/**
	 * return Date[sunday, saturday]; you can get month or date from Date by Date.getMonth() & Date.getDate();
	 * @param week [-1, 52]
	 * @return
	 */
	public static Date[] get7WeekDate(int week){
		Date[] days = new Date[7];
		Calendar calendar = Calendar.getInstance();
		if(week != -1 && week <=52)calendar.set(Calendar.WEEK_OF_YEAR, week);
		
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, -dayofweek+1);
		days[0] = calendar.getTime();
		for(int i=1;i<7; i++){
			calendar.add(Calendar.DATE, 1);
			days[i] = calendar.getTime();
		}

		return days;
	}
	
	/**
	 * return Date[sunday, saturday]; you can get month or date from Date by Date.getMonth() & Date.getDate();
	 * @param week [-1, 52]
	 * @return
	 */
	public static Date[] getWeekDate(int week){
		Calendar calendar = Calendar.getInstance();
		if(week != -1 && week <=52)calendar.set(Calendar.WEEK_OF_YEAR, week);
		
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, -dayofweek+1);
		Date sunday = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date saturday = calendar.getTime();
		return new Date[]{sunday, saturday};
	}
	
	/**
	 * 
	 * @param year 2014
	 * @param month 12
	 * @return
	 */
	public static int getMaxDayOfMonth(int year, int month){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 
	 * @return [1, 53]
	 */
	public static int getWeekOfYear(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * return yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getTimeString(){
		Date d = new Date();
		return StringUtils.getSimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(d);
	}
	
	/**
	 * return time string like yyyy-MM-dd hh:mm
	 * @param cal
	 * @return
	 */
	public static String formatTimeString(Calendar cal){
		return StringUtils.getSimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime());
	}
	
	/**
	 * return time string like yyyy-MM-dd hh:mm
	 * @param cal
	 * @return
	 */
	public static String formatTimeString(Calendar cal, String format){
		return StringUtils.getSimpleDateFormat(format).format(cal.getTime());
	}
	
	
	/**
	 * return time by long
	 * 1900-12-1 12:00 return 2938723563
	 * @param time
	 * @return
	 */
	public static long getTimeFromString(String time){
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date d = sim.parse(time);
			return d.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date d = new Date();
		return d.getTime();
	}
	
	/**
	 * return yyyy-MM-dd
	 * @return
	 */
	public static String getTimeStringDay(){
		Date d = new Date();
		return StringUtils.getSimpleDateFormat("yyyy-MM-dd").format(d);
	}
	
	/**
	 * translate 09:10:33 to new int[]{9, 10, 33};
	 * @param time
	 * @return
	 */
	public static int[] getTime2int(String time){
		int[] t = {0, 0, 0};
		if(time == null || !time.contains(":")){
			
		}else{
			String[] ts = time.split(":");
			if(ts != null && ts.length > 1){
				for(int i=0; i<ts.length; i++){
					String s = ts[i];
					if(s.length() > 1 && s.startsWith("0")){
						t[i] = Integer.valueOf(s.substring(1, 2));
					}else{
						t[i] = Integer.valueOf(s);
					}
				}
			}
		}
		return t;
	}
	
	/**
	 * translate str(9:10) to str(09:10);
	 * @param hour
	 * @param min
	 * @return
	 */
	public static String formatTimeStr(String hour, String min){
		if(hour.length() == 1){
			hour = "0" + hour;
		}
		if(min.length() == 1){
			min = "0" + min;
		}
		
		return hour + ":" + min;
	}
	
	/**
	 * translate 9, 10 to "09:10"
	 * @param h
	 * @param m
	 * @return
	 */
	public static String formatTimeStr(int h, int m){
		String hour = String.valueOf(h);
		String min = String.valueOf(m);
		if(hour.length() == 1){
			hour = "0" + hour;
		}
		if(min.length() == 1){
			min = "0" + min;
		}
		
		return hour + ":" + min;
	}
	
	/**
	 * translate "9:0" to "09:00"
	 * @param time
	 * @return
	 */
	public static String formatTimeString(String time){
		int[] ts = getTime2int(time);
		return formatTimeStr(ts[0], ts[1]);
	}
}
