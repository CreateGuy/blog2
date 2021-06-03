package com.lzx.blog.controller.User;

import com.lzx.blog.Util.CheckUtil;
import com.lzx.blog.Util.ConstantUtil;
import com.lzx.blog.Util.MailUtil;
import com.lzx.blog.Util.exception.exceptionclass.CustomException;
import com.lzx.blog.Util.exception.exceptionclass.ErrorpageException;
import com.lzx.blog.popj.ChangePassword;
import com.lzx.blog.popj.Role;
import com.lzx.blog.popj.User;
import com.lzx.blog.popj.VerCode;
import com.lzx.blog.popj.expand.Mail;
import com.lzx.blog.service.RoleServiceImp;
import com.lzx.blog.service.UserServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import java.util.*;

@RequestMapping("/user")
@Api(tags = "用户注册和找回密码的相关接口")
@Controller
@Slf4j
public class UserRegistered {

    @Autowired
    MailUtil mailUtil;

    @Value("${Blog.name}")
    String blogname;

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    RoleServiceImp roleServiceImp;

    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;

    @GetMapping("/sentemailcode")
    @ApiOperation("邮箱验证码请求")
    @ResponseBody
    public String EmailCode(
                    @ApiParam(name = "verCode", value = "验证码", required = false)
                    @Validated VerCode verCode, Model model){

        //判断发后端的用户名和邮箱名是否为空
        if(CheckUtil.CheckStringNull(verCode.getEmail())){
            throw new CustomException(1,ConstantUtil.NullValueException);
        }
        log.info("EmailCode:" + verCode.getEmail());

        Map<String,Object> map = new HashMap<>();
        map.put("mailbox",verCode.getEmail());

        List<User> users = userServiceImp.selByIUP(map);
        log.info("users.size()"+ users.size());

        mailUtil.sendSimpleEmail(verCode, blogname, "您的验证码为: ");
        return ConstantUtil.MailSuccess;

        //如果查出来未空就说明这个邮箱不存在，可以注册，测试时关闭
//        if (users.size() == 0) {
//            mailUtil.sendSimpleEmail(verCode, blogname, "您的验证码为: ");
//            model.addAttribute("msg", ConstantUtil.MailSuccess);
//            return "index";
//        }else {
//            throw new ErrorpageException(101,ConstantUtil.EmailAlreadyExistsException);
//        }
    }

    @GetMapping("/sentemailcode2")
    @ApiOperation("修改密码邮箱验证码请求")
    @ResponseBody
    public String sentemailcode2(
            @ApiParam(name = "verCode", value = "验证码", required = false)
            @Validated VerCode verCode, Model model){

        log.info("sentemailcode2");
        //判断发后端的用户名和邮箱名是否为空
        if(CheckUtil.CheckStringNull(verCode.getEmail())){
            throw new CustomException(1,ConstantUtil.NullValueException);
        }
        log.info("EmailCode:" + verCode.getEmail());

        Map<String,Object> map = new HashMap<>();
        map.put("mailbox",verCode.getEmail());

        List<User> users = userServiceImp.selByIUP(map);
        if (users.size() == 0) {
            log.info("users.size()" + users.size());
            throw new CustomException(4,ConstantUtil.MailboxNotExisted);
        }

        mailUtil.sendSimpleEmail(verCode, blogname, "尊敬的 "+ users.get(0).getUsername()+" 您的验证码为: ");
        return ConstantUtil.MailSuccess;
    }


    @PostMapping("/registereduser")
    @ApiOperation("注册请求")
    public String registereduser(
            HttpServletRequest request,
             @ApiParam(name = "mail", value = "邮箱的实体", required = false)
             @Validated  Mail mail , Model model){

        log.info("Mail:" + mail);

        VerCode verCode = mailUtil.GetLocalEmail(mail.getMailbox());
        log.info("verCode"+ verCode);

        Map<String,Object> map = new HashMap<>();
        map.put("username",mail.getUsername());
        List<User> users = userServiceImp.selByIUP(map);
        log.info("users.size()"+ users.size());

        if (verCode == null){
            log.info("verCode:" + verCode);
            throw new CustomException(4,ConstantUtil.EmailNullException);
        }
        log.info("看值：" + verCode.getCode() + " " + mail.getCode());
        log.info("比较：" + (verCode.getCode().equals(mail.getCode())));
        if (!(verCode.getCode().equals(mail.getCode()))){
            throw new CustomException(2,ConstantUtil.EmailVerificationCodeException);
        }else if (users.size() != 0){
            throw new CustomException(4,ConstantUtil.UsernameExisted);
        }
        User user = beanFactory.getBean(User.class);
        user.setall(mail);
        userServiceImp.insertuser(user);

        map.put("username",mail.getUsername());
        List<User> users1 = userServiceImp.selByIUP(map);

        HttpSession session = request.getSession();
        session.setAttribute("user",users1.get(0));

        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword(),false);
        Subject currentUser = SecurityUtils.getSubject();
        //已经将异常交给统一异常处理类处理
        currentUser.login(token);

        Role role = beanFactory.getBean(Role.class);
        role.SetAll(user.getUsername(),"ordinary","");
        Integer integer = roleServiceImp.InsertRole(role);

        if (integer == 0){
            log.info("插入Role表失败");
            return "forward:/other/notice";
        }
        return "index";
    }

    @ApiOperation("找回密码的页面")
    @GetMapping("/forget")
    public String userset(Model model){
        log.info("user/forget");
        return "user/forget";
    }

    @ApiOperation("找回密码的请求")
    @PostMapping("/changepassword")
    @ResponseBody
    public String changepassword(Model model,
                                 @Validated @RequestBody ChangePassword changePassword){

        log.info("user/changepassword");

        log.info("changePassword:"+changePassword);


        VerCode verCode = mailUtil.GetLocalEmail(changePassword.getEmail());
        log.info("verCode"+ verCode);

//        log.info("看值：" + verCode.getCode() + " " + mail.getCode());
//        log.info("比较：" + (verCode.getCode().equals(mail.getCode())));
        if (verCode == null){
            log.info("邮箱验证码为空");
            throw new CustomException(2,ConstantUtil.EmailNullException);
        }
        if (!(verCode.getCode().equals(changePassword.getVcode()))){
            log.info("邮箱验证码错误，或者超时");
            throw new CustomException(2,ConstantUtil.EmailVerificationCodeException);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("mailbox",changePassword.getEmail());
        map.put("password",changePassword.getPassword());
        Integer num = userServiceImp.UpdatePassword(map);
        if (num == 0){
            log.info("修改密码失败");
            throw new CustomException(2,ConstantUtil.ChangePasswordException);
        }

        return "success";
    }

}
