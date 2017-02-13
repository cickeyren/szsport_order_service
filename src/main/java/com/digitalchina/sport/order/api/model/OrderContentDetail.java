package com.digitalchina.sport.order.api.model;


import java.sql.Timestamp;

/**
 * 订单内容详细表
 * 包含条形码验证码
 */
public class OrderContentDetail {
    private String id;
    private String orderCode;//12位确认码
    private String orderBaseId;//所属订单编号
    private Timestamp startTime;//有限期开始时间
    private Timestamp endTime;//有效期截止时间
    private String usedNumber;//已使用次数
    private String remainNumber;//剩余次数
    private String totalNumber;//可用总数
    private String everydayNumber;//每日限次
    private String fieldId;//使用场地
    private String costPrice;//成本价
    private String sellPrice;//售价
    private String takeStatus;//取票状态
    private String checkStatus;//验票状态
    private String status;//状态（0待支付，1待使用，2已使用，3支付失败，4退款）
    private String takeType;//取票类型
    private Timestamp takeTime;//取票时间
    private String checkType;//验票类型
    private Timestamp checkTime;//验票时间
    private String remarks;//备注

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderBaseId() {
        return orderBaseId;
    }

    public void setOrderBaseId(String orderBaseId) {
        this.orderBaseId = orderBaseId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getRemainNumber() {
        return remainNumber;
    }

    public void setRemainNumber(String remainNumber) {
        this.remainNumber = remainNumber;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(String totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getEverydayNumber() {
        return everydayNumber;
    }

    public void setEverydayNumber(String everydayNumber) {
        this.everydayNumber = everydayNumber;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
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

    public String getTakeStatus() {
        return takeStatus;
    }

    public void setTakeStatus(String takeStatus) {
        this.takeStatus = takeStatus;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getTakeType() {
        return takeType;
    }

    public void setTakeType(String takeType) {
        this.takeType = takeType;
    }

    public Timestamp getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Timestamp takeTime) {
        this.takeTime = takeTime;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public Timestamp getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Timestamp checkTime) {
        this.checkTime = checkTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsedNumber() {
        return usedNumber;
    }

    public void setUsedNumber(String usedNumber) {
        this.usedNumber = usedNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}