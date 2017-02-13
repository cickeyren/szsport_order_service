package com.digitalchina.sport.order.api.dao;

import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 我的订单
 */
@Mapper
public interface MyOrderDao {
	/**
	 * 根据用户id，按照状态查询所有订单
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getAllOrderByUserId(Map<String,Object> map);

	/**
	 * 根据用户id，按照状态查询所有订单个数
	 * @param map
	 * @return
	 */
	int getCountByUserId(Map<String,Object> map);

	/**
	 *  一个订单下的所有子订单（条形码）详情
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getTotalOrderByUserIdAndOrderId(Map<String,Object> map);

	/**
	 *  一个订单下的所有子订单（条形码）个数
	 * @param orderId
	 * @return
	 */
	int getCountByOrderId(String orderId);

	/**
	 * 订单详情
	 * @param orderId
	 * @return
	 */
	Map<String,Object> getOrderDetails(String orderId);

	/**
	 * 新增订单基本信息
	 * @param orderBaseInfo
	 */
	void inserOrderBaseInfo(OrderBaseInfo orderBaseInfo);

	/**
	 * 判断订单流水号是否重复
 	 * @param orderNumber
	 * @return
	 */
	int isHaveByOrderNumer(String orderNumber);

	/**
	 * 新增子订单详情信息
	 * @param orderContentDetail
	 */
	void inserOrderContentDetail(OrderContentDetail orderContentDetail);

	/**
	 * 判断12位确认码是否重复
	 * @param orderCode
	 * @return
	 */
	int isHaveByOrderCode(String orderCode);
}
