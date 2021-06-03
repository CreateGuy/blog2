package com.lzx.blog.popj;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated  //数据校验
/**
 * 找回密码的实体
 */
public class ChangePassword {

    @Email(message="邮箱格式错误") //name必须是邮箱格式
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱验证码不能为空")
    private String vcode;
}
