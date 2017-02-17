package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.utils.*;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.MyOrderDao;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import com.digitalchina.sport.order.api.model.OrderContentDetail;
import com.google.common.collect.Maps;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的订单service
 */
@Service
public class MyOrderService {
    @Autowired
    private MyOrderDao myOrderDao;
    @Autowired
    private PropertyConfig proConfig;
    /**
     * 根据用户id，按照状态查询所有订单
     * @param params
     * @return
     */
    public List<Map<String,Object>> getAllOrderByUserId(Map<String,Object> params) throws Exception{
        return myOrderDao.getAllOrderByUserId(params);
    }

    /**
     * 根据用户id，按照状态查询所有订单个数
     * @param params
     * @return
     */
    public int getCountByUserId(Map<String,Object> params) throws Exception{
        return myOrderDao.getCountByUserId(params);
    }

    /**
     * 一个订单下的所有子订单详情
     * @param params
     * @return
     */
    public List<Map<String,Object>> getTotalOrderByUserIdAndOrderId(Map<String,Object> params) throws Exception{
        return myOrderDao.getTotalOrderByUserIdAndOrderId(params);
    }

    /**
     * 一个订单下的所有子订单个数
     * @param orderId
     * @return
     */
    public int getCountByOrderId(String orderId) throws Exception{
        return myOrderDao.getCountByOrderId(orderId);
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    public Map<String,Object> getOrderDetails(String orderId) throws Exception{
        return myOrderDao.getOrderDetails(orderId);
    }

    /**
     * 新增订单基本信息
     * @param orderBaseInfo
     */
    public int inserOrderBaseInfo(Map<String,Object> orderBaseInfo) throws Exception{
        return myOrderDao.inserOrderBaseInfo(orderBaseInfo);
    }

    /**
     * 判断订单流水号是否重复
     * @param orderNumber
     * @return
     */
    public int isHaveByOrderNumer(String orderNumber) throws Exception{
        return myOrderDao.isHaveByOrderNumer(orderNumber);
    }

    /**
     * 新增子订单详情信息
     * @param orderContentDetail
     */
    public int inserOrderContentDetail(Map<String,Object> orderContentDetail) throws Exception{
        return myOrderDao.inserOrderContentDetail(orderContentDetail);
    }

    /**
     * 判断12位确认码是否重复
     * @param orderCode
     * @return
     */
    public int isHaveByOrderCode(String orderCode) throws Exception{
        return myOrderDao.isHaveByOrderCode(orderCode);
    }

    /**
     * 根据子场馆id查询分类id
     * @param subStadiumId
     * @return
     */
    public String getClassifyBySubStadiumId(String subStadiumId)throws Exception{
        return myOrderDao.getClassifyBySubStadiumId(subStadiumId);
    }

    /**
     * 根据确认码查询订单详情
     * @param orderCode
     * @return
     * @throws Exception
     */
    public Map<String, Object> getOrderDetailByOrderCode(String orderCode)throws Exception{
        return myOrderDao.getOrderDetailByOrderCode(orderCode);
    }

    /**
     * 取票=====>>更新字单
     * @param params
     */
    public boolean updateTake(Map<String,Object> params)throws Exception{
        if (myOrderDao.updateTake(params)>0){
            return true;
        }else return false;
    }

    /**
     * 支付相关等=====>>更新字单
     * 支付相关等=====>>更新主订单
     * @param params
     */
    @Transactional
    public int updateOrder(Map<String,Object> params)throws Exception{
        String orderNumber = params.get("orderNumber").toString();
        OrderBaseInfo ob = myOrderDao.getOrderByOrderNumer(orderNumber);
        if(ob != null){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("orderId",ob.getId());
            map.put("userId",ob.getUserId());
            List<Map<String,Object>> list = myOrderDao.getTotalOrderByUserIdAndOrderId(map);
            if(list.size()>0){
                for (int i=0 ;i<list.size();i++){
                    Map<String,Object> ocmap = new HashMap<String, Object>();
                    map.put("status",params.get("status").toString());
                    map.put("orderCode",list.get(i).get("order_code"));
                    if(!list.get(i).get("status").equals("4")){//订单状态为4退单的时候，不更新该订单状态
                        myOrderDao.updateOrderContent(ocmap);
                    }
                }
            }
        }
        return myOrderDao.updateOrderBase(params);
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
        String order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
        //根据订单号去查询是否存在，否则重新生成一个订单号
        int ishave =  myOrderDao.isHaveByOrderNumer(order_number);
        if (ishave > 0){
            order_number = UtilDate.getOrderNum()+UtilDate.getThree()+UtilDate.getThree();//当前日期加6位随机数作为订单流水号
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
    public void insertOrderContentDetail(int count,Map<String,Object> orderContentDetail) throws Exception{
        for (int i=0;i<count;i++){
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
    }

    /**
     * 根据子场馆id查询分类id
     * @param subStadiumId
     * @return
     */
    public String getClassifyById(String subStadiumId) throws Exception{
        return myOrderDao.getClassifyBySubStadiumId(subStadiumId);
    }

    /*
     * 根据yearStrategyId获取门票策略和场馆详情
     * @param yearStrategyId
     * @return
     * http://192.168.31.181:8080/yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId=1";
     */
    public Map<String,Object> getYearStrategyTicketModelInfo(String yearStrategyId){
        return HttpClientUtil.getMapResultByURLAndKey(proConfig.SPORT_RESOURCEMGR_URL
                +"yearstrategyticket/api/getYearStrategyTicketModelInfo.json?yearStrategyId="
                +yearStrategyId,"根据yearStrategyId获取门票策略和场馆详情");
    }

    /**
     * 从map中提取需要的订单基本数据
     * @return
     */
    public Map<String,Object> getOrderBaseInfoFromMap(int count,Map<String,Object> map) throws Exception{
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
     * 从map中提取需要的字单内容（验票规则等）
     * @return
     */
    public Map<String,Object> getOrderContentDetailFromMap(Map<String,Object> map) throws Exception{
        Map<String,Object> orderContentDetail = new HashMap<String, Object>();
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        orderContentDetail.put("startTime",yearStrategyDetail.get("orderEffectiveStartTime"));//有限期开始时间
        orderContentDetail.put("endTime",yearStrategyDetail.get("orderEffectiveEndTime"));//有效期截止时间
        orderContentDetail.put("canRetreat",yearStrategyDetail.get("orderRefundRule"));//是否可退（0可以1不可以）
        orderContentDetail.put("hoursLimit",yearStrategyDetail.get("checkLimitedHours"));////是否限时：比如限时2小时，-1表示不限时
        orderContentDetail.put("costPrice",yearStrategyDetail.get("costPrice"));//成本价
        orderContentDetail.put("sellPrice",yearStrategyDetail.get("sellPrice"));//售价
        orderContentDetail.put("totalNumber",yearStrategyDetail.get("checkTicketAvailableTimes"));//总可用次数（-1表示不限次数)
        orderContentDetail.put("everydayNumber",yearStrategyDetail.get("checkDailyLimitedTimes"));//每日限次
        //可用日期限制0：每日 1：每周
        String dateLimit = getTimeLimit(map).get("dateLimit").toString();
        orderContentDetail.put("dateLimit",dateLimit);
        //可用时间限制10:00-11:00
        String timeLimit = getTimeLimit(map).get("timeLimit").toString();
        orderContentDetail.put("timeLimit",timeLimit);
        //屏蔽日期
        String forbiddenDate = getForbiddenDate(map).get("forbiddenDate").toString();
        orderContentDetail.put("forbiddenDate",forbiddenDate);
        System.out.println("orderContentDetail="+orderContentDetail.toString());
        return orderContentDetail;
    }

    /**
     * 可用时间
     * @param map
     * @return
     */
    public Map<String,Object> getTimeLimit(Map<String,Object> map){
        String dateLimit = "";
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        String timeLimit = "";
        List<Map<String,Object>> checkUseableTimeList = (List<Map<String, Object>>) map.get("checkUseableTimeList");
        if(checkUseableTimeList.size()>0){
            String type = checkUseableTimeList.get(0).get("checkLimitedDateType").toString();
            if(type.equals("0")){
                dateLimit = type;
            }else if(type.equals("1")){
                //1:1,2,3,4,5,6(1表示每周，冒号后的数字为周数)
                dateLimit = type+":"+yearStrategyDetail.get("checkLimitedWeekDetails");
            }
        }
        if(checkUseableTimeList.size()>0){
            for (int i=0;i<checkUseableTimeList.size();i++){
                Map<String,Object> useableTime = checkUseableTimeList.get(i);
                //返回格式09:00:00$11:00:00,12:11:00$18:00:00
                timeLimit += useableTime.get("useableStartTime")+"$"+useableTime.get("useableEndTime")+",";
            }
            timeLimit = timeLimit.substring(0, timeLimit.length() - 1);
        }
        Map<String,Object> retMap = new HashMap<String, Object>();
        retMap.put("dateLimit",dateLimit);
        retMap.put("timeLimit",timeLimit);
        return retMap;
    }

    /**
     * 屏蔽日期
     * @param map
     * @return
     */
    public Map<String,Object> getForbiddenDate(Map<String,Object> map){
        String forbiddenDate = "";
        List<Map<String,Object>> checkShieldTimeList = (List<Map<String, Object>>) map.get("checkShieldTimeList");
        if(checkShieldTimeList.size()>0){
            for(int i=0;i<checkShieldTimeList.size();i++){
                Map<String,Object> date = checkShieldTimeList.get(i);
                //返回格式2017-02-13$2017-02-14,2017-02-08$2017-02-14
                forbiddenDate += date.get("shieldStartTime")+"$"+date.get("shieldEndTime")+",";
            }
            forbiddenDate = forbiddenDate.substring(0, forbiddenDate.length() - 1);
        }
        Map<String,Object> retMap = new HashMap<String, Object>();
        retMap.put("forbiddenDate",forbiddenDate);
        return retMap;
    }

    /**
     * 修改验票状态
     * @param map
     * @return
     */
    public int updateCheckByMap(Map<String,Object> map){
        Map<String,Object> orderDetails = myOrderDao.getOrderDetailByOrderCode(map.get("orderCode").toString());
        String totalNumber = orderDetails.get("total_number").toString();
        String remainNumber = orderDetails.get("remain_number").toString();
        String everydayNumber = orderDetails.get("everyday_number").toString();
        String everydayRemainNumber = orderDetails.get("everyday_remain_number").toString();
        if(StringUtil.isEmpty(totalNumber)){
            int total = Integer.parseInt(totalNumber);
            int remain = Integer.parseInt(remainNumber);
            int everyday = Integer.parseInt(everydayNumber);
            int everydayRemain = Integer.parseInt(everydayRemainNumber);
            if(total>0){
                if(checkRemainTime(remainNumber,everydayRemainNumber).equals("0")){
                    remain = remain -1;
                    map.put("remainNumber",remain);
                }
            }//total=-1不限次数不需要修改
            if(everyday>0){
                if(checkRemainTime(remainNumber,everydayRemainNumber).equals("0")) {
                    everydayRemain = everydayRemain - 1;
                    map.put("everydayRemainNumber", everydayRemain);
                }
            }//everyday=-1不限次数不需要修改
        }
        return myOrderDao.updateCheck(map);
    }

    /**
     * 根据验票规则验票
     * @param
     * @return
     */
    public Map<String,Object> checkTicket(String orderCode){
        Map<String,Object> retMap = new HashMap<String, Object>();
        Map<String,Object>  orderDetailMap = myOrderDao.getOrderDetailByOrderCode(orderCode);
        String dqtime = DateUtil.nowtime();//当前时间
        String dqdate = DateUtil.today();//当前日期
        //验票时间是否在有效期内:start_time<dqdate<end_time
        String startDate = orderDetailMap.get("start_time").toString();
        String endDate = orderDetailMap.get("end_time").toString();
        if(!StringUtil.isEmpty(startDate)&&!StringUtil.isEmpty(endDate)){
            if(DateUtil.compareDateTo(startDate,endDate,dqdate)){//公用的日期对比方法
                //验证验票时间是否在可用日期内
                String date_limit = orderDetailMap.get("date_limit").toString();
                if(!StringUtil.isEmpty(date_limit)){
                    String[] limitTypes = date_limit.split(":");
                    if(limitTypes.length>0){
                        String limitType = limitTypes[0];
                        if (limitType.equals("1")){//type=1表示每周
                            String[] limitDays = limitTypes[1].split(",");
                            int todayWeek = DateUtil.getWeekByDate(DateUtil.parseDate(dqdate));//获取当前日期的周数
                            //比较周数是否在可用时间的周数内
                            if(StringUtil.isContainSpcifyStr(todayWeek+"",limitDays)){
                                //验证验票时间是否在可用日期内
                                if (checkTime(dqtime,orderDetailMap.get("time_limit").toString())){
                                    //验证验票是否在不可用日期内
                                    if(!checkDate(dqdate,orderDetailMap.get("forbidden_date").toString())){
                                        //是否超过当日使用次数=====是否超过剩余次数
                                        //1=超过剩余次数0=验证通过2=超过当日使用次数
                                        String returnNumber = checkRemainTime(orderDetailMap.get("remain_number").toString(),orderDetailMap.get("everyday_remain_number").toString());
                                        if(returnNumber.equals("1")){
                                            retMap.put("returnKey","false");
                                            retMap.put("message","超过剩余次数");
                                        }else if(returnNumber.equals("2")){
                                            retMap.put("returnKey","false");
                                            retMap.put("message","超过当日使用次数");
                                        }else if(returnNumber.equals("0")){
                                            retMap.put("returnKey","true");
                                            retMap.put("message","验证通过");
                                        }
                                    }else{
                                        retMap.put("returnKey","false");
                                        retMap.put("message","验票时间在不可用日期内");
                                    }
                                }else {
                                    //验票时间不在可用时间内
                                    retMap.put("returnKey","false");
                                    retMap.put("message","验票时间不在可用时间内");
                                }
                            }else {
                                //验票时间不在可用时间内
                                retMap.put("returnKey","false");
                                retMap.put("message","验票时间不在可用时间内");
                            }
                        }else {
                            //type=0表示每日,不用判断周数
                            //验证验票时间是否在可用日期内
                            if (checkTime(dqtime,orderDetailMap.get("time_limit").toString())){
                                //验证验票是否在不可用日期内
                                if(!checkDate(dqdate,orderDetailMap.get("forbidden_date").toString())){
                                    //是否超过当日使用次数=====是否超过剩余次数
                                    String remain_number = orderDetailMap.get("remain_number").toString();
                                    String everyday_remain_number = orderDetailMap.get("forbidden_date").toString();
                                    //1=超过剩余次数0=验证通过2=超过当日使用次数
                                    String returnNumber = checkRemainTime(remain_number,everyday_remain_number);
                                    if(returnNumber.equals("1")){
                                        retMap.put("returnKey","false");
                                        retMap.put("message","超过剩余次数");
                                    }else if(returnNumber.equals("2")){
                                        retMap.put("returnKey","false");
                                        retMap.put("message","超过当日使用次数");
                                    }else if(returnNumber.equals("0")){
                                        retMap.put("returnKey","true");
                                        retMap.put("message","验证通过");
                                    }
                                }else{
                                    retMap.put("returnKey","false");
                                    retMap.put("message","验票时间在不可用日期内");
                                }
                            }else {
                                //验票时间不在可用时间内
                                retMap.put("returnKey","false");
                                retMap.put("message","验票时间不在可用时间内");
                            }
                        }
                    }
                }
            }else {
                //验票时间不在有效期内
                retMap.put("returnKey","false");
                retMap.put("message","验票时间不在有效期内");
            }
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 验证验票时间是否在可用时间段内
     */
    public boolean checkTime(String dqtime,String time_limit){
        boolean compareResult = false;
        if(!StringUtil.isEmpty(time_limit)){
            String[] time_limits = time_limit.split(",");
            if(time_limits.length>0){
                for(int i=0;i<time_limits.length;i++){
                    String[] timeToTime = time_limits[i].split("\\$");
                    if (timeToTime.length>0){
                        String startTime = timeToTime[0];
                        String endTime = timeToTime[1];
                        if(DateUtil.compareTimeTo(startTime,endTime,dqtime)){//公用的时间对比方法
                            compareResult = true;
                            break;
                        }
                    }
                }
            }
        }
        return compareResult;
    }
    /**
     * 验证验票时间是否在可用日期内
     */
    public boolean checkDate(String dqdate,String date){
        boolean compareResult = false;
        //2017-02-13$2017-02-24,2017-02-22$2017-03-11
        if(!StringUtil.isEmpty(date)){
            String[] dates = date.split(",");
            if(dates.length>0){
                for(int i=0;i<dates.length;i++){
                    String[] dateToDate = dates[i].split("\\$");
                    if (dateToDate.length>0){
                        String startDate = dateToDate[0];
                        String endDate = dateToDate[1];
                        if(DateUtil.compareDateTo(startDate,endDate,dqdate)) {//公用的日期对比方法
                            compareResult = true;
                            break;
                        }
                    }
                }
            }
        }
        return compareResult;
    }

    /**
     * 次数限制判断
     * @param remain_number
     * @param everyday_remain_number
     * @return 1=超过剩余次数0=验证通过2=超过当日使用次数3=其他错误
     */
    public String checkRemainTime(String remain_number,String everyday_remain_number){
        String compareResult = "";
        if(!StringUtil.isEmpty(remain_number) && !StringUtil.isEmpty(everyday_remain_number)){
            int remain = Integer.parseInt(remain_number);
            int everydayRemain = Integer.parseInt(everyday_remain_number);
            //是否超过当日使用次数
            if(everydayRemain > 0){
                //是否超过剩余次数
                if(remain < 0){
                    compareResult = "1";//1=超过剩余次数
                }else {
                    compareResult = "0";//0=验证通过
                }
            }else {
                compareResult = "2";//2=超过当日使用次数
            }
        }else {
            compareResult = "3";//3=其他错误
        }

        return compareResult;
    }

    /**
     * 订单流水号查询商户和订单内容
     * @param orderNumber
     * @return
     */
    public Map<String,Object> getOrderAndMpByOrderNumer(String orderNumber){
        return myOrderDao.getOrderAndMpByOrderNumer(orderNumber);
    }
}