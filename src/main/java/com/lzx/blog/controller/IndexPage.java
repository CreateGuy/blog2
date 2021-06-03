package com.lzx.blog.controller;

import com.lzx.blog.Util.CalTime;
import com.lzx.blog.Util.ConstantUtil;
import com.lzx.blog.Util.HotSearchUtil;
import com.lzx.blog.Util.exception.exceptionclass.CustomException;
import com.lzx.blog.Util.exception.exceptionclass.ErrorpageException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.lzx.blog.Util.CalTime.ConversionTime;

@Controller
@Api(tags = "首页的接口")
@Slf4j
public class IndexPage {

    @Autowired
    ArticleServiceImp articleServiceImp;

    @Autowired
    UserServiceImp userServiceImp;

//    通过工厂对象获得的实体必须加上@Scope("prototype")，才能是多例
    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;

    @ApiOperation("主页面")
    @GetMapping({"/","index"})
    public String index(Model model,
                        Integer instruction,
                        Integer status){

        log.info("获取主页");
        if (instruction == null && status == null) {
            instruction = 1;
            model.addAttribute("instruction", 1);
            status = 1;
            model.addAttribute("status", 1);
        }
        else{
            model.addAttribute("instruction",instruction);
            model.addAttribute("status", status);
        }

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        List<Article> articles;
        Map<String,Object> articlemap = new HashMap<>();

        if (instruction == 1 && status == 1) {
            log.info("instruction:"+instruction);
            //所有文章
            articles = articleServiceImp.SelByAll(0);
            model.addAttribute("articles", articles);
            for (Article article :
                    articles) {
                log.info("article:" + article + " " + article.getLFlikes()+" "+article.getLFfavorites());
            }
            //所有文章的实体
            model.addAttribute("articles",articles);
            //帖子数
            model.addAttribute("postsnum",articles.size());

            log.info("postsnum:"+articles.size());
        }else {
            //综合的抢沙发
            if (instruction == 1 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("comments", -1);
            } else if (instruction == 2 && status == 1) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "未结");
            } else if (instruction == 2 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "未结");
                articlemap.put("comments", 0);
            } else if (instruction == 3 && status == 1) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "完结");
            } else if (instruction == 3 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "完结");
                articlemap.put("comments", 0);
            }
            articles = articleServiceImp.SelByComments0(articlemap);
            model.addAttribute("articles", articles);
            for (Article article :
                    articles) {
                log.info("article:" + article);
            }
            //所有文章的实体
            model.addAttribute("articles", articles);
            //帖子数
            model.addAttribute("postsnum", articles.size());
            log.info("postsnum:" + articles.size());
        }

        //置顶的帖子
        Map<String,Object> toparticlemap = new HashMap<>();
        List<Article> toparticles = articleServiceImp.SelByComments0(toparticlemap);
        model.addAttribute("toparticles", toparticles);

        //按照阅读量排序的帖子
        List<Article> particles = articleServiceImp.SelByAll(0);
        Collections.sort(particles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        model.addAttribute("particles",particles);


        //排序最近访问的用户
        List<User> users = userServiceImp.selAll();
        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                o1.Clear();
                o2.Clear();
                return o2.getLasttime().compareTo(o1.getLasttime());
            }
        });
        //写入最近登陆时间
        for (User user:
             users) {
            userServiceImp.ConversionTimeArticle(user,CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(user.getLasttime()),CalTime.DateFormat));
        }
        model.addAttribute("users",users);

        //活跃榜
        List<SignIn> signIns = userServiceImp.SelectSignInByUsername("");
        log.info("signIns:"+signIns.size());
        if (signIns.size() == 0){
            log.info("signIns:"+signIns.size());
            throw new ErrorpageException(101,"数据更新错误，数据异常");
        }
        Collections.sort(signIns, new Comparator<SignIn>() {
            @Override
            public int compare(SignIn o1, SignIn o2) {
                return o2.getConsecutivedays() - o1.getConsecutivedays();
            }
        });
        if (currentUser != null){
            for (SignIn signIn:
                    signIns) {
                //得到当前用于的签到信息
                if (signIn.getUsername().equals(currentUser.getUsername())){
                    model.addAttribute("signIn",signIn);
                    break;
                }
            }
        }
        else {
            //我们默认id为-1就是没有登陆
            signIns.get(0).setId(-1);
            model.addAttribute("signIn",signIns.get(0));
        }
        model.addAttribute("signIns",signIns);

        //热搜
        List<HotSearchList> hotSearchLists = beanFactory.getBean(HotSearchUtil.class).HotSearch();
        model.addAttribute("hotSearchLists",hotSearchLists);
        //热搜平台数量
        model.addAttribute("hotSearchListsnum",hotSearchLists.size());
        return "index";
    }


    @ApiOperation("说说页面")
    @GetMapping("/case")
    public String case1(Model model){
        log.info("case");
        return "case/case";
    }

    @ApiOperation("用户的登陆页面")
    @GetMapping("/user/login")
    public String userLogin(Model model){
        log.info("userlogin");
        return "user/login";
    }

    @ApiOperation("用户的注册页面")
    @GetMapping("/user/reg")
    public String userReg(Model model){
        log.info("userReg");
        return "user/reg";
    }

    @ApiOperation("帖子的详情")
    @GetMapping("/jie/detail")
    public String jieDetail(Model model){
        log.info("jie/detail");
        return "jie/detail";
    }

    @ApiOperation("用户的注销请求")
    @RequestMapping("/user/logout")
    public String logout(Model model) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        model.addAttribute("msg","安全退出！");
        log.info("安全退出!");
        //返回的页面会带有路径前缀
        return "redirect:../../index";
    }

    @ApiOperation("个人的详情用户中心")
    @GetMapping("/user/index/{fs}/{pages}")
    @RequiresUser
    public String userindex(Model model,
                            @ApiParam(name = "fs", value = "发帖还是收藏贴", required = true)
                            @PathVariable String fs,
                            @ApiParam(name = "pages", value = "页数", required = true)
                            @PathVariable Integer pages){
        log.info("user/index");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        //查出来某个用户的所有帖子， //按照时间排序
        List<Article> articles = articleServiceImp.SelByUsername(currentUser.getUsername());
        //查出来收藏的帖子
        List<Article> articles2 = articleServiceImp.SelByUsernameToLikesFavorites(currentUser.getUsername());

        if (articles.size() == 0) {
            log.info("articles.size():"+articles.size());
        }else {
            //发帖的信息
            if(fs.equals("f")){
                log.info("f");
                log.info("articles.size():" + articles.size());
                if (pages*12 <= articles.size()) {
                    //我们规定只能显示12条
                    model.addAttribute("articles", articles.subList(pages * 12 - 12, pages * 12));
                }else if((pages-1)*12 <= articles.size()){
                    model.addAttribute("articles", articles.subList(pages * 12 - 12,articles.size()));
                }else {
                    log.info("f发帖数量的非法请求");
                    model.addAttribute("msg","非法请求");
                    return "forward:/other/notice";
                }
                //发帖记录的最大条数
                model.addAttribute("postsnum", articles.size());

                //收藏记录的最大条数
                model.addAttribute("spostsnum", articles2.size());

                List<Integer> pageslist =  new ArrayList<>();
                int num = 1;
                if (articles.size() % 12 == 0) {
                    num = 0;
                }
                for (int i = 1; i <= (articles.size() / 12 + num); i++) {
                    pageslist.add(i);
                }
                //发帖记录最大页数集合，前端th:each必须要list或者map
                model.addAttribute("pages", pageslist);
                //当前页数
                model.addAttribute("currentpage", pages);
                //最大页数
                model.addAttribute("pagemax", (articles.size() / 12 + num));

            }else {
                log.info("s");
                log.info("articles2.size():" + articles2.size());
                if (pages*12 <= articles2.size()) {
                    //我们规定只能显示12条
                    model.addAttribute("sarticles", articles2.subList(pages * 12 - 12, pages * 12));
                }else if((pages-1)*12 <= articles2.size()){
                    model.addAttribute("sarticles", articles2.subList(pages * 12 - 12,articles2.size()));
                }else {
                    log.info("f发帖数量的非法请求");
                    model.addAttribute("msg","非法请求");
                    return "forward:/other/notice";
                }
                //发帖记录的最大条数
                model.addAttribute("postsnum", articles.size());

                //收藏记录的最大条数
                model.addAttribute("spostsnum", articles2.size());

                List<Integer> pageslist =  new ArrayList<>();
                int num = 1;
                if (articles2.size() % 12 == 0) {
                    num = 0;
                }
                for (int i = 1; i <= (articles2.size() / 12 + num); i++) {
                    pageslist.add(i);
                }
                //发帖记录最大页数集合，前端th:each必须要list或者map
                model.addAttribute("spages", pageslist);
                //当前页数
                model.addAttribute("scurrentpage", pages);
                //最大页数
                model.addAttribute("spagemax", (articles2.size() / 12 + num));
            }
        }
        if(fs.equals("f")) {
            return "user/index";
        }else
            return "user/collect";
    }

    @ApiOperation("个人的基本设置")
    @GetMapping("/user/set")
    @RequiresUser
    public String userset(Model model){
        log.info("user/set");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        model.addAttribute("user",currentUser);
        return "user/set";
    }

    @ApiOperation("个人的消息")
    @GetMapping("/user/message")
    @RequiresUser
    public String usermessage(Model model){
        log.info("user/set");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        model.addAttribute("user",currentUser);
        return "user/message";
    }


    @ApiOperation("签到")
    @GetMapping("/Sign/in")
    @RequiresUser
    public String activegang(Model model){
        log.info("Sign/in");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        List<SignIn> signIns = userServiceImp.SelectSignInByUsername(currentUser.getUsername());

        Map<String, Object> Singinmap = new HashMap<>();
        Singinmap.put("username",currentUser.getUsername());

        //连续签到好多天
        if (signIns.get(0).getBrokensign() == 1){
            Singinmap.put("consecutivedays",1);
            Singinmap.put("experience",signIns.get(0).getExperience()+5);
        }else {
            Singinmap.put("consecutivedays",signIns.get(0).getConsecutivedays()+1);

            if (signIns.get(0).getConsecutivedays() < 5){
                Singinmap.put("experience",signIns.get(0).getExperience()+5);
            }else if (signIns.get(0).getConsecutivedays() < 30){
                Singinmap.put("experience",signIns.get(0).getExperience()+10);
            }else
                Singinmap.put("experience",signIns.get(0).getExperience()+20);
        }

        Singinmap.put("lasttime",new Timestamp(System.currentTimeMillis()));


        //是否连续签到标志
        Singinmap.put("brokensign",0);
        //是否今天签到标志
        Singinmap.put("thatday",1);
        //更新签到表
        Integer integer = userServiceImp.UpdateSingin(Singinmap);
        //规定，1000一级，大于登陆的时候的值就更新用户等级
        if(currentUser.getGrade() < ((Integer) Singinmap.get("experience") / ConstantUtil.grade)){
            //更新shirosession里的用户等级
            currentUser.setGrade(currentUser.getGrade()+1);

            Map<String, Object> Usermap = new HashMap<>();
            Usermap.put("id",currentUser.getId());
            Usermap.put("grade",currentUser.getGrade());
            Integer integer1 = userServiceImp.UpdateUser(Usermap);
            if (integer1 == 0){
                log.info("更新用户等级错误");
            }
        }
        if (integer != 0){
            return "forward:/index";
        }else {
            log.info("签到错误");
            model.addAttribute("msg","签到错误");
            return "forward:/other/notice";
        }
    }


    @ApiOperation("提问,分享，讨论,搜索的主页面")
    @GetMapping("otherindex/{instruction}/{status}/{inf}")
    public String otherindex(Model model,
                        @PathVariable @ApiParam(name = "instruction", value = "是综合还是未结完结的", required = true)
                        Integer instruction,
                        @PathVariable @ApiParam(name = "instruction", value = "是按照最新还是抢沙发", required = true)
                        Integer status,
                        @PathVariable @ApiParam(name = "instruction", value = "是提问还是分享还是讨论还是搜索", required = true)
                        Integer inf,
                        @ApiParam(name = "PostName", value = "需要搜索的标题名字,不是必要", required = false)
                        String PostName){

        log.info("获取otherindex主页");
        String strname;
        List<Article> articles;
        Map<String,Object> articlemap = new HashMap<>();

        //如果都不存在我们默认是提问的综合的按最新
        if (instruction == null && status == null && inf == null) {
            instruction = 1;
            model.addAttribute("instruction", 1);
            status = 1;
            model.addAttribute("status", 1);
            inf = 1;
            model.addAttribute("inf", 1);
        }
        else{
            model.addAttribute("instruction",instruction);
            model.addAttribute("status", status);
            model.addAttribute("inf", inf);
            model.addAttribute("PostName", PostName);
        }
        if (inf == 1){
            log.info("提问");
            strname = "提问";
        }else if (inf == 2){
            log.info("分享");
            strname = "分享";
        }else if (inf == 3) {
            log.info("讨论");
            strname = "讨论";
        }else {
            log.info("搜索");
            strname = "";
            articlemap.put("search","OK");
            log.info("PostName:"+PostName);
            articlemap.put("PostName",PostName);
        }
        //根据标志位判断怎么查询数据
        if (instruction == 1 && status == 1) {
            log.info("instruction:" + instruction);
            articlemap.put("types", strname);
        }else if (instruction == 1 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("types", strname);
                articlemap.put("comments", 0);
        } else if (instruction == 2 && status == 1) {
                log.info("instruction:" + instruction);
                articlemap.put("types", strname);
                articlemap.put("status", "未结");
        } else if (instruction == 2 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "未结");
                articlemap.put("types", strname);
                articlemap.put("comments", 0);
        } else if (instruction == 3 && status == 1) {
                log.info("instruction:" + instruction);
                articlemap.put("types", strname);
                articlemap.put("status", "完结");
        } else if (instruction == 3 && status == 2) {
                log.info("instruction:" + instruction);
                articlemap.put("status", "完结");
                articlemap.put("types", strname);
                articlemap.put("comments", 0);
        }
        articles = articleServiceImp.SelByComments2(articlemap);
        for (Article article :
                articles) {
            log.info("article:" + article);
        }
        //所有文章的实体
        model.addAttribute("articles", articles);
        //帖子数
        model.addAttribute("postsnum", articles.size());
        log.info("postsnum:" + articles.size());


        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        //按照阅读量排序的帖子
        List<Article> particles = articleServiceImp.SelByAll(0);
        Collections.sort(particles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        model.addAttribute("particles",particles);


        //排序最近访问的用户
        List<User> users = userServiceImp.selAll();
        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                o1.Clear();
                o2.Clear();
                return o2.getLasttime().compareTo(o1.getLasttime());
            }
        });
        //写入最近登陆时间
        for (User user:
                users) {
            userServiceImp.ConversionTimeArticle(user,CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(user.getLasttime()),CalTime.DateFormat));
        }
        model.addAttribute("users",users);

        //活跃榜
        List<SignIn> signIns = userServiceImp.SelectSignInByUsername("");
        log.info("signIns:"+signIns.size());
        if (signIns.size() == 0){
            log.info("signIns:"+signIns.size());
            throw new ErrorpageException(101,"数据更新错误，数据异常");
        }
        Collections.sort(signIns, new Comparator<SignIn>() {
            @Override
            public int compare(SignIn o1, SignIn o2) {
                return o2.getConsecutivedays() - o1.getConsecutivedays();
            }
        });
        if (currentUser != null){
            for (SignIn signIn:
                    signIns) {
                //得到当前用于的签到信息
                if (signIn.getUsername().equals(currentUser.getUsername())){
                    model.addAttribute("signIn",signIn);
                    break;
                }
            }
        }
        else {
            //我们默认id为-1就是没有登陆
            signIns.get(0).setId(-1);
            model.addAttribute("signIn",signIns.get(0));
        }
        model.addAttribute("signIns",signIns);


        //热搜
        List<HotSearchList> hotSearchLists = beanFactory.getBean(HotSearchUtil.class).HotSearch();
        model.addAttribute("hotSearchLists",hotSearchLists);

        //根据标志位返回对应的视图
        if (inf == 1)
            return "jie/askindex";
        else if (inf == 2)
            return "jie/shareindex";
        else if (inf == 3)
            return "jie/discussindex";
        else
            return "jie/searchindex";
    }


    @ApiOperation("公告的主页面")
    @GetMapping("announcementindex")
    public String otherindex(Model model){

        log.info("获取announcementindex主页");

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        Map<String,Object> articlemap = new HashMap<>();
        List<Article> articles = articleServiceImp.SelByComments0(articlemap);
        for (Article article :
                articles) {
            log.info("article:" + article);
        }
        //所有文章的实体
        model.addAttribute("articles", articles);
        //帖子数
        model.addAttribute("postsnum", articles.size());
        log.info("postsnum:" + articles.size());


        //按照阅读量排序的帖子
        List<Article> particles = articleServiceImp.SelByAll(0);
        Collections.sort(particles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        model.addAttribute("particles",particles);


        //排序最近访问的用户
        List<User> users = userServiceImp.selAll();
        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                o1.Clear();
                o2.Clear();
                return o2.getLasttime().compareTo(o1.getLasttime());
            }
        });
        //写入最近登陆时间
        for (User user:
                users) {
            userServiceImp.ConversionTimeArticle(user,CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(user.getLasttime()),CalTime.DateFormat));
        }
        model.addAttribute("users",users);

        //活跃榜
        List<SignIn> signIns = userServiceImp.SelectSignInByUsername("");
        log.info("signIns:"+signIns.size());
        if (signIns.size() == 0){
            log.info("signIns:"+signIns.size());
            throw new ErrorpageException(101,"数据更新错误，数据异常");
        }
        Collections.sort(signIns, new Comparator<SignIn>() {
            @Override
            public int compare(SignIn o1, SignIn o2) {
                return o2.getConsecutivedays() - o1.getConsecutivedays();
            }
        });
        if (currentUser != null){
            for (SignIn signIn:
                    signIns) {
                //得到当前用于的签到信息
                if (signIn.getUsername().equals(currentUser.getUsername())){
                    model.addAttribute("signIn",signIn);
                    break;
                }
            }
        }
        else {
            //我们默认id为-1就是没有登陆
            signIns.get(0).setId(-1);
            model.addAttribute("signIn",signIns.get(0));
        }
        model.addAttribute("signIns",signIns);


        //热搜
        List<HotSearchList> hotSearchLists = beanFactory.getBean(HotSearchUtil.class).HotSearch();
        model.addAttribute("hotSearchLists",hotSearchLists);
        return "jie/announcementindex";
    }


    @ApiOperation("搜索帖子的请求,将返回视图")
    @GetMapping("/SearchPosts")
    public String SearchPosts(Model model,
                              @ApiParam(name = "PostName", value = "搜索的帖子名", required = true)
                              String PostName){
        log.info("SearchPosts");

        log.info("PostName:"+PostName);

        model.addAttribute("instruction",1);
        model.addAttribute("status", 1);
        model.addAttribute("inf", 4);
        model.addAttribute("PostName", PostName);

        //模糊查询某个帖子
        List<Article> articles = articleServiceImp.SelBlurryByPostTitle(PostName);
        model.addAttribute("articles",articles);
        //帖子数
        model.addAttribute("postsnum", articles.size());

        for (Article article:
        articles) {
            log.info("article:"+article);
        }
        //排序最近访问的用户
        List<User> users = userServiceImp.selAll();
        String currentTime = new SimpleDateFormat(CalTime.DateFormat).format(new Date());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                o1.Clear();
                o2.Clear();
                return o2.getLasttime().compareTo(o1.getLasttime());
            }
        });
        //写入最近登陆时间
        for (User user:
                users) {
            userServiceImp.ConversionTimeArticle(user,CalTime.ConversionTime(currentTime,new SimpleDateFormat(CalTime.DateFormat)
                    .format(user.getLasttime()),CalTime.DateFormat));
        }
        model.addAttribute("users",users);

        //活跃榜，有可能用户没有登陆，得到的是全部的签到信息，大工程不能这样写
        List<SignIn> signIns = userServiceImp.SelectSignInByUsername("");
        log.info("signIns:"+signIns.size());
        if (signIns.size() == 0){
            log.info("signIns:"+signIns.size());
            throw new ErrorpageException(101,"数据更新错误，数据异常");
        }
        Collections.sort(signIns, new Comparator<SignIn>() {
            @Override
            public int compare(SignIn o1, SignIn o2) {
                return o2.getConsecutivedays() - o1.getConsecutivedays();
            }
        });

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();
        if (currentUser != null){
            for (SignIn signIn:
                    signIns) {
                //得到当前用于的签到信息，得到的是全部的签到榜，但是可能用户没有登陆
                if (signIn.getUsername().equals(currentUser.getUsername())){
                    model.addAttribute("signIn",signIn);
                    break;
                }
            }
        }
        else {
            //我们默认id为-1就是没有登陆
            signIns.get(0).setId(-1);
            model.addAttribute("signIn",signIns.get(0));
        }
        model.addAttribute("signIns",signIns);


        //按照阅读量排序的帖子
        List<Article> particles = articleServiceImp.SelByAll(0);
        Collections.sort(particles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViews()-o1.getViews();
            }
        });
        model.addAttribute("particles",particles);

        //热搜
        List<HotSearchList> hotSearchLists = beanFactory.getBean(HotSearchUtil.class).HotSearch();
        model.addAttribute("hotSearchLists",hotSearchLists);
        //热搜平台数量
        model.addAttribute("hotSearchListsnum",hotSearchLists.size());

        return "jie/searchindex";
    }

    @ApiOperation("帖子点赞和收藏")
    @GetMapping("/post/LikesFavorites")
    @RequiresUser
    @ResponseBody
    public String postLikesFavorites(Model model,
                              @ApiParam(name = "PostId", value = "要点赞的帖子id", required = true)
                                Integer PostId,
                              @ApiParam(name = "LFFlag", value = "1：点赞 2：收藏", required = true)
                                Integer LFFlag,
                              @ApiParam(name = "PostName", value = "1：代表点赞或者收藏 0：点赞取消或者收藏取消", required = true)
                                Integer likefavorites) {



        log.info("/post/LikesFavorites");
        log.info("PostId:"+PostId+" "+LFFlag+" "+likefavorites);

        //得到当前登陆的对象
        Subject subject = SecurityUtils.getSubject();
        //获取登陆的时候的user
        User currentUser = (User) subject.getPrincipal();

        LikesFavorites LF = articleServiceImp.SelByArticleidToLikesFavorites(PostId, currentUser.getUsername());
        if (LF == null){
            LF = beanFactory.getBean(LikesFavorites.class);
            if (LFFlag == 1)
                LF.SetAll(currentUser.getUsername(),PostId,likefavorites,0);
            else
                LF.SetAll(currentUser.getUsername(),PostId,0,likefavorites);
            Integer integer = articleServiceImp.InsertLikesFavorites(LF);
            if (integer == 0){
                log.info("integer:"+integer);
//                throw new ErrorpageException(101,"点赞或者收藏异常，请稍后重试");
                return "点赞或者收藏异常，请稍后重试";
            }
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("articleid",PostId);
            map.put("username",currentUser.getUsername());
            if (LFFlag == 1)
                map.put("likes",likefavorites);
            else
                map.put("favorites",likefavorites);
            Integer integer1 = articleServiceImp.UpdateLikesFavorites(map);
            if (integer1 == 0){
                log.info("integer1:"+integer1);
                return "点赞或者收藏异常，请稍后重试";
            }
        }
        //更新点赞数或者收藏数
        Map<String,Object> artMap = new HashMap<>();
        artMap.put("id",PostId);
        if (LFFlag == 1) {
            if (likefavorites == 1)
                artMap.put("likes", 1);
            else
                artMap.put("likes", -1);
        }
        else {
            if (likefavorites == 1)
                artMap.put("favorites", 1);
            else
                artMap.put("favorites", -1);
        }
        Integer num = articleServiceImp.UpdateArticle(artMap);
        return "success";
    }

    @ApiOperation("发表新帖")
    @GetMapping("/jie/add")
    @RequiresUser
    public String postLikesFavorites(Model model) {

        log.info("/jie/add");
        return "jie/add";
    }


}
