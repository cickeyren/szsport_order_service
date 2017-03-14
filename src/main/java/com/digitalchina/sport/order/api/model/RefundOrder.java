package com.digitalchina.sport.order.api.model;

import java.util.Date;
import javax.persistence.*;

public class RefundOrder {
    /**
     * 退单编号
     */
    private String id;

    /**
     * 主订单编号
     */
    private String order_base_id;

    /**
     * 子订单编号
     */
    private String order_id;

    /**
     * 子订单确认码
     */
    private String order_code;

    /**
     * 有效期截止时间
     */
    private Date end_time;

    /**
     * 场地
     */
    private String field_name;

    /**
     * 场次
     */
    private String field_num;

    /**
     * 成本价
     */
    private String cost_price;

    /**
     * 销售价
     */
    private String sell_price;

    /**
     * 订单来源
     */
    private String order_from;

    /**
     * 退款申请时间
     */
    private Date refund_application_time;

    /**
     * 退款时间
     */
    private Date refund_time;

    /**
     * 退款金额
     */
    private String refund_monery;

    /**
     * 退款方式
     */
    private String refund_method;

    /**
     * 获取退单编号
     *
     * @return id - 退单编号
     */
    public String getId() {
        return id;
    }

    /**
     * 设置退单编号
     *
     * @param id 退单编号
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取主订单编号
     *
     * @return order_base_id - 主订单编号
     */
    public String getOrder_base_id() {
        return order_base_id;
    }

    /**
     * 设置主订单编号
     *
     * @param order_base_id 主订单编号
     */
    public void setOrder_base_id(String order_base_id) {
        this.order_base_id = order_base_id == null ? null : order_base_id.trim();
    }

    /**
     * 获取子订单编号
     *
     * @return order_id - 子订单编号
     */
    public String getOrder_id() {
        return order_id;
    }

    /**
     * 设置子订单编号
     *
     * @param order_id 子订单编号
     */
    public void setOrder_id(String order_id) {
        this.order_id = order_id == null ? null : order_id.trim();
    }

    /**
     * 获取子订单确认码
     *
     * @return order_code - 子订单确认码
     */
    public String getOrder_code() {
        return order_code;
    }

    /**
     * 设置子订单确认码
     *
     * @param order_code 子订单确认码
     */
    public void setOrder_code(String order_code) {
        this.order_code = order_code == null ? null : order_code.trim();
    }

    /**
     * 获取有效期截止时间
     *
     * @return end_time - 有效期截止时间
     */
    public Date getEnd_time() {
        return end_time;
    }

    /**
     * 设置有效期截止时间
     *
     * @param end_time 有效期截止时间
     */
    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    /**
     * 获取场地
     *
     * @return field_name - 场地
     */
    public String getField_name() {
        return field_name;
    }

    /**
     * 设置场地
     *
     * @param field_name 场地
     */
    public void setField_name(String field_name) {
        this.field_name = field_name == null ? null : field_name.trim();
    }

    /**
     * 获取场次
     *
     * @return field_num - 场次
     */
    public String getField_num() {
        return field_num;
    }

    /**
     * 设置场次
     *
     * @param field_num 场次
     */
    public void setField_num(String field_num) {
        this.field_num = field_num == null ? null : field_num.trim();
    }

    /**
     * 获取成本价
     *
     * @return cost_price - 成本价
     */
    public String getCost_price() {
        return cost_price;
    }

    /**
     * 设置成本价
     *
     * @param cost_price 成本价
     */
    public void setCost_price(String cost_price) {
        this.cost_price = cost_price == null ? null : cost_price.trim();
    }

    /**
     * 获取销售价
     *
     * @return sell_price - 销售价
     */
    public String getSell_price() {
        return sell_price;
    }

    /**
     * 设置销售价
     *
     * @param sell_price 销售价
     */
    public void setSell_price(String sell_price) {
        this.sell_price = sell_price == null ? null : sell_price.trim();
    }

    /**
     * 获取订单来源
     *
     * @return order_from - 订单来源
     */
    public String getOrder_from() {
        return order_from;
    }

    /**
     * 设置订单来源
     *
     * @param order_from 订单来源
     */
    public void setOrder_from(String order_from) {
        this.order_from = order_from == null ? null : order_from.trim();
    }

    /**
     * 获取退款申请时间
     *
     * @return refund_application_time - 退款申请时间
     */
    public Date getRefund_application_time() {
        return refund_application_time;
    }

    /**
     * 设置退款申请时间
     *
     * @param refund_application_time 退款申请时间
     */
    public void setRefund_application_time(Date refund_application_time) {
        this.refund_application_time = refund_application_time;
    }

    /**
     * 获取退款时间
     *
     * @return refund_time - 退款时间
     */
    public Date getRefund_time() {
        return refund_time;
    }

    /**
     * 设置退款时间
     *
     * @param refund_time 退款时间
     */
    public void setRefund_time(Date refund_time) {
        this.refund_time = refund_time;
    }

    /**
     * 获取退款金额
     *
     * @return refund_monery - 退款金额
     */
    public String getRefund_monery() {
        return refund_monery;
    }

    /**
     * 设置退款金额
     *
     * @param refund_monery 退款金额
     */
    public void setRefund_monery(String refund_monery) {
        this.refund_monery = refund_monery == null ? null : refund_monery.trim();
    }

    /**
     * 获取退款方式
     *
     * @return refund_method - 退款方式
     */
    public String getRefund_method() {
        return refund_method;
    }

    /**
     * 设置退款方式
     *
     * @param refund_method 退款方式
     */
    public void setRefund_method(String refund_method) {
        this.refund_method = refund_method == null ? null : refund_method.trim();
    }
}