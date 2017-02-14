package com.digitalchina.sport.order.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.RtnData;
import com.digitalchina.common.utils.BarcodeUtil;
import com.digitalchina.common.utils.HttpClientUtil;
import com.digitalchina.common.utils.UUIDUtil;
import com.digitalchina.common.utils.UtilDate;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import com.digitalchina.sport.order.api.service.MyOrderService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对通票的接口
 * 其他接口不能复用
 * 我的订单control
 */
@RestController
@RequestMapping("/order/api/myOrder/")
public class MyOrderController {

    //public static final Logger logger = LoggerFactory.getLogger(FieldController.class);
    @Autowired
    private PropertyConfig proConfig;
    @Autowired
    private MyOrderService myOrderService;

    /**
     * 根据用户id，按照状态查询所有订单
     * @return
     */
    @RequestMapping(value="getMyAllOrder.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> getMyAllOrder(@RequestParam(value = "pageIndex",defaultValue = "0" , required = false) int pageIndex,
                                         @RequestParam(value = "pageSize",defaultValue = "10", required = false) int pageSize,
                                         @RequestParam(value = "userId", required = false) String userId,
                                         @RequestParam(value = "status", required = false) String status) {
        Map<String,Object> map = new HashMap<String, Object>();
        if(pageIndex == 0){
            map.put("start",pageIndex);
        }else {
            map.put("start", (pageIndex - 1) * pageSize);
        }
        map.put("pageSize",pageSize);
        map.put("userId",userId);
        map.put("status",status);
        List<Map<String,Object>> list = myOrderService.getAllOrderByUserId(map);
        int count = myOrderService.getCountByUserId(map);
        Map<String,Object> reqMap=new HashMap<String, Object>();
        reqMap.put("list",list);
        reqMap.put("count",count);
        return RtnData.ok(reqMap);
    }

    /**
     * 获取一个订单中的所有子订单的详情
     * @return
     */
    @RequestMapping(value="getOrderDetails.json",method = RequestMethod.GET)
    @ResponseBody
    public RtnData<Object> getOrderDetails(@RequestParam(value = "userId", required = false) String userId,
                                  @RequestParam(value = "orderId", required = false) String orderId){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("orderId",orderId);
        List<Map<String,Object>> list = myOrderService.getTotalOrderByUserIdAndOrderId(map);
        int count = myOrderService.getCountByOrderId(orderId);
        Map<String,Object> orderDetails = myOrderService.getOrderDetails(orderId);
        Map<String,Object> reqMap=new HashMap<String, Object>();
        reqMap.put("list",list);
        reqMap.put("count",count);
        reqMap.put("orderDetails",orderDetails);
        return RtnData.ok(reqMap);
    }

    //根据策略的id获取门票的订票规则
    //根据策略的id获取门票的验票规则
    //根据策略的id获取门票的场馆信息

