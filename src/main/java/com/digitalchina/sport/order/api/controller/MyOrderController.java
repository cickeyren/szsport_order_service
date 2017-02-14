package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.BarcodeUtil;
import com.digitalchina.sport.order.api.service.MyOrderService;
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
                                         @RequestParam(value = "status", required = false) String status) {
        Map<String,Object> map = new HashMap<String, Object>();
        if(pageIndex == 0){
            map.put("start",pageIndex);
        }else {
            map.put("start", (pageIndex - 1) * pageSize);
        }
        map.put("pageSize",pageSize);
        map.put("userId",userId);
        map.put("status",status);
        List<Map<String,Object>> list = myOrderService.getAllOrderByUserId(map);
        int count = myOrderService.getCountByUserId(map);
        Map<String,Object> reqMap=new HashMap<String, Object>();
        reqMap.put("list",list);
        reqMap.put("count",count);
        logger.info("根据用户id，按照状态查询所有订单:getMyAllOrder.json");
        return RtnData.ok(reqMap);
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
        List<Map<String,Object>> list = myOrderService.getTotalOrderByUserIdAndOrderId(map);
        int count = myOrderService.getCountByOrderId(orderId);
        Map<String,Object> orderDetails = myOrderService.getOrderDetails(orderId);
        Map<String,Object> reqMap=new HashMap<String, Object>();
        reqMap.put("list",list);
        reqMap.put("count",count);
        reqMap.put("orderDetails",orderDetails);
        logger.info("获取一个订单中的所有子订单的详情:getOrderDetails.json");
        return RtnData.ok(reqMap);
    }

    //根据策略的id获取门票的订票规则
    //根据策略的id获取门票的验票规则
    //根据策略的id获取门票的场馆信息

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
        Map<String,Object> orderBaseInfo = myOrderService.getOrderBaseInfoStringFromMap(count,myOrderService.getYearStrategyTicketModelInfo(yearStrategyId));//订单基本信息
        Map<String,Object> orderContentDetail = myOrderService.getOrderContentDetailFromMap(myOrderService.getYearStrategyTicketModelInfo(yearStrategyId));//子订单详细信息
        //order基本信息
        orderBaseInfo.put("costPrice",orderBaseInfo.get("totalCostPrice").toString());
        orderBaseInfo.put("sellPrice",orderBaseInfo.get("totalSellPrice").toString());
        orderBaseInfo.put("userId",orderJsonObject.get("userId").toString());
        orderBaseInfo.put("userName",orderJsonObject.get("userName").toString());
        orderBaseInfo.put("userTel",orderJsonObject.get("userTel").toString());
        orderBaseInfo.put("orderChannel",orderJsonObject.get("orderChannel").toString());

        String orderBaseId = myOrderService.insertOrderBaseInfo(orderBaseInfo);
        //order详细内容

        orderContentDetail.put("orderBaseId",orderBaseId);//主订单的id
        String orderContentId = "";
        for(int i=0;i<count;i++){
            String contentId = myOrderService.insertOrderContentDetail(orderContentDetail);
            orderContentId += contentId+",";
        }
        if (orderContentId.length()>0){
            logger.info("生成订单成功：主订单id="+orderBaseId+"子订单id="+orderContentId);
            return RtnData.ok("下单成功!");
        }else {
            logger.info("订单生成失败");
            return RtnData.fail("下单失败!");
        }

    }


    /**
     * 根据验票码获取条形码图片
     */
    @RequestMapping(value="printBarCode.img",method = RequestMethod.GET)
    @ResponseBody
    public void printBarCode(@RequestParam(value = "orderCode", required = false)String orderCode, HttpServletResponse response) {
        try {
            //5、图形写给浏览器
            response.setContentType("image/jpeg");
            //发头控制浏览器不要缓存
            response.setDateHeader("expries", -1);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            BarcodeUtil.generate(orderCode,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("======生成条形码失败=========",e);
        }

    }
}
