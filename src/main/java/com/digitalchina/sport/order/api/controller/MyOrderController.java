package com.digitalchina.sport.order.api.controller;

import com.digitalchina.common.result.Result;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的订单control
 */
@RestController
@RequestMapping("/api/myOrder/")
public class MyOrderController {

    //public static final Logger logger = LoggerFactory.getLogger(FieldController.class);

    @Autowired
    private MyOrderService myOrderService;

    /**
     * 根据用户id，按照状态查询所有订单
     * @return
     */
    @RequestMapping(value="getMyAllOrder",method = RequestMethod.GET)
    @ResponseBody
    public String getMyAllOrder(@RequestParam(value = "pageIndex",defaultValue = "0" , required = false) int pageIndex,
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
        return Result.ok(reqMap);
    }

    /**
     * 获取一个订单中的所有子订单的详情
     * @return
     */
    @RequestMapping(value="getOrderDetails",method = RequestMethod.GET)
    @ResponseBody
    public String getOrderDetails(@RequestParam(value = "userId", required = false) String userId,
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
        return Result.ok(reqMap);
    }
}
