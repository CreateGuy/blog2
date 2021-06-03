package com.lzx.blog.controller.User;

import com.lzx.blog.Util.exception.exceptionclass.ErrorpageException;
import com.lzx.blog.Util.exception.exceptionclass.ImageVerificationCodeException;
import com.lzx.blog.popj.User;
import com.lzx.blog.service.UserServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@Api(tags = "用户登陆的相关接口")
//@Scope("request") 这个是设置同一个会话是同一个Controller
// 缺点：很大程度上增大了bean创建实例化销毁的服务器资源开销。
@Slf4j
public class UserLogin {

    @Autowired
    UserServiceImp userServiceImp;

    @ApiOperation("用户的登陆请求")
    @RequestMapping(value = "/user/signin",method = RequestMethod.POST)
    public ModelAndView  Sign(HttpServletRequest request,
                              @ApiParam(name = "user", value = "用户的实体", required = false)
                              @RequestBody
                                      Map<String,Object> map){


        String username = (String) map.get("username");
        String password = (String) map.get("password");
        Boolean rememberme = (Boolean) map.get("rememberme");
        String imgcode = (String) map.get("imgcode");

        log.info("username:"+username+" password:"+password + " " +
                "rememberme:"+rememberme + " imgcode:" + imgcode);
        //判断验证是否正确，或者该会话是否有验证码
        if (!VerificationCcode.imgvcodever(request,imgcode)){
            throw new ImageVerificationCodeException();
        }
        //进行shiro验证
        UsernamePasswordToken token = new UsernamePasswordToken(username,password,false);
        //得到对象
        Subject currentUser = SecurityUtils.getSubject();

        log.info("rememberme:"+rememberme);
        //开启记住我功能
        if(rememberme != null) {
            token.setRememberMe(rememberme);
        }
        //已经将异常交给统一异常处理类处理
        currentUser.login(token);

        //将数据放到sesssion中，交给前端渲染
        HttpSession session = request.getSession();
        Map<String,Object> usermap = new HashMap<>();
        usermap.put("username",username);
        List<User> users = userServiceImp.selByIUP(usermap);

        //因为我们使用的是动态查询，如果传入了username那么就只有一个对象
        session.setAttribute("user",users.get(0));

        //更新最后一次登录时间
        usermap.remove("username");
        usermap.put("id",users.get(0).getId());
        usermap.put("lasttime",new Timestamp(System.currentTimeMillis()));
        Integer num = userServiceImp.UpdateUser(usermap);
        if (num == 0){
            log.info("num:"+num);
            throw new ErrorpageException(101,"数据更新错误，请稍后重试");
        }
        //要在主页上进行其他的操作，我们就重定向到主页
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/index");
        return mv;

    }



    @ApiOperation("用户的注册页面的请求")
    @RequestMapping("/user/registeredpage")
    public String forgot(Model model) {
        return "userregistered";
    }


}
