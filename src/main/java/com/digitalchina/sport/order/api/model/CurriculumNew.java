package com.digitalchina.sport.order.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * [STRATO MyBatis Generator]
 * Table: curriculum
@mbggenerated do_not_delete_during_merge 2017-06-29 11:33:12
 */
public class CurriculumNew implements Serializable {
    /**
     * Column: curriculum.id
    @mbggenerated 2017-06-29 11:33:12
     */
    private Integer id;

    /**
     *   培训机构id
     * Column: curriculum.training_institutions_id
    @mbggenerated 2017-06-29 11:33:12
     */
    private String training_institutions_id;

    /**
     *   合作商家id
     * Column: curriculum.cooperative_merchant_id
    @mbggenerated 2017-06-29 11:33:12
     */
    private String cooperative_merchant_id;

    /**
     *   图片地址
     * Column: curriculum.img_url
    @mbggenerated 2017-06-29 11:33:12
     */
    private String img_url;

    /**
     *   课程名称
     * Column: curriculum.name
    @mbggenerated 2017-06-29 11:33:12
     */
    private String name;

    /**
     *   教师
     * Column: curriculum.teachers
    @mbggenerated 2017-06-29 11:33:12
     */
    private String teachers;

    /**
     *   培训类型
     * Column: curriculum.train_type
    @mbggenerated 2017-06-29 11:33:12
     */
    private String train_type;

    /**
     *   课程介绍
     * Column: curriculum.content
    @mbggenerated 2017-06-29 11:33:12
     */
    private String content;

    /**
     *   地址
     * Column: curriculum.address
    @mbggenerated 2017-06-29 11:33:12
     */
    private String address;

    /**
     *   报名必填
     * Column: curriculum.enrollment_required
    @mbggenerated 2017-06-29 11:33:12
     */
    private String enrollment_required;

    /**
     *   电话
     * Column: curriculum.phone
    @mbggenerated 2017-06-29 11:33:12
     */
    private String phone;

    /**
     *   报名须知
     * Column: curriculum.enrollment_read
    @mbggenerated 2017-06-29 11:33:12
     */
    private String enrollment_read;

    /**
     * Column: curriculum.create_date
    @mbggenerated 2017-06-29 11:33:12
     */
    private Date create_date;

    /**
     *   推荐操作时间
     * Column: curriculum.recommend_time
    @mbggenerated 2017-06-29 11:33:12
     */
    private Date recommend_time;

    /**
     *   是否设为推荐 1否，2是
     * Column: curriculum.recommend
    @mbggenerated 2017-06-29 11:33:12
     */
    private Integer recommend;

    /**
     *   推荐级别
     * Column: curriculum.recommend_level
    @mbggenerated 2017-06-29 11:33:12
     */
    private Integer recommend_level;

    /**
     *   状态 1下线，2上线
     * Column: curriculum.status
    @mbggenerated 2017-06-29 11:33:12
     */
    private Integer status;

    /**
     *   折扣状态，0-禁用，1-启用
     * Column: curriculum.discount_status
    @mbggenerated 2017-06-29 11:33:12
     */
    private String discount_status;

    /**
     * Table: curriculum
    @mbggenerated 2017-06-29 11:33:12
     */
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTraining_institutions_id() {
        return training_institutions_id;
    }

    public void setTraining_institutions_id(String training_institutions_id) {
        this.training_institutions_id = training_institutions_id == null ? null : training_institutions_id.trim();
    }

    public String getCooperative_merchant_id() {
        return cooperative_merchant_id;
    }

    public void setCooperative_merchant_id(String cooperative_merchant_id) {
        this.cooperative_merchant_id = cooperative_merchant_id == null ? null : cooperative_merchant_id.trim();
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url == null ? null : img_url.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTeachers() {
        return teachers;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers == null ? null : teachers.trim();
    }

    public String getTrain_type() {
        return train_type;
    }

    public void setTrain_type(String train_type) {
        this.train_type = train_type == null ? null : train_type.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getEnrollment_required() {
        return enrollment_required;
    }

    public void setEnrollment_required(String enrollment_required) {
        this.enrollment_required = enrollment_required == null ? null : enrollment_required.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEnrollment_read() {
        return enrollment_read;
    }

    public void setEnrollment_read(String enrollment_read) {
        this.enrollment_read = enrollment_read == null ? null : enrollment_read.trim();
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getRecommend_time() {
        return recommend_time;
    }

    public void setRecommend_time(Date recommend_time) {
        this.recommend_time = recommend_time;
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public Integer getRecommend_level() {
        return recommend_level;
    }

    public void setRecommend_level(Integer recommend_level) {
        this.recommend_level = recommend_level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDiscount_status() {
        return discount_status;
    }

    public void setDiscount_status(String discount_status) {
        this.discount_status = discount_status == null ? null : discount_status.trim();
    }
}