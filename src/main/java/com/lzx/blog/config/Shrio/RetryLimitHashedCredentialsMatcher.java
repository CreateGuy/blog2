package com.lzx.blog.config.Shrio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.lzx.blog.dao.UserMapper;
import com.lzx.blog.popj.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author: WangSaiChao
 * @date: 2018/5/25
 * @description: 登陆次数限制
 */
@Slf4j
@Data
//@Component
public class RetryLimitHashedCredentialsMatcher extends SimpleCredentialsMatcher {

    @Autowired
    private UserMapper userMapper;
    private Cache<String, AtomicInteger> passwordRetryCache;
    private Integer MaxRetryNum = 4;


    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        log.info("锁定验证开始");
        //获取用户名
        String username = (String)token.getPrincipal();
        //获取用户登录次数
        AtomicInteger retryCount = passwordRetryCache.get(username);

        if (retryCount == null) {
            //如果用户没有登陆过,登陆次数加1 并放入缓存
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if (retryCount.incrementAndGet() > MaxRetryNum) {
            //如果用户登陆失败次数大于5次 抛出锁定用户异常  并修改数据库字段
            Map<String,Object> map = new HashMap();
            map.put("username",username);
            List<User> users = userMapper.selByIUP(map);
            if (users.get(0) != null && 0 == users.get(0).getState()){
                //数据库字段 默认为 0  就是正常状态 所以 要改为1
                //修改数据库的状态字段为锁定
                users.get(0).setState(1);
                Integer num = userMapper.UpdateState(users.get(0).getUsername(),users.get(0).getState());
                log.info("num:"+num);
            }
            log.info("锁定用户" + users.get(0).getUsername());
            //抛出用户锁定异常
            throw new LockedAccountException();
        }
        //判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //如果正确,从缓存中将用户登录计数 清除
            passwordRetryCache.remove(username);
        }else{
            log.info("密码错误");
        }
        return matches;
    }

    /**
     * 根据用户名 解锁用户
     * @param username
     * @return
     */
    public void unlockAccount(String username){
        Map<String,Object> map = new HashMap();
        map.put("username",username);
        List<User> users = userMapper.selByIUP(map);
        if (users.get(0) != null){
            //修改数据库的状态字段为锁定
            users.get(0).setState(0);
            userMapper.UpdateState(users.get(0).getUsername(),users.get(0).getState());
            passwordRetryCache.remove(username);
        }
    }

}