package com.digitalchina.sport.order.api.controller;

import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.OrderHelp;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.service.CurriculumService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by yang_ on 2017/5/23.
 */
@RestController
@RequestMapping("/order/api/curriculumOrder/")
public class CurriculumOrderController {
    @Autowired
    private PropertyConfig config;
    @Autowired
    private CurriculumService curriculumService;
    /**
     *
     * @param curriculumClassId 班次id
     * @param classTimeId 班次时间段id
     * @param curriculumId 课程id
     * @param studentName 学生姓名
     * @param phone 联系方式
     * @param gender 性别
     * @param birthday 出生年月
     * @param idCard 身份证
     * @param school 学校
     * @param otherStudentMsg 其他报名信息
     * @return
     */
    @RequestMapping(value = "signUp.json", method = RequestMethod.POST)
    @ResponseBody
    public RtnData signUp(String userId, String curriculumClassId, String classTimeId, String curriculumId,
                          String studentName, String phone, Integer gender, String birthday,
                          String idCard, String school, String otherStudentMsg, String come) {
        Map<String, Object> args = Maps.newHashMap();
        if(StringUtils.isEmpty(curriculumClassId)){
            return RtnData.fail("班次id为空");
        }
        if(StringUtils.isEmpty(classTimeId)){
            return RtnData.fail("班次时间段id为空");
        }
        if(StringUtils.isEmpty(curriculumId)){
            return RtnData.fail("课程id为空");
        }
        if(StringUtils.isEmpty(studentName)){
            return RtnData.fail("学生姓名为空");
        }
        if(StringUtils.isEmpty(phone)){
            return RtnData.fail("联系方式为空");
        }if(StringUtils.isEmpty(idCard)){
            return RtnData.fail("身份证为空");
        }
        args.put("curriculumClassId",curriculumClassId);
        args.put("classTimeId",classTimeId);
        args.put("curriculumId",curriculumId);
        args.put("studentName",studentName);
        args.put("phone",phone);
        args.put("gender",gender);
        args.put("birthday",birthday);
        args.put("idCard",idCard);
        args.put("school",school);
        args.put("otherStudentMsg",otherStudentMsg);
        args.put("userId",userId);
        args.put("come",come);
        args.put("invalid_time", config.INVALID_TIME);
        try {
            Map<String,Object> res = curriculumService.signUp(args);
            if (res.get("code").toString().equals("001")){
                return RtnData.fail("没有名额");
            }else {
                return RtnData.ok(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RtnData.fail("未知异常");
        }
    }
    @RequestMapping(value = "getCurriculumOrder.json", method = RequestMethod.POST)
    @ResponseBody
    public RtnData getCurriculumOrder(String userId,String status,Integer pageNum,Integer pageSize) {
        Map<String,Object> args = Maps.newHashMap();
        args.put("userId",userId);
        args.put("status",status);
        args.put("pageNum",(pageNum-1)*pageSize);
        args.put("pageSize",pageSize);
        List<Map<String,Object>> orders = curriculumService.getCurriculumOrder(args);
        return RtnData.ok(orders);
    }
}
