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
    private  String out_trade_no;//订单支付时传入的商户订单号,不能和 trade_no同时为空。
    private  String trade_no;//支付宝交易号，和商户订单号不能同时为空
    private  Double refund_amount;//退款金额


    public Double getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(Double refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
}
