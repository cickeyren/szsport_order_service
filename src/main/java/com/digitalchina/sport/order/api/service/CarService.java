package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.MD5Utils;
import com.digitalchina.common.utils.DateUtil;
import com.digitalchina.common.utils.HttpClientUtil;
import com.digitalchina.common.utils.StringUtil;
import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.CarDao;
import com.digitalchina.sport.order.api.dao.MyOrderDao;
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
    @Autowired
    private MyOrderDao myOrderDao;
    @Autowired
    private PropertyConfig proConfig;

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

    /**
     * uid	是	用户编号（由APP提供唯一编号）
     number	是	场馆编号（苏州市市民健身中心默认为004）
     carno	是	车牌号
     title	否	消费项目名称，如：篮球馆、游泳馆
     category	否	票务类型，如普通票、成人票
     type	否	0->预定，1->散票
     ordernumber	否	消费订单编号
     mark	否	消费明细，如是预定则备注预定场地信息（如2号场、3号场2017-03-22 18:00-19:00），如是散票则备注单价和张数（如10元/人 2张）
     sign	是	校验码
     */
    public Map<String,String> getParkingInfoParams(String userId,String carNumber) throws Exception{
        Map<String,String> reMap = new HashMap<String, String>();//作为返回的参数
        reMap.put("uid",userId);
        reMap.put("carno",carNumber);

        String number = "004";
        reMap.put("number",number);

        String dqdate = DateUtil.today();//当前日期
        Map<String,Object> params = new HashMap<String, Object>();//作为返回的参数
        params.put("startTime",dqdate+" 00:00:00");
        params.put("endTime",dqdate+" 23:59:59");
        params.put("userId",userId);
        params.put("stadiumId","1001");//目前只这针对苏州市民健身中心的订单
        List<Map<String,Object>> orderList = carDao.getOrderDetailList(params);
        if(orderList.size()>0){
            Map<String,Object> orderDetail = orderList.get(0);
            reMap.put("title",orderDetail.get("subStadiumName").toString());
            if (!StringUtil.isEmpty(orderDetail.get("personKind"))){
                reMap.put("category",orderDetail.get("personKind").toString());
            }else {
                reMap.put("category","普通票");
            }
            reMap.put("ordernumber",orderDetail.get("orderNumber").toString());
            String sign = MD5Utils.getPwd(userId+number+carNumber);
            reMap.put("sign",sign);
            String ticketType = orderDetail.get("ticketType").toString();
            //0->预定，1->散票
            //0=散票/年卡，1=场地预定，2=散客预定，
            if (ticketType.equals("0")){
                reMap.put("type","1");
                String sellPrice = orderDetail.get("sellPrice").toString();
                String mark = sellPrice+"元/人 1张";
                reMap.put("mark",mark);
            }else if (ticketType.equals("1")){
                reMap.put("type","0");
                String fieldName = orderDetail.get("fieldName").toString();
                String dateLimit = orderDetail.get("dateLimit").toString();
                String timeLimit = orderDetail.get("timeLimit").toString();
                timeLimit.replace("$","-");
                String mark = fieldName+ " "+dateLimit+" "+timeLimit+"";
                reMap.put("mark",mark);
            }

        }else {

        }
        //System.out.print(reMap.toString());
        return reMap;
    }

    public Map<String,Object> getParkingInfo(Map<String,String> map){
        return HttpClientUtil.getRetMapByPost(proConfig.PARK_URL,"停车信息接口getParkingInfo",map);
       // return null;
    }
}
