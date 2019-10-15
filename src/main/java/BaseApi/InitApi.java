package BaseApi;

import CrawlerArticle.ArticleMapper;
import CrawlerArticle.appversion;
import HttpUtils.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;

public class InitApi {

    private static Logger log = LoggerFactory.getLogger(InitApi.class);

    //正式接口域名
    public static String basehost="http://api.maiguoer.com/";
    //测试域名
    public static String testbasehost="http://test-api.maiguoer.com/";
    //设备uuid
    public static String uuid="mytestdevices001";
    //登录结果
    public JSONObject loginJson;
    //登录账号的token
    public String token;
    //接口版本
    public static String version;
    //登录账号的uid
    public String uid;
    //当前登陆的账号
    public String isusername;
    public String ispassword;

    public JSONObject getLoginJson() {
        return loginJson;
    }

    public void setLoginJson(JSONObject loginJson) {
        this.loginJson = loginJson;
    }

    public String getToken() {
        return loginJson.getJSONObject("data").getString("token");
    }

    public String getUid() {
        return loginJson.getJSONObject("data").getJSONObject("loginInfo").getString("uid");
    }

    public void setUsername(String username) {
        this.isusername = username;
    }
    public String getUsername(){ return this.isusername; }

    public void setPassword(String password) {
        this.ispassword = password;
    }

    public static String md5(String text) {
        String encodeStr = DigestUtils.md5Hex(text);
        return encodeStr;
    }

	//签名算法
    public static String sign(long time, String token, String uid, String api) throws Exception{
        version=new InitApi().getversion();
        return version;
    }

    public String  getversion()throws Exception{
        appversion app=new appversion();
        app.setType("login");
        return ArticleMapper.selectversion(app);
    }

    //登录
    public void login(String username,String password,String host)throws Exception{
        long time=System.currentTimeMillis() / 1000;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("clientType", "1");
        paramsMap.put("lang", "0");
        paramsMap.put("network", "1");
        paramsMap.put("password", this.md5(String.valueOf(password)));
        paramsMap.put("sign", this.sign(time, "", "0", "v5.0/login"));
        paramsMap.put("timestamp", String.valueOf(time));
        paramsMap.put("token", "");
        paramsMap.put("uid", "0");
        paramsMap.put("username", username);
        paramsMap.put("uuid", uuid);
        paramsMap.put("version", version);
        String res;
        try{
            res= HttpUtil.PostForm(host+"v5.0/login",paramsMap);
        }catch (Exception e){
            res="{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject responseJson=JSONObject.parseObject(res);
        if(responseJson.getString("code").equals("0")){
            this.setLoginJson(responseJson);
            this.setUsername(username);
            this.setPassword(password);
            log.info("用户["+username+"::"+password+"]登录成功！");
        }else if(responseJson.getString("code").equals("101") && responseJson.getString("msg").equals("有新的 app 版本！")){
            String newversion=responseJson.getJSONObject("data").getString("version");
            version=newversion;
            appversion app=new appversion();
            app.setType("login");app.setVersion(newversion);
            ArticleMapper.updateversion(app);
        }else {
            log.error("用户["+username+"::"+password+"]登录失败，原因："+responseJson.getString("msg"));
        }
    }

    public static List<String> InitUser(int Min,int Max,int count){
        Set<Integer> set = new HashSet<Integer>();
        List<String> users = new ArrayList<>();
        for (;set.size() <= count;) {
            int a = (int)Math.round(Math.random()*(Max-Min)+Min);
            set.add(a);
        }
        for(Integer a:set){
            if(a<10){
                users.add("1300000000"+String.valueOf(a));
            }else if(a<100){
                users.add("130000000"+String.valueOf(a));
            }else{
                users.add("13000000"+String.valueOf(a));
            }
        }
        return users;
    }


}
