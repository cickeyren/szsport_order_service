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
	List<Map<String,Object>> getAllOrderByUserId(Map<String,Object> map) throws Exception;

	/**
	 * 根据用户id，按照状态查询所有订单个数
	 * @param map
	 * @return
	 */
	int getCountByUserId(Map<String,Object> map) throws Exception;

	/**
	 *  一个订单下的所有子订单详情
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getTotalOrderByUserIdAndOrderId(Map<String,Object> map) throws Exception;

	/**
	 *  一个订单下的所有子订单个数
	 * @param orderId
	 * @return
	 */
	int getCountByOrderId(String orderId) throws Exception;

	/**
	 * 订单详情
	 * @param orderId
	 * @return
	 */
	Map<String,Object> getOrderDetails(String orderId) throws Exception;

	/**
	 * 新增订单基本信息
	 * @param orderBaseInfo
	 */
	int inserOrderBaseInfo(Map<String,Object> orderBaseInfo) throws Exception;

	/**
	 * 判断订单流水号是否重复
 	 * @param orderNumber
	 * @return
	 */
	int isHaveByOrderNumer(String orderNumber) throws Exception;

	/**
	 * 新增子订单详情信息
	 * @param orderContentDetail
	 */
	int inserOrderContentDetail(Map<String,Object> orderContentDetail) throws Exception;

	/**
	 * 判断12位确认码是否重复
	 * @param orderCode
	 * @return
	 */
	int isHaveByOrderCode(String orderCode) throws Exception;

	/**
	 * 根据子场馆id查询分类id
	 * @param subStadiumId
	 * @return
	 */
	String getClassifyBySubStadiumId(String subStadiumId) throws Exception;

	/**
	 * 根据确认码查询订单详情
	 * @param orderCode
	 * @return
	 */
	Map<String,Object> getOrderDetailByOrderCode(String orderCode) throws Exception;

	/**
	 * 取票=====>>更新字单
	 * @param params
	 */
	int updateTake(Map<String,Object> params) throws Exception;

	/**
	 * 验票=====>>更新字单
	 * @param params
	 */
	int updateCheck(Map<String,Object> params) throws Exception;

	/**
	 * 支付相关等=====>>更新字单内容
	 * @param params
	 */
	int updateOrderContent(Map<String,Object> params) throws Exception;
	int updateOrderContentStatus(Map<String,Object> params) throws Exception;
	/**
	 * 支付相关等=====>>更新主订单内容
	 * @param params
	 */
	int updateOrderBase(Map<String,Object> params) throws Exception;

	/**
	 *  订单流水号查询订单
	 * @param orderNumber
	 * @return
	 */
	OrderBaseInfo getOrderByOrderNumer(String orderNumber) throws Exception;

	/**
	 * 订单流水号查询商户和订单内容
	 * @param
	 * @return
	 */
	Map<String,Object> getOrderAndMpByOrderNumer(Map<String,Object> params) throws Exception;
	/**
	 * 取消订单的字单，根据主订单的编号
	 * @param params
	 */
	int cancelOrderContent(Map<String,Object> params) throws Exception;

	/**
	 * 取消订单的主订单,根据订单的编号
	 * @param params
	 */
	int cancelOrderBase(Map<String,Object> params) throws Exception;
	/**
	 *  一个订单下的所有子订单id
	 * @param orderId
	 * @return
	 */
	List<Map<String,Object>> getAllOrderContentIdByOrderId(String orderId) throws Exception;
	/**
	 *  根据orderid查询订单是否存在
	 * @param orderId
	 * @return
	 */
	int isHaveByOrderId(String orderId) throws Exception;

	/**
	 * 新增子订单验票记录，只有年卡散票有
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int inserUsedRecords(Map<String,Object> params)throws Exception;

    /**
     * 定时任务中使用，更新订单状态，时间超过10分钟状态变为失效订单
     * @param params
     * @return
     * @throws Exception
     */
    int updateAllOrder(Map<String,Object> params)throws Exception;
    int updateAllOrderContent(Map<String,Object> params)throws Exception;

    /**
     * 定时任务中使用,每天24点更新次数票的剩余次数
     * @param params
     * @return
     * @throws Exception
     */
    int updateAllEveryRemain(Map<String,Object> params)throws Exception;

	/**
	 * 定时任务中使用,针对年票，只要是剩余次数还有，主单的状态就还是待使用，如果次数没有了，就变成已使用
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int updateOrderBaseStatus(Map<String,Object> params)throws Exception;
	int updateUnOrderBaseStatus(Map<String,Object> params)throws Exception;
	/**
	 * 根据商户id查询商户编号
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	String getMerchantNumber(String merchantId) throws Exception;

	/**
	 * 验票=====>>更新字单
	 * @param params
	 */
	int updateFieldContent(Map<String,Object> params) throws Exception;

	int getOrderCountByMap(String orderBaseId) throws Exception;

	int insertLockField(Map<String,Object> orderContentDetail) throws Exception;

	List<Map<String,Object>> getOrderContentListByOrderId(String orderId) throws Exception;

	int updateLockField(Map<String,Object> params) throws Exception;

	/**
	 * 根据状态获得订单流水号
	 * @param status
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> getOrderNumberByStatus(String status) throws Exception;

	/**
	 * -定时任务中使用,当订单超过了验票时间，状态变为已过期=8
	 * @param params
	 * @return
	 * @throws Exception
	 */
	int updateTimeOverSiteOrder(Map<String,Object> params) throws Exception;
	int updateTimeOverYearOrder(Map<String,Object> params) throws Exception;
}
