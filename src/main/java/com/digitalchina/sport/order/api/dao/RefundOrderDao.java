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
    int updateOrderAll(Map<String, Object> map);

    /**
     * 获取支付宝相应的支付方式
     * @param map
     * @return
     */
    Map<String,Object> getMerchantPayAccount(Map<String, Object> map);

    /**
     * 根据订单号查询支付的订单列表
     * @param map
     * @return
     */
    Map<String,Object> getAplyOrder(Map<String, Object> map);

    /**
     * 添加数据到退款记录表中
     * @param params
     * @return
     */
    int insertRefundOrder(Map<String, Object> params);

    /**
     * 更新子单状态
     *
     * @param map
     * @return
     */
    int updateOrderForOrder(Map<String, Object> map);

    /**
     * 更新主订单的退款状态
     * @param map
     * @return
     */
    int updateBaseOrder(Map<String, Object> map);

    /**
     * 插叙所有待使用的条数
     * @param map
     * @return
     */
    int getNumsByOrderID(Map<String, Object> map);

    /**
     * 更具主订单查询所有主订单状态
     * @param map
     * @return
     */
    List<Map<String,Object>> getByOrderID(Map<String, Object> map);

    /**
     * 根据主订单更新主订单状态
     * @param map
     */
    int updateBaseOrderByOrder(Map<String, Object> map);

    /**
     * 获取场地票的验票规则
     * @param map
     * @return
     */
    Map<String,Object> getSiteTicket(Map<String, Object> map);
}