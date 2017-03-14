package com.digitalchina.sport.order.api.service;

import com.digitalchina.sport.order.api.dao.RefundOrderDao;
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
    public int updateOrderContentDetail(Map<String, Object> map) {
        return refundOrderDao.updateOrderContentDetail(map);
    }

    /**
     * 获取支付相应的支付方式
     * @param map
     * @return
     */
    public Map<String,Object> getMerchantPayAccount(Map<String, Object> map) {
        return refundOrderDao.getMerchantPayAccount(map);
    }
}
