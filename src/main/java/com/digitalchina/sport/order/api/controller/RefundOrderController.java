package com.digitalchina.sport.order.api.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.StringUtil;
import com.digitalchina.sport.order.api.common.config.AlipayConfig;
import com.digitalchina.sport.order.api.common.config.ContextConstants;
import com.digitalchina.sport.order.api.common.config.JsonUtils;
import com.digitalchina.sport.order.api.model.AlipayRefundInfo;
import com.digitalchina.sport.order.api.service.MyOrderService;
import com.digitalchina.sport.order.api.service.RefundOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * （一句话描述）
 * 作用：
 *
 * @author 王功文
 * @date 2017/3/9 14:48
 * @see
 */
@Controller
@RequestMapping("/order/api/refundorder/")
public class RefundOrderController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RefundOrderController.class);
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private MyOrderService myOrderService;

    /**
     * 退款申请会传入  用户ID，总订单id 子订单id
     *
     * @param userId
     * @param orderId
     * @param orderContentId
     *
     * @return
     */
    @RequestMapping(value = "refundOrder.json", method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> refundOrder(@RequestParam(value = "userId", required = true) String userId,
                                       @RequestParam(value = "orderId", required = true) String orderId,
                                       @RequestParam(value = "orderContentId", required = true) String orderContentId) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(userId)) {
            userId = "4e16d3959e194774af7cd6090ed086e8";
            map.put("userId", userId);//用户id
        }
        if (StringUtil.isEmpty(orderId)) {
            orderId = "16bc1ac5579b430eafd481d62ed8bfdd";
            map.put("orderId", orderId);//总订单id
        }
        if (StringUtil.isEmpty(orderContentId)) {
            orderContentId = "291cf357c4f740d79915b228a1474e1d";
            map.put("orderContentId", orderContentId);//子订单id
        }
        try {
            //1.查询该子订单的详细信息（只有待使用的子订单可以退款）
            Map<String, Object> orderContent = refundOrderService.getorderContent(map);

            //2.验证规则  1.是否可退（0可以1不可以）2.在规定范围内可退 3.该订单是否失效及过期
            if (ContextConstants.TAKE_STATUS.equals(orderContent.get("take_status"))) {//已取票不可退
                RtnData.ok("已取票不可退");
            } else if (ContextConstants.CAN_RETREAT_NKT.equals(orderContent.get("can_retreat"))) {//标记为不可退票不可退
                RtnData.ok("该票不可退票");
            }

            //3.规则通过，修改该子订单的状态为待退款状态
//            map.put("status", ContextConstants.STATUS4);
//            refundOrderService.updateOrderContentDetail(map);
            //4.将退款信息添加到退款记录表中  该条退款信息状态为正在退款中


            //5.调用支付宝退款接口进行退款操作  此处可能会有等待时间  获取该订单商家的支付信息
            if (ContextConstants.PAY_TYPE1.equals(orderContent.get("pay_type").toString())) {
                String out_trade_no = orderContent.get("id").toString();//订单编号
                String trade_no = orderContent.get("order_number").toString();//订单交易号
                Double refund_amount = Double.parseDouble(orderContent.get("sell_price").toString());//该子订单金额
                //取得商户id,查询该商户的支付方式信息
                map.put("pay_type", orderContent.get("pay_type"));//支付方式  1.支付宝  2.微信
                map.put("merchant_id", orderContent.get("merchant_id"));//商户ID
                Map<String, Object> merchant_pay_account = refundOrderService.getMerchantPayAccount(map);
                Map<String, String> params = new HashMap<>();

                //需要的参数
                params.put("alipayurl", AlipayConfig.service);//获取接口地址
                params.put("partner", merchant_pay_account.get("partner_id").toString());//合作身份者id
                params.put("app_id", merchant_pay_account.get("app_id").toString());//应用id
                params.put("_input_charset", AlipayConfig.input_charset);//字符编码格式
                params.put("sign_type", AlipayConfig.sign_type);//加密方式
                params.put("content_type", "json");//传输类型
                params.put("private_key", merchant_pay_account.get("pay_key").toString());//支付宝商户私钥
                params.put("ali_public_key", AlipayConfig.alipay_public_key);//支付宝公共公钥
                String fromBack = alipayRefundRequest(out_trade_no, trade_no, refund_amount, params);
                if ("退款成功".equals(fromBack)) {
                    System.out.println("退款成功");
                } else {
                    System.out.println("退款失败");
                    LOGGER.error("-------------退款失败------------------");
                }

            }

            //


            //6.根据支付宝退款返回数据   修改子订单状态为已退款  修改退款记录表为已退款状态

            //7如果退款成功了要释放场地状态的，从已预订变成可预订

            //8.返回给前台数据  退款成功


            return RtnData.ok("111");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询订单包含的字单详情失败", e);
            return RtnData.fail("查询订单包含的字单详情失败");
        }
    }

    /**
     * @param out_trade_no  订单支付时传入的商户订单号,不能和 trade_no同时为空。
     * @param trade_no      支付宝交易号，和商户订单号不能同时为空
     * @param refund_amount 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
     *
     * @return
     *
     * @throws AlipayApiException
     */
    public String alipayRefundRequest(String out_trade_no, String trade_no, double refund_amount, Map<String, String> params) throws AlipayApiException {

        // 发送请求
        String strResponse = null;
        try {
            //开放平台SDK封装了签名实现，只需在创建DefaultAlipayClient对象时，设置请求网关(gateway)，
            // 应用id(app_id)，应用私钥(private_key)，编码格式(charset)，支付宝公钥(alipay_public_key)，
            // 签名类型(sign_type)即可，报文请求时会自动进行签名。
            AlipayClient alipayClient = new DefaultAlipayClient
                    (params.get("alipayurl"), params.get("app_id"),
                            params.get("private_key"), params.get("content_type"),
                            params.get("input_charset"), params.get("ali_public_key"),
                            params.get("sign_type"));
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayRefundInfo alidata = new AlipayRefundInfo();
            alidata.setOut_trade_no(out_trade_no);
            alidata.setRefund_amount(refund_amount);
            alidata.setTrade_no(trade_no);
            request.setBizContent(JsonUtils.toJSONString(alidata));
            AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) alipayClient.execute(request);
            strResponse = response.getCode();
            Thread.sleep(5000);//线程等待5秒
            if ("10000".equals(response.getCode())) {
                strResponse = "退款成功";
            } else {
                strResponse = response.getSubMsg();
            }
        } catch (Exception e) {
            LOGGER.error("请求退款失败", e);
        }
        return strResponse;
    }


}
