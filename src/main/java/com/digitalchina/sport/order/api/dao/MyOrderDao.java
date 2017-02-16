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
	 *  一个订单下的所有子订单详情
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getTotalOrderByUserIdAndOrderId(Map<String,Object> map);

	/**
	 *  一个订单下的所有子订单个数
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
	int inserOrderBaseInfo(Map<String,Object> orderBaseInfo);

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
	int inserOrderContentDetail(Map<String,Object> orderContentDetail);

	/**
	 * 判断12位确认码是否重复
	 * @param orderCode
	 * @return
	 */
	int isHaveByOrderCode(String orderCode);

	/**
	 * 根据子场馆id查询分类id
	 * @param subStadiumId
	 * @return
	 */
	String getClassifyBySubStadiumId(String subStadiumId);

	/**
	 * 根据确认码查询订单详情
	 * @param orderCode
	 * @return
	 */
	Map<String,Object> getOrderDetailByOrderCode(String orderCode);

	/**
	 * 取票=====>>更新字单
	 * @param params
	 */
	int updateTake(Map<String,Object> params);

	/**
	 * 验票=====>>更新字单
	 * @param params
	 */
	int updateCheck(Map<String,Object> params);

	/**
	 * 支付相关等=====>>更新字单内容
	 * @param params
	 */
	int updateOrderContent(Map<String,Object> params);

	/**
	 * 支付相关等=====>>更新主订单内容
	 * @param params
	 */
	int updateOrderBase(Map<String,Object> params);

	/**
	 *  订单流水号查询订单
	 * @param orderNumber
	 * @return
	 */
	OrderBaseInfo getOrderByOrderNumer(String orderNumber);

	/**
	 * 订单流水号查询商户和订单内容
	 * @param orderNumber
	 * @return
	 */
	Map<String,Object> getOrderAndMpByOrderNumer(String orderNumber);
}