    /**
     * 将以上接口返回信息中的内容存入订单详情表
     * 生成订单
     * @param orderJson
     * @return
     */
    @RequestMapping(value="createOrder.json",method = RequestMethod.POST)
    @ResponseBody
    public RtnData<Object> createOrder(@RequestBody String orderJson){
        JSONObject orderJsonObject = JSON.parseObject(orderJson);

        String yearStrategyId = orderJsonObject.get("yearStrategyId").toString();
        int count = Integer.parseInt(orderJsonObject.get("count").toString());//订单下面的字单个数
        Map<String,Object> orderBaseInfo = getOrderBaseInfoStringFromMap(count,getYearStrategyTicketModelInfo(yearStrategyId));//订单基本信息
        Map<String,Object> orderContentDetail = getOrderContentDetailFromMap(getYearStrategyTicketModelInfo(yearStrategyId));//子订单详细信息
        //order基本信息
        orderBaseInfo.put("costPrice",orderBaseInfo.get("totalCostPrice").toString());
        orderBaseInfo.put("sellPrice",orderBaseInfo.get("totalSellPrice").toString());
        orderBaseInfo.put("userId",orderJsonObject.get("userId").toString());
        orderBaseInfo.put("userName",orderJsonObject.get("userName").toString());
        orderBaseInfo.put("userTel",orderJsonObject.get("userTel").toString());
        orderBaseInfo.put("orderChannel",orderJsonObject.get("orderChannel").toString());

        String orderId = this.insertOrderBaseInfo(orderBaseInfo);
        //order详细内容

        orderContentDetail.put("orderBaseId",orderId);//主订单的id
        System.out.println(orderContentDetail);
        for(int i=0;i<count;i++){
            this.insertOrderContentDetail(orderContentDetail);
        }

        return RtnData.ok("下单成功!");
    }

    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     * @return
     */
    public String insertOrderBaseInfo(Map<String,Object> orderBaseInfo) {
        //订单基本信息
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderBaseInfo.put("id",id);
        String order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        //根据订单号去查询是否存在，否则重新生成一个订单号
        int ishave =  myOrderService.isHaveByOrderNumer(order_number);
        if (ishave > 0){
            order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        }
        orderBaseInfo.put("orderNumber",order_number);
        orderBaseInfo.put("status","0");//订单状态为未支付

        //订单基本信息入库
        myOrderService.inserOrderBaseInfo(orderBaseInfo);
        return orderBaseInfo.get("id").toString();
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public void insertOrderContentDetail(Map<String,Object> orderContentDetail) {
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderContentDetail.put("id",id);
        String orderCode = UtilDate.getSix()+UtilDate.getSix();//12位随机数作确认码
        //根据确认码去查询是否存在，否则重新生成一个
        int ishave =  myOrderService.isHaveByOrderCode(orderCode);
        for (int i=0;i<3;i++){
            if (ishave > 0){
                orderCode = UtilDate.getSix()+UtilDate.getSix();
            }
        }
        orderContentDetail.put("orderCode",orderCode);
        orderContentDetail.put("status","0");//起始为待支付
        myOrderService.inserOrderContentDetail(orderContentDetail);
    }

    /**
     * 根据子场馆id查询分类id
     * @param subStadiumId
     * @return
     */
    public String getClassifyById(String subStadiumId) {
        return myOrderService.getClassifyBySubStadiumId(subStadiumId);
    }
    /*
     * 根据ticketid获取门票策略和场馆详情
     * @param yearStrategyId
     * @return
     * http://192.168.31.181:8080/yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId=1";
     */
    public Map<String,Object> getYearStrategyTicketModelInfo(String yearStrategyId) {
        return HttpClientUtil.getMapResultByURLAndKey(proConfig.SPORT_RESOURCEMGR_URL
                + "yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId="+yearStrategyId,
                "根据ticketid获取门票策略和场馆详情");
    }

    /**
     * 从map中提取需要的订单数据
     * @return
     */
    public Map<String,Object> getOrderBaseInfoStringFromMap(int count,Map<String,Object> map){
        Map<String,Object> orderBaseInfo = new HashMap<String, Object>();
        List<Map<String,Object>> studStadiumList = (List<Map<String,Object>>) map.get("studStadiumList");
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        Map<String,Object> stadiumInfo = new HashMap<String,Object>();
        if(studStadiumList.size()>0){
            stadiumInfo = studStadiumList.get(0);
        }
        String classify = getClassifyById(stadiumInfo.get("subStadiumId").toString());
        int totalCostPrice = Integer.parseInt(yearStrategyDetail.get("sellPrice").toString())*count;
        int totalSellPrice = Integer.parseInt(yearStrategyDetail.get("sellPrice").toString())*count;
        orderBaseInfo.put("stadiumName",stadiumInfo.get("mainStdiumName"));
        orderBaseInfo.put("classify",classify);
        orderBaseInfo.put("subStadiumName",stadiumInfo.get("subStdiumName"));
        orderBaseInfo.put("ticketName",yearStrategyDetail.get("ticketName"));
        orderBaseInfo.put("ticketId",yearStrategyDetail.get("id"));
        orderBaseInfo.put("ticketType",yearStrategyDetail.get("ticketType"));
        orderBaseInfo.put("merchantId",yearStrategyDetail.get("merchantId"));

        orderBaseInfo.put("totalCostPrice",totalCostPrice);
        orderBaseInfo.put("totalSellPrice",totalSellPrice);
        orderBaseInfo.put("sonOrders",count);
        System.out.println("orderBaseInfo="+orderBaseInfo.toString());
        return orderBaseInfo;
    }
    /**
     * 从map中提取需要的订单数据
     * @return
     */
    public Map<String,Object> getOrderContentDetailFromMap(Map<String,Object> map){
        Map<String,Object> orderContentDetail = new HashMap<String, Object>();
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        orderContentDetail.put("startTime",yearStrategyDetail.get("orderEffectiveStartTime"));
        orderContentDetail.put("endTime",yearStrategyDetail.get("orderEffectiveEndTime"));
        orderContentDetail.put("costPrice",yearStrategyDetail.get("costPrice"));
        orderContentDetail.put("sellPrice",yearStrategyDetail.get("sellPrice"));
        orderContentDetail.put("totalNumber",yearStrategyDetail.get("checkTicketAvailableTimes"));
        orderContentDetail.put("everydayNumber",yearStrategyDetail.get("checkDailyLimitedTimes"));
        System.out.println("orderContentDetail="+orderContentDetail.toString());
        return orderContentDetail;
    }

    /**
     * 根据验票码获取条形码图片
     */
    public Object getBarCodeByOrderCode(String orderCode){
        //BarcodeUtil.generateFile();
        return "";
    }
}
