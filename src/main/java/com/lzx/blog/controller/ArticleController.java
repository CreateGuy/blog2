package com.lzx.blog.controller;

import com.lzx.blog.Util.ConstantUtil;
import com.lzx.blog.Util.exception.exceptionclass.CustomException;
import com.lzx.blog.Util.exception.exceptionclass.CustomException2;
import com.lzx.blog.Util.exception.exceptionclass.ImageVerificationCodeException;
import com.lzx.blog.controller.User.VerificationCcode;
import com.lzx.blog.popj.*;
import com.lzx.blog.popj.expand.ReplyArticle;
import com.lzx.blog.service.ArticleService.ArticleServiceImp;
import com.lzx.blog.service.UserServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Controller
@Slf4j
@Api(tags = "文章的相关接口")
public class ArticleController {

    @Autowired
    ArticleServiceImp articleServiceImp;

    @Autowired
    UserServiceImp userServiceImp;

    //上传图片的路径
    @Value("${ImagePath}")
    String ImagePath;

    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;


    @GetMapping("/getarticles")
    @ApiOperation("获取文章")
    @ResponseBody
    public String getarticle(Model model) {

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        List<Article> articles = articleServiceImp.SelByAll(0);
        model.addAttribute("articles",articles);
//        for ( Article article:
//              articles) {
//            log.info("article:" + article);
//        }
        model.addAttribute("articles",articles);


//        Map<String,Object> map = new HashMap<>();
//        map.put("username",user.getUsername());
//        List<User> users = userServiceImp.selByIUP(map);

        return "Success";
    }


    @PostMapping("/PostComment")
    @ApiOperation("发表评论")
    @ResponseBody
    public String PostComment(Model model,
                              @ApiParam(name = "c", value = "提交的评论实体", required = false)
                              @RequestBody Comment c) {


        Map<String,Object> map = new HashMap<>();
        map.put("username",c.getUsername());
        List<User> users = userServiceImp.selByIUP(map);
        c.setAvatar(users.get(0).getAvatar());
        c.setTime(new Timestamp(System.currentTimeMillis()));
        c.setAgree(0);
        log.info("c:" + c);

        //1代表是回复楼中楼或者回复楼层，2也就是else代表的是回复帖子
        if (c.getFlag() == 1){
            articleServiceImp.InsertReplyf(c,"replyf");

//            更新评论数
            Map<String,Object> artMap = new HashMap<>();
            artMap.put("id",c.getAuthority());
            //使用动态更新，有这个键值对就更新
            artMap.put("comments",1);
            Integer num = articleServiceImp.UpdateArticle(artMap);

            return "success";
        }else {
//            拿到该id的最高评论数，在加一
            log.info("c.getAuthority():"+c.getAuthority());
            Replyz rz = articleServiceImp.SelByAllReplyz(c.getAuthority());
            if (rz != null)
                c.setFloor(rz.getFloor()+1);
            else {
                log.info("replyz表查询不到指定帖子id的记录");
                c.setFloor(1);
            }
            //更新表
            articleServiceImp.InsertReplyz(c,users.get(0).getMailbox());


            //            更新评论数
            Map<String,Object> artMap = new HashMap<>();
            artMap.put("id",c.getAuthority());
            artMap.put("comments",1);
            Integer num = articleServiceImp.UpdateArticle(artMap);

            return "success";
        }
    }


