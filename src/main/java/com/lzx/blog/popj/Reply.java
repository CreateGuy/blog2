package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 个人页面的最近回帖的实体
 */
public class Reply {

    private Integer id;

    private String username;

    private Integer authority;

    private String title;

    private String text;

    private String rusername;

    private Timestamp time;

    /**
     * 返回给前端的更详细的时间, 比如几分钟前,几个小时前
     */
    private Integer deltime;

    /**
     * 返回给前端的更详细的时间的单位
     * */
    private String delTimeUnit;


}
