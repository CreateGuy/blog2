package com.lzx.blog.config.LocaleRelover;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
public class MyLocaleRelover implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String language = httpServletRequest.getParameter("language");
        Locale locale = Locale.getDefault();
//        log.info("language:"+language);
        if (!(language==null||"".equals(language))){
            String[] split = language.split("_");
            locale = new Locale(split[0], split[1]);
//            log.info("language:"+language);
        }else{
//            log.info("language:"+"无");
        }

        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
