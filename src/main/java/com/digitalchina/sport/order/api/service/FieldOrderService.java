package com.digitalchina.sport.order.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.utils.*;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.MyOrderDao;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 我的订单service
 */
@Service
public class FieldOrderService {
    @Autowired
    private MyOrderDao myOrderDao;
    @Autowired
    private PropertyConfig proConfig;
    /*
     * 根据ticketId获取门票策略和场馆详情
     * @param ticketId
     * @return
     * http://localhost:8080/siteTicket/api/getSiteTicketInfoToOrder.json?ticketId=513c75c6163f4476ba3a79fca90ddd51
     */
    public Map<String,Object> getSiteTicketInfoToOrder(String ticketId){
        return HttpClientUtil.getMapResultByURLAndKey(proConfig.SPORT_RESOURCEMGR_URL
                +"siteTicket/api/getSiteTicketInfoToOrder.json?ticketId="
                +ticketId,"根据ticketId获取门票策略和场馆详情");
    }

    public Map<String,Object> createFieldOrder(String orderJson){
        JSONObject orderJsonObject = JSON.parseObject(orderJson);
        Map<String,Object> retMap = new HashMap<String, Object>();
        //orderJson:ticketId门票信息，date日期，时段（多个）list(场地fieldId,价格price,时间times)
        //日期，不可跨天买
        String date = orderJsonObject.get("date").toString();
        //门票，是否为空
        if(!StringUtil.isEmpty(orderJsonObject.get("ticketId"))){
            //订单基本信息
            String ticketId = orderJsonObject.get("ticketId").toString();

            Map<String,Object> siteTicketInfo = this.getSiteTicketInfoToOrder(ticketId);//提供接口门票信息,获取订单所需字段；
            //从门票详细中获取
            //预定规则：minimum_time_limit起订时限，最少选择几个时间段
            //预定规则：site_num_limit限订场次数，最多选择几个场次
            String timesCount = orderJsonObject.get("timesCount").toString();
            //============>>判断预定规则
            Map<String,Object> checkTimesCountMap = CheckTimesCount(timesCount,siteTicketInfo);
            if(!checkTimesCountMap.get("returnKey").equals("false")){
                Map<String,Object> orderBaseInfo =  this.getOrderBaseInfoFromMap(siteTicketInfo);//从门票详细中提取订单基本信息
                //order基本信息
                if (orderBaseInfo!=null) {

                    //前端传入基本信息
                    orderBaseInfo.put("userId",orderJsonObject.get("userId").toString());
                    orderBaseInfo.put("userName",orderJsonObject.get("userName").toString());
                    orderBaseInfo.put("userTel",orderJsonObject.get("userTel").toString());
                    orderBaseInfo.put("orderChannel",orderJsonObject.get("orderChannel").toString());
                    //价格
                    orderBaseInfo.put("sellPrice",orderJsonObject.get("totalSellPrice").toString());

                    try {
                        //子订单详细信息(多个)
                        List<Map<String,Object>> timeList = (List<Map<String, Object>>) orderJsonObject.get("list");
                        //字单个数
                        if(!StringUtil.isEmpty(timeList)){
                            orderBaseInfo.put("sonOrders",timeList.size());
                            //添加主订单内容到数据库返回orderBase表id
                            String orderBaseId = this.insertOrderBaseInfo(orderBaseInfo);
                            for (int i=0;i<timeList.size();i++){
                                try {
                                    //order详细内容
                                    Map<String,Object> orderContentDetail = this.getOrderContentDetailFromMap(date,siteTicketInfo,timeList.get(i));
                                    orderContentDetail.put("orderBaseId",orderBaseId);//主订单的id
                                    //添加子订单内容到数据库返回orderBase表id
                                    this.insertOrderContentDetail(orderContentDetail);
                                    //锁定该场地、该日期、该时间段的场地状态为1：已锁定
                                    this.lockField(date,timeList);



                                    System.out.println("orderContentDetail======"+orderContentDetail);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    retMap.put("returnKey","false");
                                    retMap.put("returnMessage","订单详细信息不全,下单失败!");
                                }
                            }
                        }else {
                            retMap.put("returnKey","false");
                            retMap.put("returnMessage","未选择场次,下单失败!");
                        }

                        System.out.println("orderBaseInfo======"+orderBaseInfo);

                        retMap.put("returnKey","true");
                        retMap.put("returnMessage","下单成功!");
                        retMap.put("orderId",orderBaseInfo.get("id"));//订单编号
                        retMap.put("orderNumber",orderBaseInfo.get("orderNumber"));//订单流水号
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","订单基本信息不全,下单失败!");
                }

            }else retMap = checkTimesCountMap;
        }else {
            retMap.put("returnKey","false");
            retMap.put("returnMessage","场地票策略为空!");
        }
        System.out.print(retMap);
        return retMap;
    }
    public Map<String,Object> getOrderBaseInfoFromMap(Map<String,Object> siteTicketInfo){
        Map<String,Object> orderBaseInfo = new HashMap<String, Object>();

        orderBaseInfo.put("subStadiumId",siteTicketInfo.get("subStadiumId"));
        orderBaseInfo.put("stadiumName",siteTicketInfo.get("mainStdiumName"));
        orderBaseInfo.put("classify",siteTicketInfo.get("classify"));
        orderBaseInfo.put("subStadiumName",siteTicketInfo.get("subStadiumName"));
        orderBaseInfo.put("ticketName",siteTicketInfo.get("ticketName"));
        orderBaseInfo.put("ticketId",siteTicketInfo.get("id"));
        orderBaseInfo.put("ticketType",siteTicketInfo.get("ticketType"));
        orderBaseInfo.put("merchantId",siteTicketInfo.get("merchantId"));
        //orderBaseInfo.put("peopleNumLimit",siteTicketInfo.get("peopleNumLimit"));限用人数，暂时不做
        //orderBaseInfo.put("checkTicketWay",siteTicketInfo.get("checkTicketWay"));验票方式，暂时不做
        System.out.println("orderBaseInfo="+orderBaseInfo.toString());
        return orderBaseInfo;
    }
    /**
     * 从map中提取需要的字单内容（验票规则等）
     * @return
     */
    public Map<String,Object> getOrderContentDetailFromMap(String date,Map<String,Object> siteTicketInfo,Map<String,Object> timeDetail) throws Exception{
        Map<String,Object> orderContentDetail = new HashMap<String, Object>();
        //适用场地
        String fieldId = (String) timeDetail.get("fieldId");
        orderContentDetail.put("fieldId",fieldId);
        //适用日期 String date
        orderContentDetail.put("dateLimit",date);
        //适用时间 "timeInter":"7:00-9:00",
        orderContentDetail.put("timeLimit",timeDetail.get("timeInter"));
        //时段编号
        orderContentDetail.put("timeIntervalId",timeDetail.get("timeIntervalId"));
        //售价
        orderContentDetail.put("sellPrice",timeDetail.get("sellPrice"));//售价
        //验票规则
        orderContentDetail.put("canRetreat",siteTicketInfo.get("orderRefundRule"));//是否可退（0可以1不可以）
        orderContentDetail.put("approachTime",siteTicketInfo.get("approachTime"));//可提前多少分钟进场
        orderContentDetail.put("departureTime",siteTicketInfo.get("departureTime"));//可退后多少时间离场
        System.out.println("orderContentDetail="+orderContentDetail.toString());
        return orderContentDetail;
    }


    public Map<String,Object> CheckTimesCount(String timesCount,Map<String,Object> siteTicketInfo){
        Map<String,Object> retMap = new HashMap<String, Object>();
        if(!StringUtil.isEmpty(timesCount)){
            int count = Integer.parseInt(timesCount);
            int minTimesNum = -1;
            if(!StringUtil.isEmpty(siteTicketInfo.get("minimumTimeLimit"))) {
                minTimesNum = Integer.parseInt(siteTicketInfo.get("minimumTimeLimit").toString());
            }//-1不判断
            if (minTimesNum != -1) {
                if(count >= minTimesNum){
                    int maxTimesNum = -1;
                    if(!StringUtil.isEmpty(siteTicketInfo.get("siteNumLimit"))) {
                        maxTimesNum = Integer.parseInt(siteTicketInfo.get("siteNumLimit").toString());
                    }//-1不判断
                    if (maxTimesNum != -1) {
                        if(count<=maxTimesNum){
                            retMap.put("returnKey","true");
                            retMap.put("returnMessage","可下单!");
                        }else{
                            retMap.put("returnKey","false");
                            retMap.put("returnMessage","超过限订场次,下单失败!");
                        }
                    }else{//-1不判断
                        retMap.put("returnKey","true");
                        retMap.put("returnMessage","可下单!");
                    }
                }else {
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","未达到起订时限,下单失败!");
                }
            }else{//-1不判断
                retMap.put("returnKey","true");
                retMap.put("returnMessage","可下单!");
            }
        }else {
            retMap.put("returnKey","false");
            retMap.put("returnMessage","未选择场次,下单失败!");
        }

        System.out.println(retMap);
        return retMap;
    }

    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     * @return
     */
    public String insertOrderBaseInfo(Map<String,Object> orderBaseInfo) throws Exception{
        //订单基本信息
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderBaseInfo.put("id",id);
        String merchantId = orderBaseInfo.get("merchantId").toString();
        String merchantNumber = myOrderDao.getMerchantNumber(merchantId);
        String order_number = merchantNumber + UtilDate.getOrderNum()+UtilDate.getSix();//商户编号+日期+随机数作为订单流水号
        //根据订单号去查询是否存在，否则重新生成一个订单号
        int ishave =  myOrderDao.isHaveByOrderNumer(order_number);
        if (ishave > 0){
            order_number = merchantNumber + UtilDate.getOrderNum()+UtilDate.getSix();//当前日期加6位随机数作为订单流水号
        }
        orderBaseInfo.put("orderNumber",order_number);
        orderBaseInfo.put("status","0");//订单状态为未支付

        //订单基本信息入库
        myOrderDao.inserOrderBaseInfo(orderBaseInfo);
        return orderBaseInfo.get("id").toString();
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public void insertOrderContentDetail(Map<String,Object> orderContentDetail) throws Exception{
        String id = UUIDUtil.generateUUID();//uuid生成32位随机数，作为订单id
        orderContentDetail.put("id",id);
        String orderCode = UtilDate.getSix()+UtilDate.getSix();//12位随机数作确认码
        //根据确认码去查询是否存在，否则重新生成一个
        int ishave =  myOrderDao.isHaveByOrderCode(orderCode);
        for (int j=0;j<3;j++){
            if (ishave > 0){
                orderCode = UtilDate.getSix()+UtilDate.getSix();
            }
        }
        orderContentDetail.put("orderCode",orderCode);
        orderContentDetail.put("status","0");//起始为待支付
        myOrderDao.inserOrderContentDetail(orderContentDetail);
    }

    /**
     * 锁定该场地、该日期、该时间段的场地状态为1：已锁定
     * @param
     */
    public void lockField(String date,List<Map<String,Object>> timeList) throws Exception{
        for (int i=0;i<timeList.size();i++){
            String fieldId = (String) timeList.get(i).get("fieldId");
            if (StringUtil.isEmpty(timeList.get(i).get("timeIntervalId"))){
                String timeIntervalId[] = timeList.get(i).get("timeIntervalId").toString().split(",");
                for (int j=0;j<timeIntervalId.length;j++){
                    String id = UUIDUtil.generateUUID();//uuid生成32位随机数
                    Map<String,Object> param = new HashMap<String, Object>();
                    param.put("id",id);
                    param.put("fieldId",fieldId);
                    param.put("timeIntervalId",timeIntervalId[j]);
                    param.put("orderDate",date);
                    param.put("status","1");
                    myOrderDao.insertLockField(param);
                }
            }
        }
    }
    /**
     * 支付完成场地为2已订购
     * @param
     */
    public void updateLockField(String orderNumber,String status){
        try {
            OrderBaseInfo ob = myOrderDao.getOrderByOrderNumer(orderNumber);
            String orderBaseId = ob.getId();
            List<Map<String,Object>> orderContentList = myOrderDao.getOrderContentListByOrderId(orderBaseId);
            for (int i=0;i<orderContentList.size();i++){
                String fieldId = (String) orderContentList.get(i).get("fieldId");
                if (!StringUtil.isEmpty(orderContentList.get(i).get("timeIntervalId"))){
                    String timeIntervalId[] = orderContentList.get(i).get("timeIntervalId").toString().split(",");
                    for (int j=0;j<timeIntervalId.length;j++){
                        Map<String,Object> param = new HashMap<String, Object>();
                        param.put("fieldId",fieldId);
                        param.put("timeIntervalId",timeIntervalId[j]);
                        param.put("orderDate",orderContentList.get(i).get("dateLimit"));
                        param.put("status",status);//2已订购
                        myOrderDao.updateLockField(param);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 验票
     * 验证是否付款，是否失效等
     场地票验票逻辑
     step1：闸机和场馆的匹配
            闸机是否开启
     step2：场馆是否闭馆
     step3：状态（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
     step4：验证该票是否属于该场馆
     step5：验证该票进场时间是否超时
     * @return
     */
    public Map<String,Object> checkFieldTicket(String orderCode) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object>  orderDetailMap = myOrderDao.getOrderDetailByOrderCode(orderCode);
            String status = (String) orderDetailMap.get("status");
            //状态（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
            if(status.equals("1")){
                retMap = CheckDate(orderDetailMap);
            }else if(status.equals("0")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单未支付");
            }else if(status.equals("2")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已使用");//目前不做，增加限用人次判断
            }else if(status.equals("3")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单支付失败");
            }else if(status.equals("4")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已退款");//退款目前不做，之后可拓展为待退款和已退款
            }else if(status.equals("5")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已失效");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 验证该票进场日期是否吻合
     * dqdate
     * @param orderDetailMap
     * @return
     * @throws Exception
     */
    public Map<String,Object> CheckDate(Map<String,Object> orderDetailMap) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        String dqtime = DateUtil.nowtime();//当前时间
        String dqdate = DateUtil.today();//当前日期
        if(!StringUtil.isEmpty(orderDetailMap.get("date_limit"))){
            String dateLimit = orderDetailMap.get("date_limit").toString();
            if(!dqdate.equals(dateLimit)){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该时段不可用,请稍候尝试!");
            }else {
                //=======通过，判断下一步
                retMap = CheckApproachTime(dqdate,dqtime,orderDetailMap);
            }
        }else {
            retMap.put("returnKey","false");
            retMap.put("returnMessage","该时段不可用,请稍候尝试!");
        }
        System.out.println(retMap);
        return retMap;
    }
    /**
     * 验证该票进场时间是否超时
     * ApproachTime
     * @param orderDetailMap
     * @return
     * @throws Exception
     */
    public Map<String,Object> CheckApproachTime(String dqdate,String dqtime,Map<String,Object> orderDetailMap) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        //07:00:00$09:30:00,9:45:00$12:15:00
        if (!StringUtil.isEmpty(orderDetailMap.get("time_limit"))){
            String timeLimits = orderDetailMap.get("time_limit").toString();
            String timeToTimes[] =timeLimits.split(",");
            if (timeToTimes.length>0){
                for (int i=0;i<timeToTimes.length;i++){
                    String timeToTime[] = timeToTimes[i].split("\\$");
                    if (timeToTime.length>0){
                        String startTime = timeToTime[0];
                        String endTime = timeToTime[1];
                        if(!StringUtil.isEmpty(orderDetailMap.get("approach_time"))) {
                            int approachTime = Integer.parseInt((String) orderDetailMap.get("approach_time"));
                            if (approachTime != -1) {
                                startTime = DateUtil.offsiteDateTime(dqdate + " "+ startTime, Calendar.MINUTE, -approachTime);
                            }
                            if (DateUtil.compareTimeTo(startTime, endTime, dqtime)) {//公用的时间对比方法
                                retMap.put("returnKey", "true");
                                retMap.put("returnMessage", "验票通过，该时段可用!");
                            } else {
                                retMap.put("returnKey", "false");
                                retMap.put("returnMessage", "该时段不可用,请稍候尝试!");
                            }
                        }else {
                            retMap.put("returnKey", "false");
                            retMap.put("returnMessage", "数据有误请联系管理员!");
                        }
                    }else {
                        retMap.put("returnKey","false");
                        retMap.put("returnMessage","该时段不可用,请稍候尝试!");
                    }
                }
            }else {
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该时段不可用,请稍候尝试!");
            }
        }else {
            retMap.put("returnKey","false");
            retMap.put("returnMessage","该时段不可用,请稍候尝试!");
        }
        System.out.println(retMap);
        return retMap;
    }
    /**
     * 修改验票状态
     * @param map
     * @return
     */
    public int updateCheckByMap(Map<String,Object> map) throws Exception{
        int ret;
        try {
            map.put("remarks","场地票验票");
            ret = myOrderDao.updateFieldContent(map);

            Map<String,Object> orderDetails = myOrderDao.getOrderDetailByOrderCode(map.get("orderCode").toString());
            String orderBaseId=(String) orderDetails.get("order_base_id");
            String status = (String) myOrderDao.getOrderDetails(orderBaseId).get("status");//获得主单状态
            if (status.equals("1")){//主单是待使用状态才需要判断
                int count = myOrderDao.getOrderCountByMap(orderBaseId);//一个已付款的订单下的所有待使用的子订单个数
                if (count ==0){//当个数为0的时候修改主单为已使用
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("status","2");//状态改为已使用
                    params.put("remarks","订单已使用");
                    myOrderDao.cancelOrderBase(params);
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 失效订单，释放场地状态。0：可预订
     * @param
     */
    public void updateSxLockField() throws Exception{
        try {
            //获取所有失效订单
            List<Map<String,Object>> list = myOrderDao.getOrderNumberByStatus("5");
            if (list.size()>0) {
                for (int i=0;i<list.size();i++){
                    if (!StringUtil.isEmpty(list.get(i).get("orderNumber"))){
                        String orderNumber = (String) list.get(i).get("orderNumber");
                        this.updateLockField(orderNumber,"0");//失效订单。0：可预订
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}