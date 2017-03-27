package com.digitalchina.sport.order.api.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayEbppPdeductSignValidateModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.HttpClientUtil;
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
import com.sun.org.apache.xpath.internal.SourceTree;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    @RequestMapping(value = "refundOrder.json")
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
                        String start_time = orderContent.get("start_time").toString();//开始时间
                        String end_time = orderContent.get("end_time").toString();//结束时间
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
                            String responceMessage = refundOrderService.alipayRefundRequestwumi(out_trade_no, trade_no, refund_amount, params);


                            System.out.println("=============================="+responceMessage);
                            if ("10000".equals(responceMessage)) {
                                //5.修改该子订单的状态为已退款状态
                                map.put("statusAll", Integer.parseInt(ContextConstants.STATUS6));//
                                System.out.println("---------------------------"+map);
                                if (refundOrderService.updateOrderForOrder(map) > 0) {
                                    List<Map<String,Object>>  res =  myOrderService.getTotalOrderByUserIdAndOrderId(map);
                                    Boolean flag = true;
                                    for (Map<String,Object> m :res) {
                                        if (ContextConstants.STATUS6.equals(m.get("status"))){
                                            flag = true;
                                        }else {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    //如果所有数据都为5 ，表面全部为退款状态
                                    if (flag==true){
                                        map.put("infostatus","4");//修改主订单订单状态为全部退款状态
                                        refundOrderService.updateBaseOrderByOrder(map);
                                    }{

                                    }
                                    System.out.println(orderContentId + "订单已为已退款状态");
                                }
                                System.out.println(">>>>>>>>>>>>>支付宝退款成功<<<<<<<<<<<<<<");

                                map.put("Basestatus",ContextConstants.BASESTATUS1);
                                //更改主订购的状态为已退款状态
                                refundOrderService.updateBaseOrder(map);

                                return RtnData.ok("支付宝退款成功");

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
                    if (aplyaoder != null) {
                        //2.验证规则  1.是否可退（0可以1不可以）2.在规定范围内可退 3.该订单是否失效及过期
                        //获取场地票验证规则
                        map.put("ticket_type",orderContent.get("ticket_type"));
                        Map<String,Object> orderSiteTicket = refundOrderService.getSiteTicket(map);

                        if (ContextConstants.SITETATUS0.equals(orderSiteTicket.get("order_refund_rule"))){
                            return RtnData.fail("该票为不可退票");
                        }
                        if (ContextConstants.SITETATUS2.equals(orderSiteTicket.get("order_refund_rule"))){
                            //开场前多少小时可以退款
                            String no_refund_time =   orderSiteTicket.get("no_refund_time").toString();
                            Long refund_time = Long.parseLong(no_refund_time);

                            String date_limit = orderContent.get("date_limit").toString();//获取子单的生效日期
                            String time_limit = orderContent.get("time_limit").toString();//获取字单的生效时间
                            String time_li = time_limit.substring(0,time_limit.indexOf("$"));//截取开始时间
                            String data_time = date_limit+" "+time_li;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            Date d1 = df.parse(data_time);
                            Date d2 = new Date();
                            if (d1.getTime()<d2.getTime()){
                                return RtnData.fail("该票已经超过退票规定的时间");
                            }
                            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
                            long days = diff / (1000 * 60 * 60 * 24);

                            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
                            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
                            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");

                            Double timeAll = (Double.valueOf(days)*24)+Double.valueOf(hours)+(Double.valueOf(minutes)/60);//获取当前时间里验票时间总小时

                            if (timeAll-refund_time<0){
                                return RtnData.fail("开场前"+refund_time+"小时不可退");
                            }
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
                            String responceMessage = refundOrderService.alipayRefundRequestwumi(out_trade_no, trade_no, refund_amount, params);


                            System.out.println("=============================="+responceMessage);
                            if ("10000".equals(responceMessage)) {
                                //5.修改该子订单的状态为已退款状态
                                map.put("statusAll", Integer.parseInt(ContextConstants.STATUS6));//
                                System.out.println("---------------------------"+map);
                                if (refundOrderService.updateOrderForOrder(map) > 0) {
                                    List<Map<String,Object>>  res =  myOrderService.getTotalOrderByUserIdAndOrderId(map);
                                    Boolean flag = true;
                                    for (Map<String,Object> m :res) {
                                        if (ContextConstants.STATUS5.equals(m.get("status"))){
                                            flag = true;
                                        }else {
                                            flag = false;
                                        }
                                    }
                                    if (flag==true){
                                        map.put("infostatus","4");//修改主订单订单状态为全部退款状态
                                        refundOrderService.updateBaseOrderByOrder(map);
                                    }
                                    System.out.println(orderContentId + "订单已为已退款状态");
                                }
                                System.out.println(">>>>>>>>>>>>>支付宝退款成功<<<<<<<<<<<<<<");
                                map.put("Basestatus",ContextConstants.BASESTATUS1);
                                //更改主订购的状态为已退款状态
                                refundOrderService.updateBaseOrder(map);
                                return RtnData.ok("支付宝退款成功");

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

}
