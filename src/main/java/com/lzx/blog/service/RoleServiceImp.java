package com.lzx.blog.service;

import com.lzx.blog.dao.RoleMapper;
import com.lzx.blog.popj.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Override
    public Role SelById(int id) {
        return roleMapper.SelById(id);
    }

    @Override
    public List<Role> SelAll() {
        return roleMapper.SelAll();
    }

    @Override
    public Role SelByAll(Map map) {
        return roleMapper.SelByAll(map);
    }

    @Override
    public Integer InsertRole(Role role) {
        return roleMapper.InsertRole(role);
    }
}
