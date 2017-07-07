package com.digitalchina.sport.order.api.service;

import com.alibaba.fastjson.JSONArray;
import com.digitalchina.common.utils.DateUtil;
import com.digitalchina.common.utils.OrderHelp;
import com.digitalchina.sport.order.api.common.Constants;
import com.digitalchina.sport.order.api.dao.*;
import com.digitalchina.sport.order.api.model.CurriculumClass;
import com.digitalchina.sport.order.api.model.CurriculumClassNew;
import com.digitalchina.sport.order.api.model.CurriculumNew;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang_ on 2017/5/23.
 */
@Service
public class CurriculumService {
    @Autowired
    private CurriculumMapper curriculumMapper;
    @Autowired
    private CurriculumNewMapper curriculumNewMapper;
    @Autowired
    private CurriculumClassNewMapper curriculumClassNewMapper;
    @Autowired
    private CurriculumOrderMapper curriculumOrderMapper;
    @Autowired
    private MyOrderDao myOrderDao;

    @Transactional
    public Map<String, Object> signUp(Map<String, Object> args) throws Exception {

        Date oprDate = new Date();

        Map<String, Object> res = Maps.newHashMap();
        List<CurriculumClass> curriculumClasss = curriculumMapper.getCurriculumClassByCurriculumId(args);
        if (curriculumClasss == null || curriculumClasss.size() <= 0) {
            res.put("code", "004");
            return res;
        }

        CurriculumClass curriculumClass = curriculumClasss.get(0);

        String bm_begin = curriculumClass.getBm_time();
        String bm_end = curriculumClass.getBm_end();
        bm_begin = bm_begin == null ? "" : bm_begin;
        bm_end = bm_end == null ? "" : bm_end;
        if(!DateUtil.isDateStr(bm_begin, "yyyy-MM-dd")){
            bm_begin = "";
        }
        if(!DateUtil.isDateStr(bm_end, "yyyy-MM-dd")){
            bm_end = "";
        }
        String curDateStr = DateUtil.format(oprDate, "yyyyMMdd");
        BigDecimal curDate_bd = new BigDecimal(curDateStr);
        if(!StringUtils.isBlank(bm_begin) && StringUtils.isBlank(bm_end)){
            bm_begin = bm_begin.replace("-", "");
            BigDecimal bm_begin_bd = new BigDecimal(bm_begin);
            if(curDate_bd.compareTo(bm_begin_bd)<0){
                res.put("code", "002");
                return res;
            }
        }
        if(StringUtils.isBlank(bm_begin) && !StringUtils.isBlank(bm_end)){
            bm_end = bm_end.replace("-", "");
            BigDecimal bm_end_bd = new BigDecimal(bm_end);
            if(curDate_bd.compareTo(bm_end_bd)>0){
                res.put("code", "003");
                return res;
            }
        }
        if(!StringUtils.isBlank(bm_begin) && !StringUtils.isBlank(bm_end)){
            bm_begin = bm_begin.replace("-", "");
            bm_end = bm_end.replace("-", "");
            BigDecimal bm_begin_bd = new BigDecimal(bm_begin);
            BigDecimal bm_end_bd = new BigDecimal(bm_end);
            if(curDate_bd.compareTo(bm_begin_bd)<0){
                res.put("code", "002");
                return res;
            }
            if(curDate_bd.compareTo(bm_end_bd)>0){
                res.put("code", "003");
                return res;
            }
        }
        //判断是否有名额
        if (curriculumClass.getSign_up() >= curriculumClass.getMax_people()) {
            res.put("code", "001");
            return res;
        }

        Map<String, Object> checkargs = Maps.newHashMap();
        String xuban_curriculum = curriculumClass.getXuban_curriculum();//获取可享受该班级续报优惠的课程id
        xuban_curriculum = xuban_curriculum == null ? "[]" : xuban_curriculum;
        List<Integer> xuban_curriculums = new Gson().fromJson(xuban_curriculum, List.class);
        if(xuban_curriculums==null || xuban_curriculums.size()>0){
//            checkargs.put("idCard", args.get("idCard"));
            checkargs.put("phone", args.get("phone"));
            checkargs.put("student_name", args.get("studentName"));
            checkargs.put("xuban_curriculums", xuban_curriculums);
            if (curriculumMapper.checkXuban(checkargs) > 0) {//查看是否享受续班优惠
                args.put("xuban_flag", "1");
                args.put("xuban_fee", curriculumClass.getDiscount_fee());
            } else {
                args.put("xuban_flag", "0");
            }
        }else{
            args.put("xuban_flag", "0");
        }
        args.put("fee", curriculumClass.getFee());
        args.put("fee_msg", curriculumClass.getFee_mark());
        String order_number = OrderHelp.getCurriculumOrderNum();
        args.put("id", OrderHelp.getUUID());
        args.put("order_number", order_number);
        curriculumMapper.insertOrder(args);
        curriculumMapper.updataClassTime(args);//更新班次报名人数
        res.put("code", "000");
        res.put("order_number", order_number);
        res.put("id", args.get("id"));
        return res;
    }
    public int updataCurriculumOrder(Map<String, Object> args){
        return curriculumMapper.updataCurriculumOrder(args);
    }

