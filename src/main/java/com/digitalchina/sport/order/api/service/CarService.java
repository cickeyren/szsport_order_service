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

    public Map<String,Object> insertCar(Map<String,Object> map){
        Map<String,Object> reMap = new HashMap<String, Object>();
        int count = carDao.getCount(map);
        if (count>0) {
            reMap.put("userId",map.get("userId"));
            reMap.put("carNumber",map.get("carNumber"));
            reMap.put("returnKey","false");
            reMap.put("returnMessage","该车辆已绑定!");
        }else {
            String id = UUIDUtil.generateUUID();//uuid生成32位随机数id
            map.put("id",id);
            if (carDao.insertCar(map)>0){
                reMap.put("userId",map.get("userId"));
                reMap.put("carNumber",map.get("carNumber"));
                reMap.put("returnKey","true");
                reMap.put("returnMessage","绑定成功!");
            }else {
                reMap.put("userId",map.get("userId"));
                reMap.put("carNumber",map.get("carNumber"));
                reMap.put("returnKey","true");
                reMap.put("returnMessage","绑定失败!");
            }
        }
        return reMap;
    };

    public int deleteCar(Map<String,Object> map){
        return carDao.deleteCar(map);
    };

}
