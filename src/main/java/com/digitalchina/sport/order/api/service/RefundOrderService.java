package com.digitalchina.sport.order.api.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.sport.order.api.dao.RefundOrderDao;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        return  refundOrderDao.updateOrderForOrder(map);
    }

    /**
     * 无密退款调用方式
     *
     * @param trade_no
     * @param refund_amount
     * @param params
     *
     * @return
     */
    @Transactional
    public synchronized String alipayRefundRequestwumi(String out_trade_no, String trade_no, Double refund_amount, Map<String, String> params) throws AlipayApiException {

        // 发送请求
        String strResponse = null;
        try {
            String alipayurl = params.get("alipayurl");
            String app_id = params.get("app_id");
            String private_key = params.get("private_key");
            String content_type = params.get("content_type");
            String input_charset = params.get("_input_charset");
            String ali_public_key = params.get("ali_public_key");
            String sign_type = params.get("sign_type");

            Gson gson = new Gson();

            AlipayClient alipayClient = new DefaultAlipayClient(alipayurl, app_id, private_key, content_type,
                    input_charset, ali_public_key, sign_type);
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            Map<String, Object> refundMap = new HashMap<String, Object>();
            refundMap.put("out_trade_no", out_trade_no);
            refundMap.put("trade_no", trade_no);
            refundMap.put("out_request_no", trade_no + "_" + UtilDate.getSix());//订单流水号+6位随机数
            refundMap.put("refund_amount", refund_amount);
            refundMap.put("refund_reason", "");
            String reqStr = gson.toJson(refundMap);
            request.setBizContent(reqStr);
            AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) alipayClient.execute(request);
            System.out.println(">>>>>>>>>>>>>>第一次调用<<<<<<<<<<");
//            Thread.sleep(6000);//获取等待时间

            Map<String, Object> param = new HashMap<>();
            if ("10000".equals(response.getCode())) {
                strResponse = "10000";

//                JSONObject refundDate = JSONObject.fromObject(response.getBody());
//
//                //准备参数  不管成功与否，都将数据存到退款记录表中
//                param.put("id", UUID.randomUUID().toString());
//                param.put("code", refundDate.get("code"));
//                param.put("msg", refundDate.get("msg"));
//                param.put("buyer_logon_id", refundDate.get("buyer_logon_id"));
//                param.put("buyer_user_id", "");
//                param.put("fund_change", refundDate.get("fund_change"));
//                param.put("gmt_refund_pay", refundDate.get("gmt_refund_pay"));
//                param.put("open_id", refundDate.get("open_id"));
//                param.put("out_trade_no", refundDate.get("out_trade_no"));
//                param.put("trade_no", refundDate.get("trade_no"));
//                param.put("trade_no", refundDate.get("refund_fee"));
//                param.put("refund_monery", refund_amount);
//                param.put("refund_method", "1");
//                param.put("userId", params.get("userId"));
//                param.put("orderId", params.get("orderId"));
//                param.put("orderContentId", params.get("orderContentId"));
////                int totalnum = refundOrderDao.insertRefundOrder(param);
////                if (totalnum > 0) {
////                    System.out.println(">>>>>>>>>>>>>>>退款信息已添加到数据表中<<<<<<<<<<<<<<<<<<<");
////                }

            } else {
                strResponse = response.getSubMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse;
    }


    /**
     * 更新主订单的退款状态
     * @param map
     * @return
     */
    public int updateBaseOrder(Map<String, Object> map) {
        return refundOrderDao.updateBaseOrder(map);
    }

    /**
     * 查询主订单下子订单状态已退款的订单数量
     * @param map
     * @return
     */
    public int getNumsByOrderID(Map<String, Object> map) {
        return refundOrderDao.getNumsByOrderID(map);
    }

    /**
     * 根据主订单查询子订单
     * @param map
     * @return
     */
    public List<Map<String,Object>> getByOrderID(Map<String, Object> map) {
        return  refundOrderDao.getByOrderID(map);
    }

    /**
     * 更新主订单  订单状态
     * @param map
     */
    public int updateBaseOrderByOrder(Map<String, Object> map) {
        return refundOrderDao.updateBaseOrderByOrder(map);
    }

    /**
     * 根据用户及主订单 场地类型 获取场地票的退票规则
     * @param map
     * @return
     */
    public Map<String,Object> getSiteTicket(Map<String, Object> map) {
        return refundOrderDao.getSiteTicket(map);
    }
}
