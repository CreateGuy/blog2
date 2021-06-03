package com.lzx.blog.Util;


import java.sql.Timestamp;
import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间差计算工具类
 */
public class CalTime {

    public static String DateFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        // 计算时间差
//        System.out.println(CalTime("15:05", "14:35"));
        String str1 = "2021-04-21 21:20:00";
        String str2 = "2021-04-21 21:42:12";

//        if (str1.compareTo(str2) == 1)
//            System.out.println(ConversionTime(str1, str2,DateFormat));
//        else
//            System.out.println(ConversionTime(str2, str1,DateFormat));
        System.out.println(new Timestamp(System.currentTimeMillis()));

    }

    // 计算两个时间差，返回为秒。
    public static long ConversionTime(String time1, String time2,String format) {
        DateFormat df = new SimpleDateFormat(format);
        long minutes = 0L;
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
            minutes = diff / (1000);
        } catch (ParseException e) {
            System.out.println("抱歉，时间日期解析出错。");
        }

        return minutes;
    }
}
