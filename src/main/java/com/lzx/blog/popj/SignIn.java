package com.lzx.blog.popj;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignIn extends User{

    /**
     * 等级经验
     */
    private Integer experience;

    /**
     * 是否断签
     */
    private Integer brokensign;

    /**
     * 上次签到时间
     */
    private Timestamp lasttime;

    /**
     * 连续签到天数
     */
    private Integer consecutivedays;

    /**
     * 今日是否签到
     */
    private Integer thatday;
}
