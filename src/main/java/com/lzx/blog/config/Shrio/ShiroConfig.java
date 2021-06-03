package com.lzx.blog.config.Shrio;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig{
    //3、ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Autowired DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

        //添加shiro的内置过滤器
        /**
         anon:表示可以匿名使用。 authc:表示需要认证(登录)才能使用，没有参数
         *
         * roles：参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，
         * 例如admins/user/**=roles["admin,guest"],每个参数通过才算通过，相当于hasAllRoles()方法。
         *lo
         *
         * perms：参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，
         * 例如/admins/user/**=perms["user:add:*,user:modify:*"]，
         * 当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法。
         *
         * rest：根据请求的方法，相当于/admins/user/**=perms[user:method]
         * ,其中method为post，get，delete等。
         *
         * port：当请求的url的端口不是8081是跳转到schemal://serverName:8081?queryString,
         * 其中schmal是协议http或https等，serverName是你访问的host,8081是url配置里port的端口，
         * queryString是你访问的url里的？后面的参数。 authcBasic：没有参数表示httpBasic认证
         *
         * ssl:表示安全的url请求，协议为https user:当登入操作时不做检查
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        //必须认证才能访问
//        filterMap.put("/user/adduser","authc");
//        filterMap.put("/user/updateuser","authc");
        filterMap.put("/user/management","perms[user:super]");

        filterMap.put("/js/**", "anon");
        filterMap.put("/css/**", "anon");
        filterMap.put("/img/**", "anon");
        filterMap.put("/fonts/**", "anon");
        filterMap.put("/plugins/**", "anon");
        filterMap.put("/", "anon");
//        filterMap.put("/test2", "perms[super]");
//        filterMap.put("/test3", "perms[ordinary]");
        // 默认所有资源必须认证才能访问
//        filterMap.put("/**", "authc");

        bean.setFilterChainDefinitionMap(filterMap);

        //设置未登录时跳到登录页面
        bean.setLoginUrl("/user/signin");
        //未经授权,之后跳转的界面
        bean.setUnauthorizedUrl("/index");
        // 登录成功后要跳转的链接,不知道为什么没用
        bean.setSuccessUrl("/homepage");
        return bean;
    }

    //2、DefaultWebSecurityManager
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Autowired UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联userRealm
        securityManager.setRealm(userRealm);
        //rememberMeManager添加到管理器里面
        securityManager.setRememberMeManager(rememberMeManager());
        //记住我功能之后，再次打开浏览器会在浏览器请求地址上面带有jssionid的字段
        //使用这段代码就可以没有了
        securityManager.setSessionManager(mySessionManager());

        //注入缓存管理器;
        securityManager.setCacheManager(ehCacheManager());
        return securityManager;
    }


    //1、创建UserRealm 需自定义类
    @Bean
    public UserRealm getUserRealm(){
        UserRealm userRealm = new UserRealm();

        userRealm.setCachingEnabled(true);//开启全局缓存
        //启用身份验证缓存，即缓存AuthenticationInfo信息，默认false
        userRealm.setAuthenticationCachingEnabled(true);//启用认证缓存
        userRealm.setAuthenticationCacheName("authenticationCache");//取个名字

        userRealm.setAuthorizationCachingEnabled(true);//启用授权缓存
        userRealm.setAuthorizationCacheName("authorizationCache");
        //配置自定义密码比较器
        userRealm.setCredentialsMatcher(retryLimitCredentialsMatcher());

        return userRealm;
    }

    //shiro整合thymeleaf
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }


    /**
     * Shiro生命周期处理器
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
 　　* 开启shiro aop注解支持.
 　　* 使用代理方式;所以需要开启代码支持;
 　　*/
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

//    下面两个都是来设置记住我功能的
    /**
     * cookie对象
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        // 设置cookie名称，对应login.html页面的<input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        // 设置cookie的过期时间，单位为秒，这里为一天
        cookie.setMaxAge(60*24*7);
        return cookie;
    }

    /**
     * cookie管理对象
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie加密的密钥
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    @Bean
    public DefaultWebSessionManager mySessionManager(){
        DefaultWebSessionManager defaultSessionManager = new DefaultWebSessionManager();
        //将sessionIdUrlRewritingEnabled属性设置成false
        defaultSessionManager.setSessionIdUrlRewritingEnabled(false);
        return defaultSessionManager;
    }


    /**
     * shiro缓存管理器;
     * 需要添加到securityManager中
     * @return
     */
    @Bean
    public EhCacheManager ehCacheManager(){
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
        return cacheManager;
    }

    /**
     * 限制登录次数
     * @return 匹配器
     */
    @Bean
    public CredentialsMatcher retryLimitCredentialsMatcher() {
        RetryLimitHashedCredentialsMatcher retryLimitCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(ehCacheManager());
        retryLimitCredentialsMatcher.setMaxRetryNum(4);
        return retryLimitCredentialsMatcher;

    }
}
