package com.digitalchina.common.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.digitalchina.common.utils.DateUtil;
import com.digitalchina.sport.order.api.service.MyOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时器
 */
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    @Autowired
    private MyOrderService myOrderService;

    //定时任务1,5分钟更新订单状态
    @Scheduled(fixedRate = 1000 * 300)
    public void reportCurrentTime() {
    	// 请注意：需要保证在计划的task没有运行时，如何重新运行
        //log.info("########## The task executed at {}", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        try {
            myOrderService.updateAllOrderStatus("超过十分钟的失效订单");
            log.info(DateUtil.now()+"########## 更新失效订单成功！########## ", DateUtil.now());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(DateUtil.now()+"########## 更新失效订单失败！########## ",DateUtil.now());
        }
    }

    //定时任务2,每天晚上更新次数票的每日剩余次数
    //@Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 0 0 * * ?")
    public void reportCurrentByCron() {
        try {
            myOrderService.updateAllEveryRemain();
            log.info(DateUtil.now()+"########## 更新次数票的每日剩余次数成功！########## ", DateUtil.now());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(DateUtil.now()+"########## 更新次数票的每日剩余次数失败！########## ", DateUtil.now());
        }
    }

    //定时任务3,针对年票，只要是剩余次数还有，主单的状态就还是待使用，如果次数没有了，就变成已使用
    //@Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate = 1000 * 300)
    public void reportCurrentByTime() {
        try {
            myOrderService.updateOrderBaseStatus("次数已使用完");
            log.info(DateUtil.now()+"########## 更新次数票的主单的状态成功！########## ", DateUtil.now());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(DateUtil.now()+"########## 更新次数票的主单的状态失败！########## ", DateUtil.now());
        }
    }
}
