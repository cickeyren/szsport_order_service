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

    /**
     * 根据设备id查询设备是否启用，并且场馆是否开馆
     * @param param
     * @return
     * @throws Exception
     */
    Map<String,Object> getOpenTypeByMap(Map<String, Object> param) throws Exception;

    /**
     * 根据设备id和场馆id查询设备是否属于场馆
     * @param param
     * @return
     * @throws Exception
     */
    int getCountByMap(Map<String, Object> param) throws Exception;

    /**
     * 根据设备id查询设备详细
     * @param param
     * @return
     * @throws Exception
     */
    Map<String,Object> getDetailsByEquipmentId(Map<String, Object> param) throws Exception;
}
