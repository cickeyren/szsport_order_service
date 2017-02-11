package com.digitalchina.sport.order.api.service;

import com.digitalchina.sport.order.api.dao.MyOrderDao;
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
}
