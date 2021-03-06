package com.digitalchina.sport.order.api.service;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.common.utils.*;
import com.digitalchina.sport.order.api.common.config.PropertyConfig;
import com.digitalchina.sport.order.api.dao.MyOrderDao;
import com.digitalchina.sport.order.api.model.OrderBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 我的订单service
 */
@Service
public class MyOrderService {
    @Autowired
    private MyOrderDao myOrderDao;
    @Autowired
    private PropertyConfig proConfig;
    @Autowired
    private FieldOrderService fieldOrderService;
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
                    String orderContentId = (String) list.get(i).get("id");
                    String status = (String) list.get(i).get("status");
                    ocmap.put("status",params.get("status").toString());
                    ocmap.put("remarks",params.get("remarks").toString());
                    ocmap.put("orderContentId",orderContentId);
                    if (status.equals("0")){
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
            if (StringUtil.isEmpty(orderContentDetail.get("startTime"))){
                orderContentDetail.remove("startTime");
            }
            if (StringUtil.isEmpty(orderContentDetail.get("endTime"))){
                orderContentDetail.remove("endTime");
            }
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
        return HttpClientUtil.getMapResultByURLAndKey(proConfig.SPORT_RESOURCE_URL
                +"api/ticket/getYearStrategyTicketModelInfo.json?yearStrategyId="
                +yearStrategyId,"根据yearStrategyId获取门票策略和场馆详情");
    }

