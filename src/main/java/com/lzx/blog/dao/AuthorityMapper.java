package com.lzx.blog.dao;

import com.lzx.blog.popj.Authority;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityMapper {

    /**
     *通过id查询权限
     * @param id
     * @return
     */
    Authority SelById(int id);
}
