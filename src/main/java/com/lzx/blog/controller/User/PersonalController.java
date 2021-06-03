package com.lzx.blog.controller.User;


import com.lzx.blog.Util.CheckUtil;
import com.lzx.blog.Util.ConstantUtil;
import com.lzx.blog.Util.exception.exceptionclass.CustomException;
import com.lzx.blog.Util.exception.exceptionclass.CustomizeException;
import com.lzx.blog.Util.exception.exceptionclass.ErrorpageException;
import com.lzx.blog.popj.*;
import com.lzx.blog.popj.expand.ReplyArticle;
import com.lzx.blog.service.ArticleService.ArticleService;
import com.lzx.blog.service.ArticleService.ArticleServiceImp;
import com.lzx.blog.service.RoleServiceImp;
import com.lzx.blog.service.UserService;
import com.lzx.blog.service.UserServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@Api(tags = "个人管理页面的接口")
@Slf4j

public class PersonalController {

    @Autowired
    UserServiceImp userServiceImp;

    @Autowired
    ArticleServiceImp articleServiceImp;

    @Autowired
    RoleServiceImp roleServiceImp;

    @Value("${AvatarPath}")
    String filePath;

    @PostMapping("/user/data")
    @ApiOperation("修改个人资料")
    @RequiresUser
    @ResponseBody
    public String changeuserdata(Model model,
                                    @ApiParam(name = "changeeUserData", value = "传入需要求改的username 和这个账户的mailbox id 还有性别", required = false)
                              @RequestBody @Validated ChangeeUserData changeeUserData,
                                    HttpSession httpSession){

        log.info("changeuserdata");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        //查看数据库中是否有这个用户
        Map<String,Object> usermap = new HashMap<>();
        usermap.put("username",changeeUserData.getUsername());
        List<User> users = userServiceImp.selByIUP(usermap);
        log.info("users:" + users.isEmpty());

        //要么没有这个用户，要么这个用户是当前要改用户名的用户
        if (users.isEmpty() || users.get(0).getUsername().equals(currentUser.getUsername())){
            usermap.put("id",currentUser.getId());
//            log.info("changeeUserData.getMessage():"+changeeUserData.getMessage());
            usermap.put("message",changeeUserData.getMessage());
            usermap.put("sex",changeeUserData.getSex());
            userServiceImp.UpdateUser(usermap);
            httpSession.setAttribute("user",userServiceImp.selByIUP(usermap).get(0));
//            log.info("httpSession:" + httpSession.getAttribute("user"));

            //我们修改了当前用户的信息，需要把存在shirosession中用户同步更新
            currentUser.setUsername(changeeUserData.getUsername());
            currentUser.setSex(changeeUserData.getSex());
            currentUser.setMessage(changeeUserData.getMessage());

            return "success";

        }else {
            log.info("用户名已经存在");
            throw new CustomException(4,ConstantUtil.UsernameExisted);
        }
    }

    @PostMapping("/personal/ModifyAvatar")
    @ApiOperation("修改个人头像")
    @RequiresUser
    @ResponseBody
    public String ModifyAvatar(Model model,HttpSession session,
                               @ApiParam(name = "file", value = "传入需要更新的头像", required = false)
                                       MultipartFile file) throws IOException {

        log.info("image:" + file);

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        if (!file.isEmpty()){
            String originalFileName = file.getOriginalFilename();//获取原始图片的扩展名
            log.info("图片扩展名:"+ originalFileName);
            String newFileName = UUID.randomUUID() + originalFileName;
            String newFilePath = filePath + newFileName; //新文件的路径
            log.info("newFilePath=" + newFilePath);
            file.transferTo(new File(newFilePath));//将传来的文件写入新建的文件

//            log.info();
//            post.setImage(filePathNull+newFileName);

            //更新user表中的头像
            Map<String,Object> map = new HashMap<>();
            currentUser.setAvatar("Avatar/" + newFileName);
            map.put("id",currentUser.getId());
            map.put("avatar",currentUser.getAvatar());
            Integer num = userServiceImp.UpdateUser(map);
            session.setAttribute("user",currentUser);
            log.info("num: "+ num +" id: "+ currentUser.getId() + " avatar" + "Avatar/" + newFileName);

            //更新主回复表的头像
            Map<String,Object> replyzmap = new HashMap<>();
            replyzmap.put("username",currentUser.getUsername());
            replyzmap.put("avatar",currentUser.getAvatar());
            num = articleServiceImp.UpdateReplyz(replyzmap);

            //更新副回复表的头像
            Map<String,Object> replyfmap = new HashMap<>();
            replyfmap.put("username",currentUser.getUsername());
            replyfmap.put("avatar",currentUser.getAvatar());
            num = articleServiceImp.UpdateReplyf(replyfmap);

            if (num >= 0){
                return "success";
            }
        }
        log.info("上传图片为空");
        throw new CustomException(4,ConstantUtil.PictureEmptyException);
    }



