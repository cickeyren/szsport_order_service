package com.digitalchina.sport.order.api.dao;

import com.digitalchina.sport.order.api.model.AlipayTradeModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 支付Dao
 */
@Mapper
public interface PayTradeDao {
	/**
	 * 生成支付宝流水号
	 * @param alipayTradeModel
	 * @return
     */
	int insertAlipayTradeModel(AlipayTradeModel alipayTradeModel);

	/**
	 * 根据本地订单outTradeNo去查询支付宝流水单
	 * @param outTradeNo
	 * @return
     */
	AlipayTradeModel selectAlipayTradeModelByOutTradeNo(String outTradeNo);

	/**
	 * 更新支付宝订单状态
	 * @param alipayTradeModel
	 * @return
     */
	int updateAlipayTradeModel(AlipayTradeModel alipayTradeModel);
}
