package com.digitalchina.sport.order.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * （一句话描述）
 * 作用：
 *
 * @author 王功文
 * @date 2017/3/13 18:41
 * @see
 */
@Controller
@RequestMapping("/SendMessageController")
public class SendMessageController {
//    private final static Logger LOGGER = LoggerFactory.getLogger(SendMessageController.class);
//
//    /**
//     * 短信接口
//     *
//     * @param strreciveMobiles //接受短信的手机号码
//     * @param content          //接受短信内容
//     * @param userName         //发送人姓名
//     *
//     * @return
//     */
//    @RequestMapping(value = "/sendMessage.json", method = RequestMethod.GET ,headers = { "Accept=application/json;charset=UTF-8" })
//    @ResponseBody
//    public RtnData<Object> sendMessage(HttpServletRequest request) throws UnsupportedEncodingException {
//        //短信接口地址，需要在内网调试 可直接访问http://10.32.0.126:10010/MessageService.asmx检测网络是否通畅
//        String url = "http://10.32.0.126:10010/MessageService.asmx";//提供接口的地址
//        request.setCharacterEncoding("utf-8");
//        String soapaction = "http://tempuri.org/";   //域名，这是在server定义的
//        String  strreciveMobiles = request.getParameter("strreciveMobiles");
//        String  content = request.getParameter("content");
//        String  userName = request.getParameter("userName");
//
//        Service service = new Service();
//        try {
//            Call call = (Call) service.createCall();
//            call.setTargetEndpointAddress(url);
//            call.setOperationName(new QName(soapaction, "SendMessageForJava")); //设置要调用哪个方法
//            call.addParameter(new QName(soapaction, "sendMobile"), // 发送短信的内部短号，无线覆盖固定使用600032899998
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);
//            call.addParameter(new QName(soapaction, "strreciveMobiles"), // 接收短信息的手机号码
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);
//            call.addParameter(new QName(soapaction, "content"), //短信内容
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);
//            call.addParameter(new QName(soapaction, "deptCode"), //发送短信部门编号 体育局默认320500003028
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);
//            call.addParameter(new QName(soapaction, "userName"), //发送人姓名
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);
//            call.setReturnType(new QName(soapaction, "SendMessageForJava"), String.class); //要返回的数据类型（自定义类型）
//            call.setUseSOAPAction(true);
//            call.setSOAPActionURI(soapaction + "SendMessageForJava");
//            String rspXML = (String) call.invoke(new Object[]{"600032899998", strreciveMobiles, content, "320500003028", userName});
//            System.out.println("return value is " + rspXML);
//            return RtnData.ok("发送短信成功");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            LOGGER.error("========发送短信失败=========", ex);
//        }
//        return RtnData.fail("发送短信失败");
//    }

}
