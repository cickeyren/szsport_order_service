package com.digitalchina.sport.order.api.model;

/**
 * （一句话描述）
 * 作用：
 *
 * @author 王功文
 * @date 2017/3/10 10:17
 * @see
 */
public class AlipayRefundInfo {

    private  String batch_no;//退款批次号   //订单编号
    private  String refund_date;//退款请求时间
    private  String batch_num;//退款总笔数
    private  String detail_data;//单笔数据集
    private  Double refund_amount;//退款金额
    private  String out_trade_no;//退款单号
    private  String trade_no;//订单流水号
    private  String reason;//退款原因

    public AlipayRefundInfo() {
    }

    public Double getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(Double refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getRefund_date() {
        return refund_date;
    }

    public void setRefund_date(String refund_date) {
        this.refund_date = refund_date;
    }

    public String getBatch_num() {
        return batch_num;
    }

    public void setBatch_num(String batch_num) {
        this.batch_num = batch_num;
    }

    public String getDetail_data() {
        return detail_data;
    }

    public void setDetail_data(String detail_data) {
        this.detail_data = detail_data;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }
}
