package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 回复副表
 */
public class Replyf implements Comparable<Replyf>{

    private Integer id;

    private String username;

    private String rusername;

    private Integer authority;

    private Integer floor;

    private String text;

    private Timestamp time;

    private String avatar;

    private Integer grade;

    /**
     * 回复的人的id
     */
    private Integer userid;

    //头衔字段，通过Role表去获取
    private String[] crown;

    /**
     * 点赞数
     */
    private Integer agree;


    /**
     * 返回给前端的更详细的时间, 比如几分钟前,几个小时前
     */
    private Integer deltime;

    /**
     * 返回给前端的更详细的时间的单位
     * */
    private String delTimeUnit;

    @Override
    public int compareTo(Replyf o) {
        return o.getTime().compareTo(this.getTime());
    }
}