    @GetMapping("/articledetailsallone/{id}")
    @ApiOperation("根据id获取某个帖子的回复")
    public String articledetailsallone(
            @ApiParam(name = "id", value = "某个帖子的id", required = false)
            @PathVariable Integer id, Model model) {
        log.info("要获得id为："+id+"的回帖信息");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        //查出来某个帖子，id = 0 就是所有帖子 //按照时间排序
        List<Article> articles = articleServiceImp.SelByAll(id);
        model.addAttribute("articles",articles.get(0));


        //按照浏览量排序
        List<Article> particles = articleServiceImp.SelByAll(0);
        Collections.sort(particles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        model.addAttribute("particles",particles);

        //更新浏览量
        Map<String,Object> artMap = new HashMap<>();
        artMap.put("id",id);
        artMap.put("views",1);
        Integer num = articleServiceImp.UpdateArticle(artMap);

        if (num == 0){
            throw new CustomException(1, ConstantUtil.UpdateFailedException);
        }
        log.info("1");
        //查出来某个帖子的所有回复，id = 0 就是所有回帖
        List<ReplyArticle> replyArticles= articleServiceImp.SelAllArticle(id);
//        for (ReplyArticle r:
//            replyArticles) {
//            Collections.sort(r.getReplyfs());
//        }
        for (ReplyArticle r:
                replyArticles) {
            log.info("r:" + r);
        }
        model.addAttribute("replyArticles",replyArticles);

        //得到发帖人相关信息
        Map<String,Object> map = new HashMap<>();
        map.put("username",articles.get(0).getUsername());
        List<User> poster = userServiceImp.selByIUP(map);
        model.addAttribute("poster",poster.get(0));

        return "jie/detail";
    }


    @PostMapping("/upload/image")
    @ApiOperation("上传图片,返回图片的路径")
    @RequiresUser
    @ResponseBody
    public LayuiJson uploadimage(Model model, HttpSession session,
                               @RequestParam(value = "file")
                                       MultipartFile image,String id) throws IOException {
        LayuiJson bean = beanFactory.getBean(LayuiJson.class);

        log.info("id:" + id);
        log.info("image:" + image);
        if (!image.isEmpty()){
            String originalFileName = image.getOriginalFilename();//获取原始图片的扩展名
            log.info("图片扩展名:"+ originalFileName);
            String newFileName = UUID.randomUUID() + originalFileName;
            String newFilePath = ImagePath + newFileName; //新文件的路径
            log.info("newFilePath=" + newFilePath);
            image.transferTo(new File(newFilePath));//将传来的文件写入新建的文件

            bean.setStatus(0);
            bean.setMessage("上传成功");
            bean.setData("gerenboke/"+newFileName);
            return bean;
//            return "gerenboke/"+newFileName;
        }else
            //为了程序不报错返回一个null,实际上没有上传图片会被全局异常处理类捕捉到,再去处理
            return bean;
    }

    @PostMapping("/uploadimage")
    @ApiOperation("上传文件,返回文件的路径")
    @RequiresUser
    @ResponseBody
    public ReturnedEntity  uploadimage(Model model, HttpSession session,
                                List<MultipartFile> files) throws IOException {

        log.info("uploadimage");
        ReturnedEntity bean = beanFactory.getBean(ReturnedEntity.class);

        if (files.size() == 0){
            log.info("文件为空");
            throw new CustomException2(1, ConstantUtil.FileEmptyException);
        }
        for (MultipartFile file:
            files) {
            //没有上传图片会被全局异常处理类捕捉到,再去处理
            if (!file.isEmpty()){
                String originalFileName = file.getOriginalFilename();//获取原始图片的扩展名
                log.info("图片扩展名:"+ originalFileName);
                String newFileName = UUID.randomUUID() + originalFileName;
                String newFilePath = ImagePath + newFileName; //新文件的路径
                log.info("newFilePath=" + newFilePath);
                file.transferTo(new File(newFilePath));//将传来的文件写入新建的文件
                bean.SetSingleData("gerenboke/"+newFileName);
                bean.increase();
            }
        }
        log.info("bean:"+bean.getDataength());
        return bean;
    }

    @PostMapping("/PublishArticle")
    @ApiOperation("发表文章")
    @RequiresUser
    @ResponseBody
    public String publishArticle(HttpServletRequest request,
                                 @ApiParam(name = "article", value = "发帖的相应属性", required = true)
                                 @RequestBody @Validated  Article2 article2){

        log.info("PublishArticle");
        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        //判断验证是否正确，或者该会话是否有验证码
        if (!VerificationCcode.imgvcodever(request,article2.getVercode())){
            throw new CustomException2(1,ConstantUtil.ImageVerificationCodeException);
        }

        log.info("PublishArticle:"+article2.toString());
        Article article = beanFactory.getBean(Article.class);
        article.SetUp(currentUser.getUsername(),article2.getTitle(),article2.getText(),article2.getClass1());
        Integer num = articleServiceImp.InsertArticle(article);
        if (num == 0){
            log.info("发表文章失败");
            throw new CustomException2(1, ConstantUtil.PublishArticleException);
        }
        return "success";
    }

}
