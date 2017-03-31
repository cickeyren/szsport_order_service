package com.digitalchina.sport.order.api.dao;

import com.sun.javafx.collections.MappingChange;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
/**
 *汽车dao
 */
@Mapper
public interface CarDao {

    List<Map<String,Object>> getCarList(Map<String,Object> map);

    int insertCar(Map<String,Object> map);

    int deleteCar(Map<String,Object> map);

    int getCount(Map<String,Object> map);

    List<Map<String,Object>> getOrderDetailList(Map<String,Object> map);
}
