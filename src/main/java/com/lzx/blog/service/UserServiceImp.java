package com.lzx.blog.service;

import com.lzx.blog.dao.UserMapper;
import com.lzx.blog.popj.Article;
import com.lzx.blog.popj.SignIn;
import com.lzx.blog.popj.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author:wjup
 * @Date: 2018/9/26 0026
 * @Time: 15:23
 */
@Service
@Transactional
public class UserServiceImp implements UserService{
    @Autowired
    UserMapper userMapper;


    @Override
    public Integer UpdateState(String username,Integer state) {
        return userMapper.UpdateState(username,state);
    }

    @Override
    public List<User> selByIUP(Map<String, Object> map) {
        return userMapper.selByIUP(map);
    }

    @Override
    public Integer insertuser(User user) {
        return userMapper.insertuser(user);
    }

    @Override
    public Integer UpdateUser(Map<String, Object> map) {
        return userMapper.UpdateUser(map);
    }

    @Override
    public List<User> selAll() {
        return userMapper.selAll();
    }

    @Override
    public Integer UpdatePassword(Map<String, Object> map) {
        return userMapper.UpdatePassword(map);
    }

    @Override
    public List<SignIn> SelectSignInByUsername(String username) {
        return userMapper.SelectSignInByUsername(username);
    }

    @Override
    public Integer UpdateSingin(Map<String, Object> map) {
        return userMapper.UpdateSingin(map);
    }

    @Override
    public Integer UpdateSinginStatus1() {
        return userMapper.UpdateSinginStatus1();
    }

    @Override
    public Integer UpdateSinginStatus2() {
        return userMapper.UpdateSinginStatus2();
    }


    //细化时间
    public void ConversionTimeArticle(User user, long TimeDifference){
        if (TimeDifference < 60){//60秒前
            if (TimeDifference == 0 || TimeDifference == -1)
                user.setDeltime(1);
            else
                user.setDeltime((int) TimeDifference);
            user.setDelTimeUnit("秒前");
        }else if (TimeDifference < 60*60){
            user.setDeltime((int) (TimeDifference/60));
            user.setDelTimeUnit("分钟前");
        }else if (TimeDifference < 60*60*24){
            user.setDeltime((int) (TimeDifference/60/60));
            user.setDelTimeUnit("小时前");
        }
        else if (TimeDifference < 60*60*24*30){
            user.setDeltime((int) (TimeDifference/60/60/24));
            user.setDelTimeUnit("天前");
        }
        else if (TimeDifference < 60*60*24*30*12){
            user.setDeltime((int) (TimeDifference/60/60/24/30));
            user.setDelTimeUnit("月前");
        }else {
            user.setDeltime(-1);
        }
    }
}