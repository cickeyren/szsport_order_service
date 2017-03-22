package com.digitalchina.sport.order.api.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.StringUtil;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.sport.order.api.common.config.AlipayConfig;
import com.digitalchina.sport.order.api.common.config.AlipayNotify;
import com.digitalchina.sport.order.api.common.config.ContextConstants;
import com.digitalchina.sport.order.api.common.config.JsonUtils;
import com.digitalchina.sport.order.api.service.MyOrderService;
import com.digitalchina.sport.order.api.service.RefundOrderService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.apache.ibatis.transaction.Transaction;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    @RequestMapping(value = "refundOrder.json", method = RequestMethod.POST)
    @ResponseBody
    public synchronized RtnData<Object> refundOrder(@RequestParam(value = "userId", required = true) String userId,
                                       @RequestParam(value = "orderId", required = true) String orderId,
                                       @RequestParam(value = "orderContentId", required = true) String orderContentId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Gson gson = new Gson();
        if (StringUtil.isEmpty(userId)) {
            userId = "4e16d3959e194774af7cd6090ed086e8";
            map.put("userId", userId);//用户id
        } else {
            map.put("userId", userId);//用户id
        }
        if (StringUtil.isEmpty(orderId)) {
            orderId = "16bc1ac5579b430eafd481d62ed8bfdd";
            map.put("orderId", orderId);//总订单id
        } else {
            map.put("orderId", orderId);//总订单id
        }
        if (StringUtil.isEmpty(orderContentId)) {
            orderContentId = "291cf357c4f740d79915b228a1474e1d";
            map.put("orderContentId", orderContentId);//子订单id
        } else {
            map.put("orderContentId", orderContentId);//子订单id
        }
        try {
            //1.查询该子订单的详细信息（只有待使用的子订单可以退款）
            Map<String, Object> orderContent = refundOrderService.getorderContent(map);
            if (orderContent != null) {
                //查询已支付订单详情
                map.put("order_number", orderContent.get("order_number").toString().trim());
                Map<String, Object> aplyaoder = refundOrderService.getAplyOrder(map);

                /*--------散票/年卡退款------------start--------------------------------------------*/
                if (ContextConstants.TICKET_TYPE0.equals(orderContent.get("ticket_type"))) {
                    if (aplyaoder != null) {
                        //2.验证规则  1.是否可退（0可以1不可以）2.在规定范围内可退 3.该订单是否失效及过期
                        if (ContextConstants.TAKE_STATUS.equals(orderContent.get("take_status"))) {//已取票不可退
                            RtnData.fail("已取票不可退");
                        } else if (ContextConstants.CAN_RETREAT_NKT.equals(orderContent.get("can_retreat"))) {//标记为不可退票不可退
                            RtnData.fail("该票不可退票");
                        }

                        //3.规则通过，修改该子订单的状态为待退款状态
                        map.put("statusAll", Integer.parseInt(ContextConstants.STATUS4));
                        map.put("order_detailid", orderContent.get("order_detailid"));
                        int num1 = refundOrderService.updateOrderAll(map);
                        if (num1 > 0) {
                            System.out.println(orderContentId + "修改子订单状态为待退款状态成功！");
                        }

                        //5.调用支付宝退款接口进行退款操作  获取该订单商家的支付信息
                        if (ContextConstants.PAY_TYPE1.equals(orderContent.get("pay_type").toString())) {
                            String out_trade_no = aplyaoder.get("out_trade_no").toString().trim();//订单编号
                            String trade_no = aplyaoder.get("trade_no").toString().trim();//订单交易号
                            Double refund_amount = Double.parseDouble(orderContent.get("sell_price").toString());//该子订单金额
                            //根据支付方式查询出对应的商户基本数据
                            map.put("pay_type", orderContent.get("pay_type"));//支付方式  1.支付宝  2.微信
                            map.put("merchant_id", orderContent.get("merchant_id"));//商户ID
                            Map<String, Object> merchant_pay_account = refundOrderService.getMerchantPayAccount(map);
                            Map<String, String> params = new HashMap<>();

                            //需要的参数
                            params.put("alipayurl", "https://mapi.alipay.com/gateway.do");//获取接口地址
                            params.put("partner", merchant_pay_account.get("partner_id").toString().trim());//合作身份者id
                            params.put("app_id", merchant_pay_account.get("app_id").toString().trim());//应用id
                            params.put("_input_charset", AlipayConfig.input_charset);//字符编码格式
                            params.put("sign_type", AlipayConfig.sign_type);//加密方式
                            params.put("content_type", "json");//传输类型
                            params.put("private_key", merchant_pay_account.get("pay_key").toString().trim());//支付宝商户私钥
                            params.put("ali_public_key", AlipayConfig.alipay_public_key);//支付宝公共公钥
                            params.put("refund_method", orderContent.get("pay_type").toString());
                            params.put("userId", userId);
                            params.put("orderId", orderId);
                            params.put("orderContentId", orderContentId);

                            //支付宝无密退款公共方法
                            String responceMessage = alipayRefundRequestwumi(out_trade_no, trade_no, refund_amount, params);
                            System.out.println("=============================="+responceMessage);
                            if ("10000".equals(responceMessage)) {
                                //5.修改该子订单的状态为已退款状态
                                map.put("statusAll", Integer.parseInt(ContextConstants.STATUS6));//
                                if (refundOrderService.updateOrderAll(map) > 0) {
                                    System.out.println(orderContentId + "订单已为已退款状态");
                                }
                                System.out.println(">>>>>>>>>>>>>支付宝退款成功<<<<<<<<<<<<<<");

                                //6如果退款成功了要释放场地状态的，从已预订变成可预订

                            } else {
                                map.put("statusAll", Integer.parseInt(ContextConstants.STATUS7));
                                map.put("order_detailid", orderContent.get("order_detailid"));
                                if (refundOrderService.updateOrderAll(map) > 0) {
                                    System.out.println(orderContentId + "订单已为退款失败状态");
                                }
                                LOGGER.error("-------------支付宝退款失败------------------");
                                return RtnData.fail("支付宝退款失败，请检查订单！");
                            }

                        } else if (ContextConstants.PAY_TYPE2.equals(orderContent.get("pay_type").toString())) {
                            //微信支付退款写在此处

                        }

                    } else {
                        return RtnData.fail("该订单无支付信息！");
                    }
                }
                /*--------散票/年卡退款------------end--------------------------------------------*/


                /*--------场地票退款------------start--------------------------------------------*/
                if (ContextConstants.TICKET_TYPE1.equals(orderContent.get("ticket_type"))) {

                }
                /*--------场地票退款------------end--------------------------------------------*/


                /*--------散票退款------------start--------------------------------------------*/
                if (ContextConstants.TICKET_TYPE2.equals(orderContent.get("ticket_type"))) {

                }
                /*--------散票退款------------end--------------------------------------------*/


            } else {
                return RtnData.fail("查询子订单信息失败，请检查传入参数");
            }
            return RtnData.ok("退款成功");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("该订单信息无可退款信息，请检查该订单信息", e);
            return RtnData.fail("该订单信息无可退款信息，请检查该订单信息");
        }
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
    private String alipayRefundRequestwumi(String out_trade_no, String trade_no, Double refund_amount, Map<String, String> params) throws AlipayApiException {

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

                JSONObject refundDate = JSONObject.fromObject(response.getBody());

                //准备参数  不管成功与否，都将数据存到退款记录表中
                param.put("id", UUID.randomUUID().toString());
                param.put("code", refundDate.get("code"));
                param.put("msg", refundDate.get("msg"));
                param.put("buyer_logon_id", refundDate.get("buyer_logon_id"));
                param.put("buyer_user_id", "");
                param.put("fund_change", refundDate.get("fund_change"));
                param.put("gmt_refund_pay", refundDate.get("gmt_refund_pay"));
                param.put("open_id", refundDate.get("open_id"));
                param.put("out_trade_no", refundDate.get("out_trade_no"));
                param.put("trade_no", refundDate.get("trade_no"));
                param.put("trade_no", refundDate.get("refund_fee"));
                param.put("refund_monery", refund_amount);
                param.put("refund_method", "1");
                param.put("userId", params.get("userId"));
                param.put("orderId", params.get("orderId"));
                param.put("orderContentId", params.get("orderContentId"));
                int totalnum = refundOrderService.insertRefundOrder(param);
                if (totalnum > 0) {
                    System.out.println(">>>>>>>>>>>>>>>退款信息已添加到数据表中<<<<<<<<<<<<<<<<<<<");
                }

            } else {
                strResponse = response.getSubMsg();
            }
        } catch (Exception e) {
            LOGGER.error("请求退款失败", e);
        }
        return strResponse;
    }



}
