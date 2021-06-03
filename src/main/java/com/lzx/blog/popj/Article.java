package com.lzx.blog.popj;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Component
@Scope("prototype")
/**
 * 文章表
 */
public class Article extends LikesFavorites implements Comparable<Article>,Cloneable{

    private Integer id;

    private String username;

    private String title;

    private Timestamp time;

    private String image;

    /**
     * 发帖人的头像，从user哪来的
     */
    private String avatar;

    /**
     * 发帖人的id，从user哪来的
     */
    private String userid;

    private String text;

    public Integer comments;

    public Integer top;

    public String status;

    private String types;

    private Integer grade;

    private Integer views;
    /**
     * 返回给前端的更详细的时间, 比如几分钟前,几个小时前
     */
    private Integer deltime;

    /**
     * 返回给前端的更详细的时间的单位
     * */
    private String delTimeUnit;

    /**
     * 该帖子的点赞数
     * */
    private Integer likes;

    /**
     * 该帖子的收藏数
     * */
    private Integer favorites;


    /**
     * 点赞的样式
     */
    private String likesstyle;

    /**
     * 收藏的样式
     */
    private String favoritesstyle;

    @Override
    public int compareTo(Article o) {
        return o.getId()-this.getId();
    }

    @Override
    public Object clone() {
        Article addr = null;
        try{
            addr = (Article)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return addr;
    }

    public void SetUp(String Username, String Title, String Text, Integer class2){
        this.username = Username;
        this.title  = Title;
        this.text = Text;
        this.time = new Timestamp(System.currentTimeMillis());
        this.comments = 0;
        this.top = 0;
        this.status = "未结";
        if (class2 == 1){
            this.types = "提问";
        }else if(class2 == 2){
            this.types = "分享";
        }else if(class2 == 2){
            this.types = "讨论";
        }else
            this.types = "公告";
        this.views = 0;
        this.likes = 0;
        this.favorites = 0;
    }
}
