package com.digitalchina.sport.order.api.service;

import com.digitalchina.sport.order.api.dao.MyOrderDao;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 我的订单service
 */
@Service
public class MyOrderService {
    @Autowired
    private MyOrderDao myOrderDao;

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
    public int getCountByUserId(Map<String,Object> params) {
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
}
