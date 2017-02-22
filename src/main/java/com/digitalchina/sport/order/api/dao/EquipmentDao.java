package com.digitalchina.sport.order.api.dao;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 设备dao
 */
@Mapper
public interface EquipmentDao {
    /**
     *根据子场馆id查询该场馆使用的所有设备
     * @param
     * @return
     */
    List<Map<String,Object>> findAllEquipBySubStadiumId(Map<String, Object> param) throws Exception;

    /**
     * 根据子场馆id查询所有启用的设备id
     * @param param
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> findAllEquipIdBySubStadiumId(Map<String, Object> param) throws Exception;

    /**
     * 根据设备id查询设备是否存在
     * @param param
     * @return
     */
    int getCountByEquipmentId(Map<String, Object> param) throws Exception;
}
