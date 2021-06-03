package com.lzx.blog.Util.exception;

import com.lzx.blog.Util.exception.exceptionclass.*;
import com.lzx.blog.Util.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.mail.MailException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * controller 增强器，全局异常处理类
 *
 * @author sam
 * @since 2017/7/17
 */
@ControllerAdvice
@Slf4j
public class UserControllerAdvice {

//    /**
//     * 全局异常捕捉处理
//     * @param ex
//     * @return
//     */
//    @ResponseBody
//    @ExceptionHandler(value = Exception.class)
//    public Map errorHandler(Exception ex) {
//        Map map = new HashMap();
//        map.put("code", 100);
//        map.put("msg", ex.getMessage());
//        return map;
//    }

    /**
     * 拦截捕捉自定义异常 AuthorizationException
     * 份认证异常
     *身份令牌异常，不支持的身份令牌
     * org.apache.shiro.authc.pam.UnsupportedTokenException
     *
     * 未知账户/没找到帐号,登录失败
     * org.apache.shiro.authc.UnknownAccountException
     * 帐号锁定 -->
     * org.apache.shiro.authc.LockedAccountException
     * 用户禁用 -->
     * org.apache.shiro.authc.DisabledAccountException
     *登录重试次数，超限。只允许在一段时间内允许有一定数量的认证尝试 -->
     * org.apache.shiro.authc.ExcessiveAttemptsException
     * 个用户多次登录异常：不允许多次登录，只能登录一次 。即不允许多处登录-->
     * org.apache.shiro.authc.ConcurrentAccessException
     * 账户异常 -->
     * org.apache.shiro.authc.AccountException
     *
     * 过期的凭据异常 -->
     * org.apache.shiro.authc.ExpiredCredentialsException
     * 错误的凭据异常 -->
     * org.apache.shiro.authc.IncorrectCredentialsException
     *  凭据异常 -->
     * org.apache.shiro.authc.CredentialsException
     * org.apache.shiro.authc.AuthenticationException
     *
     * 权限异常 -->
     * 没有访问权限，访问异常 -->
     * org.apache.shiro.authz.HostUnauthorizedException
     * org.apache.shiro.authz.UnauthorizedException
     *  授权异常 -->
     * org.apache.shiro.authz.UnauthenticatedException
     * org.apache.shiro.authz.AuthorizationException
     *
     * hiro全局异常 -->
     * org.apache.shiro.ShiroException
     * @param ex
     * @return
     */

    /**
     * 无访问权限
     * @param ex
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public String myAuthorization(AuthorizationException ex,Model model) {
//        Map map = new HashMap();
//        map.put("msg", ConstantUtil.AuthorizationException);
//        log.info("msg:"+ ConstantUtil.AuthorizationException);
//        return map;
        model.addAttribute("msg", ConstantUtil.AuthorizationException);
        log.info("msg:"+ ConstantUtil.AuthorizationException);
        return "forward:/other/notice";
    }

    /**
     * 密码错误
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = IncorrectCredentialsException.class)
    public String myUnknownAccountException(IncorrectCredentialsException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.PasswordException);
        log.info("msg:"+ ConstantUtil.PasswordException);
        return ConstantUtil.PasswordException;
    }

    /**
     * 账户错误
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = AuthenticationException.class)
    public String myUnknownAccountException(AuthenticationException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.UnknownAccountException);
        log.info("msg:"+ ConstantUtil.UnknownAccountException);
        return ConstantUtil.UnknownAccountException;
    }

    /**
     * 账户已锁定
     * @param ex
     * @return
     */

    @ResponseBody
    @ExceptionHandler(value = LockedAccountException.class)
    public String myLockedAccountException(LockedAccountException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.LockedAccountException);
        log.info("msg:"+ ConstantUtil.LockedAccountException);
        return ConstantUtil.LockedAccountException;
    }


    /**
     * 图片验证码错误
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ImageVerificationCodeException.class)
    public String myImageVerificationCode(ImageVerificationCodeException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.ImageVerificationCodeException);
        log.info("msg:"+ ConstantUtil.ImageVerificationCodeException);
        return ConstantUtil.ImageVerificationCodeException;
    }

    /**
     * "邮箱验证码错误，或者超时
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = EmailVerificationCodeException.class)
    public String myEmailVerificationCodeException(EmailVerificationCodeException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.EmailVerificationCodeException);
        log.info("msg:"+ ConstantUtil.EmailVerificationCodeException);
        //registeredmsg是index页面的一个 th:fragment="registeredmsg  的标签
        return ConstantUtil.EmailVerificationCodeException;
    }

    /**
     * 邮箱错误
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MailException.class)
    public String myMailException(MailException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.MailException);
        log.info("msg:"+ ConstantUtil.MailException);
        //registeredmsg是index页面的一个 th:fragment="registeredmsg  的标签
        return ConstantUtil.MailException;
    }

    /**
     * 数据格式异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public String myBindException(BindException ex, Model model) {
        model.addAttribute("msg", ConstantUtil.BindException);
        log.info("msg:"+ "Bind" + ConstantUtil.BindException);
        return ConstantUtil.BindException;
    }

    /**
     * 数据格式异常
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public String myMethodArgumentNotValidException(MethodArgumentNotValidException ex, Model model) {
        model.addAttribute("msg", "MethodArgumentNotValid" + ConstantUtil.BindException);
        log.info("msg:"+ ConstantUtil.BindException);
        return ConstantUtil.BindException;
    }

//    /**
//     * 空值异常
//     * @param ex
//     * @return
//     */
//    @ResponseBody
//    @ExceptionHandler(value = NullPointerException.class)
//    public String myNullPointerException(NullPointerException ex, Model model) {
//        model.addAttribute("msg", "空值异常");
//        log.info("msg:"+ "空值异常");
////        log.info("msg:"+ ex);
//        return "空值异常";
//    }

    /**
     * 自定义异常
     * @param ex
     * @return
     */

    @ResponseBody
    @ExceptionHandler(value = CustomException.class)
    public String myCustomException(CustomException ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        log.info("msg:"+ ex.getMessage());
        return ex.getMessage();
    }
    @ResponseBody
    @ExceptionHandler(value = CustomException2.class)
    public CustomException2 myCustomException2(CustomException2 ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        log.info("msg:"+ ex.getMessage());
        return ex;
    }


    /**
     * 自定义异常
     * @param ex
     * @return
     */

    @ResponseBody
    @ExceptionHandler(value = CustomizeException.class)
    public String myCustomizeException(CustomizeException ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        log.info("msg:"+ ex.getMessage());
        return ex.getViewName();
    }

    /**
     * 自定义异常
     * @param ex
     * @return
     */

    @ResponseBody
    @ExceptionHandler(value = ErrorpageException.class)
    public String mErrorpageException(ErrorpageException ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        model.addAttribute("code", ex.getCode());
        log.info("msg:"+ ex.getMessage());
        return ex.getMessage();
    }

}