    /**
     * 下单接口
     */
    public Map<String,Object> createOrder(JSONObject orderJsonObject) throws Exception {
        Map<String,Object> retMap = new HashMap<String, Object>();
        if(StringUtil.isEmpty(orderJsonObject.get("yearStrategyId"))){
            retMap.put("returnKey","false");
            retMap.put("returnMessage","年卡策略为空!");
        }else if(StringUtil.isEmpty(orderJsonObject.get("count"))){
            retMap.put("returnKey","false");
            retMap.put("returnMessage","购买个数为空!");
        }else{
            String yearStrategyId = orderJsonObject.get("yearStrategyId").toString();
            int count = Integer.parseInt(orderJsonObject.get("count").toString());//订单下面的字单个数
            List<Map<String,Object>> studStadiumList = (List<Map<String, Object>>) this.getYearStrategyTicketModelInfo(yearStrategyId).get("studStadiumList");
            Map<String,Object> yearStrategyDetail = (Map<String, Object>) this.getYearStrategyTicketModelInfo(yearStrategyId).get("yearStrategyDetail");
            if(StringUtil.isEmpty(yearStrategyDetail)){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该年卡策略不存在!");
            }else if (studStadiumList.size()<=0){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该年卡策略有误!");
            }else if(count <= 0){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","购买个数必须大于0!");
            }else{
                //订单基本信息
                Map<String,Object> orderBaseInfo = this.getOrderBaseInfoFromMap(count,this.getYearStrategyTicketModelInfo(yearStrategyId));
                //子订单详细信息
                Map<String,Object> orderContentDetail = this.getOrderContentDetailFromMap(this.getYearStrategyTicketModelInfo(yearStrategyId));
                //order基本信息
                orderBaseInfo.put("costPrice",orderBaseInfo.get("totalCostPrice").toString());
                orderBaseInfo.put("sellPrice",orderBaseInfo.get("totalSellPrice").toString());
                orderBaseInfo.put("userId",orderJsonObject.get("userId").toString());
                orderBaseInfo.put("userName",orderJsonObject.get("userName").toString());
                orderBaseInfo.put("userTel",orderJsonObject.get("userTel").toString());
                /**
                 * orderChannel用途：APP表示线上，订单售价为线上价格；其余为线下价格即为门市价
                 * 目前暂时均为线上，线下暂时不做
                 */
                orderBaseInfo.put("orderChannel",orderJsonObject.get("orderChannel").toString());
                //添加主订单内容到数据库返回orderBase表id
                String orderBaseId = this.insertOrderBaseInfo(orderBaseInfo);
                //order详细内容
                orderContentDetail.put("orderBaseId",orderBaseId);//主订单的id
                //添加子订单内容到数据库返回orderBase表id
                this.insertOrderContentDetail(count,orderContentDetail);
                System.out.println("orderBaseInfo="+orderBaseInfo);
                System.out.println("orderContentDetail="+orderContentDetail);
                retMap.put("returnKey","true");
                retMap.put("returnMessage","下单成功!");
                retMap.put("orderNumber",orderBaseInfo.get("orderNumber"));//订单流水号
            }
        }
        return retMap;
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
        stadiumInfo = studStadiumList.get(0);
        String classify = getClassifyById(stadiumInfo.get("subStadiumId").toString());
        double totalCostPrice = Double.parseDouble((String) yearStrategyDetail.get("costPrice"))*count;
        double totalSellPrice = Double.parseDouble((String) yearStrategyDetail.get("sellPrice"))*count;
        orderBaseInfo.put("subStadiumId",stadiumInfo.get("subStadiumId"));
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
        //开始时间，结束时间，从当日开始
        orderContentDetail.put("startTime",getStartDateAndEndDate(yearStrategyDetail).get("startDate"));//有限期开始时间
        orderContentDetail.put("endTime",getStartDateAndEndDate(yearStrategyDetail).get("endDate"));//有效期截止时间
        orderContentDetail.put("canRetreat",yearStrategyDetail.get("orderRefundRule"));//是否可退（0可以1不可以）
        orderContentDetail.put("hoursLimit",yearStrategyDetail.get("checkLimitedHours"));////是否限时：比如限时2小时，-1表示不限时
        orderContentDetail.put("costPrice",yearStrategyDetail.get("costPrice"));//成本价
        orderContentDetail.put("sellPrice",yearStrategyDetail.get("sellPrice"));//售价
        orderContentDetail.put("totalNumber",yearStrategyDetail.get("checkTicketAvailableTimes"));//总可用次数（-1表示不限次数)
        orderContentDetail.put("remainNumber",yearStrategyDetail.get("checkTicketAvailableTimes"));//
        orderContentDetail.put("everydayNumber",yearStrategyDetail.get("checkDailyLimitedTimes"));//每日限次
        orderContentDetail.put("everydayRemainNumber",yearStrategyDetail.get("checkDailyLimitedTimes"));//
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
     * order_effective_type=0时预订之后固定日期有效
     * @param
     * @return
     * @throws Exception
     */
    public Map<String,Object> getStartDateAndEndDate(Map<String,Object> yearStrategyDetail) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        String orderEffectiveType = (String) yearStrategyDetail.get("orderEffectiveType");
        String startDate ="";
        String endDate ="";
        if(!StringUtil.isEmpty(orderEffectiveType)){
            if(orderEffectiveType.equals("1")){
                startDate = (String) yearStrategyDetail.get("orderEffectiveStartTime");
                endDate= (String) yearStrategyDetail.get("orderEffectiveEndTime");
            }else if(orderEffectiveType.equals("0")){
                String orderFixDay = (String) yearStrategyDetail.get("orderFixDay");
                if(!StringUtil.isEmpty(orderFixDay)){
                    int fixDay = Integer.parseInt(orderFixDay);
                    if(fixDay > 0){
                        startDate = DateUtil.formatDate(new Date());
                        endDate = DateUtil.formatDate(DateUtil.offsiteDate(new Date(), Calendar.DAY_OF_YEAR, fixDay));//当前日期+fixDay=endDate
                    }
                }
            }
        }
        retMap.put("startDate",startDate);
        retMap.put("endDate",endDate);
        return retMap;
    }

