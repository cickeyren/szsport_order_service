package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.sport.order.api.dao.CarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int insertCar(Map<String,Object> map){
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数id
        map.put("id",id);
        return carDao.insertCar(map);
    };

    public int deleteCar(Map<String,Object> map){
        return carDao.deleteCar(map);
    };

}
