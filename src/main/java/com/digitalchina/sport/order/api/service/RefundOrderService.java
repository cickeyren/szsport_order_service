package com.digitalchina.sport.order.api.service;

import com.digitalchina.sport.order.api.dao.RefundOrderDao;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * （一句话描述）
 * 作用：
 *
 * @author 王功文
 * @date 2017/3/9 14:47
 * @see
 */
@Service
public class RefundOrderService {

    @Autowired
    private RefundOrderDao refundOrderDao;

    /**
     * 通过信息查询子订单信息
     * @param map
     * @return
     */
    public Map<String,Object> getorderContent(Map<String, Object> map) {
        return  refundOrderDao.getorderContent(map);
    }

    /**
     * 更新子订单的状态
     * @param map
     * @return
     */
    public int updateOrderAll(Map<String, Object> map) {
        return refundOrderDao.updateOrderAll(map);
    }

    /**
     * 获取支付相应的支付方式
     * @param map
     * @return
     */
    public Map<String,Object> getMerchantPayAccount(Map<String, Object> map) {
        return refundOrderDao.getMerchantPayAccount(map);
    }

    /**
     * 根據订单号查询支付的订单信息
     * @param map
     * @return
     */
    public Map<String,Object> getAplyOrder(Map<String, Object> map) {
        return refundOrderDao.getAplyOrder(map);
    }

    /**
     * 添加数据到退款记录表中
     * @param params
     * @return
     */
    public int insertRefundOrder(Map<String, Object> params) {
        return refundOrderDao.insertRefundOrder(params);
    }

    /**
     * 更新子单状态
     * @param map
     * @return
     */
    public int updateOrderForOrder(Map<String, Object> map) {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+map);
        return  refundOrderDao.updateOrderForOrder(map);
    }

}
