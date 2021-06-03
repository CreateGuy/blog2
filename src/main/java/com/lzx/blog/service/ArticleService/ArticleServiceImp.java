package com.lzx.blog.service.ArticleService;

import com.lzx.blog.Util.CalTime;
import com.lzx.blog.dao.ArticleMapper.ArticleMapper;

import com.lzx.blog.popj.*;
import com.lzx.blog.popj.expand.ReplyArticle;
import com.lzx.blog.service.RoleServiceImp;
import com.lzx.blog.service.UserServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author:wjup
 * @Date: 2018/9/26 0026
 * @Time: 15:23
 */
@Service
@Transactional
@Slf4j


public class ArticleServiceImp implements ArticleService {
    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    RoleServiceImp roleServiceImp;

    @Autowired
    UserServiceImp userServiceImp;

    @Override
    public List<Article> SelByAll(Integer id) {
        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        List<Article> articles;

        //判断用户有没有登陆，登陆了我们就显示哪些帖子收藏和点赞，不然就都没有
        if (currentUser == null)
            articles = articleMapper.SelByAll(id,"");
        else
            articles = articleMapper.SelByAll(id,currentUser.getUsername());
        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
            articles) {
            log.info("art:"+art);
            //在对回复的楼中楼排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
            //设置当前登陆的用户显示的帖子的收藏和点赞的样式

            if (art.getLFlikes() == null){
                art.setLikesstyle("/res/images/likes1.png");
            }else if (art.getLFlikes() == 1){
                art.setLikesstyle("/res/images/likes2.png");
            }else {
                art.setLikesstyle("/res/images/likes1.png");
            }

            if (art.getLFfavorites() == null){
                art.setFavoritesstyle("/res/images/Favorites1.png");
            }else if (art.getLFfavorites() == 1){
                art.setFavoritesstyle("/res/images/Favorites2.png");
            }else {
                art.setFavoritesstyle("/res/images/Favorites1.png");
            }
        }

        return articles;
    }

    @Override
    public List<ReplyArticle> SelAllArticle(Integer authority) {
        List<ReplyArticle> replyArticles = articleMapper.SelAllArticle(authority);

        List<Role> roles = roleServiceImp.SelAll();

        Map<String,Object> usermap = new HashMap();

        List<User> users = userServiceImp.selAll();

        //排序。倒叙
        Collections.sort(replyArticles);
        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        SimpleDateFormat DateFormat = new SimpleDateFormat(CalTime.DateFormat);
        //在对回复的楼层排序，倒叙,以及赋予头衔,设置等级
        for (ReplyArticle re:
                replyArticles) {
            //在对回复的楼中楼排序，倒叙
            Collections.sort(re.getReplyfs());
            //对时间进行进一步的划分，表示 几分钟前或者几个小时前。。。。。
            ConversionTimeReplyz(re, CalTime.ConversionTime(currentTime, DateFormat.format(re.getTime()), CalTime.DateFormat));

            for (Replyf r :
                    re.getReplyfs()) {

                // 放入该用户的等级和id
                for (User user:
                        users) {
                    if (user.getUsername().equals(r.getUsername())){
                        r.setGrade(user.getGrade());
                        r.setUserid(user.getId());
                    }
                }

                //            放入该角色的头衔
                for (Role role:
                        roles) {
                    if (role.getUsername().equals(r.getUsername())){
                        String[] strArr = role.getCrown().split("_");
                        r.setCrown(strArr);
                    }
                }


                ConversionTimeReplyf(r, CalTime.ConversionTime(currentTime, DateFormat.format(r.getTime()), CalTime.DateFormat));
            }

//            放入该角色的头衔
            for (Role role:
                    roles) {
                if (role.getUsername().equals(re.getUsername())){
                    String[] strArr = role.getCrown().split("_");
                    re.setCrown(strArr);
                }
            }
//            放入该用户的等级和id
            for (User user:
                    users) {
                if (user.getUsername().equals(re.getUsername())){
                    re.setGrade(user.getGrade());
                    re.setUserid(user.getId());
                }
            }
        }
        return replyArticles;
    }

    @Override
    public Integer InsertReplyf(Comment c, String TableName) {
        return articleMapper.InsertReplyf(c,TableName);
    }

    @Override
    public Replyz SelByAllReplyz(Integer authority) {
        return articleMapper.SelByAllReplyz(authority);
    }

    @Override
    public Integer InsertReplyz(Comment c, String mailbox) {
        return articleMapper.InsertReplyz(c,mailbox);
    }

    @Override
    public List<Article> SelByMap(Map<String, Object> map) {
        List<Article> articles = articleMapper.SelByMap(map);

//        articles类实现了Comparable接口，但是用id进行排序，现在用自定义的接口实现时间排序
        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });

