package CrawlerArticle;

import BaseApi.InitApi;
import HttpUtils.HttpUtil;
import Util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import Util.MyPair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class PostArticle extends InitApi {
    private static Logger log = LoggerFactory.getLogger(PostArticle.class);

    //发布文章到脉果儿
    public static void main(String[] agrs)throws Exception{
        PostArticle init=new PostArticle();

        List<Articles> art=new ArrayList<>();
        art=ArticleMapper.selectarticleby12();
        if(art.size()==0){
            log.info("----------文章已全部发布，Ending----------");
            return;
        }
        for(int i=0;i<10;i++){
            //清除上一次的图片
            File picdir=new File(DownloadPic.path);
            if(picdir.exists() && picdir.isDirectory() && picdir.listFiles().length!=0 ){
                for(File pic:picdir.listFiles()){ pic.delete(); }
            }
            //随机登录一个用户
            init.login(InitUser(900,999,50).get(new Random().nextInt(50)),"000000",InitApi.basehost);
            try{
                Articles articles=init.bsjreplaceimg(art.get(i),InitApi.basehost);
                init.postmessage(init.getUsername(),articles,InitApi.basehost);
            }catch (Exception e){}
            int[] time=new int[]{7,8,9,10};
            int t=time[new Random().nextInt(time.length)];
            for(int k=0;k<t;k++){
                log.info("----------文章发布等待30s----------");
                Thread.sleep(30000);
            }
        }
        log.info("*************发布文章结束*************");
    }

    //上传文章图片
    public JSONArray putfile(String username,String path, String host)throws Exception {
        long time = System.currentTimeMillis() / 1000;
        List<MyPair<String, ContentBody>> reqParam = new ArrayList();
        reqParam.add(new  MyPair<>("clientType",new StringBody("1", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("lang",new StringBody("0", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("network",new StringBody("1", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("sign",new StringBody( this.sign(time, getToken(), getUid(), "v5.0/zone/put_article_file"), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("timestamp",new StringBody(String.valueOf(time), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("token",new StringBody(getToken(), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("uid",new StringBody(getUid(), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("uuid",new StringBody(uuid, ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("version",new StringBody(version, ContentType.MULTIPART_FORM_DATA)));
        File picdir=new File(DownloadPic.path);
        if(path!=null){
            //上传指定图片
            reqParam.add(new  MyPair<>("images[]",new FileBody(new File(path))));
        }else {
            //上传多张图片
            if(picdir.exists() && picdir.isDirectory() && picdir.listFiles().length!=0 ){
                for(File pic:picdir.listFiles()){
                    reqParam.add(new  MyPair<>("images[]",new FileBody(pic)));
                }
            }else {
                log.warn("---文件夹【"+picdir+"】为空或不存在，请检查---");
                return null;
            }
        }
        String res;
        try {
            res = HttpUtil.PostFile(host + "v5.0/zone/put_article_file", reqParam);
        } catch (Exception e) {
            res = "{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject picres = JSONObject.parseObject(res);
        if(picres.getInteger("code").equals(0)){
            log.info("用户["+username+"]上传图片【"+path+"】成功");
        }else if(picres.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]上传图片【"+path+"】失败，原因："+picres.getString("msg"));
        }
        return picres.getJSONObject("data").getJSONArray("images");
    }

    //处理文章详情，下载图片并上传替换可用图片链接
    public  Articles  bsjreplaceimg(Articles articles,String host) throws Exception {
        String detail= articles.getOlddetail();
        //匹配[mgeimg:url]
        String regex="\\[[mgeimg].*?]";
        List<String> urls=StringUtil.getMatchString(detail,regex);
        for(String url:urls){
            try {
                if(url==null){continue;}
                String newpath="";
                if(url.contains("http://img.qsbk.com")){
                    File picdir=new File(PostArticle.class.getClassLoader().getResource("funnypic").getPath());
                    File[] pic=picdir.listFiles();
                    newpath=pic[new Random().nextInt(pic.length)].getPath();
                }else {
                    newpath= DownloadPic.downloadPicture(url.substring(8,url.length()-1),null);
                }
                String picname="";
                //根据平台处理获取的图片识别标识
                switch (articles.getWebsource()){
                    case "币世界":
                        picname =url.substring(url.indexOf("com/")+4,url.lastIndexOf("?")-5);
                        if(picname.startsWith("news_")){
                            picname=picname.replace("news_","");
                        }
                        break;
                    case "金色财经":
                        picname =url.substring(url.indexOf("com/")+4,url.indexOf("com/")+11);
                        break;
                    case "56财经":
                        String uri =url.substring(url.indexOf("Uploads")+19);
                        if(uri.startsWith("resources_")){
                            uri=uri.replace("resources_","");
                        }
                        picname=uri.substring(0,10);
                        break;
                    case "投资界":
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                    case "投中网":
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                    case "大数据中国":
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                    case "中国数谷":
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                    case "和讯理财":
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                    case "IT199":
                        picname =url.substring(url.lastIndexOf("_")+1,url.lastIndexOf("."));
                        break;
                    case "天气加":
                        picname =url.substring(url.lastIndexOf(".")-8,url.lastIndexOf("."));
                        break;
//                    case "慢钱头条":
//                        if(picname.contains("pic.manqian.cn")){
//                            picname=picname.substring(picname.lastIndexOf("="));
//                        }else if(picname.contains("toutiao.manqian.cn")){
//                            picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
//                        }else {
//                            picname =url.substring(15,25);
//                        }
//                        break;
                    case "今日头条":
                        picname =url.substring(url.lastIndexOf("/")+1,url.length()-1);
                        break;
                    case "糗事百科":
                        picname="qsbk";
                        break;
                    case "美妆":
                        picname=url.substring(url.lastIndexOf("/"),url.lastIndexOf("."));
                        break;
                    default:
                        picname =url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
                        break;
                }
                JSONArray json=this.putfile(this.getUsername(),newpath,host);
                Thread.sleep(1500);
                if(json.size()>0){
                    detail=detail.replaceAll("((https|http)?://).+("+picname+").*]", String.valueOf(json.get(0))+"]");
                }
            } catch (Exception e) {
                e.printStackTrace(); }
        }
        articles.setNewdetail(detail);
        articles.setStatus(2);
        ArticleMapper.uparticledetail(articles);
        return articles;
    }

    //发布文章
    public void  postmessage(String username,Articles articles,String host)throws Exception{
        if(articles.getTitle()==null ||articles.getOlddetail()==null || String.valueOf(articles.getTagname())==null){
            log.warn("---"+articles.getWebsource()+articles.getNewsid()+"文章内容或标题或标签为空，不予发布！---");
            return;
        }
        Thread.sleep(2000);
        long time=System.currentTimeMillis() / 1000;
        int id=articles.getId();
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        paramsMap.put("goodsIds","");
        paramsMap.put("lang", "0");
        paramsMap.put("message",articles.getNewdetail());
        paramsMap.put("network", "1");
        paramsMap.put("fView",String.valueOf(articles.getArthot()));
        paramsMap.put("sign", this.sign(time, getToken(), getUid(), "v5.0/zone/put_article"));
        paramsMap.put("tagId", String.valueOf(articles.getTagname()));
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("title", articles.getTitle());
        paramsMap.put("token", getToken());
        paramsMap.put("uid", getUid());
        paramsMap.put("uuid", uuid);
        paramsMap.put("fRcmd","1");
        paramsMap.put("version", version);
        String res;
        try{
            res= HttpUtil.PostForm(host+"v5.0/zone/put_article",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject articleres = JSONObject.parseObject(res);
        if(articleres.getInteger("code").equals(0)){
            Articles a=new Articles();
            a.setId(id);
            a.setStatus(3);
            ArticleMapper.uparticlestatus(a);
            log.info("用户["+username+"]发表文章【"+articles.getTitle()+"】成功");
        }else if(articleres.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]发表文章【"+articles.getTitle()+"】失败，原因："+articleres.getString("msg"));
        }
    }

    public void test()throws Exception{
        this.login("10224405","123456",InitApi.testbasehost);
        List<Articles> art=ArticleMapper.selectarticleby12();
        Articles aa=art.get(0);
        aa=this.bsjreplaceimg(aa,InitApi.testbasehost);
        this.postmessage("10224405",aa,InitApi.testbasehost);
    }

}
