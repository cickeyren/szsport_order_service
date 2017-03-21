package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.sport.order.api.service.CarService;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的停车相关接口
 *
 */
@RestController
@RequestMapping("/order/api/myCar/")
public class MyCarController {

    public static final Logger logger = LoggerFactory.getLogger(MyCarController.class);
    @Autowired
    private MyOrderService myOrderService;
    @Autowired
    private CarService carService;

    @RequestMapping(value="getMyCarList.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> getMyCarList(@RequestParam(value = "userId", required = true) String userId){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object> param = new HashMap<String, Object>();
            param.put("userId",userId);
            List<Map<String,Object>> carList = carService.getCarList(param);
            retMap.put("carList",carList);
            return RtnData.ok(retMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败!",e);
            return RtnData.fail("查询失败!");
        }
    }

    @RequestMapping(value="insertCar.json")
    @ResponseBody
    public RtnData<Object> insertCar(@RequestParam(value = "userId", required = true) String userId,
                                     @RequestParam(value = "carNumber", required = true) String carNumber){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object> param = new HashMap<String, Object>();
            param.put("userId",userId);
            param.put("carNumber",carNumber);
            int count = carService.insertCar(param);
            if (count>0){
                retMap.put("userId",userId);
                retMap.put("carNumber",carNumber);
                return RtnData.ok(retMap,"添加成功!");
            }else {
                retMap.put("userId",userId);
                retMap.put("carNumber",carNumber);
                return RtnData.ok(retMap,"添加失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加失败!",e);
            return RtnData.fail("添加失败!");
        }
    }

    @RequestMapping(value="deleteCar.json")
    @ResponseBody
    public RtnData<Object> deleteCar(@RequestParam(value = "id", required = true) String id){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object> param = new HashMap<String, Object>();
            param.put("id",id);
            int count = carService.deleteCar(param);
            if (count>0){
                retMap.put("id",id);
                return RtnData.ok(retMap,"解绑成功!");
            }else {
                retMap.put("id",id);
                return RtnData.ok(retMap,"解绑失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解绑失败!",e);
            return RtnData.fail("解绑失败!");
        }
    }
}
