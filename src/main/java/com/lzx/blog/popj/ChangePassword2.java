package com.lzx.blog.popj;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated  //数据校验
/**
 * 修改密码的实体
 */
public class ChangePassword2 {


    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "新密码不能为空")
    private String newpassword;
}
