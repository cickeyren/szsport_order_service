package com.digitalchina.sport.order.api.service;

import com.alibaba.fastjson.JSONArray;
import com.digitalchina.common.utils.OrderHelp;
import com.digitalchina.sport.order.api.common.Constants;
import com.digitalchina.sport.order.api.dao.CurriculumClassNewMapper;
import com.digitalchina.sport.order.api.dao.CurriculumMapper;
import com.digitalchina.sport.order.api.dao.CurriculumNewMapper;
import com.digitalchina.sport.order.api.dao.CurriculumOrderMapper;
import com.digitalchina.sport.order.api.model.CurriculumClass;
import com.digitalchina.sport.order.api.model.CurriculumClassNew;
import com.digitalchina.sport.order.api.model.CurriculumNew;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Map<String, Object> signUp(Map<String, Object> args) throws Exception {
        Map<String, Object> res = Maps.newHashMap();
        List<CurriculumClass> curriculumClasss = curriculumMapper.getCurriculumClassByCurriculumId(args);
        if (curriculumClasss.size() == 1) {
            CurriculumClass curriculumClass = curriculumClasss.get(0);
            if (curriculumClass.getSign_up() >= curriculumClass.getMax_people()) {//判断是否有名额
                res.put("code", "001");//没有名额了
            }
            Map<String, Object> checkargs = Maps.newHashMap();
            String xuban_curriculum = curriculumClass.getXuban_curriculum();//获取可享受该班级续报优惠的课程id
            List<Integer> xuban_curriculums = new Gson().fromJson(xuban_curriculum, List.class);
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
        }
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
