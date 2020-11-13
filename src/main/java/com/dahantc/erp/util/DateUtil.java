package com.dahantc.erp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.enums.ReportType;

public class DateUtil {

	private static final Logger logger = LogManager.getLogger(DateUtil.class);

	/** yyyy-MM-dd */
	public static final String format1 = "yyyy-MM-dd";

	/** yyyy-MM-dd HH:mm:ss */
	public static final String format2 = "yyyy-MM-dd HH:mm:ss";

	/** yyyy-MM-dd HH:mm */
	public static final String format3 = "yyyy-MM-dd HH:mm";

	/** yyyy-MM */
	public static final String format4 = "yyyy-MM";

	/** dd */
	public static final String format5 = "dd";

	/** MM */
	public static final String format6 = "MM";

	/** HH:mm:ss */
	public static final String format7 = "HH:mm:ss";

	/** 00:00:00 */
	public static final String time = "00:00:00";

	/** 23:59:59 */
	public static final String endTime = "23:59:59";

	/** yyyyMMddHHmmss */
	public static final String format8 = "yyyyMMddHHmmss";

	/** yyMMddHHmm */
	public static final String format9 = "yyMMddHHmm";

	/** yyyyMMddHHmm */
	public static final String format10 = "yyyyMMddHHmm";

	/** yyyy */
	public static final String format11 = "yyyy";

	/** yyyy/MM/dd */
	public static final String format12 = "yyyy/MM/dd";

	public static final String format13 = "yyyyMMdd";

	/** 122400000l */
	public static final long Day = 122400000l;

	/**
	 * 得到当前时间『yyyy-MM-dd HH:mm:ss』
	 * 
	 * @return
	 */
	public static Date getCurrDate() {
		return convert2(new SimpleDateFormat(format2).format(new Date()));
	}

	/**
	 * 取得下月第一天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getNextMonthFirst() {

		Calendar cal = Calendar.getInstance();
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
		return calfirstday.getTime();

	}

	/**
	 * 取得当月第一天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getThisMonthFirst() {

		Calendar cal = Calendar.getInstance();
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		return calfirstday.getTime();

	}

	/**
	 * 取得当月第一天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getThisMonthFirst(Date d) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		return calfirstday.getTime();

	}

	public static Date getThisMonthFinal(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getThisMonthFirst(d));
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	public static Date getThisMonthFinalTime(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getThisMonthFirst(d));
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.SECOND, -1);
		return cal.getTime();
	}

	/**
	 * 取得当年第一天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getThisYearFirst() {
		Calendar cal = Calendar.getInstance();
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), 0, 1);
		return calfirstday.getTime();
	}

	public static Date getCurrYearLast() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearLast(currentYear);
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getYearLast(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year + 1);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}

	/**
	 * 获取某年最后一天日期
	 *
	 * @param date
	 *            日期
	 * @return Date
	 */
	public static Date getYearLast(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getYearLast(calendar.get(Calendar.YEAR));
	}

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year
	 *            年份
	 * @return
	 */
	public static Date getYearFirst(int year) {
		Calendar calfirstday = new GregorianCalendar(year, 0, 1);
		return calfirstday.getTime();
	}

	/**
	 * 获取某年第一天日期
	 *
	 * @param date
	 *            日期
	 * @return
	 */
	public static Date getYearFirst(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getYearFirst(calendar.get(Calendar.YEAR));
	}

	/**
	 * 取得上月第一天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getLastMonthFirst() {

		Calendar cal = Calendar.getInstance();
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1, 1);
		return calfirstday.getTime();

	}

	/**
	 * 取得上月最后一天23:59:59
	 *
	 * @return
	 */
	public static Date getLastMonthFinal() {
		Calendar cal = Calendar.getInstance();
		cal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		cal.add(Calendar.SECOND, -1);
		return cal.getTime();
	}

