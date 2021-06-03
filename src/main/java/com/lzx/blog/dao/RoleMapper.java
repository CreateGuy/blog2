package com.lzx.blog.dao;

import com.lzx.blog.popj.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RoleMapper {

    /**
     * 通过id查询角色
     * @param id
     * @return
     */
    Role SelById(int id);

    /**
     * 查询所有角色信息
     * @return
     */
    List<Role> SelAll();

    /**
     * 查询指定用户名的角色
     * @return
     */
    Role SelByAll(Map map);

    /**
     * 插入角色
     * @param role
     * @return
     */
    Integer InsertRole(Role role);

}
