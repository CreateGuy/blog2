package com.lzx.blog.popj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Validated  //数据校验
/**
 * 前端发给后端的邮箱表，方便保存到缓存中
 */
public class VerCode implements Serializable {

    private String username;

    @Email(message="邮箱格式错误") //name必须是邮箱格式
    private String email;

    private String code;

    private String date;

    public VerCode(@Email(message = "邮箱格式错误") String email, String code) {
        this.email = email;
        this.code = code;
    }
}
