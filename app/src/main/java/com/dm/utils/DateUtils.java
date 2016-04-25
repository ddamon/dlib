package com.dm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	/* 定义常用时间格式 */
	/** yyyy-MM-dd HH:mm:ss */
	public static final String DATE_DEFAULT_STR = "yyyy-MM-dd HH:mm:ss";
	/** yyyy-MM-dd kk:mm:ss.SSS */
	public static final String DATE_LONG_STR = "yyyy-MM-dd kk:mm:ss.SSS";
	/** yyyy-MM-dd */
	public static final String DATE_SMALL_STR = "yyyy-MM-dd";
	/** yyyy-MM-dd */
	public static final String DATE_MONTH_STR = "yyyy-MM";
	/** yyMMddHHmmss */
	public static final String DATE_KEY_STR = "yyMMddHHmmss";
	/** yyyyMMddHHmmss */
	public static final String DATE_All_KEY_STR = "yyyyMMddHHmmss";
	/** 时间格式 HH:mm:ss */
	public static final String DATE_TIME_DEFAULT = "HH:mm:ss";

	/** 每天小时数 */
	private static final long HOURS_PER_DAY = 24;
	/** 每小时分钟数 */
	private static final long MINUTES_PER_HOUR = 60;
	/** 每分钟秒数 */
	private static final long SECONDS_PER_MINUTE = 60;
	/** 每秒的毫秒数 */
	private static final long MILLIONSECONDS_PER_SECOND = 1000;
	/** 每分钟毫秒数 */
	private static final long MILLIONSECONDS_PER_MINUTE = MILLIONSECONDS_PER_SECOND * SECONDS_PER_MINUTE;
	/** 每天毫秒数 */
	@SuppressWarnings("unused")
	private static final long MILLIONSECONDS_SECOND_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIONSECONDS_PER_SECOND;

	public static TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

	/**
	 * 将日期对象格式化成yyyy-MM-dd格式的字符串
	 * 
	 * @param date
	 *            待格式化日期对象
	 * @return 格式化后的字符串
	 * @see #formatDate(Date, String, String)
	 */
	public static String formatDate(Date date) {
		return formatDate(date, DATE_SMALL_STR);
	}

	/**
	 * 将当前日期对象格式化成yyyy-MM-dd格式的字符串
	 * 
	 * @param date
	 *            待格式化日期对象
	 * @return 格式化后的字符串
	 * @see #formatDate(Date, String, String)
	 */
	public static String formatDateNow() {
		return formatDate(new Date(), DATE_SMALL_STR);
	}

	/**
	 * 将日期对象格式化成yyyy-MM-dd HH:mm:ss格式的字符串
	 * 
	 * @param date
	 *            待格式化日期对象
	 * @return 格式化后的字符串
	 * @see #formatDate(Date, String, String)
	 */
	public static String forDatetime(Date date) {
		if (date != null) {
			return formatDate(date, DATE_DEFAULT_STR);
		} else {
			return null;
		}

	}

	/**
	 * 将日期对象格式化成HH:mm:ss格式的字符串
	 * 
	 * @param date
	 *            待格式化日期对象
	 * @return 格式化后的字符串
	 * @see #formatDate(Date, String, String)
	 */
	public static String formatTime(Date date) {
		return formatDate(date, DATE_TIME_DEFAULT);
	}

	/**
	 * 将日期对象格式化成指定类型的字符串
	 * 
	 * @param date
	 *            待格式化日期对象
	 * @param format
	 *            格式化格式
	 * @return 格式化后的字符串
	 * @see #formatDate(Date, String, String)
	 */
	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static String formatDate(long time, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return new SimpleDateFormat(format).format(cal.getTime());
	}

	/**
	 * 带时区的格式化时间
	 * 
	 * @param date
	 * @param format
	 * @param timeZone
	 * @return
	 */
	public static String formatDateTimeZone(Date date, String format, TimeZone timeZone) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(timeZone);
		return sdf.format(date);
	}

	/**
	 * 按照默认yyyy-MM-dd HH:mm:ss格式返回当前时间
	 * 
	 * @return string
	 */
	public static String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_DEFAULT_STR);
		return df.format(new Date());
	}

	/**
	 * 按照默认yyyy-MM-dd 格式返回当前时间
	 * 
	 * @return string
	 */
	public static String getNowDate() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_SMALL_STR);
		return df.format(new Date());
	}

	/**
	 * 
	 * @Description: 获得明天字符串
	 * @param dateStr
	 * @return
	 * @return String
	 * @throw
	 */
	public static String getNextDate(String dateStr) {
		Date cDate = parse(dateStr, "yyyy-MM-dd");
		dateStr = DateUtils.formatDate(DateUtils.changeDays(cDate, 1), "yyyy-MM-dd");
		return dateStr;
	}

	/**
	 * 
	 * @Description: 获得前一天字符串
	 * @param dateStr
	 * @return
	 * @return String
	 * @throw
	 */
	public static String getPreDate(String dateStr) {
		Date cDate = parse(dateStr, "yyyy-MM-dd");
		dateStr = DateUtils.formatDate(DateUtils.changeDays(cDate, -1), "yyyy-MM-dd");
		return dateStr;
	}

	/**
	 * 按照默认yyyy-MM 格式返回当前时间
	 * 
	 * @return string
	 */
	public static String getNowMonth() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_MONTH_STR);
		return df.format(new Date());
	}

	/**
	 * 根据pattern日期格式，返回当前时间字符串
	 * 
	 * @param pattern
	 * @return string
	 */
	public static String getNowTime(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(new Date());
	}

	/**
	 * 根据日期date字符串，返回对应Date类型时间（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param date
	 * @return Date
	 */
	public static Date parse(String date) {
		return parse(date, DATE_DEFAULT_STR);
	}

	/**
	 * 根据日期date字符串，按照pattern解析并返回对应Date类型时间
	 * 
	 * @param date
	 * @param pattern
	 * @return Date
	 */
	public static Date parse(String date, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 比较传入的date与当前时间 <br>
	 * 返回结果: 1 比当前时间大，0.当前时间相同，-1：比当前时间小
	 * 
	 * @param date
	 * @return int
	 */
	public static int compareDateWithNow(Date date) {
		Date now = new Date();
		int rnum = date.compareTo(now);
		return rnum;
	}

	/**
	 * 传入进来的timestamp与当前时间的进行比较 <br>
	 * 返回结果: 1 比当前时间大，0.当前时间相同，-1：比当前时间小
	 * 
	 * @param
	 * @return
	 */
	public static int compareDateWithNow(long timestamp) {
		long now = nowDateToTimestamp();
		if (timestamp > now) {
			return 1;
		} else if (timestamp < now) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 将yyyy-MM-dd HH:mm:ss返回成timestamp
	 * 
	 * @param date
	 * @return long
	 * @throws ParseException
	 */
	public static long dateToTimestamp(String date) throws ParseException {
		return dateToTimestamp(date, DATE_DEFAULT_STR);
	}

	/**
	 * 按照dateFormat格式date转换成date日期后，返回该date的timestamp
	 * 
	 * @param date
	 * @param dateFormat
	 * @return long
	 * @throws ParseException
	 */
	public static long dateToTimestamp(String date, String dateFormat) throws ParseException {
		long timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
		return timestamp;
	}

	/**
	 * 返回当前时间的时间戳
	 * 
	 * @return long
	 */
	public static long nowDateToTimestamp() {
		long timestamp = new Date().getTime();
		return timestamp;
	}

	/**
	 * 将时间戳返回dateFormat格式的日期字符串
	 * 
	 * @param timestamp
	 * @param dateFormat
	 * @return String
	 */
	public static String timestampToDate(long timestamp, String dateFormat) {
		String date = new SimpleDateFormat(dateFormat).format(new Date(timestamp));
		return date;
	}

	/**
	 * 将时间戳返回("yyyy-MM-dd HH:mm:ss)格式的日期字符串
	 * 
	 * @param timestamp
	 * @return String
	 */
	public static String timestampToDate(long timestamp) {
		return timestampToDate(timestamp, DATE_DEFAULT_STR);
	}

	/***
	 * 指定日期时间分钟上加上分钟数
	 * 
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date changeMinute(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * 指定日期时间上加上时间数
	 * 
	 * @param date
	 * @param hours
	 * @return
	 */
	public static Date changeHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/**
	 * 指定的日期加减天数
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date changeDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/**
	 * 指定的日期加减年数
	 * 
	 * @param date
	 * @param years
	 * @return
	 */
	public static Date changeYear(Date date, int years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}

	/**
	 * 获得两个日期之间相差的分钟数。（date1 - date2）
	 * 
	 * @param date1
	 * @param date2
	 * @return 返回两个日期之间相差的分钟数值
	 */
	public static int intervalMinutes(Date date1, Date date2) {
		long intervalMillSecond = date1.getTime() - date2.getTime();
		// 相差的分钟数 = 相差的毫秒数 / 每分钟的毫秒数 (小数位采用进位制处理，即大于0则加1)
		return (int) (intervalMillSecond / MILLIONSECONDS_PER_MINUTE + (intervalMillSecond % MILLIONSECONDS_PER_MINUTE > 0 ? 1 : 0));
	}

	/**
	 * 获得两个日期之间相差的秒数差（date1 - date2）
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int intervalSeconds(Date date1, Date date2) {
		long intervalMillSecond = date1.getTime() - date2.getTime();
		return (int) (intervalMillSecond / MILLIONSECONDS_PER_SECOND + (intervalMillSecond % MILLIONSECONDS_PER_SECOND > 0 ? 1 : 0));
	}

	/**
	 * 比较date1，date2两个时间
	 * 
	 * @return boolean
	 * @throws
	 */
	public static boolean beforeDate(Date date1, Date date2) {
		return date1.before(date2);
	}

	/**
	 * 比较date1，date2，两个时间字符串
	 * 
	 * @return boolean
	 * @throws
	 */
	public static boolean beforeDate(String date1, String date2) {
		Date dt1, dt2;
		dt1 = parse(date1);
		dt2 = parse(date2);
		return beforeDate(dt1, dt2);
	}

	/**
	 * 判断是否闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
	}

	/**
	 * 一个月有几天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int dayInMonth(int year, int month) {
		boolean yearleap = isLeapYear(year);
		int day;
		if (yearleap && month == 2) {
			day = 29;
		} else if (!yearleap && month == 2) {
			day = 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		} else {
			day = 31;
		}
		return day;
	}

	/**
	 * 获取输入 周的开始日期
	 * 
	 * @param week
	 *            like 201412
	 * @return 8位日期 like 20140317
	 */
	public static String getWeekBeginDate(String week) {
		if (week == null || "".equals(week) || week.length() < 5) {
			throw new RuntimeException("由于缺少必要的参数，系统无法进行制定的周换算.");
		}
		try {
			Calendar cal = Calendar.getInstance();
			cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置一个星期的第一天为星期1，默认是星期日
			cal.set(Calendar.YEAR, Integer.parseInt(week.substring(0, 4)));
			cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.substring(4, week.length())));
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			return df.format(cal.getTime());
		} catch (Exception e) {
			throw new RuntimeException("进行周运算时输入得参数不符合系统规格." + e);
		}
	}

	/**
	 * 获取输入 周的结束日期
	 * 
	 * @param week
	 *            like 201412
	 * @return 8位日期 like 20140323
	 */
	public static String getWeekEndDate(String week) {
		if (week == null || "".equals(week) || week.length() < 5) {
			throw new RuntimeException("由于缺少必要的参数，系统无法进行制定的周换算.");
		}
		try {
			Calendar cal = Calendar.getInstance();
			cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置一个星期的第一天为星期1，默认是星期日
			cal.set(Calendar.YEAR, Integer.parseInt(week.substring(0, 4)));
			cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.substring(4, week.length())));
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			return df.format(cal.getTime());
		} catch (Exception e) {
			throw new RuntimeException("进行周运算时输入得参数不符合系统规格." + e);
		}
	}

	/**
	 * 比较两个日期
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareDates(String s1, String s2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(s1);
			Date date2 = sdf.parse(s2);
			return date1.compareTo(date2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 月日变为标准日期格式
	 * 
	 * @param md
	 * @return
	 */
	public static Date string2Date(String md) {
		md = md.replace("月", "-").replace("日", "").replace("年", "-");
		int year = Calendar.getInstance().get(Calendar.YEAR);
		Date d = null;
		md = year + "-" + md;
		try {
			SimpleDateFormat dsd = new SimpleDateFormat("yyyy-MM-dd");
			d = dsd.parse(md);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	// //////////////////////////////
	/**
	 * 
	 */
	/**
	 * 返回unix时间戳 (1970年至今的秒数)
	 * 
	 * @return
	 */
	public static long getUnixStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 得到昨天的日期
	 * 
	 * @return
	 */
	public static String getYestoryDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yestoday = sdf.format(calendar.getTime());
		return yestoday;
	}

	/**
	 * 得到今天的日期
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		return date;
	}

	/**
	 * 时间戳转化为时间格式
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String timeStampToStr(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}

	/**
	 * 得到日期 yyyy-MM-dd
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String formatDate(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}

	/**
	 * 得到时间 HH:mm:ss
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String getTime(long timeStamp) {
		String time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(timeStamp * 1000);
		String[] split = date.split("\\s");
		if (split.length > 1) {
			time = split[1];
		}
		return time;
	}

	/**
	 * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
	 * <p>
	 * 以今天为2015年8月13日、周四为例 8月13日 周四 显示为 今天 8月12日 周三 显示为 昨天 8月11日 周二 显示为 周二 8月10日
	 * 周一 显示为 周一 8月9日 周日 显示为 8月9日 （每周从周一到周日，到上一周时若非昨天即用日期，跨年时显示年份） 第一版可以简化为 今天
	 * 昨天，之前的均为日期
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String convertTimeToFormat(long timeStamp) {
		long curTime = System.currentTimeMillis() / (long) 1000;
		long time = curTime - timeStamp;

		if (time < 60 && time >= 0) {
			return "刚刚";
		} else if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		} else if (time >= 3600 && time < 3600 * 24) {
			return time / 3600 + "小时前";
		} else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
			return time / 3600 / 24 + "天前";
		} else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 + "个月前";
		} else if (time >= 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 / 12 + "年前";
		} else {
			return "刚刚";
		}
	}

	/**
	 * 将一个时间戳转换成提示性时间字符串，(多少分钟)
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String timeStampToFormat(long timeStamp) {
		long curTime = System.currentTimeMillis() / (long) 1000;
		long time = curTime - timeStamp;
		return time / 60 + "";
	}

	/**
	 * 比较两个时间
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int compareTime(String time1, String time2) {
		if (time1 == null || time1.equals("") || !time1.contains(":")) {
			return 0;
		}
		if (time2 == null || time2.equals("") || !time2.contains(":")) {
			return 0;
		}
		Date date = new Date();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		int hour1 = Integer.valueOf(time1.split(":")[0]);
		int minite1 = Integer.valueOf(time1.split(":")[1]);
		cal1.set(Calendar.HOUR_OF_DAY, hour1);
		cal1.set(Calendar.MINUTE, minite1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date);
		int hour2 = Integer.valueOf(time2.split(":")[0]);
		int minite2 = Integer.valueOf(time2.split(":")[1]);
		cal2.set(Calendar.HOUR_OF_DAY, hour2);
		cal2.set(Calendar.MINUTE, minite2);
		return cal1.compareTo(cal2);
	}

	/**
	 * 比较给定日期是否在两个日期中间
	 * 
	 * @param inDate
	 * @param start
	 * @param end
	 * @return
	 */
	public static int compareDate(Date inDate, Date start, Date end) {
		if (inDate == null || start == null || end == null) {
			return -1;
		}
		int t1 = inDate.compareTo(start);
		int t2 = inDate.compareTo(end);
		if (t1 > 0 && t2 < 0) {
			return 1;
		} else {
			return -1;
		}
	}
}
