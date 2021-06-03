package com.lzx.blog.service;

import com.lzx.blog.dao.AuthorityMapper;
import com.lzx.blog.popj.Authority;
import com.lzx.blog.popj.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author:wjup
 * @Date: 2018/9/26 0026
 * @Time: 15:23
 */
@Service
@Transactional
public class AuthorityServiceImp implements AuthorityService {
    @Autowired
    AuthorityMapper authorityMapper;


    @Override
    public Authority SelById(int id) {
        return authorityMapper.SelById(id);
    }
}