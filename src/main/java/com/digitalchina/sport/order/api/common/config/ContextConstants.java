package com.digitalchina.sport.order.api.common.config;

/**
 * create by wanggw  2017/3/13 10:55
 */

public class ContextConstants {
    /**
     * 取票状态
     */
    public final static String TAKE_STATUS = "1";//已取票
    public final static String CHECK_STATUS = "1";//已验票
    public final static String CAN_RETREAT_KT = "0";//可退
    public final static String CAN_RETREAT_NKT = "1";//不可退
    /**
     * 子订单状态
     */
    public final static String STATUS0 = "0";//0待支付
    public final static String STATUS1 = "1";//1待使用
    public final static String STATUS2 = "2";//2已使用
    public final static String STATUS3 = "3";//3支付失败
    public final static String STATUS4 = "4";//4待退款
    public final static String STATUS5 = "5";//5失效订单
    public final static String STATUS6 = "6";//6.已退款
    public final static String STATUS7 = "7";//7.退款失敗
    /**
     * 支付状态
     */
    public final static String PAY_TYPE1 = "1";//支付宝
    public final static String PAY_TYPE2 = "2";//微信

    /**
     * 订票类型
     */
    public final static String TICKET_TYPE0 = "0";//散票/年卡
    public final static String TICKET_TYPE1 = "1";//场地票
    public final static String TICKET_TYPE2 = "2";//散客预定
    /**
     *退款状态：0待退款，1已退款，2退款失败
     */
    public final static String BASESTATUS0 = "0";//0待退款
    public final static String BASESTATUS1= "1";//0已退款
    public final static String BASESTATUS2 = "2";//0退款失败

    /**
     * 场地票退票规则  退款规则 0：不可退 1：随时退 2：条件退
     */
    public final static String SITETATUS0 = "0";//0不可退
    public final static String SITETATUS1 = "1";//1，随时退
    public final static String SITETATUS2 = "2";//2.条件退





}

