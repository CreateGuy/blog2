package com.lzx.blog.popj.expand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
/**
 * 前端注册时候的实体
 */
public class Mail {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @Email
    private String mailbox;

    @NotEmpty
    private String code;
}
