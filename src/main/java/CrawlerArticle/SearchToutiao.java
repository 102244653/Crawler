package CrawlerArticle;

import HttpUtils.HttpClientInit;
import HttpUtils.HttpUtil;
import Util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.*;


public class SearchToutiao {
    private static Logger log = LoggerFactory.getLogger(SearchToutiao.class);
    //今日头条网站文章爬取链接
    //PC端：https://www.toutiao.com/api/pc/feed/?category=news_hot&utm_source=toutiao&widen=1&max_behot_time=1570505546&max_behot_time_tmp=1570505546&tadrequire=true&as=A135EDB9ACB053B&cp=5D9CB0C5C36B5E1&_signature=3WeIbgAAgQM1vaoT9HcvUd1niH
    //H5端：https://m.toutiao.com/list/?tag=news_tech&ac=wap&count=20&format=json_raw&as=A1857D891DA7DC4&cp=5D9D776DACB4BE1&min_behot_time=0&_signature=p676ggAA-kJPdNj.BsMkpaeu-p&i=

    //设置今日头条专属请求头
    private  String TouTiaoGet(String type)throws Exception{
        JSONObject json=this.Signature();
//        HttpGet httpGet = new HttpGet("https://m.toutiao.com/list/?tag="+type+"&ac=wap&count=20&format=json_raw&as="+json.getString("as")+"&cp="+json.getString("cp")+"&min_behot_time=0&_signature="+json.getString("_signature"));
        HttpGet httpGet = new HttpGet("https://www.toutiao.com/api/pc/feed/?category="+type+"&utm_source=toutiao&widen=1&max_behot_time=0&max_behot_time_tmp=0&tadrequire=true&as="+json.getString("as")+"&cp="+json.getString("cp")+"&_signature="+json.getString("_signature"));
        httpGet.setHeader(HttpHeaders.CONTENT_ENCODING,"UTF-8");
        httpGet.setHeader(HttpHeaders.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko");
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE,"text/xml");
        httpGet.setHeader(HttpHeaders.TIMEOUT, "60000");
        return new HttpClientInit().sendGet(httpGet);
    }

    //破解今日头条签名
    private  JSONObject Signature() throws ScriptException,
            FileNotFoundException, NoSuchMethodException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("JavaScript"); // 得到脚本引擎
        String reader = null;
        //获取文件所在的相对路径
        reader =SearchToutiao.class.getClassLoader().getResource("toutiao.js").getPath();
        //this.getClass().getClassLoader().getResource("");
        FileReader fReader = new FileReader(reader);
        engine.eval(fReader );
        Invocable inv = (Invocable) engine;
        //调用js中的方法
        Object test2 = inv.invokeFunction("get_as_cp_signature");
        JSONObject signature=JSONObject.parseObject((String) test2);
        try{
            fReader.close();
        }catch(Exception e){ }
        return signature;
    }

    //uniocode解码
    public static String unicodetoString(String unicode) {
        if (unicode == null || "".equals(unicode)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode.substring(pos, i));
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        return sb.toString();
    }

    //解码文章内容
    private static String toHtml(String content){
        Document document = Jsoup.parse(unicodetoString(content.replaceAll("&quot;","\"").replaceAll("&#x3D;","=")));
        return  String.valueOf(document).replaceAll("&quot;","").replaceAll("\\\\","");
    }

    //获取文章列表
    public  List<Articles> searchTouTiaoList(String articletype,int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        String res="";
        switch (articletype){
            case "热点":
                res=TouTiaoGet("news_hot");
                break;
            case "科技":
                res=TouTiaoGet("news_tech");
                break;
            case "游戏":
                res=TouTiaoGet("news_game");
                break;
            case "体育":
                res=TouTiaoGet("news_sports");
                break;
            case "财经":
                res=TouTiaoGet("news_finance");
                break;
            case "时尚":
                res=TouTiaoGet("news_fashion");
                break;
            case "搞笑":
                res=TouTiaoGet("funny");
                break;
            case "美食":
                res=TouTiaoGet("news_food");
                break;
            case "旅游":
                res=TouTiaoGet("news_travel");
                break;
            case "军事":
                res=TouTiaoGet("news_military");
                break;
            case "养生":
                res=TouTiaoGet("news_regimen");
                break;
            case "育儿":
                res=TouTiaoGet("news_baby");
                break;
            case "娱乐":
                res=TouTiaoGet("news_entertainment");
                break;

        }
        JSONObject article=JSONObject.parseObject(res);
        JSONArray articlelist =article.getJSONArray("data");
        for (Object art:articlelist) {
            JSONObject artres=(JSONObject) art;
            if(!artres.getString("article_genre").equals("article")){
                continue;
            }
            if(!artres.containsKey("middle_image")){
                continue;
            }
            //判断文章是否已爬取
            IsCheck is= ArticleMapper.iscrawler(artres.getInteger("behot_time"),"今日头条");
            if(is!=null){
                log.warn("---今日头条文章【"+artres.getString("title")+"】已存在，自动跳过！---");
                continue;
            }
            Articles jrtt=new Articles();
            jrtt.setWebsource("今日头条");
            jrtt.setTagname(tagid);//设置文章标签
            jrtt.setNewsid(artres.getInteger("behot_time"));
//            jrtt.setArthot(artres.getInteger("comments_count"));
            jrtt.setArthot(new Random().nextInt(1000));
            jrtt.setArturl("https://www.toutiao.com"+artres.getString("source_url"));
            if(artres.getString("title").length()>20){
                jrtt.setTitle(artres.getString("title").substring(0,20));
            }else {
                jrtt.setTitle(artres.getString("title"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(artres.getString("abstract")+"\n");
            result.append("[mgeimg:"+artres.getString("middle_image")+"]\n");
            jrtt.setOlddetail(new String(result));
            jrtt.setCreattime(DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT));
            articles.add(jrtt);
        }
        log.info("本次从【今日头条--"+articletype+"】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    public  List<Articles> analysisJRTTHtml(List<Articles> Artcilelist ) throws Exception {
        for(Articles art:Artcilelist ) {
            try {
                String html = String.valueOf(Jsoup.connect(art.getArturl())
                        .header("Accept", "text/html, application/xhtml+xml, */*")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko")
                        .get());
                String content = html.substring(html.indexOf("articleInfo:") + 12, html.indexOf("commentInfo")).trim();
                String json = content.substring(0, content.lastIndexOf(","));
                String arttext = toHtml(json.substring(json.indexOf("content:") + 9, json.indexOf("groupId")).replaceFirst("&quot;", "").replaceFirst("'", "").replaceAll("'.slice(6, -6),", ""));
                Document document = Jsoup.parse(arttext.replaceAll("\\&quot;", "\"").replaceAll("&#x3D;", "="));
                Elements detail = null;
                if (document.body().child(0).tagName().equals("div")) {
                    detail = document.body().child(0).children();
                } else {
                    detail = document.body().children();
                }
                StringBuilder result = new StringBuilder();
                for (Element de : detail) {
                    if (de.tagName().equals("img") || de.tagName().equals("div")) {
                        result.append("[mgeimg:" + de.select("img").attr("src") + "]\n");
                    } else {
                        result.append(de.text() + "\n\r");
                    }
                }
                art.setOlddetail(new String(result));
                art.setStatus(1);
            }catch (Exception e){}
        }
        return Artcilelist;
    }

}
