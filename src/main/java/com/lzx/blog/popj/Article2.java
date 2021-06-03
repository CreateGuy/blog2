package com.lzx.blog.popj;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Validated  //数据校验
/**
 * 第二个文章表，主要是为了接受前端发表文章的数据
 */
public class Article2{

    @Max(value = 4,message = "文章分类最大为3")
    @Min(value = 1,message = "文章分类最小为1")
    //文章的分类
    private Integer class1;

    @NotBlank(message = "标题不能为空")
    //文章的标题
    private String title;

    @NotBlank(message = "验证码不能为空")
    //验证码
    private String vercode;

    @NotBlank(message = "正文不能为空")
    //正文
    private String text;

    @Override
    public String toString() {
        return "Article2{" +
                "class1='" + class1 + '\'' +
                ", title='" + title + '\'' +
                ", vercode='" + vercode + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
