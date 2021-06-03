package com.lzx.blog.dao;

import com.lzx.blog.popj.SignIn;
import com.lzx.blog.popj.User;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author:wjup
 * @Date: 2018/9/26 0026
 * @Time: 15:20
 */
@Repository
public interface UserMapper {


    /**
     * 更新用户的状态信息
     * @param username 用户名
     * @param state 是否锁定的状态
     * @return
     */
    Integer UpdateState(String username,Integer state);

    /**
     * 动态查询用户
     * @param map  条件
     * @return
     */
    List<User> selByIUP(Map<String,Object> map);

    /**
     * 注册一个用户，若传入的只有一个对象必须加@Param
     * @param user
     * @return
     */
    Integer insertuser(@Param("user")User user);


    /**
     * 动态修改user表的各个参数
     * @param map 要修改的map集合
     * @return 返回的是成功的记录条数
     */
    Integer UpdateUser(Map<String,Object> map);

    /**
     * 查询所有
     * @return
     */
    List<User> selAll();

    /**
     * 修改密码
     * @param map
     * @return
     */
    Integer UpdatePassword(Map<String,Object> map);


    /**
     * 查询签到信息
     * @param username 为空代表查询所有
     * @return
     */
    List<SignIn> SelectSignInByUsername(@Param("username")String username);


    /**
     * 更新签到表
     * @param map
     * @return
     */
    Integer UpdateSingin(Map<String,Object> map);


    /**
     * 每日更新签到表，更新今日是否签到，和增加联系天数
     * @return
     */
    Integer UpdateSinginStatus1();

    /**
     * 每日更新签到表，更新断签和连续天数
     * @return
     */
    Integer UpdateSinginStatus2();

}
