package com.digitalchina.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.pay.alipay.config.AlipayConfig;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.PayTradeDao;
import com.digitalchina.sport.order.api.model.AlipayTradeModel;
import com.digitalchina.sport.order.api.service.CurriculumService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yang_ on 2017/5/23.
 */
@RestController
@RequestMapping(value = "/order/api/alipayCurriculum")
public class AlipayPayCurriculumController {
    private Log logger = LogFactory.getLog(AlipayPayCurriculumController.class);
    @Autowired
    private CurriculumService curriculumService;
    @Autowired
    private PayTradeDao payTradeDao;
    @Autowired
    private PropertyConfig config;

    /**
     * 根据订单ID生成支付宝相关支付信息
     *
     * @param orderNumber
     * @throws IOException
     */
    @RequestMapping(value = "/returnPayInfo.json")
    @ResponseBody
    public Object returnPay(@RequestParam(required = true) String orderNumber, String userId) throws Exception {
        Map<String, Object> ordermap = new HashMap();
        ordermap.put("orderNumber", orderNumber);
        ordermap.put("payWay", "1");//支付宝方式，2微信，以后扩展
        ordermap.put("userId", userId);//支付宝方式，2微信，以后扩展
        Map<String, Object> order=null;
        try {
             order = curriculumService.getCurriculumOrderbyOrderNumber(ordermap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(order)) {
            return RtnData.fail(orderNumber + "订单不存在!");
        }
        if ("1".equals(order.get("status"))) {
            return RtnData.fail(orderNumber + "该订单已经支付无法再次支付!");
        }
        if ("4".equals(order.get("status"))) {
            return RtnData.fail(orderNumber + "该订单已经失效!");
        }
        String privateKey = (String) order.get("payKey");
        String outTradeNo = UtilDate.getOrderNum() + UtilDate.getThree();
        AlipayTradeModel alipayTradeModel = payTradeDao.selectAlipayTradeModelByOutTradeNo(outTradeNo);
        if (null != alipayTradeModel) {
            //生成订单如果订单存在则继续生成订单，尝试10次否则放弃
            for (int i = 0; i < 10; i++) {
                outTradeNo = UtilDate.getOrderNum() + UtilDate.getThree();
                alipayTradeModel = payTradeDao.selectAlipayTradeModelByOutTradeNo(outTradeNo);
                if (null == alipayTradeModel) {
                    break;
                }
            }
        }

        if (null != alipayTradeModel) {
            return RtnData.fail("生成支付宝订单失败请稍后再试!");
        }

        String subjectContent = order.get("name") + "-" + order.get("className") + "报名";
        String partner = "partner=" + "\"" + order.get("partnerId") + "\"";//合作商户id
        String seller_id = "&seller_id=" + "\"" + order.get("sellerId") + "\"";//"sztt2016@sina.cn";
        String out_trade_no = "&out_trade_no=" + "\"" + outTradeNo + "\"";
        String subject = "&subject=" + "\"" + subjectContent + "\"";
        String body = "&body=" + "\"" + "报名" + "\"";
        String total_fee = "&total_fee=" + "\"" + order.get("fee") + "\"";
        String notify_url = "&notify_url=" + "\"" + config.ALIPAY_NOTIFY_CURRICULUM_URL + "\"";
        String service = "&service=" + "\"" + "mobile.securitypay.pay" + "\"";
        String payment_type = "&payment_type=\"1\"";
        String _input_charset = "&_input_charset=" + "\"" + AlipayConfig.input_charset + "\"";
        String it_b_pay = "&it_b_pay=" + "\"" + "90m" + "\"";
        String return_url = "&return_url=\"m.alipay.com\"";
        String reqContent = partner +
                seller_id +
                out_trade_no +
                subject +
                body +
                total_fee +
                notify_url +
                service +
                payment_type +
                _input_charset +
                it_b_pay +
                return_url;
        String sign = "";
        String signType = (String) order.get("signType");
        try {
            if ("RSA".equals(order.get("signType"))) {
                sign = AlipaySignature.rsaSign(reqContent, privateKey, AlipayConfig.input_charset);
            } else if ("RSA2".equals(order.get("signType"))) {
                sign = AlipaySignature.rsa256Sign(reqContent, privateKey, AlipayConfig.input_charset);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("==============支付宝生成订单签名时发生异常=================", e);
            return RtnData.fail("支付宝生成订单签名时发生异常!");
        }

//        String newsign =  "&sign=" +"\""+ java.net.URLEncoder.encode(AlipaySignature.rsaSign(reqContent,privateKey,AlipayConfig.input_charset),AlipayConfig.input_charset) +"\"" ;
        String newsign = "&sign=" + "\"" + java.net.URLEncoder.encode(sign, AlipayConfig.input_charset) + "\"";
        String sign_type = "&sign_type=" + "\"" + signType + "\"";
        String reqNew = reqContent + newsign + sign_type;
        //生成支付宝订单
        alipayTradeModel = new AlipayTradeModel();
        alipayTradeModel.setOutTradeNo(outTradeNo);
        alipayTradeModel.setOrderNumber(orderNumber);
        alipayTradeModel.setBody(subjectContent);
        alipayTradeModel.setSubject(subjectContent);
        alipayTradeModel.setUserId((String) order.get("userId"));
        alipayTradeModel.setPartnerId((String) order.get("partnerId"));
        if (payTradeDao.insertAlipayTradeModel(alipayTradeModel) > 0) {
            return RtnData.ok(reqNew);
        }
        return RtnData.fail("支付宝生成订单签名时发生异常!");
    }

    @RequestMapping(value = "/notify.json")
    public void alipayCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("========================支付宝回调通知=================");
        PrintWriter out = response.getWriter();
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        logger.error(params);
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号
        String out_trade_no = request.getParameter("out_trade_no");
        //支付宝交易号
        String trade_no = request.getParameter("trade_no");
        //交易状态
        String trade_status = request.getParameter("trade_status");
        String buyer_id = request.getParameter("buyer_id");
        String body = request.getParameter("body");
        String notify_time = request.getParameter("notify_time");
        String subject = request.getParameter("subject");
        String discount = request.getParameter("discount");
        String buyer_email = request.getParameter("buyer_email");
        String gmt_create = request.getParameter("gmt_create");
        String price = request.getParameter("price");
        String total_fee = request.getParameter("total_fee");
        String seller_id = request.getParameter("seller_id");
//		String trade_status = "TRADE_FINISHED";
        logger.error("========================" + trade_status + "=================");
        AlipayTradeModel tradeVo = null;
        try {
            tradeVo = payTradeDao.selectAlipayTradeModelByOutTradeNo(out_trade_no);
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.error("========================查询订单时发生错误=================", e1);
            return;
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //判断回调参数是否包含refund_status字段
        if (params.containsKey("refund_status")) {
            logger.error("进入退款----------支付宝退款成功（第一次条件判断）=================");
            return;
        } else {
            if (com.digitalchina.pay.alipay.util.AlipayNotify.verify(params, tradeVo.getPartnerId())) {
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if (trade_status.equals("TRADE_FINISHED")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                } else if (trade_status.equals("TRADE_SUCCESS")) {

                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    logger.error("进入支付----------=================");
                    if (null != tradeVo) {
                        tradeVo.setOutTradeNo(out_trade_no);
                        tradeVo.setTradeNo(trade_no);
                        tradeVo.setAlipayStatus(trade_status);
                        tradeVo.setBuyerId(buyer_id);
                        tradeVo.setBody(body);
                        tradeVo.setNotifyTime(notify_time);
                        tradeVo.setSubject(subject);
                        tradeVo.setDiscount(discount);
                        tradeVo.setBuyerEmail(buyer_email);
                        tradeVo.setGmtCreate(gmt_create);
                        tradeVo.setPrice(price);
                        tradeVo.setTotalFee(total_fee);
                        tradeVo.setSellerId(seller_id);
                        tradeVo.setLocalSatus("1");
                        System.out.println(params.toString());
                        try {
                            //更新支付状态
                            if (payTradeDao.updateAlipayTradeModel(tradeVo) == 0) {
                                logger.error("========================更新支付状态发生错误=================");
                            } else {
                                logger.error("========================更新支付宝流水订单成功=================");
                            }
                            //更新用户状态
                            Map<String, Object> orderUpdateMap = new HashMap<String, Object>();
                            String orderNumber = tradeVo.getOrderNumber();
                            orderUpdateMap.put("orderNumber", tradeVo.getOrderNumber());
                            orderUpdateMap.put("remarks", "支付更新订单状态status=1");
                            orderUpdateMap.put("status", "1");
                            orderUpdateMap.put("payType", "1");
                            orderUpdateMap.put("payAcount", buyer_email);//置为押金已付款
                            orderUpdateMap.put("payPrice", total_fee);
                            orderUpdateMap.put("payTime", gmt_create);
//                                if(orderService.updateOrder(orderUpdateMap) == 0) {
//                                    logger.error("========================更新订单【"+orderNumber+"】状态发生错误=================");
//                                } else {
//                                    logger.error("========================更新订单【"+orderNumber+"】状态成功=================");
//                                }

                            logger.error("退出支付----------=================");
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("========================支付成功后业务处理发生错误=================", e);
                        }
                    } else {
                        logger.error("========================查询到订单为空=================");
                    }
                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的


                } else if (trade_status.equals("TRADE_CLOSED")) {
                    logger.error("进入退款==================支付宝主单金额已全部退款完毕=================");
                    return;
                }

                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

                out.println("success");    //请不要修改或删除

                //////////////////////////////////////////////////////////////////////////////////////////
            } else {//验证失败
                logger.error("========================支付宝回调验签失败=================");
                out.println("fail");
            }
        }
        out.flush();
        out.close();
    }
}
