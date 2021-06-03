package com.lzx.blog.Util;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.lzx.blog.popj.HotSearch;
import com.lzx.blog.popj.HotSearchList;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
public class HotSearchUtil {
    @Autowired
    private org.springframework.beans.factory.BeanFactory beanFactory;

    public List<HotSearchList> HotSearch() {
        List<HotSearchList> hotSearchLists = new ArrayList<HotSearchList>();

        hotSearchLists.add(WeiboHot());
        hotSearchLists.add(BaiduHot());

        return hotSearchLists;
    }

    /**
     * 爬取微博热搜榜
     * @return
     */
    public HotSearchList WeiboHot(){
        try {
            init();
            String urlStr = "https://s.weibo.com/top/summary";
            final Document doc = Jsoup.connect(urlStr).get();//获取html
            Elements trs = doc.select("tbody").select("tr");//获取tbody下的所有tr下的html内容

            HotSearchList bean = beanFactory.getBean(HotSearchList.class);
            bean.setPlatformName("微博热搜");
            bean.setSiteId(1);
            for (org.jsoup.nodes.Element tr : trs) {
                HotSearch HotSearchBean = beanFactory.getBean(HotSearch.class);
//                log.info("HotSearchBean:" + HotSearchBean.hashCode());

                Elements tds = tr.select("td");
                HotSearchBean.setRank(tds.get(0).text());//排名
                HotSearchBean.setNum(tds.get(1).select("span").text());//热度指数
                HotSearchBean.setTitle(tds.get(1).select("a").text());//热搜标题
                HotSearchBean.setUrl("https://s.weibo.com"+tds.get(1).select("a").attr("href"));//相对地址

                bean.getHotSearches().add(HotSearchBean);
//                log.info("热搜："+hotSearchLists.get(windex).getHotSearches().get(lindex));
            }
            return bean;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 百度热搜
     * @return
     */
    public HotSearchList BaiduHot(){
        try {
            Document doc =  Jsoup.connect("http://top.baidu.com/buzz?b=1").timeout(5000).get();
            // 获取目标HTML代码
            Elements table = doc.select("[class=list-table]");
            Elements trs = table.select("tr");
            HotSearchList bean = beanFactory.getBean(HotSearchList.class);
            bean.setPlatformName("百度热搜");
            bean.setSiteId(2);
            for (Element tr : trs) {
                Elements tds=tr.select("td");
                HotSearch HotSearchBean = beanFactory.getBean(HotSearch.class);

                if(tds.size()==4){
                    HotSearchBean.setRank(tds.get(0).select("span").text());
                    HotSearchBean.setTitle(tds.get(1).select("a").get(0).text());
                    HotSearchBean.setUrl(tds.get(1).select("a").get(0).attr("href"));
                    HotSearchBean.setNum(tds.get(3).select("span").text());
                    bean.getHotSearches().add(HotSearchBean);

                }
            }
            return  bean;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



    //java使用jsoup时绕过https证书验证,不绕过会爆证书错误
    static public void init() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
    }
}