    public List<Map<String, Object>> getCurriculumOrder(Map<String, Object> args) {
        return curriculumMapper.getCurriculumOrder(args);
    }
    public Map<String, Object> getCurriculumOrderbyOrderNumber(Map<String, Object> args) {
        return curriculumMapper.getCurriculumOrderbyOrderNumber(args);
    }
    public int isHaveByParams(Map<String, Object> args){
        return curriculumMapper.isHaveByParams(args);
    }
    public Map<String, Object> getCurriculumOrderDetailByOrderId(String orderId,String userId) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId",orderId);
        params.put("userId",userId);
        return curriculumMapper.getCurriculumOrderDetailByOrderId(params);
    }
    @Transactional
    public int cancelOrderByOrderId(String orderId,String userId,String class_time_id) throws Exception{
        String remarks = "用户取消订单";
        myOrderDao.updateClassTimeSignUp(class_time_id);
        int count = myOrderDao.updateCurriculumOrderStatus(userId,orderId,remarks);

        return count;
    }
    public int cancelOrderByOrderId(String orderId,String userId) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId",orderId);
        params.put("userId",userId);
        params.put("status","4");
        params.put("remarks","用户取消订单");
        return curriculumMapper.cancelOrderByOrderId(params);
    }
    //（未支付超时订单根据失效时间逻辑判断）
    public int updateOrderByOrderTime() throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status","4");
        params.put("remarks","未支付超时订单");
        return curriculumMapper.updateOrderByOrderTime(params);
    }


    public Map<String,Object> getCurriculumDiscount(Map<String, Object> params) {
        Map<String,Object> rtnMap = new HashMap<String,Object>();
        rtnMap.put(Constants.RTN_CODE, Constants.RTN_CODE_FAIL);

        Integer id = (Integer) params.get("id");
        String class_id = (String) params.get("class_id");
        String student_name = (String) params.get("student_name");
        String phone = (String) params.get("phone");
        if(id==null){
            rtnMap.put(Constants.RTN_MSG, "课程id为空！");
            return rtnMap;
        }
        if(StringUtils.isBlank(class_id)){
            rtnMap.put(Constants.RTN_MSG, "课程班次id为空！");
            return rtnMap;
        }
        if(StringUtils.isBlank(student_name)){
            rtnMap.put(Constants.RTN_MSG, "学员姓名为空！");
            return rtnMap;
        }
        if(StringUtils.isBlank(phone)){
            rtnMap.put(Constants.RTN_MSG, "联系手机为空！");
            return rtnMap;
        }

        CurriculumNew curriculum = curriculumNewMapper.selectByPrimaryKey(id);
        if(curriculum==null){
            rtnMap.put(Constants.RTN_MSG, "课程信息不存在！");
            return rtnMap;
        }
        CurriculumClassNew curriculumClass = curriculumClassNewMapper.selectByPrimaryKey(class_id);
        if(curriculumClass==null){
            rtnMap.put(Constants.RTN_MSG, "课程班次信息不存在！");
            return rtnMap;
        }

        String status = this.getCurriculumDiscountStatus(id, class_id, student_name, phone, curriculumClass.getXuban_curriculum());

        rtnMap.put("status", status);
        rtnMap.put(Constants.RTN_CODE, Constants.RTN_CODE_SUCCESS);
        rtnMap.put(Constants.RTN_MSG, "");
        return rtnMap;
    }

    private String getCurriculumDiscountStatus(Integer id, String class_id, String student_name, String phone, String xubanCurriculum) {
        String status = "0";
        if(!StringUtils.isBlank(xubanCurriculum)){
            List<Integer> xubanCurriculumIdList = JSONArray.parseArray(xubanCurriculum, Integer.class);
            if(xubanCurriculumIdList!=null && xubanCurriculumIdList.size()>0){
                int joinCurriculumCount = curriculumOrderMapper.selectJoinCurriculumCount(student_name, phone, xubanCurriculumIdList);
                if(joinCurriculumCount>0){
                    status = "1";
                }
            }
        }
        return status;
    }
}
