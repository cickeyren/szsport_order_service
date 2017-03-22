package com.digitalchina.sport.order.api.model;

import java.util.Date;
import javax.persistence.*;

public class RefundOrder {
    /**
     * 退单编号
     */
    private String id;

    /**
     * 退款信息返回码
     */
    private String code;

    /**
     * 退款信息
     */
    private String msg;

    /**
     * 退款登录账户
     */
    private String buyer_logon_id;


    /**
     * 退款账户
     */
    private String buyer_user_id;

    /**
     * 退款信息
     */
    private String fund_change;

    /**
     * 退款时间
     */
    private String gmt_refund_pay;


    private String open_id;

    /**
     * 订单编号
     */
    private String out_trade_no;


    /**
     * 交易流水号
     */
    private String trade_no;

    /**
     * 退款金额
     */
    private String refund_monery;
    /**
     * 退款方式
     */
    private String refund_method;
    /**
     * 退款人id
     */
    private String userId;
    /**
     * 主订单id
     */
    private String orderId;
    /**
     * 子订单id
     */
    private String orderContentId;
    /**
     * 子订单id
     */
    private String refund_fee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBuyer_logon_id() {
        return buyer_logon_id;
    }

    public void setBuyer_logon_id(String buyer_logon_id) {
        this.buyer_logon_id = buyer_logon_id;
    }

    public String getBuyer_user_id() {
        return buyer_user_id;
    }

    public void setBuyer_user_id(String buyer_user_id) {
        this.buyer_user_id = buyer_user_id;
    }

    public String getFund_change() {
        return fund_change;
    }

    public void setFund_change(String fund_change) {
        this.fund_change = fund_change;
    }

    public String getGmt_refund_pay() {
        return gmt_refund_pay;
    }

    public void setGmt_refund_pay(String gmt_refund_pay) {
        this.gmt_refund_pay = gmt_refund_pay;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getRefund_monery() {
        return refund_monery;
    }

    public void setRefund_monery(String refund_monery) {
        this.refund_monery = refund_monery;
    }

    public String getRefund_method() {
        return refund_method;
    }

    public void setRefund_method(String refund_method) {
        this.refund_method = refund_method;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderContentId() {
        return orderContentId;
    }

    public void setOrderContentId(String orderContentId) {
        this.orderContentId = orderContentId;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }
}