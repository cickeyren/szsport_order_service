package com.digitalchina.sport.order.api.model;

import java.math.BigDecimal;

public class CurriculumClass {

    private String id;

    private Integer curriculum_id;

    /**
     * 班级名称
     */
    private String name;

    /**
     * 课时
     */
    private String class_long;

    /**
     * 上课次数
     */
    private Integer class_times;
    private Integer status;

    /**
     * 每班人数
     */
    private Integer student_num;

    /**
     * 限报人数
     */
    private Integer max_people;
    private Integer sign_up;

    /**
     * 上课日期
     */
    private String lean_time;
    private String leantime_type;

    /**
     * 报名开始时间
     */
    private String bm_time;

    /**
     * 报名截止时间
     */
    private String bm_end;

    /**
     * 招生对象
     */
    private String target;

    /**
     * 授课内容
     */
    private String content;

    private String fee_code;

    /**
     * 费用
     */
    private BigDecimal fee;
    /**
     * 续报费用
     */
    private BigDecimal discount_fee;

    /**
     * 费用备注
     */
    private String fee_mark;
    /**
     * 享受续班优惠课程id
     */
    private String xuban_curriculum;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return curriculum_id
     */
    public Integer getCurriculum_id() {
        return curriculum_id;
    }

    /**
     * @param curriculum_id
     */
    public void setCurriculum_id(Integer curriculum_id) {
        this.curriculum_id = curriculum_id;
    }

    /**
     * 获取班级名称
     *
     * @return name - 班级名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置班级名称
     *
     * @param name 班级名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取课时
     *
     * @return class_long - 课时
     */
    public String getClass_long() {
        return class_long;
    }

    /**
     * 设置课时
     *
     * @param class_long 课时
     */
    public void setClass_long(String class_long) {
        this.class_long = class_long == null ? null : class_long.trim();
    }

    /**
     * 获取上课次数
     *
     * @return class_times - 上课次数
     */
    public Integer getClass_times() {
        return class_times;
    }

    /**
     * 设置上课次数
     *
     * @param class_times 上课次数
     */
    public void setClass_times(Integer class_times) {
        this.class_times = class_times;
    }

    /**
     * 获取每班人数
     *
     * @return student_num - 每班人数
     */
    public Integer getStudent_num() {
        return student_num;
    }

    /**
     * 设置每班人数
     *
     * @param student_num 每班人数
     */
    public void setStudent_num(Integer student_num) {
        this.student_num = student_num;
    }

    /**
     * 获取上课日期
     *
     * @return lean_time - 上课日期
     */
    public String getLean_time() {
        return lean_time;
    }

    /**
     * 设置上课日期
     *
     * @param lean_time 上课日期
     */
    public void setLean_time(String lean_time) {
        this.lean_time = lean_time == null ? null : lean_time.trim();
    }

    /**
     * 获取报名开始时间
     *
     * @return bm_time - 报名开始时间
     */
    public String getBm_time() {
        return bm_time;
    }

    /**
     * 设置报名开始时间
     *
     * @param bm_time 报名开始时间
     */
    public void setBm_time(String bm_time) {
        this.bm_time = bm_time == null ? null : bm_time.trim();
    }

    /**
     * 获取报名截止时间
     *
     * @return bm_end - 报名截止时间
     */
    public String getBm_end() {
        return bm_end;
    }

    /**
     * 设置报名截止时间
     *
     * @param bm_end 报名截止时间
     */
    public void setBm_end(String bm_end) {
        this.bm_end = bm_end == null ? null : bm_end.trim();
    }

    /**
     * 获取招生对象
     *
     * @return target - 招生对象
     */
    public String getTarget() {
        return target;
    }

    /**
     * 设置招生对象
     *
     * @param target 招生对象
     */
    public void setTarget(String target) {
        this.target = target == null ? null : target.trim();
    }

    /**
     * 获取授课内容
     *
     * @return content - 授课内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置授课内容
     *
     * @param content 授课内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * @return fee_code
     */
    public String getFee_code() {
        return fee_code;
    }

    /**
     * @param fee_code
     */
    public void setFee_code(String fee_code) {
        this.fee_code = fee_code == null ? null : fee_code.trim();
    }

    /**
     * 获取费用
     *
     * @return fee - 费用
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 设置费用
     *
     * @param fee 费用
     */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    /**
     * 获取费用备注
     *
     * @return fee_mark - 费用备注
     */
    public String getFee_mark() {
        return fee_mark;
    }

    /**
     * 设置费用备注
     *
     * @param fee_mark 费用备注
     */
    public void setFee_mark(String fee_mark) {
        this.fee_mark = fee_mark == null ? null : fee_mark.trim();
    }

    public String getLeantime_type() {
        return leantime_type;
    }

    public void setLeantime_type(String leantime_type) {
        this.leantime_type = leantime_type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMax_people() {
        return max_people;
    }

    public Integer getSign_up() {
        return sign_up;
    }

    public void setMax_people(Integer max_people) {
        this.max_people = max_people;
    }

    public void setSign_up(Integer sign_up) {
        this.sign_up = sign_up;
    }

    public BigDecimal getDiscount_fee() {
        return discount_fee;
    }

    public void setDiscount_fee(BigDecimal discount_fee) {
        this.discount_fee = discount_fee;
    }

    public String getXuban_curriculum() {
        return xuban_curriculum;
    }

    public void setXuban_curriculum(String xuban_curriculum) {
        this.xuban_curriculum = xuban_curriculum;
    }
}