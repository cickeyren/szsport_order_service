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

    /**
     * 验票（判断设备和场馆是否匹配）
     * @param map
     * @return
     * @throws Exception
     */
    public Map<String,Object> checkEquipIsInStadium(String checkType,Map<String,Object> map) throws Exception{
        Map<String,Object> reqMap=new HashMap<String, Object>();
        Map<String,Object> param=new HashMap<String, Object>();
        if(!StringUtil.isEmpty(map.get("subStadiumId")) && !StringUtil.isEmpty(checkType)){
            param.put("subStadiumId",map.get("subStadiumId").toString());
            param.put("equipmentId",checkType);//checkType就是设备编号
            int count = equipmentDao.getCountByMap(param);//count>0表示属于
            if(count > 0){//判断是否开馆和是否启用
                Map<String,Object> opentypeMap =  equipmentDao.getOpenTypeByMap(param);
                String eqstatus = (String) opentypeMap.get("eqstatus");//1正常
                String substatus = (String) opentypeMap.get("substatus");//1正常，0闭馆，2作废
                if(!StringUtil.isEmpty(eqstatus) && !StringUtil.isEmpty(substatus)){
                    if (eqstatus.equals("1")){
                        if (substatus.equals("1")){
                            reqMap.put("returnKey","true");
                            reqMap.put("returnMessage","该设备属于该场馆,可继续验票");
                        }else {
                            reqMap.put("returnKey","false");
                            reqMap.put("returnMessage","该场馆闭馆!");
                        }
                    }else {
                        reqMap.put("returnKey","false");
                        reqMap.put("returnMessage","设备未启用!");
                    }
                }else {
                    reqMap.put("returnKey","false");
                    reqMap.put("returnMessage","该门票不属于该场馆!");
                }
            }else {
                reqMap.put("returnKey","false");
                reqMap.put("returnMessage","该门票不属于该场馆!");
            }
        }else {
            reqMap.put("returnKey","false");
            reqMap.put("returnMessage","该门票不属于该场馆!");
        }
        return reqMap;
    }
}
