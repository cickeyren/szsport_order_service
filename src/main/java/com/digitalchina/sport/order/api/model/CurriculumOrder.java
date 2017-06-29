package com.digitalchina.sport.order.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * [STRATO MyBatis Generator]
 * Table: curriculum_order
@mbggenerated do_not_delete_during_merge 2017-06-29 11:45:39
 */
public class CurriculumOrder implements Serializable {
    /**
     *   订单编号
     * Column: curriculum_order.id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String id;

    /**
     *   订单流水
     * Column: curriculum_order.order_number
    @mbggenerated 2017-06-29 11:45:39
     */
    private String order_number;

    /**
     *   订单来源
     * Column: curriculum_order.come
    @mbggenerated 2017-06-29 11:45:39
     */
    private String come;

    /**
     *   操作用户
     * Column: curriculum_order.user_id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String user_id;

    /**
     *   学员姓名
     * Column: curriculum_order.student_name
    @mbggenerated 2017-06-29 11:45:39
     */
    private String student_name;

    /**
     *   联系电话
     * Column: curriculum_order.phone
    @mbggenerated 2017-06-29 11:45:39
     */
    private String phone;

    /**
     *   性别 0 男 1 女
     * Column: curriculum_order.gender
    @mbggenerated 2017-06-29 11:45:39
     */
    private Integer gender;

    /**
     * Column: curriculum_order.birthday
    @mbggenerated 2017-06-29 11:45:39
     */
    private Date birthday;

    /**
     *   身份证号
     * Column: curriculum_order.id_card
    @mbggenerated 2017-06-29 11:45:39
     */
    private String id_card;

    /**
     *   其他学生报名信息
     * Column: curriculum_order.other_student_msg
    @mbggenerated 2017-06-29 11:45:39
     */
    private String other_student_msg;

    /**
     *   学校
     * Column: curriculum_order.school
    @mbggenerated 2017-06-29 11:45:39
     */
    private String school;

    /**
     *   课程id
     * Column: curriculum_order.curriculum_id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String curriculum_id;

    /**
     *   班次id
     * Column: curriculum_order.curriculum_class_id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String curriculum_class_id;

    /**
     * Column: curriculum_order.curriculum_class_time_id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String curriculum_class_time_id;

    /**
     * Column: curriculum_order.invalid_time
    @mbggenerated 2017-06-29 11:45:39
     */
    private Date invalid_time;

    /**
     *   订单创建时间
     * Column: curriculum_order.create_time
    @mbggenerated 2017-06-29 11:45:39
     */
    private Date create_time;

    /**
     *   更新时间
     * Column: curriculum_order.update_time
    @mbggenerated 2017-06-29 11:45:39
     */
    private Date update_time;

    /**
     *   订单费用
     * Column: curriculum_order.fee
    @mbggenerated 2017-06-29 11:45:39
     */
    private BigDecimal fee;

    /**
     *   费用说明
     * Column: curriculum_order.fee_msg
    @mbggenerated 2017-06-29 11:45:39
     */
    private String fee_msg;

    /**
     *   支付金额
     * Column: curriculum_order.pay_fee
    @mbggenerated 2017-06-29 11:45:39
     */
    private BigDecimal pay_fee;

    /**
     *   支付方式
     * Column: curriculum_order.pay_type
    @mbggenerated 2017-06-29 11:45:39
     */
    private String pay_type;

    /**
     *   支付人
     * Column: curriculum_order.pay_acount
    @mbggenerated 2017-06-29 11:45:39
     */
    private String pay_acount;

    /**
     *   支付时间
     * Column: curriculum_order.pay_time
    @mbggenerated 2017-06-29 11:45:39
     */
    private Date pay_time;

    /**
     *   订单状态 0未支付，1支付成功，2支付失败，3已退款，4失效订单（取消订单或（未支付超时订单根据失效时间逻辑判断）），5异常订单
     * Column: curriculum_order.status
    @mbggenerated 2017-06-29 11:45:39
     */
    private Integer status;

