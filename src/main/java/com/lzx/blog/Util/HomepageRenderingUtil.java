package com.lzx.blog.Util;

import com.lzx.blog.popj.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Component
@Slf4j
/**
 * 切入类
 */
public class HomepageRenderingUtil {


//    @AfterReturning("execution(public String com.lzx.blog.controller.User.UserLogin.Sign(..)) ||" +
//            "execution(public String com.lzx.blog.controller.User.UserRegistered.registereduser(..)) ||" +
//            "execution(public String com.lzx.blog.controller.User.UserLogin.homepage(..))")
//    public void beforMethod(JoinPoint point){
//        String methodName = point.getSignature().getName();
////        List<Object> args = Arrays.asList(point.getArgs());
////        System.out.println("调用前连接点方法为：" + methodName + ",参数为：" + args);
//        log.info("调用前连接点方法为：" + methodName);
//    }


    /**
     * 切入所有的controller请求，因为如果为如果设置了记住我功能之后，再次打开浏览器
     * 是没有关于用户的session的，我们都是在登陆请求的时候放入的session的，那么我们就判断
     * 该用户是或在shrio的session中存在，如果存在就说明是已经登陆的，我们将他放入httpsession中
     * @param point
     */
    @Before("execution(public String com.lzx.blog.controller..*.*(..))")
    public void beforMethod(JoinPoint point){
        String methodName = point.getSignature().getName();
//        List<Object> args = Arrays.asList(point.getArgs());
//        System.out.println("调用前连接点方法为：" + methodName + ",参数为：" + args);
        log.info("调用前连接点方法为：" + methodName);

        //得到当前对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候报错的user
        User currentUser = (User) subject.getPrincipal();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            User user = (User) session.getAttribute("user");
//            log.info("aopUser" + user);
            //前端的标签我们可以用shiro标签获取用户名，
            // 但是前端要在js中获取session中的对象，如果不放入对象，那么前端那么不调用也会报错
            //那么我们就放入一个id为-1的对象，代表未登录
            if (user == null) {
                if (currentUser == null) {
                    User user1 = new User();
                    user1.setId(-1);
                    user1.setUsername("-1");
                    session.setAttribute("user", user1);
                }else
                    session.setAttribute("user", currentUser);
            }
            log.info("user:" + currentUser);
            // Do something
        }else
            log.info("requestAttributes == null");

    }

}