    @PostMapping("/personal/changePassword")
    @ApiOperation("修改个人密码")
    @RequiresUser
    @ResponseBody
    public String changePassword(Model model,
                                    @ApiParam(name = "ChangePassword2", value = "传入这个账户的旧密码和新密码，并且里面的参数不能为空", required = false)
                                    @RequestBody @Validated ChangePassword2 changePassword2,
                                    HttpSession httpSession){

        log.info("personalchangePassword ");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        log.info("原密码:"+currentUser.getPassword());
        //查看原密码是否正确
        if(!currentUser.getPassword().equals(changePassword2.getPassword())){
            log.info("原密码错误");
            throw new CustomException(4,"原"+ConstantUtil.PasswordException);
        }
        if (currentUser.getPassword() == changePassword2.getNewpassword()){
            log.info("新旧密码一样");
            throw new CustomException(4,ConstantUtil.PasswordNewException);
        }
        Map<String,Object> usermap = new HashMap<>();
        usermap.put("id",currentUser.getId());
        usermap.put("password",changePassword2.getNewpassword());
        userServiceImp.UpdateUser(usermap);

        //将新的用户信息放入session中
        httpSession.setAttribute("user",userServiceImp.selByIUP(usermap).get(0));

        //我们修改了当前用户的密码，需要把存在shirosession中用户同步更新
        currentUser.setPassword(changePassword2.getNewpassword());


        return "success";

    }


    @GetMapping("/personal/PersonalPosts")
    @ApiOperation("查看个人帖子")
    @RequiresUser
//    @ResponseBody
    public String PersonalPosts(Model model,
                                       @ApiParam(name = "id", value = "传入要查看的用户username", required = false)
                                               String username){
        log.info("username:"+username);
        Map<String,Object> articlemap = new HashMap<>();
        articlemap.put("username",username);
        List<Article> articles = articleServiceImp.SelByMap(articlemap);
        model.addAttribute("articles",articles);
        for ( Article article:
                articles) {
            log.info("article:" + article);
        }
        model.addAttribute("articles",articles);
        model.addAttribute("postsnum",articles.size());
        return "GeRen/people_article";
    }


    @PostMapping("/personal/DeletePost")
    @ApiOperation("删除某个人的某个帖子")
    @RequiresUser
//    @ResponseBody
    public String DeletePost(Model model,
                                @ApiParam(name = "map", value = "删除的帖子id，和该用户的的用户名", required = false)
                                @RequestBody
                                        Map<String,Object> map){
        log.info("进入了DeletePost："+map.get("id"));
        Integer id = (Integer) map.get("id");
        String username = (String) map.get("username");
        String password = (String) map.get("password");

        //查看是否有这个用户
        Map<String,Object> usermap = new HashMap<>();
        usermap.put("username",username);
        List<User> users = userServiceImp.selByIUP(usermap);

        if (!users.isEmpty()){
            //查看密码是否正确
            if (users.get(0).getPassword().equals(password)){
                //重新根据id查询贴，在放到model中
                Map<String,Object> articlemap = new HashMap<>();
                articlemap.put("id",id);
                Integer num = articleServiceImp.DeletePost(articlemap);
                if (num > 0){
                    articlemap.put("username",username);
                    List<Article> articles = articleServiceImp.SelByMap(articlemap);
                    model.addAttribute("articles",articles);
                    for ( Article article:
                            articles) {
                        log.info("article:" + article);
                    }
                    model.addAttribute("articles",articles);
                    return "GeRen/people_article";
                }
            }
        }
        throw new CustomizeException(1, ConstantUtil.DeletePostException,"GeRen/people_password::msg");

    }


