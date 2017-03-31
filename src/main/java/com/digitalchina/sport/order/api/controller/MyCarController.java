package com.digitalchina.sport.order.api.controller;

import com.digitalchina.sport.order.api.common.Constants;
import net.sf.json.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.HttpClientUtil;
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
            retMap = carService.insertCar(userId,carNumber);
            return RtnData.ok(retMap);

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
                return RtnData.ok(retMap,"解绑失败，车辆不存在!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解绑失败!",e);
            return RtnData.fail("解绑失败!");
        }
    }

    @RequestMapping(value="getParkInfo.json")
    @ResponseBody
    public RtnData<Object> getParkInfo(@RequestParam(value = "userId", required = true) String userId,
                                       @RequestParam(value = "carNumber", required = true) String carNumber){
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,String> params = carService.getParkingInfoParams(userId,carNumber);

            retMap = carService.getParkingInfo(params);
            String status = retMap.get("status").toString();
            status = status.substring(0, status.length() - 2);
            if (status.equals(Constants.RTN_SIGN_SUCCESS)){
                logger.info("查询成功!");
                return RtnData.ok(retMap,"查询成功!");
            }else {
                logger.info("查询失败!");
                return RtnData.fail(retMap,"查询失败!");
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败!",e);
            return RtnData.fail("解绑失败!");
        }
    }
}
