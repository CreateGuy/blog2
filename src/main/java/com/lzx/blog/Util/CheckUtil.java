package com.lzx.blog.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 检查空值工具类
 */
public class CheckUtil {

    /**
     * 判断传入的字符串是否为空
     * @param Strs
     * @return
     */
    public static Boolean CheckStringNull(String ... Strs){
        for (String str:
                Strs) {
//            log.info("str:" + str);
            if (str == null || str.equals(""))
                return true;
        }
        return false;
    }
}
