package com.digitalchina.sport.order.api.controller;

import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.DateUtil;
import com.digitalchina.common.utils.OrderHelp;
import com.digitalchina.sport.order.api.common.Constants;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.service.CurriculumService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang_ on 2017/5/23.
 */
@RestController
@RequestMapping("/order/api/curriculumOrder/")
public class CurriculumOrderController {

    public static final Logger logger = LoggerFactory.getLogger(CurriculumOrderController.class);
    @Autowired
    private PropertyConfig config;
    @Autowired
    private CurriculumService curriculumService;
    /**
     *报名 生成订单
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
        }
//        if(StringUtils.isEmpty(idCard)){
//            return RtnData.fail("身份证为空");
//        }

        if(!org.apache.commons.lang.StringUtils.isBlank(birthday) && !DateUtil.isDateStr(birthday, "yyyy-MM-dd")){
            return RtnData.fail("出生日期格式错误");
        }
        if(gender!=null && gender != 0 && gender != 1){
            return RtnData.fail("性别代码错误");
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
            String code = (String) res.get("code");
            if ("001".equals(code)){
                return RtnData.fail("没有名额");
            }else if("002".equals(code)){
                return RtnData.fail("课程班次报名时间未开始");
            }else if("003".equals(code)){
                return RtnData.fail("课程班次报名时间已结束");
            }else if("004".equals(code)){
                return RtnData.fail("课程班次不存在");
            }else {
                return RtnData.ok(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RtnData.fail("未知异常");
        }
    }

    /**
     * 根据用信息获取订单
     * @param userId
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "getCurriculumOrder.json", method = RequestMethod.POST)
    @ResponseBody
    public RtnData getCurriculumOrder(String userId,String status,
                                      @RequestParam(value = "pageNum",defaultValue = "0" , required = false) int pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10", required = false) int pageSize) {
        try {
            Map<String,Object> args = Maps.newHashMap();
            args.put("userId",userId);
            args.put("status",status);
/*            args.put("pageNum",(pageNum-1)*pageSize);
            args.put("pageSize",pageSize);*/
            if(pageNum == 0){
                args.put("startIndex",pageNum);
            }else {
                args.put("startIndex", (pageNum - 1) * pageSize);
            }
            args.put("pageSize",pageSize);
            List<Map<String,Object>> orders = curriculumService.getCurriculumOrder(args);
            curriculumService.updateOrderByOrderTime();
            return RtnData.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取培训订单失败");
            return RtnData.fail("获取培训订单失败");
        }
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @RequestMapping(value = "curriculumOrderDetail.json", method = RequestMethod.GET)
    @ResponseBody
    public RtnData curriculumOrderDetail(@RequestParam(required = true) String orderId,
                                         @RequestParam(required = true) String userId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        Map<String,Object> reqMap=new HashMap<String, Object>();
        try {
            if (curriculumService.isHaveByParams(params)>0){
                //订单详情
                reqMap.put("orderDetails", curriculumService.getCurriculumOrderDetailByOrderId(orderId,userId));

                return RtnData.ok(reqMap);
            }else {
                return RtnData.fail("没有查到该培训订单!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取培训订单详情失败");
            return RtnData.fail("获取培训订单详情失败");
        }
    }
    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @RequestMapping(value="cancelOrderByOrderId.json")
    @ResponseBody
    public RtnData<Object> cancelOrderByOrderId(@RequestParam(value = "orderId", required = true) String orderId,
                                                @RequestParam(required = true) String userId){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        Map<String, Object> retMap = new HashMap<String, Object>();
        try {
            if (curriculumService.isHaveByParams(params)>0){
                Map<String, Object> orderDetail = curriculumService.getCurriculumOrderDetailByOrderId(orderId,userId);
                String status = orderDetail.get("status").toString();
                //订单状态 0未支付，1支付成功，2支付失败，3已退款，4失效订单（取消订单或（未支付超时订单根据失效时间逻辑判断）），5异常订单',
                if(status.equals("0")){
                    if (curriculumService.cancelOrderByOrderId(orderId,userId)>0){
                        return RtnData.ok("取消培训订单成功");
                    }else {
                        return RtnData.fail("取消培训订单失败");
                    }
                }else if(status.equals("1")){
                    return RtnData.fail("该培训订单已支付");
                }else if(status.equals("2")){
                    return RtnData.fail("该培训订单支付失败");
                }else if(status.equals("3")){
                    return RtnData.fail("该培训订单已退款");
                }else if(status.equals("4")){
                    return RtnData.fail("该培训订单已失效");
                }else if(status.equals("5")){
                    return RtnData.fail("该培训订单异常");
                }else {
                    return RtnData.fail("取消培训订单失败!");
                }
            }else {
                return RtnData.fail("没有查到该培训订单!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取消培训订单失败",e);
            return RtnData.fail("取消培训订单失败!");
        }
    }


    /**
     * 根据手机号和姓名获取是否存在续班优惠
     * @return
     */
    @RequestMapping(value = "/getCurriculumDiscount.json", method = RequestMethod.GET)
    @ResponseBody
    public RtnData getCurriculumDiscount(@RequestParam Integer id, @RequestParam String class_id, @RequestParam String student_name, @RequestParam String phone){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("class_id", class_id);
        params.put("student_name", student_name);
        params.put("phone", phone);

        try {
            Map<String, Object> curriculumDiscount = curriculumService.getCurriculumDiscount(params);

            if (!Constants.RTN_CODE_SUCCESS.equals(curriculumDiscount.get(Constants.RTN_CODE))) {
                return RtnData.fail("999999", (String) curriculumDiscount.get(Constants.RTN_MSG));
            }else{
                curriculumDiscount.remove(Constants.RTN_CODE);
                curriculumDiscount.remove(Constants.RTN_MSG);
                return RtnData.ok(curriculumDiscount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("根据手机号和姓名获取是否存在续班优惠失败",e);
            return RtnData.fail("根据手机号和姓名获取是否存在续班优惠失败");
        }
    }
}
