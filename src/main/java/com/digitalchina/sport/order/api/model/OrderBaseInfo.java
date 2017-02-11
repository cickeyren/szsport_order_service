package com.digitalchina.sport.order.api.model;


import java.sql.Timestamp;

/**
 * 订单基本信息表
 */
public class OrderBaseInfo {
    private String id;
    private String orderNumber;//订单流水号
    private String stadiumName;//主场馆名称
    private String classify;
    private String subStadiumName;//子场馆名称
    private String personKind;//适用人群票型（1成人票，2儿童票，3老年票）
    private String ticketId;//产品id（就是门票id）
    private String ticketName;//产品名称
    private String ticketType;//产品类型 0：散票 1：场地票 2：通票
    private Timestamp orderTime;
    private String orderChannel;//下单方式app
    private String status;//状态（待支付，待使用，已使用，支付失败，退款）
    private String costPrice;//订单成本
    private String sellPrice;//订单售价
    private String userId;//用户id
    private String userName;//用户名
    private String userTel;//用户手机
    private String payType;//支付方式（支付宝，微信）
    private String payAcount;//支付账户
    private Timestamp payTime;//支付时间
    private String payPrice;//支付金额
    private String merchantId;//商户id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getSubStadiumName() {
        return subStadiumName;
    }

    public void setSubStadiumName(String subStadiumName) {
        this.subStadiumName = subStadiumName;
    }

    public String getPersonKind() {
        return personKind;
    }

    public void setPersonKind(String personKind) {
        this.personKind = personKind;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(String orderChannel) {
        this.orderChannel = orderChannel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayAcount() {
        return payAcount;
    }

    public void setPayAcount(String payAcount) {
        this.payAcount = payAcount;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }
}