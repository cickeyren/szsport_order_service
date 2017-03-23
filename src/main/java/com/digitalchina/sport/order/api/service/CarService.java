package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.sport.order.api.dao.CarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Service
public class CarService {
    @Autowired
    private CarDao carDao;

    public List<Map<String,Object>> getCarList(Map<String,Object> map){
        return carDao.getCarList(map);
    };

    public Map<String,Object> insertCar(String userId,String carNumber){
        Map<String,Object> reMap = new HashMap<String, Object>();//作为返回的参数

        Map<String,Object> param = new HashMap<String, Object>();//作为添加车辆的参数
        param.put("userId",userId);//添加车辆的参数
        param.put("carNumber",carNumber);//添加车辆的参数
        Map<String,Object> map = new HashMap<String, Object>();//作为判断的参数
        map.put("userId",userId);
        map.put("carNumber",carNumber);
        int count = carDao.getCount(map);//判断车辆是否被绑定
        Map<String,Object> map2 = new HashMap<String, Object>();//作为判断的参数
        map2.put("userId",userId);
        int count2 = carDao.getCount(map2);//判断最多绑定5个车牌号
        if (count>0) {
            reMap.put("returnKey","false");
            reMap.put("returnMessage","该车辆已绑定!");
        }else if (count2>=5) {
            reMap.put("returnKey","false");
            reMap.put("returnMessage","同一账号最多可绑定5个车牌号!");
        }else {
            String id = UUIDUtil.generateUUID();//uuid生成32位随机数id
            param.put("id",id);
            if (carDao.insertCar(param)>0){
                reMap.put("returnKey","true");
                reMap.put("returnMessage","绑定成功!");
            }else {
                reMap.put("returnKey","true");
                reMap.put("returnMessage","绑定失败!");
            }
        }
        reMap.put("userId",userId);
        reMap.put("carNumber",carNumber);
        return reMap;
    };

    public int deleteCar(Map<String,Object> map){
        return carDao.deleteCar(map);
    };

}
