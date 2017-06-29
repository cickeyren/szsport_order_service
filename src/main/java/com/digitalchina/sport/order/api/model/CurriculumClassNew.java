package com.digitalchina.sport.order.api.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * [STRATO MyBatis Generator]
 * Table: curriculum_class
@mbggenerated do_not_delete_during_merge 2017-06-29 11:33:13
 */
public class CurriculumClassNew implements Serializable {
    /**
     * Column: curriculum_class.id
    @mbggenerated 2017-06-29 11:33:13
     */
    private String id;

    /**
     * Column: curriculum_class.curriculum_id
    @mbggenerated 2017-06-29 11:33:13
     */
    private Integer curriculum_id;

    /**
     *   班级名称
     * Column: curriculum_class.name
    @mbggenerated 2017-06-29 11:33:13
     */
    private String name;

    /**
     *   课时
     * Column: curriculum_class.class_long
    @mbggenerated 2017-06-29 11:33:13
     */
    private String class_long;

    /**
     *   上课次数
     * Column: curriculum_class.class_times
    @mbggenerated 2017-06-29 11:33:13
     */
    private Integer class_times;

    /**
     *   每班人数
     * Column: curriculum_class.student_num
    @mbggenerated 2017-06-29 11:33:13
     */
    private Integer student_num;

    /**
     *   限报人数
     * Column: curriculum_class.max_num
    @mbggenerated 2017-06-29 11:33:13
     */
    private Integer max_num;

    /**
     *   上课日期
     * Column: curriculum_class.lean_time
    @mbggenerated 2017-06-29 11:33:13
     */
    private String lean_time;

    /**
     *   报名开始时间
     * Column: curriculum_class.bm_time
    @mbggenerated 2017-06-29 11:33:13
     */
    private String bm_time;

    /**
     *   报名截止时间
     * Column: curriculum_class.bm_end
    @mbggenerated 2017-06-29 11:33:13
     */
    private String bm_end;

    /**
     *   招生对象
     * Column: curriculum_class.target
    @mbggenerated 2017-06-29 11:33:13
     */
    private String target;

    /**
     *   授课内容
     * Column: curriculum_class.content
    @mbggenerated 2017-06-29 11:33:13
     */
    private String content;

    /**
     * Column: curriculum_class.fee_code
    @mbggenerated 2017-06-29 11:33:13
     */
    private String fee_code;

    /**
     *   优惠费用金额
     * Column: curriculum_class.discount_fee
    @mbggenerated 2017-06-29 11:33:13
     */
    private BigDecimal discount_fee;

    /**
     *   费用
     * Column: curriculum_class.fee
    @mbggenerated 2017-06-29 11:33:13
     */
    private BigDecimal fee;

    /**
     *   费用备注
     * Column: curriculum_class.fee_mark
    @mbggenerated 2017-06-29 11:33:13
     */
    private String fee_mark;

    /**
     *   上课时间类型 1固定，2常年，3需预约
     * Column: curriculum_class.leantime_type
    @mbggenerated 2017-06-29 11:33:13
     */
    private String leantime_type;

    /**
     *   状态，1禁用 2启用
     * Column: curriculum_class.status
    @mbggenerated 2017-06-29 11:33:13
     */
    private Integer status;

    /**
     *   享受续报优惠课程id
     * Column: curriculum_class.continue_curriculum_id
    @mbggenerated 2017-06-29 11:33:13
     */
    private String continue_curriculum_id;

    /**
     *   续班费
     * Column: curriculum_class.xubanfee
    @mbggenerated 2017-06-29 11:33:13
     */
    private BigDecimal xubanfee;

    /**
     *   享受续班优惠的课程id
     * Column: curriculum_class.xuban_curriculum
    @mbggenerated 2017-06-29 11:33:13
     */
    private String xuban_curriculum;

    /**
     * Table: curriculum_class
    @mbggenerated 2017-06-29 11:33:13
     */
    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getCurriculum_id() {
        return curriculum_id;
    }

    public void setCurriculum_id(Integer curriculum_id) {
        this.curriculum_id = curriculum_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getClass_long() {
        return class_long;
    }

    public void setClass_long(String class_long) {
        this.class_long = class_long == null ? null : class_long.trim();
    }

    public Integer getClass_times() {
        return class_times;
    }

    public void setClass_times(Integer class_times) {
        this.class_times = class_times;
    }

    public Integer getStudent_num() {
        return student_num;
    }

    public void setStudent_num(Integer student_num) {
        this.student_num = student_num;
    }

    public Integer getMax_num() {
        return max_num;
    }

    public void setMax_num(Integer max_num) {
        this.max_num = max_num;
    }

    public String getLean_time() {
        return lean_time;
    }

    public void setLean_time(String lean_time) {
        this.lean_time = lean_time == null ? null : lean_time.trim();
    }

    public String getBm_time() {
        return bm_time;
    }

    public void setBm_time(String bm_time) {
        this.bm_time = bm_time == null ? null : bm_time.trim();
    }

    public String getBm_end() {
        return bm_end;
    }

    public void setBm_end(String bm_end) {
        this.bm_end = bm_end == null ? null : bm_end.trim();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target == null ? null : target.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getFee_code() {
        return fee_code;
    }

    public void setFee_code(String fee_code) {
        this.fee_code = fee_code == null ? null : fee_code.trim();
    }

    public BigDecimal getDiscount_fee() {
        return discount_fee;
    }

    public void setDiscount_fee(BigDecimal discount_fee) {
        this.discount_fee = discount_fee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getFee_mark() {
        return fee_mark;
    }

    public void setFee_mark(String fee_mark) {
        this.fee_mark = fee_mark == null ? null : fee_mark.trim();
    }

    public String getLeantime_type() {
        return leantime_type;
    }

    public void setLeantime_type(String leantime_type) {
        this.leantime_type = leantime_type == null ? null : leantime_type.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContinue_curriculum_id() {
        return continue_curriculum_id;
    }

    public void setContinue_curriculum_id(String continue_curriculum_id) {
        this.continue_curriculum_id = continue_curriculum_id == null ? null : continue_curriculum_id.trim();
    }

    public BigDecimal getXubanfee() {
        return xubanfee;
    }

    public void setXubanfee(BigDecimal xubanfee) {
        this.xubanfee = xubanfee;
    }

    public String getXuban_curriculum() {
        return xuban_curriculum;
    }

    public void setXuban_curriculum(String xuban_curriculum) {
        this.xuban_curriculum = xuban_curriculum == null ? null : xuban_curriculum.trim();
    }
}