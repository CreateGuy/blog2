package com.lzx.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Slf4j
public class ResourcesConfig implements WebMvcConfigurer {

    @Value("${AvatarPath}")
    private String AvatarDir;

    @Value("${ImagePath}")
    private String PictureDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("PictureDir" + PictureDir);
        //和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("/Avatar/**")
                .addResourceLocations("file:" + AvatarDir);

        registry.addResourceHandler("/gerenboke/**")
                .addResourceLocations("file:" + PictureDir);

        //上传的图片在D盘下的storage目录下，访问路径如：http://localhost:8081/storage/1.jpg
        //其中image表示访问的前缀。"file:D:/storage/"是文件真实的存储路径
//        registry.addResourceHandler("/file/**").addResourceLocations("file:D:/storage/");
    }
}