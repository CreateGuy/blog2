package com.lzx.blog.controller.other;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Api(tags = "一些错误的页面")
@Slf4j
public class otherController {

    @GetMapping(value = "/other/notice")
    String notice(Model model){
        log.info("other/notice");
        return "other/notice";
    }
}
