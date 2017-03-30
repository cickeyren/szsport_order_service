package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.BarcodeUtil;
import com.digitalchina.common.utils.MyOrcode;
import com.digitalchina.common.utils.StringUtil;
import com.digitalchina.sport.order.api.service.EquipmentService;
import com.digitalchina.sport.order.api.service.FieldOrderService;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对通票的接口
 * 其他接口不能复用
 * 我的订单control
 */
@RestController
@RequestMapping("/order/api/myOrder/")
public class MyOrderController {

    public static final Logger logger = LoggerFactory.getLogger(MyOrderController.class);
    @Autowired
    private MyOrderService myOrderService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private FieldOrderService fieldOrderService;

    /**
     * 根据用户id，按照状态查询所有订单
     * @return
     */
    @RequestMapping(value="getMyAllOrder.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> getMyAllOrder(@RequestParam(value = "pageIndex",defaultValue = "0" , required = false) int pageIndex,
                                         @RequestParam(value = "pageSize",defaultValue = "10", required = false) int pageSize,
                                         @RequestParam(value = "userId", required = false) String userId,
                                         @RequestParam(value = "status", required = false) String status,
                                         @RequestParam(value = "refundStatus", required = false) String refundStatus,
                                         @RequestParam(value = "orderNumber", required = false) String orderNumber) {
        Map<String,Object> map = new HashMap<String, Object>();
        if(pageIndex == 0){
            map.put("start",pageIndex);
        }else {
            map.put("start", (pageIndex - 1) * pageSize);
        }
        map.put("pageSize",pageSize);
        map.put("userId",userId);
        map.put("status",status);
        map.put("refundStatus",refundStatus);
        map.put("orderNumber",orderNumber);
        try {
            myOrderService.updateAllOrderStatus("超过十分钟的失效订单");
            //超过十分钟的失效订单
            List<Map<String,Object>> list = myOrderService.getAllOrderByUserId(map);
            int count = myOrderService.getCountByUserId(map);
            Map<String,Object> reqMap=new HashMap<String, Object>();
            reqMap.put("list",list);
            reqMap.put("count",count);
            return RtnData.ok(reqMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询订单列表失败",e);
            return RtnData.fail("查询订单列表失败");
        }

    }

    /**
     * 获取一个订单中的所有子订单的详情
     * @return
     */
    @RequestMapping(value="getOrderDetails.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> getOrderDetails(@RequestParam(value = "userId", required = true) String userId,
                                           @RequestParam(value = "orderId", required = true) String orderId){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("orderId",orderId);
        try {
            List<Map<String,Object>> list = myOrderService.getTotalOrderByUserIdAndOrderId(map);
            int count = myOrderService.getCountByOrderId(orderId);
            Map<String,Object> orderDetails = myOrderService.getOrderDetails(orderId);
            Map<String,Object> reqMap=new HashMap<String, Object>();
            reqMap.put("list",list);
            reqMap.put("count",count);
            reqMap.put("orderDetails",orderDetails);
            return RtnData.ok(reqMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询订单包含的字单详情失败",e);
            return RtnData.fail("查询订单包含的字单详情失败");
        }
    }

    /**
     * 将以上接口返回信息中的内容存入订单详情表
     * 生成订单
     * @param orderJson
     * @return
     */
    @RequestMapping(value="createOrder.json",method = RequestMethod.POST)
    @ResponseBody
    public RtnData<Object> createOrder(@RequestBody String orderJson){
        Map<String,Object> retMap = new HashMap<String, Object>();
        JSONObject orderJsonObject = JSON.parseObject(orderJson);
        try {
            retMap = myOrderService.createOrder(orderJsonObject);
            return RtnData.ok(retMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("下单失败!",e);
            return RtnData.fail("下单失败!");
        }
    }

    /**
     * 根据验票码获取条形码图片
     */
    @RequestMapping(value="printBarCode.img",method = RequestMethod.GET)
    @ResponseBody
    public void printBarCode(@RequestParam(value = "orderCode", required = true) String orderCode, HttpServletResponse response) throws Exception {
        String barCode = "";
        if(myOrderService.isHaveByOrderCode(orderCode) >0){
            barCode = orderCode;
        }else{
            barCode = "";
        }
        try {
            //5、图形写给浏览器
            response.setContentType("image/jpeg");
            //发头控制浏览器不要缓存
            response.setDateHeader("expries", -1);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            BarcodeUtil.generate(barCode,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("生成条形码失败!",e);
        }
    }

    /**
     * 只做查询操作
     * 根据验票码验证是否存在，存在则返回该票的详情，不存在则返回提示信息
     * @param orderCode
     * @return
     */
    @RequestMapping(value="checkTicketByOrderCode.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> checkTicketByOrderCode(@RequestParam(value = "orderCode", required = true) String orderCode){
        Map<String,Object> retMap = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("orderCode",orderCode);
        try {
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                Map<String,Object> orderDetailsMap = myOrderService.getOrderDetailByOrderCode(orderCode);
                String takeStatus = "";
                if (!StringUtil.isEmpty(orderDetailsMap.get("take_status"))){
                    takeStatus = orderDetailsMap.get("take_status").toString();
                }
                if(takeStatus.equals("1")){
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","该票已取过票,请核实!");
                    return RtnData.fail(retMap);
                }else {
                    //判断是否支付
                    Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                    checkReturnMap = myOrderService.takeTicket(orderCode);
                    String returnKey = checkReturnMap.get("returnKey").toString();
                    if(returnKey.equals("true")){
                        //未取票状态，可以取票
                        retMap.put("returnKey","true");
                        retMap.put("returnMessage","可以取票!");
                        retMap.put("orderDetailsMap",myOrderService.getOrderDetailByOrderCode(orderCode));
                        return RtnData.ok(retMap);
                    }else{
                        return RtnData.fail(checkReturnMap);
                    }
                }
            }else {
                retMap.put("returnKey","false");
                retMap.put("returnMessage","没有查询到符合条件的订单记录!");
                return RtnData.fail(retMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败",e);
            return RtnData.fail("查询失败!");
        }
    }

    /**
     * 取票操作
     * 根据验票码验证是否存在，存在则返回该票的详情,并修改取票状态，表示取票成功，不存在则返回提示信息
     * @param orderCode
     * @param
     * @return
     */
    @RequestMapping(value="takeTicket.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> takeTicket(@RequestParam(value = "orderCode", required = true) String orderCode,
                                      @RequestParam(value = "takeType", required = false) String takeType){

        Map<String,Object> retMap = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("orderCode",orderCode);
        map.put("takeStatus","1");//取票状态改为1，已取票
        map.put("status","1");//使用状态改为1，待使用
        map.put("takeType",takeType);
        try {
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                Map<String,Object> orderDetailsMap = myOrderService.getOrderDetailByOrderCode(orderCode);
                String takeStatus = "";
                if (!StringUtil.isEmpty(orderDetailsMap.get("take_status"))){
                    takeStatus = orderDetailsMap.get("take_status").toString();
                }
                if(takeStatus.equals("1")){
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","该票已取过票,请核实!");
                    return RtnData.fail(retMap);
                }else {
                    //判断是否支付
                    Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                    checkReturnMap = myOrderService.takeTicket(orderCode);
                    String returnKey = checkReturnMap.get("returnKey").toString();
                    if(returnKey.equals("true")){
                        if (myOrderService.updateTake(map)){
                            //retMap.put("orderCode",orderCode);
                            //取票成功返回门票信息
                            retMap.put("returnKey","true");
                            retMap.put("returnMessage","取票成功!");
                            retMap.put("orderDetailsMap",myOrderService.getOrderDetailByOrderCode(orderCode));
                            return RtnData.ok(retMap);
                        }else {
                            retMap.put("returnKey","false");
                            retMap.put("returnMessage","取票失败!");
                            return RtnData.fail(retMap);
                        }
                    }else{
                        return RtnData.fail(checkReturnMap);
                    }
                }
            }else {
                retMap.put("returnKey","false");
                retMap.put("returnMessage","没有查询到符合条件的订单记录!");
                return RtnData.fail(retMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取票状态更新状态失败",e);
            return RtnData.fail("取票状态更新状态失败!");
        }
    }


    /**
     *
     * checkType改为闸机的编号
     * 验票
     * 根据验票规则
     * @param orderCode
     * @return
     */
    @RequestMapping(value="checkTicket.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> checkTicket(@RequestParam(value = "orderCode", required = true) String orderCode,
                                       @RequestParam(value = "checkType", required = true) String checkType){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            //orderCode是否存在
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                Map<String,Object> orderDetailsMap = myOrderService.getOrderDetailByOrderCode(orderCode);
                //判断设备和场馆是否匹配
                Map<String,Object> checkEquMap = new HashMap<String, Object>();
                checkEquMap = equipmentService.checkEquipIsInStadium(checkType,orderDetailsMap);
                retMap.put("checkReturnMap",checkEquMap);
                String equKey = checkEquMap.get("returnKey").toString();
                if(equKey.equals("false")){
                    return RtnData.fail(retMap,"验票失败!");//不匹配
                }else {
                    //产品类型:0=散票/年卡，1=场地预定，2=散客预定
                    String ticketType = (String) orderDetailsMap.get("ticket_type");
                    if(!StringUtil.isEmpty(ticketType)){
                        Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                        if (ticketType.equals("0")) {
                            //年票  =======     根据验票规则验票
                            checkReturnMap = myOrderService.checkTicket(orderCode);
                            retMap.put("checkReturnMap",checkReturnMap);
                            String returnKey = checkReturnMap.get("returnKey").toString();
                            if (returnKey.equals("true")){
                                Map<String,Object> checkParam = new HashMap<String, Object>();
                                checkParam.put("orderCode",orderCode);
                                checkParam.put("checkStatus","1");//验票状态改为1，已验票
                                checkParam.put("status","2");//使用状态改为2，已使用
                                checkParam.put("checkType",checkType);
                                if(myOrderService.updateCheckByMap(checkParam) >0){
                                    //验票成功返回门票信息
                                    //针对年票/散票，只要是剩余次数还有，主单的状态就还是待使用，如果次数没有了，就变成已使用
                                    myOrderService.updateOrderBaseStatus();
                                    retMap.put("orderDetailsMap",myOrderService.getOrderDetailByOrderCode(orderCode));
                                    return RtnData.ok(retMap);
                                }else {
                                    return RtnData.fail(retMap,"验票状态修改失败!");
                                }
                            }else {
                                return RtnData.fail(retMap,"验票失败!");
                            }
                        } else if (ticketType.equals("1")){
                            //场地票  =======     根据验票规则验票
                            checkReturnMap = fieldOrderService.checkFieldTicket(orderCode);
                            retMap.put("checkReturnMap",checkReturnMap);
                            String returnKey = checkReturnMap.get("returnKey").toString();
                            if (returnKey.equals("true")){
                                Map<String,Object> checkParam = new HashMap<String, Object>();
                                checkParam.put("orderCode",orderCode);
                                checkParam.put("checkStatus","1");//验票状态改为1，已验票
                                checkParam.put("status","2");//使用状态改为2，已使用
                                checkParam.put("checkType",checkType);
                                if(fieldOrderService.updateCheckByMap(checkParam) >0){
                                    //验票成功返回门票信息
                                    retMap.put("orderDetailsMap",myOrderService.getOrderDetailByOrderCode(orderCode));
                                    return RtnData.ok(retMap);
                                }else {
                                    return RtnData.fail(retMap,"验票状态修改失败!");
                                }
                            }else {
                                return RtnData.fail(retMap,"验票失败!");
                            }
                        }else {
                            retMap.put("returnKey","false");
                            retMap.put("returnMessage","门票类型找不到!");
                            return RtnData.fail(retMap,"门票类型找不到!");
                        }
                    }else {
                        retMap.put("returnKey","false");
                        retMap.put("returnMessage","门票类型为空!");
                        return RtnData.fail(retMap,"门票类型为空!");
                    }


                }
            }else {
                Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                checkReturnMap.put("returnKey","false");
                checkReturnMap.put("returnMessage","订单编号不存在");
                retMap.put("checkReturnMap",checkReturnMap);
                return RtnData.fail(retMap,"没有查询到符合条件的订单记录!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("验票失败",e);
            return RtnData.fail("验票失败!");
        }
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @RequestMapping(value="cancelOrderByOrderId.json")
    @ResponseBody
    public RtnData<Object> cancelOrderByOrderId(@RequestParam(value = "orderId", required = true) String orderId){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            retMap = myOrderService.cancelOrderByOrderId(orderId);
            //主动取消的订单，释放场地状态
            fieldOrderService.updateSxLockField();
            return RtnData.ok(retMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取票订单失败",e);
            return RtnData.fail("取消订单失败!");
        }
    }


    /**
     * 根据验票码获取二维码图片
     */
    @RequestMapping(value="printQrCode.img",method = RequestMethod.GET)
    @ResponseBody
    public void printQrCode(@RequestParam(value = "orderCode", required = true) String orderCode, HttpServletResponse response) throws Exception {
        String qrCode = "";
        if(myOrderService.isHaveByOrderCode(orderCode) >0){
            qrCode = orderCode;
        }else{
            qrCode = "";
        }
        try {
            //5、图形写给浏览器
            response.setContentType("image/jpeg");
            //发头控制浏览器不要缓存
            response.setDateHeader("expries", -1);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            MyOrcode handler = new MyOrcode();
            handler.encoderQRCode(qrCode,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("生成二维码失败!",e);
        }
    }
}
