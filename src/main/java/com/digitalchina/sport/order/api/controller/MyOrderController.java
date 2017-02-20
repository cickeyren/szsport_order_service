package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.BarcodeUtil;
import com.digitalchina.common.utils.StringUtil;
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
        map.put("orderNumber",orderNumber);
        try {
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
    public RtnData<Object> getOrderDetails(@RequestParam(value = "userId", required = false) String userId,
                                           @RequestParam(value = "orderId", required = false) String orderId){
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
        JSONObject orderJsonObject = JSON.parseObject(orderJson);

        String yearStrategyId = orderJsonObject.get("yearStrategyId").toString();
        int count = Integer.parseInt(orderJsonObject.get("count").toString());//订单下面的字单个数
        try {
            //订单基本信息
            Map<String,Object> orderBaseInfo = myOrderService.getOrderBaseInfoFromMap(count,myOrderService.getYearStrategyTicketModelInfo(yearStrategyId));
            Map<String,Object> orderContentDetail = myOrderService.getOrderContentDetailFromMap(myOrderService.getYearStrategyTicketModelInfo(yearStrategyId));//子订单详细信息
            //order基本信息
            orderBaseInfo.put("costPrice",orderBaseInfo.get("totalCostPrice").toString());
            orderBaseInfo.put("sellPrice",orderBaseInfo.get("totalSellPrice").toString());
            orderBaseInfo.put("userId",orderJsonObject.get("userId").toString());
            orderBaseInfo.put("userName",orderJsonObject.get("userName").toString());
            orderBaseInfo.put("userTel",orderJsonObject.get("userTel").toString());
            /**
             * orderChannel用途：APP表示线上，订单售价为线上价格；其余为线下价格即为门市价
             * 目前暂时均为线上，线下暂时不做
             */
            orderBaseInfo.put("orderChannel",orderJsonObject.get("orderChannel").toString());
            String orderBaseId = myOrderService.insertOrderBaseInfo(orderBaseInfo);
            //order详细内容
            orderContentDetail.put("orderBaseId",orderBaseId);//主订单的id
            myOrderService.insertOrderContentDetail(count,orderContentDetail);

            System.out.println("orderBaseInfo="+orderBaseInfo);
            System.out.println("orderContentDetail="+orderContentDetail);
            Map<String,Object> retMap = new HashMap<String, Object>();
            retMap.put("orderNumber",orderBaseInfo.get("orderNumber"));//订单流水号
            return RtnData.ok(retMap,"下单成功!");
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
    public void printBarCode(@RequestParam(value = "orderCode", required = false) String orderCode, HttpServletResponse response) throws Exception {
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
    public RtnData<Object> checkTicketByOrderCode(@RequestParam(value = "orderCode", required = false) String orderCode){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                retMap.put("orderDetailsMap",myOrderService.getOrderDetailByOrderCode(orderCode));
                return RtnData.ok(retMap);
            }else {
                return RtnData.fail("没有查询到符合条件的订单记录!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败，",e);
            return RtnData.fail("查询失败!");
        }
    }

    /**
     * 取票操作
     * 根据验票码验证是否存在，存在则返回该票的详情,并修改取票状态，表示取票成功，不存在则返回提示信息
     * @param orderCode
     * @param takeType
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
        map.put("takeType",takeType);
        try {
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                Map<String,Object> orderDetailsMap = myOrderService.getOrderDetailByOrderCode(orderCode);
                if(!StringUtil.isEmpty(orderDetailsMap.get("take_status"))){
                    if(orderDetailsMap.get("take_status").equals("1")){
                        return RtnData.fail("该票已取过票,请核实!");
                    }else {
                        if (myOrderService.updateTake(map)){
                            retMap.put("orderCode",orderCode);
                            return RtnData.ok(retMap,"取票成功，取票状态更新成功!");
                        }else {
                            return RtnData.fail("取票状态更新状态失败!");
                        }
                    }
                }else {
                    if (myOrderService.updateTake(map)){
                        retMap.put("orderCode",orderCode);
                        return RtnData.ok(retMap,"取票成功，取票状态更新成功!");
                    }else {
                        return RtnData.fail("取票状态更新状态失败!");
                    }
                }
            }else {
                return RtnData.fail("没有查询到符合条件的订单记录!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取票状态更新状态失败",e);
            return RtnData.fail("取票状态更新状态失败!");
        }
    }

    /**
     * 验票
     * 根据验票规则
     * @param orderCode
     * @return
     */
    @RequestMapping(value="checkTicket.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> checkTicket(@RequestParam(value = "orderCode", required = false) String orderCode,
                                       @RequestParam(value = "checkType", required = false) String checkType){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            if(myOrderService.isHaveByOrderCode(orderCode)>0){
                Map<String,Object> orderDetailsMap = myOrderService.getOrderDetailByOrderCode(orderCode);
                if(!StringUtil.isEmpty(orderDetailsMap.get("check_status"))){
                    if(!orderDetailsMap.get("check_status").equals("1")){
                        //根据验票规则验票
                        Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                        checkReturnMap = myOrderService.checkTicket(orderCode);
                        retMap.put("checkReturnMap",checkReturnMap);
                        String returnKey = checkReturnMap.get("returnKey").toString();
                        if (returnKey.equals("true")){
                            Map<String,Object> checkParam = new HashMap<String, Object>();
                            checkParam.put("orderCode",orderCode);
                            checkParam.put("checkStatus","1");//验票状态改为1，已验票
                            checkParam.put("checkType",checkType);
                            if(myOrderService.updateCheckByMap(checkParam) >0){
                                return RtnData.ok(retMap,"验票状态修改成功!");
                            }else {
                                return RtnData.fail(retMap,"验票状态修改失败!");
                            }
                        }else {
                            return RtnData.fail(retMap,"验票失败!");
                        }
                    }else  return RtnData.fail("该票已验过票,请核实!");
                }else {
                    //根据验票规则验票
                    Map<String,Object> checkReturnMap = new HashMap<String, Object>();
                    checkReturnMap = myOrderService.checkTicket(orderCode);
                    retMap.put("checkReturnMap",checkReturnMap);
                    String returnKey = checkReturnMap.get("returnKey").toString();
                    if (returnKey.equals("true")){
                        Map<String,Object> checkParam = new HashMap<String, Object>();
                        checkParam.put("orderCode",orderCode);
                        checkParam.put("checkStatus","1");//验票状态改为1，已验票
                        checkParam.put("checkType",checkType);
                        if(myOrderService.updateCheckByMap(checkParam) >0){
                            return RtnData.ok(retMap,"验票状态修改成功!");
                        }else {
                            return RtnData.fail(retMap,"验票状态修改失败!");
                        }
                    }else {
                        return RtnData.fail(retMap,"验票失败!");
                    }
                }
            }else {
                return RtnData.fail("没有查询到符合条件的订单记录!");
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
            return RtnData.ok(retMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取票订单失败",e);
            return RtnData.fail("取消订单失败!");
        }
    }
}
