package BaseApi;

import HttpUtils.HttpUtil;
import Util.MyPair;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class UpdateUserInfo extends InitApi{

    //更新用户信息
    public JSONObject update(String[] userinfo, String host)throws Exception {
        long time = System.currentTimeMillis() / 1000;
        List<MyPair<String, ContentBody>> reqParam = new ArrayList();
        reqParam.add(new  MyPair<>("bornDate",new StringBody(userinfo[0], ContentType.MULTIPART_FORM_DATA)));//1992-01-01
        reqParam.add(new  MyPair<>("clientType",new StringBody("1", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("gender",new StringBody(userinfo[1], ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("lang",new StringBody("0", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("network",new StringBody("1", ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("sign",new StringBody( this.sign(time, getToken(), getUid(), "v5.0/member/put_basic_profile"), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("timestamp",new StringBody(String.valueOf(time), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("token",new StringBody(getToken(), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("uid",new StringBody(getUid(), ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("username",new StringBody(userinfo[2], Charset.forName("utf-8"))));
        reqParam.add(new  MyPair<>("uuid",new StringBody(uuid, ContentType.MULTIPART_FORM_DATA)));
        reqParam.add(new  MyPair<>("version",new StringBody(version, ContentType.MULTIPART_FORM_DATA)));
        //上传指定图片
        reqParam.add(new  MyPair<>("avatar",new FileBody(new File(userinfo[3]))));
        String res;
        try {
            res = HttpUtil.PostFile(host + "v5.0/member/put_basic_profile", reqParam);
        } catch (Exception e) {
            res = "{\"code\":111,\"msg\":\"请求失败！\"}";
        }
        JSONObject responseJson=JSONObject.parseObject(res);
        return responseJson;
    }

    //更改用户资料
    @Test
    public void test() throws Exception{
        List<String[]> userinfo=new ArrayList<>();
        List<String> manname=this.getname("C:\\Users\\mge\\Desktop\\man.txt");
        List<String> womanname=this.getname("C:\\Users\\mge\\Desktop\\woman.txt");
        File manpic=new File("C:\\Users\\mge\\Desktop\\man");
        File womanpic=new File("C:\\Users\\mge\\Desktop\\women");
        for(File pic:manpic.listFiles()){
            int i=new Random().nextInt(manname.size());
            userinfo.add(new String[]{this.randomDate(),"1",manname.get(i),pic.getPath()});
            manname.remove(i);
        }
        for(File pic:womanpic.listFiles()){
            int i=new Random().nextInt(womanname.size());
            userinfo.add(new String[]{this.randomDate(),"2",womanname.get(i),pic.getPath()});
            womanname.remove(i);
        }
        System.out.println(userinfo.size());
        for(int i=1;i<1000;i++){
            String num="";
            if(i<10){
                num="00"+i;
            }else if(i<100){
                num="0"+i;
            }else {
                num=String.valueOf(i);
            }
            this.login("13000000"+num,"000000",InitApi.basehost);
            int user=new Random().nextInt(userinfo.size());
            JSONObject responseJson= this.update(userinfo.get(user),InitApi.basehost);
            userinfo.remove(user);
            if(responseJson.getString("code").equals("0")){
                System.out.println("用户13000000"+num+"更新资料成功");
            }else {
                System.out.println("---------用户13000000"+num+"更新资料失败----------");
            }
        }

    }

    public List<String> getname(String path){
        List<String> name=new ArrayList<>();
        InputStreamReader reader=null;
        BufferedReader br=null;
        try {
            reader = new InputStreamReader(new FileInputStream(path),"gbk");
            br = new BufferedReader(reader);
            String str;
            while ((str = br.readLine()) != null){
                name.add(str);
            }
            br.close();
            reader.close();
        }catch (Exception e){}
        return name;
    }


    public String randomDate() {
        Random rndYear = new Random();
        double y=Math.random();
        int year=0;
        if(y<=0.4){
             year = rndYear.nextInt(10) + 1980;  //的整数；年
        }
        if(y>0.4&&y<=0.9){
            year = rndYear.nextInt(10) + 1990;
        }
        if(y>0.9){
             year = rndYear.nextInt(6) + 2000;
        }

        Random rndMonth = new Random();
        int month = rndMonth.nextInt(12) + 1;   //生成[1,12]的整数；月
        String m="";
        if(month<10){
             m="0"+month;
        }else {
            m= String.valueOf(month);
        }
        Random rndDay = new Random();
        int Day = rndDay.nextInt(28) + 1;       //生成[1,28]的整数；日
        String d="";
        if(Day<10){
            d="0"+Day;
        }else {
            d= String.valueOf(Day);
        }
        return year + "-" + m + "-" + d;

    }

}
