package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
/**
 * 角色表
 */
public class Role {

    private int id;

    private String username;

    private String role;

    private String crown;

    /**
     * 数据库中是一个字符串，但是我们显示给前端的必须的一个头衔或者身份集合
     */
    private String[] roles;

    private String[] crowns;

    public void SetAll( String username, String role, String crown) {
        this.username = username;
        this.role = role;
        this.crown = crown;
    }
}
