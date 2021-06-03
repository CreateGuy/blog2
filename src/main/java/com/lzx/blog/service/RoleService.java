package com.lzx.blog.service;

import com.lzx.blog.popj.Role;

import java.util.List;
import java.util.Map;

public interface RoleService {
    public Role SelById(int id);

    List<Role> SelAll();

    Role SelByAll(Map map);

    Integer InsertRole(Role role);
}