    /**
     * 可用时间
     * @param map
     * @return
     */
    public Map<String,Object> getTimeLimit(Map<String,Object> map){
        String dateLimit = "";
        String timeLimit = "";
        Map<String,Object> yearStrategyDetail = (Map<String, Object>) map.get("yearStrategyDetail");
        List<Map<String,Object>> checkUseableTimeList = (List<Map<String, Object>>) map.get("checkUseableTimeList");
        if(checkUseableTimeList.size()>0){
            for (int i=0;i<checkUseableTimeList.size();i++){
                Map<String,Object> useableTime = checkUseableTimeList.get(i);
                //返回格式09:00:00$11:00:00,12:11:00$18:00:00
                timeLimit += useableTime.get("useableStartTime")+"$"+useableTime.get("useableEndTime")+",";
            }
            timeLimit = timeLimit.substring(0, timeLimit.length() - 1);
        }else{
            timeLimit ="";
        }
        //时间段end
        String type = (String) yearStrategyDetail.get("checkLimitedDateType");
        if(type.equals("0")){
            dateLimit = type;
        }else if(type.equals("1")){
            //1:1,2,3,4,5,6(1表示每周，冒号后的数字为周数)
            dateLimit = type+":"+ yearStrategyDetail.get("checkLimitedWeekDetails");
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
    public int updateCheckByMap(Map<String,Object> map) throws Exception{
        Map<String,Object> orderDetails = myOrderDao.getOrderDetailByOrderCode(map.get("orderCode").toString());
        String totalNumber = "";
        String remainNumber = "";
        map.put("remainNumber",orderDetails.get("remain_number"));
        map.put("everydayRemainNumber",orderDetails.get("everyday_remain_number"));
        if(!StringUtil.isEmpty(orderDetails.get("total_number"))){
            totalNumber = (String) orderDetails.get("total_number");
            if(!StringUtil.isEmpty(totalNumber)){
                if(StringUtil.isEmpty(orderDetails.get("remain_number"))){
                    remainNumber = totalNumber;
                }else {
                    remainNumber = (String) orderDetails.get("remain_number");
                }
                double total = Double.parseDouble(totalNumber);
                double remain = Double.parseDouble(remainNumber);
                if(total>0){
                    if(checkRemainTime(remainNumber)){
                        remain = remain -1;
                        map.put("remainNumber",remain);
                    }
                }
            }
        }
        //everyday=-1不限次数不需要修改
        String everydayNumber = "";
        String everydayRemainNumber = "";
        if(!StringUtil.isEmpty(orderDetails.get("everyday_number"))){
            everydayNumber = (String) orderDetails.get("everyday_number");
            if(!StringUtil.isEmpty(everydayNumber)){
                if(StringUtil.isEmpty(orderDetails.get("everyday_remain_number"))){
                    everydayRemainNumber = everydayNumber;
                }else {
                    everydayRemainNumber = (String) orderDetails.get("everyday_remain_number");
                }
                double everyday = Double.parseDouble(everydayNumber);
                double everydayRemain = Double.parseDouble(everydayRemainNumber);
                if(everyday>0){
                    if(checkRemainTime(everydayRemainNumber)){
                        everydayRemain = everydayRemain -1;
                        map.put("everydayRemainNumber",everydayRemain);
                    }
                }
            }
        }
        inserUsedRecords(map,orderDetails);
        return myOrderDao.updateCheck(map);
    }

    public int inserUsedRecords(Map<String,Object> map,Map<String,Object> orderDetails) throws Exception{
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id",UUIDUtil.generateUUID());//uuid生成32位随机数
        params.put("usedNumber","1");
        params.put("remainNumber",map.get("remainNumber"));
        params.put("everydayRemainNumber",map.get("everydayRemainNumber"));
        params.put("orderCode",map.get("orderCode"));
        params.put("orderContentId",orderDetails.get("orderContentId"));
        params.put("remarks",DateUtil.now()+"验票成功!");
        return myOrderDao.inserUsedRecords(params);
    }

    /**
     * 取票验证
     * 验证是否付款，是否失效，是否已使用等
     * @return
     */
    public Map<String,Object> takeTicket(String orderCode) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object>  orderDetailMap = myOrderDao.getOrderDetailByOrderCode(orderCode);
            String status = (String) orderDetailMap.get("status");
            //状态（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
            if(status.equals("1")){
                retMap.put("returnKey","true");
                retMap.put("returnMessage","该订单可取票");
            }else if(status.equals("0")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单未支付");
            }else if(status.equals("2")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已使用");
            }else if(status.equals("3")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单支付失败");
            }else if(status.equals("4")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单待退款");//退款目前不做，之后可拓展为待退款和已退款
            }else if(status.equals("5")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已失效");
            }else if(status.equals("6")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已退款");
            }else if(status.equals("7")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单退款失败");
            }else if(status.equals("8")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已过期");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 验票
     * 验证是否付款，是否失效等
     * 验票逻辑：
     step1：是否已经验过票
     step2：状态为1才可以验票（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
     step3：验证验票日期是否在有效期内，start_date<dqdate<end_date
     step4：验证验票日期是否在不可用日期内dqdate is not in forbiddenDate
     step5：验证验票时间是否在可用日期内，type=0表示每日，不做判断
                                       type=1表示每周，判断周数
     step6：验证验票时间是否在可用时间段内：start_time<dqtime<end_time
     step7：验证是否超过剩余次数：remain_number（-1表示不限次数）
     step8：验证是否超过当日剩余次数：everyday_remain_number（-1表示不限次数）
     * @return
     */
    public Map<String,Object> checkTicket(String orderCode) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        try {
            Map<String,Object>  orderDetailMap = myOrderDao.getOrderDetailByOrderCode(orderCode);
            String status = (String) orderDetailMap.get("status");
            //状态（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
            if(status.equals("1")){
                retMap = CheckUseableTime(orderCode);
            }else if(status.equals("0")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单未支付");
            }else if(status.equals("2")){
                retMap = CheckUseableTime(orderCode);
            }else if(status.equals("3")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单支付失败");
            }else if(status.equals("4")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单待退款");//退款目前不做，之后可拓展为待退款和已退款
            }else if(status.equals("5")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已失效");
            }else if(status.equals("6")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已退款");
            }else if(status.equals("7")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单退款失败");
            }else if(status.equals("8")){
                retMap.put("returnKey","false");
                retMap.put("returnMessage","该订单已过期");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(retMap);
        return retMap;
    }


    /**
     * 根据验票规则验证票是否在有效期内
     * @param
     * @return
     */
    public Map<String,Object> CheckUseableTime(String orderCode) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        Map<String,Object>  orderDetailMap = myOrderDao.getOrderDetailByOrderCode(orderCode);
        String dqtime = DateUtil.nowtime();//当前时间
        String dqdate = DateUtil.today();//当前日期
        //验票时间是否在有效期内:start_time<dqdate<end_time
        String startDate = "";
        String endDate = "";
        //验票时间是否在有效期内:start_time<dqdate<end_time
        if(!StringUtil.isEmpty(orderDetailMap.get("start_time")) && !StringUtil.isEmpty(orderDetailMap.get("end_time"))){
            startDate = orderDetailMap.get("start_time").toString();
            endDate = orderDetailMap.get("end_time").toString();
            if(!StringUtil.isEmpty(startDate)&&!StringUtil.isEmpty(endDate)){
                //if(DateUtil.compareDateTo(startDate,endDate,dqdate)) {//公用的日期对比方法
                if(DateUtil.compareDateTimeTo(startDate,endDate,dqdate)) {//公用的日期对比方法

                    //验证有效期结束=========>>进入下一步验证
                    retMap = CheckForbidden(dqtime,dqdate,orderDetailMap);
                }else {
                    //验票时间不在有效期内
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","验票时间不在有效期内");
                }
            }else {
                //没有有效期限制。========>>进入下一步验证
                retMap = CheckForbidden(dqtime,dqdate,orderDetailMap);
            }
        }else {
            //没有有效期限制。========>>进入下一步验证
            retMap = CheckForbidden(dqtime,dqdate,orderDetailMap);
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * //验证验票是否在不可用日期内
     * @param dqtime
     * @param dqdate
     * @param orderDetailMap
     * @return
     * @throws Exception
     */
    public Map<String,Object> CheckForbidden(String dqtime,String dqdate,Map<String,Object> orderDetailMap) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        //验票时间在屏蔽内:forbiddenDate ====  dqdate
        if(!StringUtil.isEmpty(orderDetailMap.get("forbidden_date"))){
            if(!checkDate(dqdate,orderDetailMap.get("forbidden_date").toString())){
                //不在屏蔽日期内 ==========>>下一步
                retMap =CheckCanUseDate(dqtime,dqdate,orderDetailMap);
            }else {
                //返回true表示在不可用日期段内
                retMap.put("returnKey","false");
                retMap.put("returnMessage","验票时间不在可用时间内");
            }
        }else {
            //没有屏蔽日期 ==========>>下一步
            retMap = CheckCanUseDate(dqtime,dqdate,orderDetailMap);
        }

        System.out.println(retMap);
        return retMap;
    }

    /**
     * 验票时间是否在可用日期内
     * @param dqtime
     * @param dqdate
     * @param orderDetailMap
     * @return
     * @throws Exception
     */
    public Map<String,Object> CheckCanUseDate(String dqtime,String dqdate,Map<String,Object> orderDetailMap) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        //验票时间是否在可用日期内
        if(!StringUtil.isEmpty(orderDetailMap.get("date_limit"))){
            String date_limit = orderDetailMap.get("date_limit").toString();
            if(!StringUtil.isEmpty(date_limit)){
                String[] limitTypes = date_limit.split(":");
                if(limitTypes.length>0){
                    String limitType = limitTypes[0];
                    if (limitType.equals("1")) {
                        //type=1表示每周
                        String[] limitDays = limitTypes[1].split(",");
                        if(limitDays.length>0){
                            int todayWeek = DateUtil.getWeekByDate(DateUtil.parseDate(dqdate));//获取当前日期的周数
                            if(todayWeek==0){
                                todayWeek=7;
                            }
                            //比较周数是否在可用时间的周数内
                            if (StringUtil.isContainSpcifyStr(todayWeek + "", limitDays)) {
                                //验证可用日期通过==============>>下一步
                                retMap = CheckCanUseTime(dqtime,dqdate,orderDetailMap);
                            }else {
                                retMap.put("returnKey","false");
                                retMap.put("returnMessage","不在可用日期内!");
                            }
                        }else {
                            //无需判断可用时间段=======>>下一步
                            retMap = CheckRemainNumber(dqtime,dqdate,orderDetailMap);
                        }
                    }else {
                        //type=0表示每日，不用判断周数==============>>下一步
                        retMap = CheckCanUseTime(dqtime,dqdate,orderDetailMap);
                    }
                }else {
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","没有可用日期,请核实!");
                }
            }else {
                retMap.put("returnKey","false");
                retMap.put("returnMessage","没有可用日期,请核实!");
            }
        }else {
            retMap.put("returnKey","false");
            retMap.put("returnMessage","没有可用日期,请核实!");
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 验证验票时间是否在可用时间段内
     * @param dqtime
     * @param dqdate
     * @param orderDetailMap
     * @return
     * @throws Exception
     */
    public Map<String,Object> CheckCanUseTime(String dqtime,String dqdate,Map<String,Object> orderDetailMap) throws Exception{
        Map<String,Object> retMap = new HashMap<String, Object>();
        //验证验票时间是否在可用时间段内
        if(!StringUtil.isEmpty(orderDetailMap.get("time_limit"))) {
            if (checkTime(dqtime, orderDetailMap.get("time_limit").toString())) {
                //验证验票在可用时间段内=============>>下一步
                retMap = CheckRemainNumber(dqtime,dqdate,orderDetailMap);
            }else {
                //验证验票不在可用时间段内
                retMap.put("returnKey","false");
                retMap.put("returnMessage","验票时间不在可用时间段内");
            }

        }else {
            //没有可用时间段限制 ======>>下一步
            retMap = CheckRemainNumber(dqtime,dqdate,orderDetailMap);
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 是否超过剩余次数
     * @param dqtime
     * @param dqdate
     * @param orderDetailMap
     * @return
     */
    public Map<String,Object> CheckRemainNumber(String dqtime,String dqdate,Map<String,Object> orderDetailMap){
        Map<String,Object> retMap = new HashMap<String, Object>();
        //是否超过剩余次数
        if(!StringUtil.isEmpty(orderDetailMap.get("remain_number"))){
            String remain_number = orderDetailMap.get("remain_number").toString();
            if(remain_number.equals("-1")){//-1表示不限次数
                //-1表示不限次数， 下一步判断每日限次
                retMap = CheckEveryRemainNumber(dqtime,dqdate,orderDetailMap);
            }else {
                if (checkRemainTime(remain_number)){//是否超过剩余次数
                    //true，下一步判断每日限次
                    retMap = CheckEveryRemainNumber(dqtime,dqdate,orderDetailMap);
                }else {
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","超过剩余次数!");
                }
            }
        }
        System.out.println(retMap);
        return retMap;
    }

    /**
     * 是否超过当日剩余次数
     * @param dqtime
     * @param dqdate
     * @param orderDetailMap
     * @return
     */
    public Map<String,Object> CheckEveryRemainNumber(String dqtime,String dqdate,Map<String,Object> orderDetailMap){
        Map<String,Object> retMap = new HashMap<String, Object>();
        //是否超过剩余次数
        if(!StringUtil.isEmpty(orderDetailMap.get("everyday_remain_number"))){
            String remain_number = orderDetailMap.get("everyday_remain_number").toString();
            if(remain_number.equals("-1")){//-1表示不限次数
                //-1表示不限次数，通过
                retMap.put("returnKey","true");
                retMap.put("returnMessage","验证通过!");
            }else {
                if (checkRemainTime(remain_number)){//是否超过剩余次数
                    //true，通过
                    retMap.put("returnKey","true");
                    retMap.put("returnMessage","验证通过!");
                }else {
                    retMap.put("returnKey","false");
                    retMap.put("returnMessage","超过每日限次!");
                }
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
                        //if(DateUtil.compareDateTo(startDate,endDate,dqdate)) {//公用的日期对比方法
                        if(DateUtil.compareDateTimeTo(startDate,endDate,dqdate)) {//公用的日期对比方法

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
     * @return
     */
    public boolean checkRemainTime(String remain_number){
        boolean compareResult = false;
        if(!StringUtil.isEmpty(remain_number)){
            double remain = Double.parseDouble(remain_number);
            if(remain > 0){
                //是否超过剩余次数
                compareResult = true;//未超过
            }
        }
        return compareResult;
    }

    /**
     * 订单流水号查询商户和订单内容
     * @param map
     * @return
     */
    public Map<String,Object> getOrderAndMpByOrderNumer(Map<String,Object> map) throws Exception{
        return myOrderDao.getOrderAndMpByOrderNumer(map);
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     * @throws Exception
     */
    public Map<String,Object> cancelOrderByOrderId(String orderId) throws Exception{
        //客户主动取消订单
        //状态（0待支付，1待使用，2已使用，3支付失败，4退款:待退款，已退款，5失效订单）
        String status = "5";
        Map<String,Object> retMap = new HashMap<String, Object>();
        Map<String,Object> baseParams = new HashMap<String, Object>();
        if(StringUtil.isEmpty(orderId)){
            retMap.put("returnKey","false");
            retMap.put("returnMessage","订单编号为空");
        }else if(myOrderDao.isHaveByOrderId(orderId) <= 0){
            retMap.put("returnKey","false");
            retMap.put("returnMessage","订单编号不存在");
        }else {
            //更新主单状态
            baseParams.put("orderId",orderId);
            baseParams.put("status",status);
            baseParams.put("remarks","取消订单");
            myOrderDao.cancelOrderBase(baseParams);
            //查询字单，并修改字单状态
            List<Map<String,Object>> list = myOrderDao.getAllOrderContentIdByOrderId(orderId);
            int count = myOrderDao.getCountByOrderId(orderId);
            if(list.size()>0){
                for(int i = 0;i<list.size();i++){
                    Map<String,Object> contentParams = new HashMap<String, Object>();
                    String orderContentId = (String) list.get(i).get("id");
                    contentParams.put("id",orderContentId);
                    contentParams.put("status",status);
                    contentParams.put("remarks","取消订单");
                    myOrderDao.cancelOrderContent(contentParams);
                }
                retMap.put("returnKey","true");
                retMap.put("returnMessage","取消订单成功");
            }else {
                retMap.put("returnKey","true");
                retMap.put("returnMessage","取消订单成功");
            }
        }
        return retMap;
    }

    /**
     * 定时任务中使用，更新订单状态，时间超过10分钟状态变为失效订单
     * @param
     * @return
     * @throws Exception
     */
    public void updateAllOrderStatus(String remarks) throws Exception{
        //获取所有失效订单但是还没有改变状态的list
        //List<Map<String,Object>> list = myOrderDao.getOrderNumberByStatus("5");
        List<Map<String,Object>> list = myOrderDao.getSxOrderNotChange();
        if (list.size()>0){
            fieldOrderService.updateSxLockField(list);
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("remarks",remarks);
        myOrderDao.updateAllOrder(params);
        myOrderDao.updateAllOrderContent(params);
    }

    /**
     * 定时任务中使用,每天24点更新次数票的剩余次数
     * @param
     * @return
     * @throws Exception
     */
    public void updateAllEveryRemain() throws Exception{
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("status","2");//使用中的
        myOrderDao.updateAllEveryRemain(params);
    }

    /**
     * 定时任务中使用，针对年票，只要是剩余次数还有，主单的状态就还是待使用，如果次数没有了，就变成已使用
     * @param
     * @return
     * @throws Exception
     */
    @Transactional
    public void updateOrderBaseStatus() throws Exception{
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("remarks","次数已使用完");
        myOrderDao.updateOrderBaseStatus(params);
        Map<String,Object> params1 = new HashMap<String, Object>();
        params1.put("remarks","次数未使用完");
        myOrderDao.updateUnOrderBaseStatus(params1);
        Map<String,Object> params2 = new HashMap<String, Object>();
        params2.put("remarks","验过票的未使用订单修改状态");
        myOrderDao.updateOrderContentStatus(params2);
    }
    /**
     * 定时任务中使用,当订单超过了验票时间，状态变为已过期=8
     */
    public void updateTimeOverOrder() throws Exception{
        List<Map<String,Object>> listSite = myOrderDao.getTimeOverSiteOrder();
        List<Map<String,Object>> listYear = myOrderDao.getTimeOverYearOrder();
        if (listSite.size()>0){
            for (int i=0;i<listSite.size();i++){
                String id = listSite.get(i).get("id").toString();
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("remarks","超过了验票时间,已过期");
                params.put("id",id);
                myOrderDao.updateTimeOver(params);
                this.updateTimeOverMainOrder(id);
            }
        }
        if (listYear.size()>0){
            for (int i=0;i<listYear.size();i++){
                String id = listYear.get(i).get("id").toString();
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("remarks","超过了验票时间,已过期!");
                params.put("id",id);
                myOrderDao.updateTimeOver(params);
                this.updateTimeOverMainOrder(id);
            }
        }
    }
    /**
     * 修改主单过期状态
     * 当主单的所有子单都变成过期，则主单也过期
     * @param
     * @return
     */
    public void updateTimeOverMainOrder(String id) throws Exception{
        try {
            String orderBaseId = myOrderDao.getOrderBaseByOrderContentId(id);
            if(!StringUtil.isEmpty(orderBaseId)) {
                Map<String, Object> orderBaseDetails = myOrderDao.getOrderDetails(orderBaseId);
                String status = "";
                int sonOrders = 0;
                if (!StringUtil.isEmpty(orderBaseDetails.get("status"))) {
                    status = orderBaseDetails.get("status").toString();//获得主单状态
                }
                if (!StringUtil.isEmpty(orderBaseDetails.get("sonOrders"))) {
                    sonOrders = Integer.parseInt(orderBaseDetails.get("sonOrders").toString());//获得子单个数
                }
                if (status.equals("1")) {//主单是待使用状态才需要判断
                    Map<String, Object> params1 = new HashMap<String, Object>();
                    params1.put("orderBaseId", orderBaseId);
                    params1.put("status", "8");//已过期=8
                    int count = myOrderDao.getOrderCountByMap(params1);//当主单的所有子单都变成过期，则主单也过期
                    if (count == sonOrders) {//过期的子单个数=总的子单个数
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("status", "8");//状态改为已过期
                        params.put("remarks", "订单已过期");
                        params.put("orderId", orderBaseId);
                        myOrderDao.cancelOrderBase(params);
                    }else{
                        Map<String, Object> paramsd = new HashMap<String, Object>();
                        paramsd.put("orderBaseId", orderBaseId);
                        paramsd.put("status", "1");
                        int dCount = myOrderDao.getOrderCountByMap(paramsd);//主单中的待使用订单为0的时候
                        if (dCount == 0){
                            Map<String, Object> paramsy = new HashMap<String, Object>();
                            paramsy.put("orderBaseId", orderBaseId);
                            paramsy.put("status", "2");
                            int yCount = myOrderDao.getOrderCountByMap(paramsy);//主单中的有存在已使用的订单
                            if(yCount>0){
                                Map<String, Object> params = new HashMap<String, Object>();
                                params.put("status", "2");//状态改为已使用
                                params.put("remarks", "子单已过期，存在已使用订单");
                                params.put("orderId", orderBaseId);
                                myOrderDao.cancelOrderBase(params);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<String,Object> getOrderNumberByOrderId(String id) throws Exception{
        return myOrderDao.getOrderNumberByOrderId(id);
    };

    @Transactional
    public void updateAllCurriculumOrderStatus(String remark) throws Exception{
        List<Map<String,Object>> list = myOrderDao.getOverdueCurriculumOrderList();
        if (list.size()>0){
            //释放报名人数，上课时段+1
            myOrderDao.batchUpdateClassTimeSignUp(list);
            //更改订单状态为 4-过期
            myOrderDao.batchUpdateCurriculumOrderStatus(list);
        }
    }
}