	/**
	 * 取得下一天的凌晨0:10时间
	 * 
	 * @return
	 */
	public static Date getNextDay() {
		Calendar cal = Calendar.getInstance();
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1);
		calnextday.add(Calendar.MINUTE, 10);
		return calnextday.getTime();
	}

	/**
	 * 取得今天凌晨0:10时间
	 * 
	 * @return
	 */
	public static Date getToday() {
		Calendar cal = Calendar.getInstance();
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		calnextday.add(Calendar.MINUTE, 10);
		return calnextday.getTime();
	}

	/**
	 * 取得指定日期和其凌晨00:00:00相差的毫秒数
	 * 
	 * @param date
	 *            需要换算的日期
	 * @return 相差毫秒数
	 */
	public static long getDiffTimes(Date date) {

		// 日期的00:00:00取得
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		// 日期取得
		Calendar dcal = Calendar.getInstance();
		dcal.setTime(date);
		// 返回两个日期之间的毫秒数
		return dcal.getTime().getTime() - calnextday.getTime().getTime();
	}

	/**
	 * 根据同00:00:00相差的毫秒数取得时间
	 * 
	 * @return res[0]:小时，res[1]:分钟
	 */
	public static Integer[] getDiffTimes1(Long times) {

		// 日期的00:00:00取得
		Integer[] res = { -1, -1 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		Long nowTimes = calnextday.getTime().getTime() + times;
		Date nowDate = new Date(nowTimes);
		Calendar nowcal = Calendar.getInstance();
		nowcal.setTime(nowDate);
		res[0] = nowcal.get(Calendar.HOUR_OF_DAY);
		res[1] = nowcal.get(Calendar.MINUTE);
		return res;
	}

	/**
	 * 取得给定时间的间隔小时的开始时间（支持负数）
	 * 
	 * @return
	 */
	public static Date getIntervalHourFirst(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 取得给定时间的间隔小时的结束时间（支持负数）
	 * 
	 * @return
	 */
	public static Date getIntervalHourFinal(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 取得给定时间的间隔分钟的开始时间（支持负数）
	 *
	 * @return
	 */
	public static Date getIntervalMinute(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * 取得给定时间的间隔分钟的开始时间（支持负数）
	 * 
	 * @return
	 */
	public static Date getIntervalMinuteFirst(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 取得给定时间的间隔分钟的结束时间（支持负数）
	 * 
	 * @return
	 */
	public static Date getIntervalMinuteFinal(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 取得下一天的时间字符串
	 * 
	 * @return
	 */
	public static String getNextDay(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat(format1);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			logger.error("Exception:", e);
		}
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(cal.getTime());
	}

	/**
	 * 获取当月最后一天
	 * 
	 * @param date
	 * @return
	 * @author
	 */
	public static Date getMonthEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		int index = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.add(Calendar.DATE, (-index));
		return calendar.getTime();
	}

	/**
	 * 取得指定月份的下月第一天凌晨0:00时间
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午02:20:47
	 */
	public static Date getNextMonthFirst(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format4);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			logger.error("Exception:", e);
		}
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
		return calfirstday.getTime();
	}

	/**
	 * 取得指定月份的下月第一天凌晨00:00时间
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午04:28:12
	 */
	public static Date getNextMonthFirst(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
		return calfirstday.getTime();
	}

	/**
	 * 取得指定月份的下年第一天凌晨00:00时间
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午04:28:12
	 */
	public static Date getNextYearFirst(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR) + 1, cal.get(Calendar.MONTH), 1);
		return calfirstday.getTime();
	}

	/**
	 * 取得指定月份的上月第一天凌晨00:00时间
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午04:28:12
	 */
	public static Date getLastMonthFirst(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar calfirstday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1, 1);
		return calfirstday.getTime();
	}

	/**
	 * 取得指定月份的上月最后一天最后一秒23:59:59
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午05:14:40
	 */
	public static Date getLastMonthFinal(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		cal.add(Calendar.SECOND, -1);
		return cal.getTime();
	}

	/**
	 * 取得指定月份的最后一天最后一秒23:59:59
	 * 
	 * @param date
	 * @return
	 * @author 8533 2012-4-1 下午05:14:40
	 */
	public static Date getMonthFinal(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
		cal.add(Calendar.SECOND, -1);
		return cal.getTime();
	}

	/**
	 * 字符串转化成日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date convert1(String date) {
		Date retValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format1);
		try {
			retValue = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return retValue;
	}

	/**
	 * 时间格式『yyyy-MM-dd HH:mm:ss』
	 * 
	 * @param date
	 * @return
	 */
	public static Date convert2(String date) {
		Date retValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format2);
		try {
			retValue = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return retValue;
	}

	public static Date convert3(String date) {
		Date retValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format3);
		try {
			retValue = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return retValue;
	}

	public static Date convert4(String date) {
		Date retValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format4);
		try {
			retValue = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return retValue;
	}

	/**
	 * 日期转化成字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String convert(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String retstr = sdf.format(date);
		return retstr;
	}

	public static Date convert(String date, String format) {
		Date retValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			retValue = sdf.parse(date);
		} catch (ParseException e) {
			retValue = null;
		}
		return retValue;
	}

	/**
	 * 取得与今天相隔plus天凌晨0:00时间
	 * 
	 * @return
	 */
	public static Date getDateFromToday(int plus) {

		Calendar cal = Calendar.getInstance();
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + plus);
		return calnextday.getTime();
	}

	public static String getTableSuffix(Date date) {

		Calendar cNextMonth = Calendar.getInstance();
		cNextMonth.setTime(date);
		String year = String.valueOf(cNextMonth.get(Calendar.YEAR)).substring(2);
		String month = String.valueOf(cNextMonth.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;
		return year + month;

	}

	/**
	 * 取得list中比默认值小的最小date,null认为最大,全比默认值大，返回默认值
	 * 
	 * @param dateList
	 * @param defaultDate
	 * @return
	 */
	public static Date getMinDateByList(List<Date> dateList, Date defaultDate) {
		Date tempDate = defaultDate;
		for (int i = 0; i < dateList.size(); i++) {
			if (dateList.get(i) == null)
				continue;
			if (dateList.get(i).getTime() < tempDate.getTime())
				tempDate = dateList.get(i);
		}
		return tempDate;
	}

	/**
	 * 两个日期相差天数
	 * 
	 * @param endDate
	 * @param startDate
	 * @return
	 */
	public static int getDiffDays(Date endDate, Date startDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * 两个日期相差分钟数
	 * 
	 * @param endDate	结束时间
	 * @param startDate	开始时间
	 * @return
	 */
	public static int getDiffmins(Date endDate, Date startDate) {
		Long min = 0l;
		try {
			long nm = 1000 * 60;
			// long ns = 1000;
			// 获得两个时间的毫秒时间差异
			long diff = endDate.getTime() - startDate.getTime();
			// 计算差多少分钟
			min = diff / nm;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return min.intValue();
	}

	/**
	 * 两个日期相差月份数
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static int getDiffMonths(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		int diffMonth = 0;
		int reverse = 1;
		if (start.compareTo(end) == 0) {
			return diffMonth;
		} else if (start.after(end)) {
			reverse = -1;
			start.setTime(endDate);
			end.setTime(startDate);
		}
		while (end.after(start)) {
			start.add(Calendar.MONTH, 1);
			diffMonth++;
		}
		return reverse * (diffMonth - 1);
	}

	/**
	 * 获得两个日期之间相差的月份
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getMonth(Date start, Date end) {
		if (start.after(end)) {
			Date t = start;
			start = end;
			end = t;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);
		Calendar temp = Calendar.getInstance();
		temp.setTime(end);
		temp.add(Calendar.DATE, 1);

		int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

		if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month + 1;
		} else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month;
		} else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
			return year * 12 + month;
		} else {
			return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
		}
	}

	/**
	 * 获得某年某月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(String year, String month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		// 某年某月的最后一天
		return String.valueOf(cal.getActualMaximum(Calendar.DATE));
	}

	/**
	 * 获取当天的日期
	 * 
	 * @return
	 */
	public static long getTodayTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取日期开始时的long值<br>
	 * 即yyyy-MM-dd 00:00:00 00 对应的long值
	 * 
	 * @return
	 */
	public static long getStartTime(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(convert1(date));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取指定的日期的结束时的long值<br>
	 * 即yyyy-MM-dd 23:59:59 999999999对应的long值
	 * 
	 * @return
	 */
	public static long getEndTime(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(convert1(date));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999999999);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取前一天
	 * 
	 * @return
	 */
	public static long getYesterdayTime() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		if (month == 11 && day == 1) {// 跨年
			int year = cal.get(Calendar.YEAR);
			cal.set(Calendar.YEAR, year - 1);
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.DATE, 31);
		} else {
			if (day == 1) {
				cal.set(Calendar.MONTH, month - 1);
				int date = cal.getMaximum(Calendar.DATE);
				cal.set(Calendar.DATE, date);
			} else {
				cal.set(Calendar.DATE, day - 1);
			}
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * 根据日期获取前一天日期
	 * 
	 * @return
	 */
	public static Date getYesterdayDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 四舍五入且保留两位有效小数
	 * 
	 * @param num
	 * @return
	 */
	public static String numberTwoDecimal(Double num) {
		BigDecimal big = new BigDecimal(num);
		return big.setScale(2, RoundingMode.HALF_UP).toString();
	}

	/**
	 * 四舍五入且保留三位有效小数
	 * 
	 * @param num
	 * @return
	 */
	public static String numberThreeDecimal(Double num) {
		BigDecimal big = new BigDecimal(num);
		return big.setScale(3, RoundingMode.HALF_UP).toString();
	}

	/**
	 * 得到当天的日期，形如：2012-02-08
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String currentDate = sdf.format(cal.getTime());
		return currentDate;
	}

	/**
	 * 获取当天开始时间
	 * 
	 * @return
	 */
	public static Date getCurrentStartDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getYesterdayStartDateTime() {
		return getYesterdayStartDateTime(null);
	}

	public static Date getYesterdayStartDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getYesterdayEndDateTime() {
		return getYesterdayEndDateTime(null);
	}

	public static Date getYesterdayEndDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取前天开始时间
	 * 
	 * @return
	 */
	public static Date getFrontdayStartDateTime() {
		return getFrontdayStartDateTime(null);
	}

	public static Date getFrontdayStartDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.DATE, -2);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 获取当天结束时间
	 * 
	 * @return
	 */
	public static Date getCurrentEndDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 方法描述：获取前一天的开始时间
	 * 
	 * @return
	 * @author: 8534
	 * @date: 2012-3-1 下午02:49:15
	 */
	public static Date getPreviousStartDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 000);
		return cal.getTime();
	}

	/**
	 * 方法描述：获取前一天的结束时间
	 * 
	 * @return
	 * @author: 8534
	 * @date: 2012-3-1 下午02:54:01
	 */
	public static Date getPreviousEndDateTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/**
	 * 方法描述：获取当月第一天的时间,如：2012-03-01 00:00:00
	 * 
	 * @return @author：8536
	 * @date: 2012-3-7下午09:09:03
	 */
	public static Date getCurrentMonthFirstDay() {
		Date firstDay = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar cal = Calendar.getInstance();
			String startTime = sdf.format(cal.getTime()) + "-01 00:00:00";
			firstDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return firstDay;
	}

	/**
	 * 方法描述：获取当月最后一天的时间,如：2012-03-31 23:59:59
	 * 
	 * @return @author：8536
	 * @date: 2012-3-7下午09:09:03
	 */
	public static Date getCurrentMonthFinalDay() {
		Date finalDay = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar cal = Calendar.getInstance();
			int lastDay = cal.get(Calendar.DAY_OF_MONTH);// 当前月的最后一天
			String endTime = sdf.format(cal.getTime()) + "-" + lastDay + " 23:59:59";
			finalDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
		} catch (Exception e) {
			logger.error("", e);
		}
		return finalDay;
	}

	/**
	 * 按指定格式转换Date型数据为String格式
	 * 
	 * @param date
	 * @param inFormat
	 * @return
	 */
	public static String transFormString(Date date, String inFormat) {
		String _format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat df;
		if (inFormat == null || inFormat.equals("")) {
			df = new SimpleDateFormat(_format);
		} else {
			df = new SimpleDateFormat(inFormat);
		}
		return df.format(date);
	}

	/**
	 * 获得给定日期date相隔days天的日期(支持负数)
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date getDaysOfDistance(Date date, int days) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(date);
		int day = aCalendar.get(Calendar.DATE);
		aCalendar.set(Calendar.DATE, day + days);
		return aCalendar.getTime();
	}

	/**
	 * 根据yyyy-mm-dd得到yyyy-mm-dd开始的Date
	 * 
	 * @return
	 */
	public static Date getTodayStatrTime(String date) {
		return convert2(date + " " + time);
	}

	/**
	 * 根据yyyy-mm-dd得到yyyy-mm-dd结束的Date
	 *
	 * @return
	 */
	public static Date getTodayEndTime(String date) {
		return convert2(date + " " + endTime);
	}

	/**
	 * 根据yyyy-mm-dd得到明天开始的Date
	 * 
	 * @return
	 */
	public static Date getTomorrowStatrTime(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getTodayStatrTime(date));
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 24小时之后的时间
	 * 
	 * @return
	 */
	public static Date afterDay() {
		Long now = System.currentTimeMillis();
		return new Date(now + Day);
	}

	/**
	 * 获取当前月份
	 * 
	 * @return int @throws
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String month = new SimpleDateFormat(format6).format(cal.getTime());
		return Integer.parseInt(month);
	}

	/**
	 * 获取昨天日期
	 * 
	 * @return int @throws
	 */
	public static int getPreviousDay() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String yesterday = new SimpleDateFormat(format5).format(cal.getTime());
		return Integer.parseInt(yesterday);
	}

	/**
	 * 判断给定日期是否小于当前指定天数后的时间
	 * 
	 * @param date
	 *            给定日期
	 * @param days
	 *            指定天数
	 * @return
	 */
	public static boolean isBeforToday(Date date, int days) {
		Calendar day = Calendar.getInstance();
		day.setTime(new Date());
		day.add(Calendar.DATE, days);
		return date.before(day.getTime());
	}

	/**
	 * 获取间隔分钟
	 * 
	 * @param n
	 * @return
	 */
	public static Date getIntervalHour(int n) {
		Calendar cal = Calendar.getInstance();
		Calendar calnextday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		calnextday.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY) + n,
				cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		return calnextday.getTime();
	}

	public static Date getIntervalHour(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/**
	 * 日期转化成字符串
	 * 
	 * @param dateTime
	 *            日期
	 * @param format
	 *            格式
	 * @return
	 */
	public static String convert(long dateTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String retstr = sdf.format(dateTime);
		return retstr;
	}

	/**
	 * 根据日期返回这个星期的第几天
	 * 
	 * @param date
	 *            yyyy-MM-dd格式
	 * @return
	 */
	public static int getDayOfWeek(String date) {
		LocalDate localdate = LocalDate.parse(date);
		return localdate.getDayOfWeek().ordinal();
	}

	/**
	 * 根据日期返回这个星期的第几天
	 * 
	 * @param date
	 *            yyyy-MM-dd格式
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate localdate = instant.atZone(zoneId).toLocalDate();
		return localdate.getDayOfWeek().ordinal();
	}

	public static Date getLastYearStart() {
		Instant instant = new Date().toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		int year = instant.atZone(zoneId).toLocalDate().getYear() - 1;
		return convert1(year + "-01-01");
	}

	/**
	 * 获取两个时间之间的所有月份
	 */
	public static Map<String, List<ToQueryMonthRespDto>> getMonthBetweenDate(Date startDate, Date endDate) {
		Map<String, List<ToQueryMonthRespDto>> timeMap = new HashMap<String, List<ToQueryMonthRespDto>>();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate start = startDate.toInstant().atZone(zoneId).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(zoneId).toLocalDate();
		// 开始时间必须在结束时间之前
		if (start.equals(end) || start.isBefore(end)) {
			// 1、如果开始时间和结束时间是同一年
			int startYear = start.getYear();
			int endYear = end.getYear();
			if (startYear == endYear) {
				timeMap.put(start.getYear() + "", getMonthList(start.getMonthValue(), end.getMonthValue()));
			} else {
				for (int i = startYear; i <= endYear; i++) {
					List<ToQueryMonthRespDto> month = new ArrayList<ToQueryMonthRespDto>();
					if (i == endYear) {
						month = getMonthList(1, end.getMonthValue());
					} else if (i == startYear) {
						month = getMonthList(start.getMonthValue(), 12);
					} else {
						month = getMonthList(1, 12);
					}
					timeMap.put(i + "", month);
				}
			}
		}
		Map<String, List<ToQueryMonthRespDto>> result = new LinkedHashMap<>();
		timeMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
		return result;
	}

	public static List<ToQueryMonthRespDto> getMonthList(int startMonth, int endMonth) {
		String[] _month = new String[] { "", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };
		List<ToQueryMonthRespDto> month = new ArrayList<ToQueryMonthRespDto>();
		for (int i = startMonth; i <= endMonth; i++) {
			month.add(new ToQueryMonthRespDto(i, _month[i]));
		}
		return month;
	}

	/**
	 * 根据 年-月 获取对应月份的最大日期
	 * 
	 * @return 月末日期（yyyy-MM-dd）
	 */
	public static Date getMaxDateTime(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 0);
		cal.getMaximum(Calendar.DATE);
		return DateUtil.convert(DateUtil.convert(cal.getTime(), DateUtil.format1), DateUtil.format1);
	}

	/**
	 * 
	 * 获取去年到当前月份的map
	 * 
	 * key yyyy-MM格式
	 * 
	 * @return
	 */
	public static <T> Map<String, T> getYearMonth(int reqYear) {
		Map<String, T> map = new LinkedHashMap<>();
		for (int i = 1; i <= 12; i++) {
			map.put((reqYear) + "-" + String.format("%02d", i), null);
		}
		return map;
	}

	/**
	 * 获取一天的开始时间
	 * 
	 * @param date
	 *            某一天
	 * @return
	 */
	public static Date getDateStartDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 000);
		return cal.getTime();
	}

	/**
	 * 获取一天的结束时间，日期 23:59:59.999
	 *
	 * @param date
	 *            某一天
	 * @return
	 */
	public static Date getDateEndDateTime(Date date) {
		return getDateEndDateTime(date, false);
	}

	/**
	 * 获取一天的结束时间，日期 23:59:59
	 * 
	 * @param date
	 *            某一天
	 * 
	 * @param millis0
	 *            是否毫秒数改为0（毫秒数>500存到MySQL会自动加1秒）
	 * 
	 * @return
	 */
	public static Date getDateEndDateTime(Date date, boolean millis0) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		if (millis0) {
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.set(Calendar.MILLISECOND, 999);
		}
		return cal.getTime();
	}

	/**
	 * 当前季度的开始时间
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterStartTime() {
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 1);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 4);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	public static Date getDayStart(Date date) {
		return convert1(convert(date, format1));
	}

	public static Date getNextDayStart(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return convert1(convert(cal.getTime(), format1));
	}

	public static Date getLastDayStart(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return convert1(convert(cal.getTime(), format1));
	}

	// 当前不生效
	public static Date getCurrentMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		monday = getCurrentStartDateTime();
		return monday;
	}

	public static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		// 由于Calendar提供的都是以星期日作为周一的开始时间
		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	public static Date getThisWeekMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 获得当前日期是一个星期的第几天
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		return cal.getTime();
	}

	public static int getDayDiffer(Date startDate, Date endDate) throws ParseException {
		SimpleDateFormat yearFormat = new SimpleDateFormat(format11);
		String startYear = yearFormat.format(startDate);
		String endYear = yearFormat.format(endDate);
		if (startYear.equals(endYear)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int startDay = calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTime(endDate);
			int endDay = calendar.get(Calendar.DAY_OF_YEAR);
			return endDay - startDay;
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format1);
			long startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
			long endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
			return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
		}
	}

	/**
	 * 获取指定日期 一个月以前的时间
	 * 
	 * @return 时间
	 */
	public static Long getMonthBefore(int month) {
		return getMonthBefore(month, false);
	}

	/**
	 * 获取指定日期 一个月以前的时间
	 *
	 * @return 时间
	 */
	public static Long getMonthBefore(int month, boolean zero) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// 几个月之前
		calendar.add(Calendar.MONTH, -month);
		// 时间算前一天的最后时间
		if (zero) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		return calendar.getTime().getTime();
	}

	/**
	 * 获取指定日期以前
	 * 
	 * @param date
	 *            那天
	 * @param day
	 *            几天
	 * @return 日期
	 */
	public static Long getDayBefore(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 时间算前一天的最后时间
		calendar.add(Calendar.DATE, -day - 1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime().getTime();
	}

	/**
	 * 获取month个月后的今天的23:59:59
	 * 
	 * @param month
	 *            延后月数
	 * @return
	 */
	public static Date getMonthAfter(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// 几个月之后
		calendar.add(Calendar.MONTH, month);
		// 23:59:59
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取某天在month个月后的23:59:59
	 *
	 * @param date
	 *            日期
	 * @param month
	 *            延后月数
	 * @return
	 */
	public static Date getMonthAfter(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 几个月之后
		calendar.add(Calendar.MONTH, month);
		// 23:59:59
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// ---------------------------------周期相关------------------------------------
	/**
	 * @param reportType
	 *            {@link ReportType}
	 * @return cycle
	 */
	@SuppressWarnings("deprecation")
	public static int getCycleNum(int reportType, Date date) {
		if (reportType == ReportType.WEEKLY.ordinal()) { // 周
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar.get(Calendar.WEEK_OF_MONTH);
		} else if (reportType == ReportType.MONTHLY_REPORT.ordinal()) { // 月
			return date.getMonth() + 1;
		} else if (reportType == ReportType.QUARTERLY_REPORT.ordinal()) { // 季
			int month = date.getMonth() + 1;
			return month % 3 == 0 ? month / 3 : (month / 3 + 1);
		} else if (reportType == ReportType.SEMIANNUAL_REPORT.ordinal()) { // 半年
			int month = date.getMonth() + 1;
			return month % 6 == 0 ? month / 6 : (month / 6 + 1);
		}
		return -1;
	}

	public static int getCycleNum(int reportType) {
		return getCycleNum(reportType, new Date());
	}

	public static int getMonthDays(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
