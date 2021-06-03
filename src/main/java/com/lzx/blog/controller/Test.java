package com.lzx.blog.controller;

//import com.lzx.blog.config.Shrio.RetryLimitHashedCredentialsMatcher;
import com.lzx.blog.Util.ConstantUtil;
import com.lzx.blog.Util.MailUtil;
import com.lzx.blog.dao.UserMapper;
import com.lzx.blog.popj.User;
import com.lzx.blog.popj.VerCode;
import com.lzx.blog.service.CacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequiresAuthentication
@Api(tags = "test相关接口")
@Slf4j
public class Test {

    @GetMapping("test")
//    @ResponseBody
    public String GetUser(){
        return "test1";
    }

    @RequiresPermissions(value = {"add"})
    @GetMapping("test2")
    @ResponseBody
    public String test2(){
        return "test2";
    }

    @RequiresPermissions(value = {"cuo"})
    @GetMapping("test3")
    @ResponseBody
    public String test3(){
        return "test3";
    }

    @RequiresRoles(value = {"super"})
    @GetMapping("test4")
    @ResponseBody
    public String test4(){
        return "test4";
    }


//    /**
//     * 解除admin 用户的限制登录
//     * 写死的 方便测试
//     * @return
//     */
//    @Autowired
//    RetryLimitHashedCredentialsMatcher r;
//    @RequestMapping("/unlockAccount")
//    public String unlockAccount(Model model){
//        model.addAttribute("msg","用户解锁成功");
//
//        r.unlockAccount("lzx");
//
//        return "login";
//    }

    @Autowired
    private CacheService cacheService;

    @ApiOperation("测试Cache保存请求")
    @PostMapping("/testPutCache")
    @ResponseBody
    public String testPutCache(@RequestBody VerCode code) {
        cacheService.SaveLocalEmail(code);
        return "success";
    }

    @ApiOperation("测试Cache读取请求")
    @GetMapping("/testGetCache")
    @ResponseBody
    public VerCode testGetCache(String email) {
        return cacheService.GetLocalEmail(email);
    }

    @Autowired
    MailUtil mailUtil;
    @ApiOperation("测试邮件请求")
    @GetMapping("/testSendEmail")
    @ResponseBody
    public String mailUtil(VerCode verCode, String subject ,String content,Model model) {
        mailUtil.sendSimpleEmail(verCode,subject,content);
        model.addAttribute("msg", ConstantUtil.MailSuccess);
        return "index::registeredmsg";
    }

    @Autowired
    UserMapper userMapper;
    @GetMapping("/sel")
    @ApiOperation("测试动态查询请求")
    @ResponseBody
    public String userMapper() {
        Map<String,Object> map = new HashMap<>();
        map.put("mailbox","17623250093@163.com");
        List<User> users = userMapper.selByIUP(map);
        for (User user :
                users) {
            log.info("user:"+user);
        }
        return "index::registeredmsg";
    }

    @GetMapping("/t1")
    @ApiOperation("主页")
    public String GeRenBoKe() {
        log.info("pa"+"t1");
        return "test1";
    }

    @PostMapping("/t2")
    @ApiOperation("主页")
    public String t2(@RequestBody User user,Model model) {
        log.info("user:" + user);
        model.addAttribute("msg","hello");
        return "test::test1";
    }

    @GetMapping("/t3")
    @ApiOperation("主页")
    public String t3(Model model) {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        User user3 = new User();
        user3.setUsername("user3");
        log.info("t3:");
        users.add(user1);
        users.add(user2);
        users.add(user3);
        model.addAttribute("users",users);
        return "test1";
    }


    @GetMapping("/home1")
    @ApiOperation("主页")
    public String home1(Model model) {

        return "View/index/index";
    }
}