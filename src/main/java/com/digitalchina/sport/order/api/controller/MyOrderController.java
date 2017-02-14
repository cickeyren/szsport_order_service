package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.result.Result;
import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    //public static final Logger logger = LoggerFactory.getLogger(FieldController.class);

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
        return RtnData.ok(reqMap);
    }

    //根据门票的id获取门票的订票规则
    //根据门票的id获取门票的验票规则
    //根据门票的id获取门票的场馆信息

    /**
     * 将以上三个接口返回信息中的内容存入订单详情表
     * 生成订单
     * @param orderJson
     * @return
     */
    @RequestMapping(value="createOrder.json",method = RequestMethod.POST)
    @ResponseBody
    public RtnData<Object> createOrder(@RequestBody String orderJson){
        JSONObject orderInfoJson = JSON.parseObject(orderJson);
        int count = Integer.parseInt(orderInfoJson.get("count").toString());//订单下面的字单个数
        /**
         * order基本信息
         */
        JSONObject orderBaseInfoJson = orderInfoJson.getJSONObject("orderBaseInfo");
        System.out.println(orderBaseInfoJson);
        String costPrice = orderBaseInfoJson.get("totalCostPrice").toString();
        String sellPrice = orderBaseInfoJson.get("totalSellPrice").toString();
        orderBaseInfoJson.put("costPrice",costPrice);
        orderBaseInfoJson.put("sellPrice",sellPrice);
        orderBaseInfoJson.put("sonOrders",count);
        String orderId = this.insertOrderBaseInfo(orderBaseInfoJson);
        /**
         * order详细内容
         */
        JSONObject orderContentDetail = orderInfoJson.getJSONObject("orderContentDetail");
        orderContentDetail.put("orderBaseId",orderId);//主订单的id
        System.out.println(orderContentDetail);
        for(int i=0;i<count;i++){
            this.insertOrderContentDetail(orderContentDetail);
        }
        Map<String,Object> reqMap=new HashMap<String, Object>();
        return RtnData.ok("下单成功!");
    }

    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     * @return
     */
    public String insertOrderBaseInfo(JSONObject orderBaseInfo) {
        OrderBaseInfo obinfo = JSON.parseObject(orderBaseInfo.toString(), OrderBaseInfo.class);//订单基本信息
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        obinfo.setId(id);
        String order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        //根据订单号去查询是否存在，否则重新生成一个订单号
        int ishave =  myOrderService.isHaveByOrderNumer(order_number);
        if (ishave > 0){
            order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        }
        obinfo.setOrderNumber(order_number);
        obinfo.setStatus("0");//订单状态为未支付

        //订单基本信息入库
        myOrderService.inserOrderBaseInfo(obinfo);
        System.out.print("id=!!!!!!!!!!!!"+obinfo.getId());
        return obinfo.getId();
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public void insertOrderContentDetail(JSONObject orderContentDetail) {
        OrderContentDetail ocinfo = JSON.parseObject(orderContentDetail.toString(), OrderContentDetail.class);
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        ocinfo.setId(id);
        String orderCode = UtilDate.getSix()+UtilDate.getSix();//12位随机数作确认码
        //根据确认码去查询是否存在，否则重新生成一个
        int ishave =  myOrderService.isHaveByOrderCode(orderCode);
        for (int i=0;i<3;i++){
            if (ishave > 0){
                orderCode = UtilDate.getSix()+UtilDate.getSix();
            }
        }
        ocinfo.setOrderCode(orderCode);
        ocinfo.setStatus("0");//起始为待支付
        myOrderService.inserOrderContentDetail(ocinfo);
    }
}
