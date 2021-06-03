package com.lzx.blog.service.ArticleService;

import com.lzx.blog.popj.*;
import com.lzx.blog.popj.expand.ReplyArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    List<Article> SelByAll(Integer id);

    public List<ReplyArticle>SelAllArticle(Integer authority);

    public Integer InsertReplyf(@Param("c") Comment c, @Param("TableName")String TableName);

    public Replyz SelByAllReplyz(@Param("authority") Integer authority);

    public Integer InsertReplyz(@Param("c") Comment c,@Param("mailbox")String mailbox);

    public List<Article> SelByMap(Map<String,Object> map);

    public Integer DeletePost(Map<String,Object> map);

    public Integer InsertArticle(@Param("article") Article article);

    Integer UpdateReplyz(Map<String,Object> map);

    Integer UpdateReplyf(Map<String,Object> map);

    Integer UpdateArticle(Map<String,Object> map);

    List<Article> SelByUsername(@Param("username")String username);

    public List<Reply> SelRecentByUsername(@Param("username")String username);

    public List<Reply> SelRecentByUsername2(@Param("username")String username);

    public List<Article> SelByComments0(Map<String,Object> map);

    public List<Article> SelByComments2(Map<String,Object> map);

    public List<Article> SelBlurryByPostTitle(@Param("PostName")String PostName);


    public LikesFavorites SelByArticleidToLikesFavorites(@Param("articleid")Integer articleid, @Param("username")String username);

    public Integer InsertLikesFavorites(LikesFavorites likesFavorites);

    public Integer UpdateLikesFavorites(Map<String,Object> map);

    List<Article> SelByUsernameToLikesFavorites(@Param("username")String username);

}