    /**
     *   确认状态0-未确认，1-已确认
     * Column: curriculum_order.re_status
    @mbggenerated 2017-06-29 11:45:39
     */
    private Integer re_status;

    /**
     *   退款状态1-未退款，1-已退款
     * Column: curriculum_order.refund_status
    @mbggenerated 2017-06-29 11:45:39
     */
    private Integer refund_status;

    /**
     *   指定优惠配置id
     * Column: curriculum_order.discount_configure_id
    @mbggenerated 2017-06-29 11:45:39
     */
    private String discount_configure_id;

    /**
     *   备注
     * Column: curriculum_order.remarks
    @mbggenerated 2017-06-29 11:45:39
     */
    private String remarks;

    /**
     *   续班标识，0-未续班，1-续班
     * Column: curriculum_order.xuban_flag
    @mbggenerated 2017-06-29 11:45:39
     */
    private String xuban_flag;

    /**
     *   续班费用
     * Column: curriculum_order.xuban_fee
    @mbggenerated 2017-06-29 11:45:39
     */
    private BigDecimal xuban_fee;

    /**
     * Table: curriculum_order
    @mbggenerated 2017-06-29 11:45:39
     */
    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number == null ? null : order_number.trim();
    }

    public String getCome() {
        return come;
    }

    public void setCome(String come) {
        this.come = come == null ? null : come.trim();
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id == null ? null : user_id.trim();
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name == null ? null : student_name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card == null ? null : id_card.trim();
    }

    public String getOther_student_msg() {
        return other_student_msg;
    }

    public void setOther_student_msg(String other_student_msg) {
        this.other_student_msg = other_student_msg == null ? null : other_student_msg.trim();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    public String getCurriculum_id() {
        return curriculum_id;
    }

    public void setCurriculum_id(String curriculum_id) {
        this.curriculum_id = curriculum_id == null ? null : curriculum_id.trim();
    }

    public String getCurriculum_class_id() {
        return curriculum_class_id;
    }

    public void setCurriculum_class_id(String curriculum_class_id) {
        this.curriculum_class_id = curriculum_class_id == null ? null : curriculum_class_id.trim();
    }

    public String getCurriculum_class_time_id() {
        return curriculum_class_time_id;
    }

    public void setCurriculum_class_time_id(String curriculum_class_time_id) {
        this.curriculum_class_time_id = curriculum_class_time_id == null ? null : curriculum_class_time_id.trim();
    }

    public Date getInvalid_time() {
        return invalid_time;
    }

    public void setInvalid_time(Date invalid_time) {
        this.invalid_time = invalid_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getFee_msg() {
        return fee_msg;
    }

    public void setFee_msg(String fee_msg) {
        this.fee_msg = fee_msg == null ? null : fee_msg.trim();
    }

    public BigDecimal getPay_fee() {
        return pay_fee;
    }

    public void setPay_fee(BigDecimal pay_fee) {
        this.pay_fee = pay_fee;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type == null ? null : pay_type.trim();
    }

    public String getPay_acount() {
        return pay_acount;
    }

    public void setPay_acount(String pay_acount) {
        this.pay_acount = pay_acount == null ? null : pay_acount.trim();
    }

    public Date getPay_time() {
        return pay_time;
    }

    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRe_status() {
        return re_status;
    }

    public void setRe_status(Integer re_status) {
        this.re_status = re_status;
    }

    public Integer getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(Integer refund_status) {
        this.refund_status = refund_status;
    }

    public String getDiscount_configure_id() {
        return discount_configure_id;
    }

    public void setDiscount_configure_id(String discount_configure_id) {
        this.discount_configure_id = discount_configure_id == null ? null : discount_configure_id.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getXuban_flag() {
        return xuban_flag;
    }

    public void setXuban_flag(String xuban_flag) {
        this.xuban_flag = xuban_flag == null ? null : xuban_flag.trim();
    }

    public BigDecimal getXuban_fee() {
        return xuban_fee;
    }

    public void setXuban_fee(BigDecimal xuban_fee) {
        this.xuban_fee = xuban_fee;
    }
}