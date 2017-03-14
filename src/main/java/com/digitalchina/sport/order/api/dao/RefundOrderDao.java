package com.digitalchina.sport.order.api.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RefundOrderDao {
    /**
     * 通过信息查询子订单信息
     * @param map
     * @return
     */
    Map<String,Object> getorderContent(Map<String, Object> map);

    /**
     * 修改该条订单状态为待退单状态
     * @param map
     * @return
     */
    int updateOrderContentDetail(Map<String, Object> map);

    /**
     * 获取支付宝相应的支付方式
     * @param map
     * @return
     */
    Map<String,Object> getMerchantPayAccount(Map<String, Object> map);
}