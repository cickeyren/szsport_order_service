
package com.digitalchina.common.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/* *
 *类名：UtilDate
 *功能：自定义订单类
 *详细：工具类，可以用作获取系统日期、订单编号等
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class UtilDate {
	
    /** 年月日时分秒(无下划线) yyyyMMddHHmmssSSS */
    public static final String dtLong                  = "yyyyMMddHHmmssSSS";
    
    /** 完整时间 yyyy-MM-dd HH:mm:ss */
    public static final String simple                  = "yyyy-MM-dd HH:mm:ss";
    
    /** 年月日(无下划线) yyyyMMdd */
    public static final String dtShort                 = "yyyyMMdd";
	
    public static SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 返回系统当前时间(精确到毫秒),作为一个唯一的订单编号
     * @return
     *      以yyyyMMddHHmmssSSS为格式的当前系统时间
     */
	public  static String getOrderNum(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat(dtLong);
		return df.format(date);
	}
	
	/**
	 * 获取系统当前日期(精确到毫秒)，格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public  static String getDateFormatter(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat(simple);
		return df.format(date);
	}
	
	/**
	 * 获取系统当期年月日(精确到天)，格式：yyyyMMdd
	 * @return
	 */
	public static String getDate(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat(dtShort);
		return df.format(date);
	}
	
	/**
	 * 产生随机的三位数
	 * @return
	 */
	public static String getThree(){
		Random rad=new Random();
		int count = rad.nextInt(900)+100;
		return count + "";
	}

	/**
	 * 产生随机的6位数
	 * @return
	 */
	public static String getSix(){
		Random rad=new Random();
		int count = rad.nextInt(900000)+100000;
		return count + "";
	}
	/**
	 * 比较传入的时间加上3个月后，是否超出当前时间
	 * @param datetime
	 * @return
	 * @throws Exception
	 */
	public static boolean compareDate3MonthsLaterContainsNow(String datetime) throws Exception {  
        Date date = sdfSimple.parse(datetime);  
        Calendar cl = Calendar.getInstance();  
        cl.setTime(date);
        cl.add(Calendar.DATE,88);//以最小的28+30+30 为例，超出这个时间就标识为要手动退款
        date = cl.getTime();
        if(date.getTime() > System.currentTimeMillis()) {
        	return true;
        }
        return false;
    }
	
	public static void main(String[] args) throws Exception {
//		for(int i = 0;i<100;i++) {
//			String orderNum = getOrderNum()+getThree();
//		System.out.println(orderNum);
//		}
		System.out.println(getDateFormatter());
		//System.out.println(compareDate3MonthsLaterContainsNow("2016-01-01 10:00:00"));
	}
}
