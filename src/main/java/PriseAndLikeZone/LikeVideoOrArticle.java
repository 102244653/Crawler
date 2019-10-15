package PriseAndLikeZone;

import BaseApi.InitApi;
import HttpUtils.HttpUtil;
import Util.RandomNum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LikeVideoOrArticle extends InitApi {
    private static Logger log = LoggerFactory.getLogger(LikeVideoOrArticle.class);

    //评论池
    commentdata comment;
    public LikeVideoOrArticle(){
        comment =new commentdata();
    }

    //视频点赞
    public void likevideo(String username,JSONObject zoneInfo,String host)throws Exception{
        //用户是否点过赞
        if(zoneInfo.getJSONObject("zoneInfo").getInteger("praiseStatus")==1){
            log.info("--用户["+username+"]已赞过视频动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】--");
            return;
        }
        long time=System.currentTimeMillis() / 1000;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        paramsMap.put("count", "1");
        paramsMap.put("lang", "0");
        paramsMap.put("network", "1");
        paramsMap.put("sign", this.sign(time, getToken(), getUid(), "v5.0/zone/put_multiple_praise"));
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("token", getToken());
        paramsMap.put("uid", getUid());
        paramsMap.put("uuid", uuid);
        paramsMap.put("version", version);
        paramsMap.put("zid", zoneInfo.getJSONObject("zoneInfo").getString("zid"));
        String res;
        try{
             res= HttpUtil.PostForm(host+"v5.0/zone/put_multiple_praise",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject likevideores = JSONObject.parseObject(res);
        if(likevideores.getInteger("code").equals(0)){
            log.info("用户["+username+"]点赞视频动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】成功！");
        }else if(likevideores.getJSONObject("zoneInfo").getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]点赞视频动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】失败，原因："+likevideores.getString("msg"));
        }
    }

    //文章点赞
    public void likearticle(String username,JSONObject zoneInfo,String host)throws Exception{
        //用户是否点过赞
        if(zoneInfo.getJSONObject("zoneInfo").getInteger("praiseStatus")==1){
            log.info("--用户["+username+"]已赞过文章动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】--");
            return;
        }
        long time=System.currentTimeMillis() / 1000;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        paramsMap.put("lang", "0");
        paramsMap.put("network", "1");
        paramsMap.put("sign", this.sign(time, getToken(), getUid(), "v5.0/zone/put_praise"));
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("token", getToken());
        paramsMap.put("uid", getUid());
        paramsMap.put("uuid", uuid);
        paramsMap.put("version",version);
        paramsMap.put("zid", zoneInfo.getJSONObject("zoneInfo").getString("zid"));
        String res;
        try{
             res= HttpUtil.PostForm(host+"v5.0/zone/put_praise",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject likearticleres = JSONObject.parseObject(res);
        if(likearticleres.getInteger("code").equals(0)){
            log.info("用户["+username+"]点赞文章动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】成功！");
        }else if(likearticleres.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]点赞文章动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】失败，原因："+likearticleres.getString("msg"));
        }
    }

    //动态评论
    public void reviewzone(String username,JSONObject zoneInfo,String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        String content=comment.sendcomment(zoneInfo.getJSONObject("zoneInfo").getInteger("curType"),zoneInfo.getJSONObject("zoneInfo").getJSONObject("tag").getString("name"));
        paramsMap.put("content",content );
        paramsMap.put("lang", "0");
        paramsMap.put("network", "1");
        paramsMap.put("sign", this.sign(time, getToken(), getUid(), "v5.0/zone/put_comment"));
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("token", getToken());
        paramsMap.put("uid", getUid());
        paramsMap.put("uuid", uuid);
        paramsMap.put("version", version);
        paramsMap.put("zid", zoneInfo.getJSONObject("zoneInfo").getString("zid"));
        String res;
        try{
             res= HttpUtil.PostForm(host+"v5.0/zone/put_comment",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject reviewzoneres = JSONObject.parseObject(res);
        if(reviewzoneres.getInteger("code").equals(0)){
            log.info("用户["+username+"]已评论动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】:"+content);
        }else if(reviewzoneres.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]评论动态【"+zoneInfo.getJSONObject("zoneInfo").getString("title")+"】失败，原因："+reviewzoneres.getString("msg"));
        }
    }

    //获取动态标签
    public JSONObject gettagid(String username,String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        String header="v5.0/zone/my_tag_list?"
                +"uid="+getUid()
                +"&lang=0&"
                +"sign="+URLEncoder.encode(this.sign(time,getToken(), getUid(), "v5.0/zone/my_tag_list"), "UTF-8")
                +"&timestamp="+URLEncoder.encode(String.valueOf(time), "UTF-8")
                +"&token="+URLEncoder.encode(getToken(), "UTF-8")
                +"&version="+version
                +"&clientType=0&network=1";
        String response;
        try{
            response=HttpUtil.GetURL(host+header);
        }catch (Exception e){
            response="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject taglist = JSONObject.parseObject(response);
        if(taglist.getInteger("code").equals(0)){
            log.info("用户["+username+"]获取动态标签列表成功！");
        }else {
            log.error("用户["+username+"]获取动态标签列表失败！");
        }
        return taglist;
    }

    //按标签获取文章动态列表
    public JSONObject getarticlelist(String username,String tagid,String page,String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        String header="v5.1/zone/article_list?clientType=1&lang=0&network=1&sign="
                + URLEncoder.encode(this.sign(time,getToken(), getUid(), "v5.1/zone/article_list"), "UTF-8")
                +"&tagId="+URLEncoder.encode(String.valueOf(tagid), "UTF-8")
                +"&timestamp="+URLEncoder.encode(String.valueOf(time), "UTF-8")
                +"&token="+URLEncoder.encode(getToken(), "UTF-8")
                +"&uid="+getUid()
                +"&uuid="+URLEncoder.encode(LikeVideoOrArticle.uuid, "UTF-8")
                +"&version="+version
                +"&page="+URLEncoder.encode(page, "UTF-8");
        String response;
        try{
            response=HttpUtil.GetURL(host+header);
        }catch (Exception e){
            response="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject zoneres = JSONObject.parseObject(response);
        if(zoneres.getInteger("code").equals(0)){
            log.info("用户["+username+"]获取文章动态列表成功！");
        }else if(zoneres.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]获取文章动态列表失败！");
        }
        return zoneres;
    }

    //按标签获取视频动态列表
    public JSONObject getvideolist(String username,String tagid,String page,String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        String header="v5.1/zone/video_list?clientType=1&lang=0&network=1&refresh=1&sign="
                + URLEncoder.encode(this.sign(time,getToken(), getUid(), "v5.1/zone/video_list"), "UTF-8")
                +"&tagId="+tagid
                +"&timestamp="+URLEncoder.encode(String.valueOf(time), "UTF-8")
                +"&token="+URLEncoder.encode(getToken(), "UTF-8")
                +"&uid="+getUid()
                +"&uuid="+URLEncoder.encode(LikeVideoOrArticle.uuid, "UTF-8")
                +"&version="+version
                +"&page="+URLEncoder.encode(page, "UTF-8");
        String response;
        try{
            response=HttpUtil.GetURL(host+header);
        }catch (Exception e){
          e.getMessage();
            response="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject zonelist = JSONObject.parseObject(response);
        if(zonelist.getInteger("code").equals(0)){
            log.info("用户["+username+"]获取视频动态列表成功！");
        }else if(zonelist.getInteger("code").equals(110)){
            this.login(isusername,ispassword,host);
        }else {
            log.error("用户["+username+"]获取视频动态列表失败！");
        }
        return zonelist;
    }

    //文章动态标签
    public String setarttagid(){
        String[] tagid={"40","55","59","61","63","65","66","79","8","81","77","0",""};
        return  tagid[new RandomNum().getrandom(tagid.length)];
    }
    //视频动态标签
    public String setvideotagid(){
        String[] tagid={"8","40","61","81","55","63","59","65","66","0",""};
        return  tagid[new RandomNum().getrandom(tagid.length)];
    }


    //文章动态点赞和评论//zonetype-- 视频/文章
    public void likeandreview(String username,String password,String host)throws Exception{
        this.login(username,password,host);
        String TagId="";
        String zonetype=(Math.random()>0.5?"视频":"文章");//随机评论文章或者视频
        if(zonetype.equals("视频")){
            TagId=this.setvideotagid();//随机视频动态标签
        }
        if (zonetype.equals("文章")){
            TagId=this.setarttagid();//随机文章动态标签
        }
        String page="";//默认页数为空
        int k=new Random().nextInt(4)+1 ;//刷新次数，一次20条
        for (int i=1;i<=k;i++){
            JSONObject zoneres=new JSONObject();
            //获取动态列表
            if(zonetype.equals("视频")){
                zoneres= this.getvideolist(username,TagId,page,host);
            }
            if (zonetype.equals("文章")){
                zoneres= this.getarticlelist(username,TagId,page,host);
            }
            if(zoneres.getInteger("code").equals("111")){
                break;
            }
            if(k==4){
                page="";
            }else {
                page = zoneres.getJSONObject("data").getString("nextPage");
            }
            int code = zoneres.getInteger("code");
            if (code == 110) {
                this.login(isusername, ispassword,host);
                continue;
            }
            JSONArray zonelist = zoneres.getJSONObject("data").getJSONArray("zoneList");
            if(zonelist.isEmpty()){
                break;
            }
            for (int num = 0; num <=zonelist.size() ; num++) {
                try{
                    JSONObject zoneinfo = zonelist.getJSONObject(num);
                    if(zoneinfo.isEmpty()){
                        continue;
                    }
                    int curtype = zoneinfo.getJSONObject("zoneInfo").getInteger("curType");
                    switch (curtype) {
                        case 5:
                            //随机0和1
                            if((Math.random()>0.8?1:0)==0){
                                this.likevideo(username,zoneinfo,host);
                                Thread.sleep(2000);
                            }
                            break;
                        case 0:
                            if((Math.random()>0.8?1:0)==0) {
                                this.likearticle(username,zoneinfo,host);
                                Thread.sleep(3000);
                            }
                            break;
                    }
                    if((Math.random()>0.8?1:0)==0){
                        this.reviewzone(username,zoneinfo,host);
                        Thread.sleep(2000);
                    }
                }catch (Exception e){}
            }
        }
    }


    /**
     * 账号：13000000000~13000000050
     * 密码：000000
     * */
    //数据驱动
    //@DataProvider(name = "Start",parallel = true)
    public  Object[][] TestData() {
        Object[][] testdata= new String[][]{
                {"13534173939","Td147852"},
                {"13545801985","123456"}};
        return testdata;
    }

    //@Test(dataProvider="TestData",dataProviderClass= LikeVideoOrArticle.class,threadPoolSize = 2, invocationCount = 2,  timeOut = 10000)
    public void Start(String username,String password){
        try {
            new LikeVideoOrArticle().likeandreview(username,password,basehost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test(){
        try {
        this.login("13534173939","Td147852",basehost);
        this.getarticlelist("13534173939","77","",basehost);
        this.getvideolist("13534173939","60","",basehost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //抽奖测试
    public String luckly(String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        paramsMap.put("lang", "0");
        paramsMap.put("network", "1");
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("token", getToken());
        paramsMap.put("uid", getUid());
        paramsMap.put("uuid", uuid);
        paramsMap.put("version", InitApi.version);
        paramsMap.put("sign", this.sign(time, getToken(), getUid(), "v5.0/luck/draw"));
        paramsMap.put("action", "v5.0/luck/draw");
        String res;
        try{
            res= HttpUtil.PostForm(host+"v5.0/luck/draw",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
       return res;
    }

}
