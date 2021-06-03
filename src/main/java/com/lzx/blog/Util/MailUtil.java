package com.lzx.blog.Util;

import com.lzx.blog.popj.VerCode;
import com.lzx.blog.service.CacheService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
/**
 * 邮箱工具类
 */
public class MailUtil {

    //默认的发送验证码的的对象
    @Autowired
    JavaMailSender mailSender;

    //默认的发送验证码的号码
    @Value("${spring.mail.username}")
    private String EmailUsername;

    //ecache对象
    @Autowired
    private CacheService cacheService;

    //bean工厂对象，为了获取某个对象的不同实例
    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;

    /**
     * 发送验证码，为了不耽误时间，我们设置成异步的
     * @param verCode 要发送的验证码
     * @param subject 主题
     * @param content 内容
     */
    @Async
    public void sendSimpleEmail(VerCode verCode,String subject,String content){

        long startTimestamp = System.currentTimeMillis();

        //产生验证码
        String code = RandomUtil.getSecurityCode();
        //保存在缓存中
        Savevercode(verCode,code);
        log.info("code："+code);
        log.info("Emailcode："+verCode);
        content += code;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EmailUsername); // 邮件发送者
        message.setTo(verCode.getEmail()); // 邮件接受者
        message.setSubject(subject); // 主题
        message.setText(content); // 内容

        mailSender.send(message);
        log.info("Send mail success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);

    }


    /**
     * 将邮箱验证码保存在缓存中
     * @param verCode 要保持的验证码实体
     * @param vercode1 要保持的验证码
     */
    public void Savevercode(VerCode verCode,String vercode1){
        verCode.setCode(vercode1);
        String datef = Formatdate(new Date());
        verCode.setDate(datef);
        cacheService.SaveLocalEmail(verCode);
    }

    /**
     * 从缓存中读取邮箱验证码
     * @param email 通过邮箱名读取验证码
     * @return
     */
    public VerCode  GetLocalEmail(String email){
        return cacheService.GetLocalEmail(email);
    }

    /**
     * 转换日期格式
     * @param date
     * @return
     */
    public String Formatdate(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datef = sdf.format(date);
//        log.info(now);
        return datef;
    }

}