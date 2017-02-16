package com.digitalchina.sport.order.api.controller.pay;

import com.alipay.util.AlipayNotify;
import com.digitalchina.sport.order.api.dao.PayTradeDao;
import com.digitalchina.sport.order.api.model.AlipayTradeModel;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝回调通知控制器
 * @Author:yangyt
 * @Description
 * @Date:Created in 10:58 2017/2/16
 */

@RestController
@RequestMapping(value = "/alipay")
public class AlipayPayController {
    @Autowired
    private PayTradeDao payTradeDao;
    @Autowired
    private MyOrderService orderService;
//    @Autowired
//    private UserDao userDao;
    private Log logger = LogFactory.getLog(AlipayPayController.class);
    @RequestMapping(value = "/notify.json")
    public void alipayCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("========================支付宝回调通知=================");
        PrintWriter out = response.getWriter();
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
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
        logger.error("========================"+trade_status+"=================");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        if(AlipayNotify.verify(params)){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
            } else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                AlipayTradeModel tradeVo = null;
                try {
                    tradeVo = payTradeDao.selectAlipayTradeModelByOutTradeNo(out_trade_no);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logger.error("========================查询订单时发生错误=================",e1);
                }
                if( null != tradeVo) {
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
                    System.out.println(params.toString());
                    try {
                        //更新支付状态
                        if(payTradeDao.updateAlipayTradeModel(tradeVo) == 0) {
                            logger.error("========================更新支付状态发生错误=================");
                        }
                        //更新用户状态
                        Map<String, Object> orderUpdateMap = new HashMap<String,Object>();
                        orderUpdateMap.put("payType","1");
                        orderUpdateMap.put("payAcount", buyer_email);//置为押金已付款
                        orderUpdateMap.put("payPrice", total_fee);
                        if(orderService.updateOrder(orderUpdateMap) > 0) {
                            logger.error("========================更新用户状态发生错误=================");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("========================支付成功后业务处理发生错误=================",e);
                    }
                } else {
                    logger.error("========================查询到订单为空=================");
                }
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            out.println("success");	//请不要修改或删除

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//验证失败
            logger.error("========================支付宝回调验签失败=================");
            out.println("fail");
        }
        out.flush();
        out.close();
    }
    }