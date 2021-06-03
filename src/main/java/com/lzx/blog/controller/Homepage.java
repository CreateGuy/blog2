//package com.lzx.blog.controller;
//
//import com.lzx.blog.Util.ConstantUtil;
//import com.lzx.blog.Util.exception.exceptionclass.CustomizeException;
//import com.lzx.blog.popj.Article;
//import com.lzx.blog.popj.User;
//import com.lzx.blog.popj.expand.ReplyArticle;
//import com.lzx.blog.service.ArticleService.ArticleService;
//import com.lzx.blog.service.ArticleService.ArticleServiceImp;
//import com.lzx.blog.service.UserService;
//import com.lzx.blog.service.UserServiceImp;
//import com.sun.org.apache.xpath.internal.operations.Mod;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.authz.annotation.RequiresAuthentication;
//import org.apache.shiro.authz.annotation.RequiresUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpSession;
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
//@Controller
//@Api(tags = "主页的相关接口")
//@Slf4j
//public class Homepage {
//
//    @Autowired
//    ArticleServiceImp articleServiceImp;
//
//    @Autowired
//    UserService userService;
//
//    @Value("${ImagePath}")
//    String ImagePath;
//
//    @Autowired
//    private org.springframework.beans.factory.BeanFactory beanFactory;
//
//    @GetMapping("/avatar")
//    @ApiOperation("获取图片")
//    public String t2(@ApiParam(name = "id", value = "某个帖子的id", required = false)
//                     @RequestBody User user, Model model) {
//        log.info("user:" + user);
//        model.addAttribute("msg","hello");
//        return "test::test1";
//    }
//
//
//    @GetMapping("/articledetailsall")
//    @ApiOperation("获取所有帖子的回复")
//    @ResponseBody
//    public String articledetails(Model model) {
//        List<ReplyArticle> replyArticles= articleServiceImp.SelAllArticle(0);
//
//        for (ReplyArticle r:
//                replyArticles) {
//            log.info("r:" + r);
//        }
//        return "Success";
//    }
//
//
//    @PostMapping("/uploadimage")
//    @ApiOperation("上传图片,返回图片的路径")
//    @RequiresUser
//    @ResponseBody
//    public String  uploadimage(Model model, HttpSession session,
//                                     MultipartFile image) throws IOException {
//
//        log.info("image:" + image);
//        if (!image.isEmpty()){
//            String originalFileName = image.getOriginalFilename();//获取原始图片的扩展名
//            log.info("图片扩展名:"+ originalFileName);
//            String newFileName = UUID.randomUUID() + originalFileName;
//            String newFilePath = ImagePath + newFileName; //新文件的路径
//            log.info("newFilePath=" + newFilePath);
//            image.transferTo(new File(newFilePath));//将传来的文件写入新建的文件
//            return "gerenboke/"+newFileName;
//        }else
//            //为了程序不报错返回一个null,实际上没有上传图片会被全局异常处理类捕捉到,再去处理
//            return "null";
//    }
//
//    @PostMapping("/homepage/publishArticle")
//    @ApiOperation("发表文章")
//    @RequiresUser
//    @ResponseBody
//    public String publishArticle(@ApiParam(name = "map", value = "发帖的相应属性", required = false)
//                                     @RequestBody Map<String,Object> map){
//        String username = (String) map.get("username");
//        String title = (String) map.get("title");
//        String text = (String) map.get("text");
//        String imageAddress = (String) map.get("imageAddress");
////        log.info("username:" + username + " title:"+title+" text:"
////                +text +" imageAddress:"+imageAddress);
//
//        Article article = beanFactory.getBean(Article.class);
//        article.setUsername(username);
//        article.setTitle(title);
//        article.setText(text);
//        article.setImage(imageAddress);
//        Integer num = articleServiceImp.InsertArticle(article);
//        return "Refresh";
//    }
//
//}