//        for (Article art:
//                articles) {
//            log.info("art:" + art);
//        }
        return articles;
    }

    @Override
    public Integer DeletePost(Map<String, Object> map) {
        return articleMapper.DeletePost(map);
    }

    @Override
    public Integer InsertArticle(Article article) {
        article.setTime(new Timestamp(System.currentTimeMillis()));
        return articleMapper.InsertArticle(article);
    }

    @Override
    public Integer UpdateReplyz(Map<String, Object> map) {
        return articleMapper.UpdateReplyz(map);
    }

    @Override
    public Integer UpdateReplyf(Map<String, Object> map) {
        return articleMapper.UpdateReplyf(map);
    }

    @Override
    public Integer UpdateArticle(Map<String, Object> map) {
        return articleMapper.UpdateArticle(map);
    }

    @Override
    public List<Article> SelByUsername(String username) {
        List<Article> articles = articleMapper.SelByUsername(username);

        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
                articles) {
            //在对回复的楼中楼排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
        }

        return articles;
    }

    @Override
    public List<Reply> SelRecentByUsername(String username) {
        List<Reply> replies = articleMapper.SelRecentByUsername(username);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        for (Reply reply:
            replies) {

            ConversionTimeReply(reply, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(reply.getTime()),CalTime.DateFormat));
//            log.info("reply:"+reply);
        }

        return replies;
    }

    @Override
    public List<Reply> SelRecentByUsername2(String username) {
        List<Reply> replies = articleMapper.SelRecentByUsername2(username);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        for (Reply reply:
                replies) {
            //在对Reply表排序
            ConversionTimeReply(reply, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(reply.getTime()),CalTime.DateFormat));
//            log.info("reply2:"+reply);
        }
        return replies;
    }

    @Override
    public List<Article> SelByComments0(Map<String,Object> map) {

        List<Article> articles = articleMapper.SelByComments0(map);

        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
                articles) {
            //在对回复的楼中楼排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
        }

        return articles;
    }

    @Override
    public List<Article> SelByComments2(Map<String, Object> map) {
        List<Article> articles = articleMapper.SelByComments2(map);

        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
                articles) {
            //在对回复的楼中楼排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
        }

        return articles;
    }

    @Override
    public List<Article> SelBlurryByPostTitle(String PostName) {

        List<Article> articles = articleMapper.SelBlurryByPostTitle(PostName);
        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
                articles) {
            //在对回复的楼中楼排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
        }
        return articles;
    }

    @Override
    public LikesFavorites SelByArticleidToLikesFavorites(Integer articleid, String username) {
        return articleMapper.SelByArticleidToLikesFavorites(articleid,username);
    }

    @Override
    public Integer InsertLikesFavorites(LikesFavorites likesFavorites) {
        return articleMapper.InsertLikesFavorites(likesFavorites);
    }

    @Override
    public Integer UpdateLikesFavorites(Map<String,Object> map) {
        return articleMapper.UpdateLikesFavorites(map);
    }

    @Override
    public List<Article> SelByUsernameToLikesFavorites(String username) {

        List<Article> articles = articleMapper.SelByUsernameToLikesFavorites(username);

        //排序。倒叙
        Collections.sort(articles);

        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        log.info("currentTime:" + currentTime);
        //在对回复的楼层排序，倒叙
        for (Article art:
                articles) {
            //对帖子的时间进行细化和排序，倒叙
            ConversionTimeArticle(art, CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(art.getTime()),CalTime.DateFormat));
//            log.info("TimeDifference:"+TimeDifference);
        }

        return articles;
    }

    //细化时间
    public void ConversionTimeReply(Reply reply,long TimeDifference){
        if (TimeDifference < 60){//60秒前
            if (TimeDifference == 0 || TimeDifference == -1)
                reply.setDeltime(1);
            else
                reply.setDeltime((int) TimeDifference);
            reply.setDelTimeUnit("秒前");
        }else if (TimeDifference < 60*60){
            reply.setDeltime((int) (TimeDifference/60));
            reply.setDelTimeUnit("分钟前");
        }else if (TimeDifference < 60*60*24){
            reply.setDeltime((int) (TimeDifference/60/60));
            reply.setDelTimeUnit("小时前");
        }
        else if (TimeDifference < 60*60*24*30){
            reply.setDeltime((int) (TimeDifference/60/60/24));
            reply.setDelTimeUnit("天前");
        }
        else if (TimeDifference < 60*60*24*30*12){
            reply.setDeltime((int) (TimeDifference/60/60/24/30));
            reply.setDelTimeUnit("月前");
        }else {
            reply.setDeltime(-1);
        }
    }


    //细化时间
    public void ConversionTimeArticle(Article art,long TimeDifference){
        if (TimeDifference < 60){//60秒前
            if (TimeDifference == 0 || TimeDifference == -1)
                art.setDeltime(1);
            else
                art.setDeltime((int) TimeDifference);
            art.setDelTimeUnit("秒前");
        }else if (TimeDifference < 60*60){
            art.setDeltime((int) (TimeDifference/60));
            art.setDelTimeUnit("分钟前");
        }else if (TimeDifference < 60*60*24){
            art.setDeltime((int) (TimeDifference/60/60));
            art.setDelTimeUnit("小时前");
        }
        else if (TimeDifference < 60*60*24*30){
            art.setDeltime((int) (TimeDifference/60/60/24));
            art.setDelTimeUnit("天前");
        }
        else if (TimeDifference < 60*60*24*30*12){
            art.setDeltime((int) (TimeDifference/60/60/24/30));
            art.setDelTimeUnit("月前");
        }else {
            art.setDeltime(-1);
        }
    }

    public void ConversionTimeReplyz(ReplyArticle art,long TimeDifference){
        if (TimeDifference < 60){//60秒前
            if (TimeDifference == 0 || TimeDifference == -1)
                art.setDeltime(1);
            else
                art.setDeltime((int) TimeDifference);
            art.setDelTimeUnit("秒前");
        }else if (TimeDifference < 60*60){
            art.setDeltime((int) (TimeDifference/60));
            art.setDelTimeUnit("分钟前");
        }else if (TimeDifference < 60*60*24){
            art.setDeltime((int) (TimeDifference/60/60));
            art.setDelTimeUnit("小时前");
        }
        else if (TimeDifference < 60*60*24*30){
            art.setDeltime((int) (TimeDifference/60/60/24));
            art.setDelTimeUnit("天前");
        }
        else if (TimeDifference < 60*60*24*30*12){
            art.setDeltime((int) (TimeDifference/60/60/24/30));
            art.setDelTimeUnit("月前");
        }else {
            art.setDeltime(-1);
        }
    }

    public void ConversionTimeReplyf(Replyf art, long TimeDifference){
        if (TimeDifference < 60){//60秒前
            if (TimeDifference == 0 || TimeDifference == -1)
                art.setDeltime(1);
            else
                art.setDeltime((int) TimeDifference);
            art.setDelTimeUnit("秒前");
        }else if (TimeDifference < 60*60){
            art.setDeltime((int) (TimeDifference/60));
            art.setDelTimeUnit("分钟前");
        }else if (TimeDifference < 60*60*24){
            art.setDeltime((int) (TimeDifference/60/60));
            art.setDelTimeUnit("小时前");
        }
        else if (TimeDifference < 60*60*24*30){
            art.setDeltime((int) (TimeDifference/60/60/24));
            art.setDelTimeUnit("天前");
        }
        else if (TimeDifference < 60*60*24*30*12){
            art.setDeltime((int) (TimeDifference/60/60/24/30));
            art.setDelTimeUnit("月前");
        }else {
            art.setDeltime(-1);
        }
    }

}