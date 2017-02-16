package com.digitalchina.sport.order.api.model;

import java.io.Serializable;
/**
 * 支付宝订单
 *
 */
public class AlipayTradeModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4068082503691633187L;
	/**
	 * 商户网站唯一订单号
	 */
	private String outTradeNo;
	/**
	 * 
	 *支付宝系统中的交易号 
	 */
	private String tradeNo;
	/**
	 * 买家支付宝用户号
	 */
	private String buyerId;
	/**
	 * 商品描述
	 */
	private String body;
	/**
	 * 　通知的发送时间。格式为yyyy-MM-dd HH:mm:ss。
	 */
	private String notifyTime;
	/**
	 * 商品名称
	 */
	private String subject;
	/**
	 *签名方式
	 */
	private String signType;
	/**
	 * 折扣
	 */
	private String discount;
	/**
	 * 买家支付宝账号
	 */
	private String buyerEmail;
	/**
	 * 交易创建时间
	 */
	private String gmtCreate;
	/**
	 * 商品单价
	 */
	private String price;
	/**
	 * 商品总价
	 */
	private String totalFee;
	/**
	 * 卖家支付宝用户号
	 */
	private String sellerId;
	/**
	 * '用户ID
	 */
	private String userId;

	/**
	 * WAIT_BUYER_PAY:交易创建，等待买家付款。TRADE_CLOSED:在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。TRADE_SUCCESS:交易成功，且可对该交易做操作，如：多级分润、退款等。TRADE_FINISHED:交易成功且结束，即不可再做任何操作。
	 */
	private String alipayStatus;
	/**
	 * 创建时间
	 */
	private String createTime;

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	public String getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAlipayStatus() {
		return alipayStatus;
	}

	public void setAlipayStatus(String alipayStatus) {
		this.alipayStatus = alipayStatus;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
