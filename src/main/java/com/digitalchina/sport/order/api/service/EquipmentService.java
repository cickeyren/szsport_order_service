package com.digitalchina.sport.order.api.service;


import com.digitalchina.common.utils.StringUtil;
import com.digitalchina.sport.order.api.dao.EquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
@Service
public class EquipmentService {

    @Autowired
    private EquipmentDao equipmentDao;

    public List<Map<String,Object>> findAllEquipBySubStadiumId(Map<String,Object> param) throws Exception{

        return equipmentDao.findAllEquipBySubStadiumId(param);
    }

    /**
     * 验票（判断设备和场馆是否匹配）
     * @param map
     * @return
     * @throws Exception
     */
    public Map<String,Object> checkEquipIsInStadium(String checkType,Map<String,Object> map) throws Exception{
        Map<String,Object> reqMap=new HashMap<String, Object>();
        Map<String,Object> param=new HashMap<String, Object>();
        if(!StringUtil.isEmpty(map.get("subStadiumId"))){
            param.put("subStadiumId",map.get("subStadiumId").toString());
            String equipmentId = "";
            String equipmentIds = "";
            if(!StringUtil.isEmpty(checkType)){
                equipmentId = checkType;//checkType就是设备编号
                param.put("equipmentId",equipmentId);
                List<Map<String,Object>> mapList = equipmentDao.findAllEquipIdBySubStadiumId(param);
                if (mapList.size()>0){
                    for (int i =0;i<mapList.size();i++){
                        Map<String,Object> map1 = mapList.get(i);
                        equipmentIds += map1.get("equipmentId")+",";
                    }
                    equipmentIds = equipmentIds.substring(0, equipmentIds.length() - 1);
                    String[] ids = equipmentIds.split(",");
                    if(StringUtil.isIn(equipmentId,ids)){
                        reqMap.put("returnKey","true");
                        reqMap.put("returnMessage","该设备属于该场馆");
                    }else {
                        reqMap.put("returnKey","false");
                        reqMap.put("returnMessage","该设备不属于该场馆");
                    }
                }else {
                    reqMap.put("returnKey","false");
                    reqMap.put("returnMessage","该场馆还未添加设备!");
                }
            }else {
                reqMap.put("returnKey","false");
                reqMap.put("returnMessage","设备号为空!");
            }
        }

        return reqMap;
    }

    public int getCountByEquipmentId(Map<String,Object> map) throws Exception {
        return equipmentDao.getCountByEquipmentId(map);
    }
}
