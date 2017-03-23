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
        Map<String,Object> param = map;//添加车辆的参数

        Map<String,Object> reMap = new HashMap<String, Object>();
        int count = carDao.getCount(map);//判断车辆是否被绑定
        if (count>0) {
            reMap.put("returnKey","false");
            reMap.put("returnMessage","该车辆已绑定!");
        }
        map.remove("carNumber");
        int count2 = carDao.getCount(map);//判断最多绑定5个车牌号
        if (count2>=5) {
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
        reMap.put("userId",param.get("userId"));
        reMap.put("carNumber",param.get("carNumber"));
        return reMap;
    };

    public int deleteCar(Map<String,Object> map){
        return carDao.deleteCar(map);
    };

}