    @GetMapping("/user/home/{id}")
    @ApiOperation("根据id获取某个用户的主页")
    public String userhome(
            @ApiParam(name = "id", value = "用户的id", required = false)
            @PathVariable Integer id, Model model) {

        log.info("要获得id为："+id+"的用户的主页");

        //查询某个用户
        Map<String,Object> usermap = new HashMap<>();
        usermap.put("id",id);
        List<User> users = userServiceImp.selByIUP(usermap);
        log.info("user:"+users.size());
        //查询某个用户,不存在接返回一个错误页面
        if (users.size() == 0) {
            log.info("users.size():"+users.size());
            model.addAttribute("msg","页面不存在");
            return "forward:/other/notice";
        }
        users.get(0).Clear();
        model.addAttribute("user",users.get(0));

        //查出来某个用户的所有帖子， //按照时间排序
        List<Article> articles = articleServiceImp.SelByUsername(users.get(0).getUsername());
//        for (Article article:
//            articles) {
//            log.info("article"+article.toString());
//        }
        if (articles.size() == 0) {
            log.info("articles.size():"+articles.size());
        }else
            model.addAttribute("articles",articles);

        Map<String,Object> rolemap = new HashMap<>();
        rolemap.put("username",users.get(0).getUsername());
        // 放入该角色的头衔
        Role role = roleServiceImp.SelByAll(rolemap);
        if (role == null){
            log.info("role:"+ role);
        }else{
            String[] roleArr = role.getCrown().split("_");
            role.setCrowns(roleArr);
            model.addAttribute("roleArr", role);
        }
//        //查出来某个帖子的所有回复，id = 0 就是所有回帖
        List<Reply> replies1 = articleServiceImp.SelRecentByUsername(users.get(0).getUsername());
        if (replies1.size() == 0){
            log.info("replies1.size():"+replies1.size());
        }
        List<Reply> replies2 = articleServiceImp.SelRecentByUsername2(users.get(0).getUsername());
        if (replies1.size() == 0){
            log.info("replies2.size():"+replies1.size());
        }

        for (int i = 0; i < replies1.size(); i++) {
            replies2.add(replies1.get(i));
        }

        Collections.sort(replies2, new Comparator<Reply>() {
            @Override
            public int compare(Reply o1, Reply o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        for (Reply reply:
        replies2) {
            log.info("replies2:"+reply);
        }
        //检测是否有五条记录，我们前端设置最大显示五条
        if (replies2.size() >= 5){
            model.addAttribute("replies",replies2.subList(0, 5));
        }else {
            model.addAttribute("replies",replies2.subList(0, replies2.size()));
        }

        return "user/home";

    }



//    @ApiOperation("个人的详情用户中心的收藏板块")
//    @GetMapping("/user/collect/{fs}/{pages}")
//    @RequiresUser
//    public String usercollect(Model model,
//                            @ApiParam(name = "fs", value = "发帖还是收藏贴", required = true)
//                            @PathVariable String fs,
//                            @ApiParam(name = "pages", value = "页数", required = true)
//                            @PathVariable Integer pages){
//        log.info("/usercollect");
//
//        //得到当前登陆的对象
//        Subject subject = SecurityUtils.getSubject();
//        //获取登陆的时候的user
//        User currentUser = (User) subject.getPrincipal();
//
//        //查出来某个用户的所有帖子， //按照时间排序
//        List<Article> articles = articleServiceImp.SelByUsername(currentUser.getUsername());
//        //查出来收藏的帖子
//        List<Article> articles2 = articleServiceImp.SelByUsernameToLikesFavorites(currentUser.getUsername());
//
//        if (articles2.size() == 0) {
//            log.info("articles.size():"+articles.size());
//        }else {
//            //发帖的信息
//            if(fs.equals("f")){
//                log.info("f");
//                log.info("articles.size():" + articles.size());
//                if (pages*12 <= articles.size()) {
//                    //我们规定只能显示12条
//                    model.addAttribute("articles", articles.subList(pages * 12 - 12, pages * 12));
//                }else if((pages-1)*12 <= articles.size()){
//                    model.addAttribute("articles", articles.subList(pages * 12 - 12,articles.size()));
//                }else {
//                    log.info("f发帖数量的非法请求");
//                    model.addAttribute("msg","非法请求");
//                    return "forward:/other/notice";
//                }
//                //发帖记录的最大条数
//                model.addAttribute("postsnum", articles.size());
//
//                //收藏记录的最大条数
//                model.addAttribute("postsnum2", articles2.size());
//
//                List<Integer> pageslist =  new ArrayList<>();
//                int num = 1;
//                if (articles.size() % 12 == 0) {
//                    num = 0;
//                }
//                for (int i = 1; i <= (articles.size() / 12 + num); i++) {
//                    pageslist.add(i);
//                }
//                //发帖记录最大页数集合，前端th:each必须要list或者map
//                model.addAttribute("pages", pageslist);
//                //当前页数
//                model.addAttribute("currentpage", pages);
//                //最大页数
//                model.addAttribute("pagemax", (articles.size() / 12 + num));
//            }
//        }
//        return "user/index";
//    }

}
