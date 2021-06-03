package com.lzx.blog.config;

import com.google.code.kaptcha.impl.DefaultKaptcha; import
        com.google.code.kaptcha.util.Config; import
        org.springframework.context.annotation.Bean; import
        org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration public class KaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 图片宽
        properties.setProperty("kaptcha.image.width", "140");
        // 图片高
        properties.setProperty("kaptcha.image.height", "45");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "37");
        // session key
        properties.setProperty("kaptcha.session.key", "code");
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 文字间隔
        properties.setProperty("kaptcha.textproducer.char.space", "10");
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");

//        // 背景颜色渐变，开始颜色
//        properties.setProperty("kaptcha.background.clear.from", "red");
//        // 背景颜色渐变，结束颜色
//        properties.setProperty("kaptcha.background.clear.to", "blue");

        // 背景颜色渐变，结束颜色
        properties.setProperty("kaptcha.noise.color", "white");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}