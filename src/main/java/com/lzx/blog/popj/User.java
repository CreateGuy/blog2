package com.lzx.blog.popj;


import com.lzx.blog.popj.expand.Mail;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author : feng
 * @description: User
 * @date : 2019-05-13 11:44
 * @version: : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
/**
 * 用户表
 */
public class User implements Serializable {


    @Value("${Blog.defaultavatar}")
    private String defaultavatar;

    private Integer id;

    private String username;

    private String password;

    private String mailbox;

    private String sex;

    /**
     * 注册时间
     */

    private Timestamp registrationtime;

    private Integer state;

    public String avatar;

    public Integer grade;

    /**
     * 返回给前端的更详细的时间, 比如几分钟前,几个小时前
     */
    private Integer deltime;

    /**
     * 返回给前端的更详细的时间的单位
     * */
    private String delTimeUnit;

    /**
     * 最后一次访问时间
     */
    public Timestamp lasttime;

    /**
     * 自传
     */
    public String message;


    public void setall(Mail mail){
        this.username = mail.getUsername();
        this.password = mail.getPassword();
        this.mailbox = mail.getMailbox();
        this.registrationtime = new Timestamp(System.currentTimeMillis());
        this.state = 0;
        this.sex = "男";
        this.avatar = defaultavatar;
        this.grade = 0;
        this.lasttime = new Timestamp(System.currentTimeMillis());
        this.message = "";
    }
    public void Clear(){
        this.password = "";
    }

}