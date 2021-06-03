package com.lzx.blog.service;

import com.lzx.blog.popj.SignIn;
import com.lzx.blog.popj.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


public interface UserService {

    Integer UpdateState(String username,Integer state);

    List<User> selByIUP(Map<String,Object> map);

    Integer insertuser(User user);

    Integer UpdateUser(Map<String,Object> map);

    List<User> selAll();

    Integer UpdatePassword(Map<String,Object> map);

    List<SignIn> SelectSignInByUsername(@Param("username")String username);

    Integer UpdateSingin(Map<String,Object> map);

    Integer UpdateSinginStatus1();

    Integer UpdateSinginStatus2();

}
