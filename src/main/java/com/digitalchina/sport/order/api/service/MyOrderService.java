package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.utils.HttpClientUtil;
import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.MyOrderDao;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的订单service
 */
@Service
public class MyOrderService {
    @Autowired
    private MyOrderDao myOrderDao;
    @Autowired
    private PropertyConfig proConfig;
    /**
     * 根据用户id，按照状态查询所有订单
     * @param params
     * @return
     */
    public List<Map<String,Object>> getAllOrderByUserId(Map<String,Object> params) {
        return myOrderDao.getAllOrderByUserId(params);
    }

    /**
     * 根据用户id，按照状态查询所有订单个数
     * @param params
     * @return
     */
    public int getCountByUserId(Map<
            String,Object> params) {
        return myOrderDao.getCountByUserId(params);
    }

    /**
     * 一个订单下的所有子订单（条形码）详情
     * @param params
     * @return
     */
    public List<Map<String,Object>> getTotalOrderByUserIdAndOrderId(Map<String,Object> params) {
        return myOrderDao.getTotalOrderByUserIdAndOrderId(params);
    }

    /**
     * 一个订单下的所有子订单（条形码）个数
     * @param orderId
     * @return
     */
    public int getCountByOrderId(String orderId) {
        return myOrderDao.getCountByOrderId(orderId);
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    public Map<String,Object> getOrderDetails(String orderId){
        return myOrderDao.getOrderDetails(orderId);
    }

    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     */
    public void inserOrderBaseInfo(Map<String,Object> orderBaseInfo){
        myOrderDao.inserOrderBaseInfo(orderBaseInfo);
    }

    /**
     * 判断订单流水号是否重复
     * @param orderNumber
     * @return
     */
    public int isHaveByOrderNumer(String orderNumber){
        return myOrderDao.isHaveByOrderNumer(orderNumber);
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public void inserOrderContentDetail(Map<String,Object> orderContentDetail){
        myOrderDao.inserOrderContentDetail(orderContentDetail);
    }

    /**
     * 判断12位确认码是否重复
     * @param orderCode
     * @return
     */
    public int isHaveByOrderCode(String orderCode){
        return myOrderDao.isHaveByOrderCode(orderCode);
    }

    /**
     * 根据子场馆id查询分类id
     * @param subStadiumId
     * @return
     */
    public String getClassifyBySubStadiumId(String subStadiumId){
        return myOrderDao.getClassifyBySubStadiumId(subStadiumId);
    }


    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     * @return
     */
    public String insertOrderBaseInfo(Map<String,Object> orderBaseInfo) {
        //订单基本信息
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderBaseInfo.put("id",id);
        String order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        //根据订单号去查询是否存在，否则重新生成一个订单号
        int ishave =  myOrderDao.isHaveByOrderNumer(order_number);
        if (ishave > 0){
            order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        }
        orderBaseInfo.put("orderNumber",order_number);
        orderBaseInfo.put("status","0");//订单状态为未支付

        //订单基本信息入库
        myOrderDao.inserOrderBaseInfo(orderBaseInfo);
        return orderBaseInfo.get("id").toString();
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public String insertOrderContentDetail(Map<String,Object> orderContentDetail) {
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderContentDetail.put("id",id);
        String orderCode = UtilDate.getSix()+UtilDate.getSix();//12位随机数作确认码
        //根据确认码去查询是否存在，否则重新生成一个
        int ishave =  myOrderDao.isHaveByOrderCode(orderCode);
        for (int i=0;i<3;i++){
            if (ishave > 0){
                orderCode = UtilDate.getSix()+UtilDate.getSix();
            }
        }
        orderContentDetail.put("orderCode",orderCode);
        orderContentDetail.put("status","0");//起始为待支付
        myOrderDao.inserOrderContentDetail(orderContentDetail);
        return orderContentDetail.get("id").toString();
    }

    /**
     * 根据子场馆id查询分类id
     * @param subStadiumId
     * @return
     */
    public String getClassifyById(String subStadiumId) {
        return myOrderDao.getClassifyBySubStadiumId(subStadiumId);
    }
    /*
 * 根据ticketid获取门票策略和场馆详情
 * @param yearStrategyId
 * @return
 * http://192.168.31.181:8080/yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId=1";
 */
    public Map<String,Object> getYearStrategyTicketModelInfo(String yearStrategyId) {
        return HttpClientUtil.getMapResultByURLAndKey(proConfig.SPORT_RESOURCEMGR_URL
                        + "yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId="+yearStrategyId,
                "根据ticketid获取门票策略和场馆详情");
    }
    /**
     * 从map中提取需要的订单数据
     * @return
     */
    public Map<String,Object> getOrderBaseInfoStringFromMap(int count,Map<String,Object> map){
        Map<String,Object> orderBaseInfo = new HashMap<String, Object>();
        List<Map<String,Object>> studStadiumList = (List<Map<String,Object>>) map.get("studStadiumList");
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        Map<String,Object> stadiumInfo = new HashMap<String,Object>();
        if(studStadiumList.size()>0){
            stadiumInfo = studStadiumList.get(0);
        }
        String classify = getClassifyById(stadiumInfo.get("subStadiumId").toString());
        int totalCostPrice = Integer.parseInt(yearStrategyDetail.get("sellPrice").toString())*count;
        int totalSellPrice = Integer.parseInt(yearStrategyDetail.get("sellPrice").toString())*count;
        orderBaseInfo.put("stadiumName",stadiumInfo.get("mainStdiumName"));
        orderBaseInfo.put("classify",classify);
        orderBaseInfo.put("subStadiumName",stadiumInfo.get("subStdiumName"));
        orderBaseInfo.put("ticketName",yearStrategyDetail.get("ticketName"));
        orderBaseInfo.put("ticketId",yearStrategyDetail.get("id"));
        orderBaseInfo.put("ticketType",yearStrategyDetail.get("ticketType"));
        orderBaseInfo.put("merchantId",yearStrategyDetail.get("merchantId"));

        orderBaseInfo.put("totalCostPrice",totalCostPrice);
        orderBaseInfo.put("totalSellPrice",totalSellPrice);
        orderBaseInfo.put("sonOrders",count);
        System.out.println("orderBaseInfo="+orderBaseInfo.toString());
        return orderBaseInfo;
    }
    /**
     * 从map中提取需要的订单数据
     * @return
     */
    public Map<String,Object> getOrderContentDetailFromMap(Map<String,Object> map){
        Map<String,Object> orderContentDetail = new HashMap<String, Object>();
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        orderContentDetail.put("startTime",yearStrategyDetail.get("orderEffectiveStartTime"));
        orderContentDetail.put("endTime",yearStrategyDetail.get("orderEffectiveEndTime"));
        orderContentDetail.put("costPrice",yearStrategyDetail.get("costPrice"));
        orderContentDetail.put("sellPrice",yearStrategyDetail.get("sellPrice"));
        orderContentDetail.put("totalNumber",yearStrategyDetail.get("checkTicketAvailableTimes"));
        orderContentDetail.put("everydayNumber",yearStrategyDetail.get("checkDailyLimitedTimes"));
        System.out.println("orderContentDetail="+orderContentDetail.toString());
        return orderContentDetail;
    }
}
