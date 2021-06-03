package com.lzx.blog.dao.ArticleMapper;

import com.lzx.blog.popj.*;
import com.lzx.blog.popj.expand.ReplyArticle;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ArticleMapper {

    /**
     * 查询某个帖子，通过id，可以设置为0那么就是所有 帖子
     * @param id 帖子id
     * @return
     */
    public List<Article> SelByAll(@Param("id")Integer id,@Param("username")String username);


    /**
     * 根据map集合查询帖子
     * @param
     * @return
     */
    public List<Article> SelByComments0(Map<String,Object> map);

    /**
     * 根据map集合查询帖子,没有了置顶的帖子
     * @param
     * @return
     */
    public List<Article> SelByComments2(Map<String,Object> map);

    /**
     * 查询帖子
     * @param map  通过何种条件查询的集合
     * @return
     */
    public List<Article> SelByMap(Map<String,Object> map);


    /**
     * 查询某个帖子的所有回复，通过id，可以设置为0那么就是所有 帖子
     * @param authority 帖子id
     * @return
     */
    public List<ReplyArticle>SelAllArticle(@Param("authority")Integer authority);

    /**
     *  发表评论
     * @param c 评论实体
     * @param TableName 表名
     * @return
     */
    public Integer InsertReplyf(@Param("c") Comment c,@Param("TableName")String TableName);

    /**
     * 查询该帖子的最大的楼层
     * @param authority 帖子id
     * @return
     */
    public Replyz SelByAllReplyz(@Param("authority") Integer authority);

    /**
     * 插入一个楼层
     * @param c
     * @param mailbox
     * @return
     */
    public Integer InsertReplyz(@Param("c") Comment c,@Param("mailbox")String mailbox);


    /**
     * 删除帖子
     * @param map 要删除的帖子的特点
     * @return
     */
    public Integer DeletePost(Map<String,Object> map);

    /**
     * 发帖
     * @param article 帖子的实体
     * @return 插入的记录条数
     */
    public Integer InsertArticle(@Param("article") Article article);


    /**
     * 更新Replyz的头像
     * @param map
     * @return
     */
    Integer UpdateReplyz(Map<String,Object> map);

    /**
     * 更新Replyz的头像
     * @param map
     * @return
     */
    Integer UpdateReplyf(Map<String,Object> map);


    Integer UpdateArticle(Map<String,Object> map);


    /**
     * 查询某个帖子，通过Username，可以设置为0那么就是所有 帖子
     * @param username 帖子id
     * @return
     */
    public List<Article> SelByUsername(@Param("username")String username);

    /**
     * 查找replyz表的最近五条记录
     * @param username
     * @return
     */

    public List<Reply> SelRecentByUsername(@Param("username")String username);


    /**
     * 查找replyf表的最近五条记录
     * @param username
     * @return
     */

    public List<Reply> SelRecentByUsername2(@Param("username")String username);

    /**
     * 模糊查询帖子
     * @param PostName 根据帖子名查询
     * @return
     */
    public List<Article> SelBlurryByPostTitle(@Param("PostName")String PostName);


    /**
     * 查询用户有没有给某个帖子点赞或者收藏
     * @param articleid 帖子id
     * @param username  用户名
     * @return
     */
    public LikesFavorites SelByArticleidToLikesFavorites(@Param("articleid")Integer articleid,@Param("username")String username);


    /**
     * 插入点赞和收藏表
     * @param likesFavorites
     * @return
     */
    public Integer InsertLikesFavorites(LikesFavorites likesFavorites);
    /**
     * 更新改用户对于某个帖子点赞和收藏状态
     * @return
     */
    Integer UpdateLikesFavorites(Map<String,Object> map);


    /**
     * 查询用户收藏的帖子
     * @param username
     * @return
     */
    List<Article> SelByUsernameToLikesFavorites(@Param("username")String username);
}
