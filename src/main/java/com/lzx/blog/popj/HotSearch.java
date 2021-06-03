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
public class HotSearch {

    private String rank;//排名

    private String num;//热度指数

    private String title;//热搜标题

    private String url;//热搜URL网址(相对地址)

    private String baseurl;//和上述url组成完整可访问的单个热搜URL
}
