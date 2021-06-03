package com.lzx.blog.Util;

import com.lzx.blog.service.UserServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 定时任务
 */
@Component
@Slf4j
public class TimedTaskUtil {

    @Autowired
    UserServiceImp userServiceImp;
    @Scheduled(cron = "0 0 0 * * ?") //每天凌晨执行
    public void sum(){
        Integer integer = userServiceImp.UpdateSinginStatus1();
        if (integer == 0){
            log.info("更新UpdateSinginStatus1失败");
        }
        integer = userServiceImp.UpdateSinginStatus2();
        if (integer == 0){
            log.info("更新UpdateSinginStatus2失败");
        }
        log.info("每日更新签到表时间:"+new Date());
    }
}
