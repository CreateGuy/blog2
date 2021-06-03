package com.lzx.blog.config.Shrio;

import com.lzx.blog.popj.Authority;
import com.lzx.blog.popj.Role;
import com.lzx.blog.popj.User;
import com.lzx.blog.service.AuthorityServiceImp;
import com.lzx.blog.service.RoleService;
import com.lzx.blog.service.UserServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.*;

@Slf4j
public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserServiceImp serviceImp;

    @Autowired
    AuthorityServiceImp authorityServiceImp;

    @Autowired
    RoleService characterService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("AuthorizationInfo->执行了授权");
        //SimpleAuthorizationInfo
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //获取当前登录的对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        Authority authority = authorityServiceImp.SelById(currentUser.getId());
        Role role = characterService.SelById(currentUser.getId());

        if(authority != null) {
            //授权
            String[] strArr = authority.getAuthority().split("_");
            for (String str :
                    strArr) {
                log.info("权限:" + str);
                info.addStringPermission(str);
            }
        }
        if (role != null) {
            //赋予角色
            Set<String> roles = new HashSet<String>();
            String[] roleArr = role.getRole().split("_");
            for (String str :
                    roleArr) {
                log.info("角色:" + str);
                roles.add(str);

            }
            info.setRoles(roles);
        }
        log.info("当前用户授权,赋予角色完毕");
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("AuthenticationInfo->执行了认证");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        log.info("realm:"+token);
        Map<String,Object> map = new HashMap();
        map.put("username",token.getUsername());
        log.info("1");
        List<User> users1 = serviceImp.selAll();
        log.info("2");
        List<User> users =  serviceImp.selByIUP(map);
        if(users.get(0) == null){
            return null;
        }
        log.info("认证中");
        //密码认证
        return new SimpleAuthenticationInfo(users.get(0),users.get(0).getPassword(),getName());
    }

}
