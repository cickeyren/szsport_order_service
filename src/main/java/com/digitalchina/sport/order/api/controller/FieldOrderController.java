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
 * 场地票相关接口
 * 我的订单control
 */
@RestController
@RequestMapping("/order/api/fieldOrder/")
public class FieldOrderController {

    public static final Logger logger = LoggerFactory.getLogger(FieldOrderController.class);
    @Autowired
    private MyOrderService myOrderService;
    @Autowired
    private FieldOrderService fieldOrderService;

    @RequestMapping(value="createFieldOrder.json",method = RequestMethod.POST)
    @ResponseBody
    public RtnData<Object> createFieldOrder(@RequestBody String orderJson){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            retMap = fieldOrderService.createFieldOrder(orderJson);
            String returnKey = retMap.get("returnKey").toString();
            if (returnKey.equals("true")){
                return RtnData.ok(retMap,"下单成功！");
            }else {
                return RtnData.fail(retMap,"下单失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("下单失败!",e);
            return RtnData.fail("下单失败!");
        }
    }
}
