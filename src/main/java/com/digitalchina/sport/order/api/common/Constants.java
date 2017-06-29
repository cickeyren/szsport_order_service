package com.digitalchina.sport.order.api.common;

public class Constants {
    /* 平台代码规范常量 */
    /**
     * 通用成功代码
     */
    public final static String RTN_CODE_SUCCESS = "000000"; //通用成功代码
    public final static String RTN_CODE_FAIL = "999999"; //通用错误代码
    public final static String RTN_CODE = "RTN_CODE"; //通用返回码名称
    public final static String RTN_MSG = "RTN_MSG"; //通用返回信息名称

    public final static String RTN_SIGN_SUCCESS = "200";
    /**
     * 通用错误代码
     */
    public final static String RTN_SIGN_FAIL = "100";
    public final static String RTN_SIGN_NULL = "101";
    public final static String RTN_SIGN_NOCAR = "201";//车辆未入场
    public final static String RTN_SIGN_OTHER = "102";


    public final static String RTN_STATUS_SUCCESS = "OK";
    public final static String RTN_STATUS_ERROR = "ERROR";


    /**
     * 通用错误信息
     */
    public final static String RTN_MESSAGE_ERROR = "请求发生异常";
    
}
