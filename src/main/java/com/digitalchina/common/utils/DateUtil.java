package com.digitalchina.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiaoning.sun on 2016/11/30.
 */
public class DateUtil {
    public static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /** milliiseconds */
    public final static long MS = 1;
    /**  Number of milliseconds in a standard second.
     */
    public final static long MILLIS_PER_SECOND = MS * 1000;
    /** Number of milliseconds in a standard minute */
    public final static long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    /** Number of milliseconds in a standard hour */
    public final static long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
    /** Number of milliseconds in a standard day */
    public final static long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

    /** 标准日期格式 */
    public final static String NORM_DATE_PATTERN = "yyyy-MM-dd";
    /** 标准时间格式 */
    public final static String NORM_TIME_PATTERN = "HH:mm:ss";
    /** 标准日期时间格式 */
    public final static String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** HTTP头中日期时间格式 */
    public final static String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /** 标准日期（不含时间）格式化器 */
    private final static SimpleDateFormat NORM_DATE_FORMAT = new SimpleDateFormat(NORM_DATE_PATTERN);
    /** 标准时间格式化器 */
    private final static SimpleDateFormat NORM_TIME_FORMAT = new SimpleDateFormat(NORM_TIME_PATTERN);
    /** 标准日期时间格式化器 */
    private final static SimpleDateFormat NORM_DATETIME_FORMAT = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    /** HTTP日期时间格式化器 */
    private final static SimpleDateFormat HTTP_DATETIME_FORMAT = new SimpleDateFormat(HTTP_DATETIME_PATTERN, Locale.US);

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return formatDateTime(new Date());
    }

    /**
     * 当前日期，格式 yyyy-MM-dd
     * @return 当前日期的标准形式字符串
     */
    public static String today() {
        return formatDate(new Date());
    }

    // ------------------------------------ Format start ----------------------------------------------
    /**
     * 根据特定格式格式化日期
     * @param date 被格式化的日期
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式 yyyy-MM-dd HH:mm:ss
     * @param date 被格式化的日期
     * @return 格式化后的日期
     */
    public static String formatDateTime(Date date) {
//		return format(d, "yyyy-MM-dd HH:mm:ss");
        return NORM_DATETIME_FORMAT.format(date);
    }

    /**
     * 格式化为Http的标准日期格式
     * @param date 被格式化的日期
     * @return HTTP标准形式日期字符串
     */
    public static String formatHttpDate(Date date) {
//		return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).format(date);
        return HTTP_DATETIME_FORMAT.format(date);
    }

    /**
     * 格式 yyyy-MM-dd
     * @param date 被格式化的日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
//		return format(d, "yyyy-MM-dd");
        return NORM_DATE_FORMAT.format(date);
    }
    // ------------------------------------ Format end ----------------------------------------------

    // ------------------------------------ Parse start ----------------------------------------------
    /**
     * 将特定格式的日期转换为Date对象
     * @param dateString 特定格式的日期
     * @param format 格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static Date parse(String dateString, String format){
        try {
            return (new SimpleDateFormat(format)).parse(dateString);
        } catch (ParseException e) {
            logger.error("Parse " + dateString + " with format " + format + " error!", e);
        }
        return null;
    }

    /**
     * 格式yyyy-MM-dd HH:mm:ss
     * @param dateString 标准形式的时间字符串
     * @return 日期对象
     */
    public static Date parseDateTime(String dateString){
        try {
            return NORM_DATETIME_FORMAT.parse(dateString);
        } catch (ParseException e) {
            logger.error("Parse " + dateString + " with format " + NORM_DATETIME_FORMAT.toPattern() + " error!", e);
        }
        return null;
    }

    /**
     * 格式yyyy-MM-dd
     * @param dateString 标准形式的日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateString){
        try {
            return NORM_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            logger.error("Parse " + dateString + " with format " + NORM_DATE_PATTERN + " error!", e);
        }
        return null;
    }

    /**
     * 格式HH:mm:ss
     * @param timeString 标准形式的日期字符串
     * @return 日期对象
     */
    public static Date parseTime(String timeString){
        try {
            return NORM_TIME_FORMAT.parse(timeString);
        } catch (ParseException e) {
            logger.error("Parse " + timeString + " with format " + NORM_TIME_PATTERN + " error!", e);
        }
        return null;
    }

    /**
     * 格式：<br>
     * 1、yyyy-MM-dd HH:mm:ss<br>
     * 2、yyyy-MM-dd<br>
     * 3、HH:mm:ss>
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date parse(String dateStr) {
        int length = dateStr.length();
        try {
            if(length == DateUtil.NORM_DATETIME_PATTERN.length()) {
                return parseDateTime(dateStr);
            }else if(length == DateUtil.NORM_DATE_PATTERN.length()) {
                return parseDate(dateStr);
            }else if(length == DateUtil.NORM_TIME_PATTERN.length()){
                return parseTime(dateStr);
            }
        }catch(Exception e) {
            logger.error("Parse " + dateStr + " with format normal error!", e);
        }
        return null;
    }
    // ------------------------------------ Parse end ----------------------------------------------

    // ------------------------------------ Offset start ----------------------------------------------

    /**
     * 昨天
     * @return 昨天
     */
    public static Date yesterday() {
        return offsiteDate(new Date(), Calendar.DAY_OF_YEAR, -1);
    }

    /**
     * 上周
     * @return 上周
     */
    public static Date lastWeek() {
        return offsiteDate(new Date(), Calendar.WEEK_OF_YEAR, -1);
    }

    /**
     * 上个月
     * @return 上个月
     */
    public static Date lastMonth() {
        return offsiteDate(new Date(), Calendar.MONTH, -1);
    }

    /**
     * 获取指定日期偏移指定时间后的时间
     * @param date 基准日期
     * @param calendarField 偏移的粒度大小（小时、天、月等）使用Calendar中的常数
     * @param offsite 偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offsiteDate(Date date, int calendarField, int offsite){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarField, offsite);
        return cal.getTime();
    }

    public static String offsiteDateTime(String dateTime, int calendarField, int offsite){
        System.out.print(dateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(dateTime,"yyyy-MM-dd HH:mm:ss"));
        cal.add(calendarField, offsite);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置格式
        return df.format(cal.getTime());
    }


    // ------------------------------------ Offset end ----------------------------------------------

    /**
     * 判断两个日期相差的时长<br/>
     * 返回 minuend - subtrahend 的差
     * @param subtrahend 减数日期
     * @param minuend 被减数日期
     * @param diffField 相差的选项：相差的天、小时
     * @return 日期差
     */
    public static long diff(Date subtrahend, Date minuend, long diffField){
        long diff = minuend.getTime() - subtrahend.getTime();
        return diff/diffField;
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：纳秒
     * @param preTime 之前记录的时间
     * @return 时间差，纳秒
     */
    public static long spendNt(long preTime) {
        return System.nanoTime() - preTime;
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：毫秒
     * @param preTime 之前记录的时间
     * @return 时间差，毫秒
     */
    public static long spendMs(long preTime) {
        return System.currentTimeMillis() - preTime;
    }

    public static Date getNow() {
        return new Date();
    }

    public static String toWeekName(Integer week){
        String weeksName = "";
        switch(week){
            case 1:
                weeksName="周一";
                break;
            case 2:
                weeksName="周二";
                break;
            case 3:
                weeksName="周三";
                break;
            case 4:
                weeksName="周四";
                break;
            case 5:
                weeksName="周五";
                break;
            case 6:
                weeksName="周六";
                break;
            case 7:
                weeksName="周日";
                break;
        }
        return weeksName;
    }

    public static int getWeekByDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }

    /**
     * 当前时间，格式 HH:mm:ss
     * @return 当前时间的标准形式字符串
     */
    public static String nowtime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置格式
        return df.format(new Date());
    }

    /**
     * 判断时间是否在一个时间段内
     * @param startDate
     * @param endDate
     * @param compareDate
     * @return
     */
    public static boolean compareDateTo(String startDate,String endDate,String compareDate) {
        Date start =  parseDate(startDate);
        Date end = parseDate(endDate);
        Date compare = parseDate(compareDate);
        if(compare.compareTo(start) >= 0){//当 >= 0 , 表示 compare 大于或等于 start 继续和 end 比较 , 当 compare 小于 start 时，为 -1
            if(compare.compareTo(end) <= 0){//当 <= 0 ,表示 compare 小于或等于 end 返回true , 当 compare 大于 end 时，为 1
                return true;
            }else return false;
        }else return false;
    }
    public static boolean compareTimeTo(String startTime,String endTime,String compareTime) {
        Date start =  parseTime(startTime);
        Date end = parseTime(endTime);
        Date compare = parseTime(compareTime);
        if(compare.compareTo(start) >= 0){//当 >= 0 , 表示 compare 大于或等于 start 继续和 end 比较 , 当 compare 小于 start 时，为 -1
            if(compare.compareTo(end) <= 0){//当 <= 0 ,表示 compare 小于或等于 end 返回true , 当 compare 大于 end 时，为 1
                return true;
            }else return false;
        }else return false;
    }
    /**
     * 判断时间是否在一个时间段内
     * @param startDate
     * @param endDate
     * @param compareDate
     * @return
     */
    public static boolean compareDateTimeTo(String startDate,String endDate,String compareDate) {
        Date start =  parseDateTime(startDate);
        Date end = parseDateTime(endDate);
        Date compare = parseDateTime(compareDate);
        if(compare.compareTo(start) >= 0){//当 >= 0 , 表示 compare 大于或等于 start 继续和 end 比较 , 当 compare 小于 start 时，为 -1
            if(compare.compareTo(end) <= 0){//当 <= 0 ,表示 compare 小于或等于 end 返回true , 当 compare 大于 end 时，为 1
                return true;
            }else return false;
        }else return false;
    }
    public static void main(String[] args) {
/*        System.out.println("now() = [" + now() + "]");
        System.out.println("today() = [" + today() + "]");
        System.out.println("format(Date,String) = [" + format(new Date(),NORM_DATE_PATTERN) + "]");
        System.out.println("formatDateTime(Date) = [" + formatDateTime(new Date()) + "]");
        System.out.println("formatHttpDate(Date) = [" + formatHttpDate(new Date()) + "]");
        System.out.println("formatDate(Date) = [" + formatDate(new Date()) + "]");
        System.out.println("parse(String,String) = [" + parse("2016-11-30",NORM_DATE_PATTERN) + "]");
        System.out.println("parseDateTime(String) = [" + parseDateTime("2016-11-30 14:41:41") + "]");
        System.out.println("parseDate(String) = [" + parseDate("2016-11-30") + "]");
        System.out.println("parseTime(String) = [" + parseTime("14:41:41") + "]");
        System.out.println("parse(String) = [" + parse("2016-11-30 14:41:41") + "]");
        System.out.println("yesterday() = [" + yesterday() + "]");
        System.out.println("lastWeek() = [" + lastWeek() + "]");
        System.out.println("lastMonth() = [" + lastMonth() + "]");
        System.out.println("getWeekByDate() = [" + getWeekByDate(new Date()) + "]");
        System.out.println("nowtime() = [" + nowtime() + "]");*/
        System.out.println("compareDateTimeTo() = [" + compareDateTimeTo("2017-07-09 00:00:00","2017-07-10 00:00:00","2017-07-10 12:00:00") + "]");
        System.out.println("compareTimeTo() = [" + compareTimeTo("12:00:00","18:00:00","12:00:00") + "]");
/*        System.out.println("offsiteDate() = [" + formatDateTime(offsiteDate(new Date(), Calendar.DAY_OF_YEAR, 7)) + "]");
        String date = "2017-03-16";
        String time = "10:35:00";
        System.out.println("parse() = [" + DateUtil.parse(date + " "+ time) + "]");
        int approachTime=10;
        System.out.println("offsiteDateTime() = [" + offsiteDateTime(date + " "+ time,Calendar.MINUTE, -approachTime) + "]");*/



/*
        int sorts[] = {1,2,3,5,6,8,10,13,14,15};
        String start= sorts[0]+"";
        String end = "";
        String answer= "";
        for(int i=0;i<sorts.length-1;i++){
            int sort = sorts[i];
            int next = sorts[i+1];
            if(next-sort!=1){
                end=sort+"";
                if(start.equals(end)){
                    answer += start+",";
                }else {
                    answer += start + "-"+end+",";
                }
                start = next+"";
            }
            if (i==sorts.length-2){
                end = sorts[sorts.length-1]+"";
                if(start.equals(end)){
                    answer += start+",";
                }else {
                    answer += start + "-"+end+",";
                }
            }
        }
        System.out.println(answer);*/
    }
}
