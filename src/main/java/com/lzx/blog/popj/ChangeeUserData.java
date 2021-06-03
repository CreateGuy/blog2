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
public class ChangeeUserData {

    @NotBlank(message = "名称不能为空")
    private String username;

    @NotBlank(message = "签名不能为空")
    private String message;

    @NotBlank(message = "性别")
    private String sex;
}
