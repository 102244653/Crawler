package HttpUtils;

import Util.MyPair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static final HttpClientInit client = new HttpClientInit();
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static HttpClientInit getClient() {
        return client;
    }

    public static String GetURL(String httpUrl) {
        // 创建get请求
//        logger.info("开始发送GET请求...");
        HttpGet httpGet = new HttpGet(httpUrl);
        httpGet.setHeader("Connection", "keep-alive");
        //httpGet.addHeader(new BasicHeader("Cookie", cookies));
        initMethodParam(httpGet, Config.CONTENT_TYPE_TEXT_HTML, Config.CHARSET_UTF_8);
        return client.sendGet(httpGet);
    }

    //参数格式化
    private static void initMethodParam(HttpUriRequest method, String contentType, String encoding){
        if (contentType != null){
            method.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }
        method.setHeader(HttpHeaders.CONTENT_ENCODING, encoding);
        method.setHeader(HttpHeaders.TIMEOUT, "60000");
    }


    /**
     * post发送文件
     */
    public static String PostFile(String httpUrl, List<MyPair<String, ContentBody>> reqParam) {
        logger.info("正在发送POST（上传文件）请求...");
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        httpPost.setHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;)");
        try{
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            for(Map.Entry<String,ContentBody> param : reqParam.entrySet()){
//                builder.addPart(param.getKey(), param.getValue());
//            }
            for(MyPair<String,ContentBody> param:reqParam){
                builder.addPart(param.getKey(),param.getValue());
            }
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);
        }catch (Exception e){
            logger.error(e.toString());
        }
        return client.sendPost(httpPost);

    }


    /**
     * post发送json
     */
    public static String PostJason(String httpUrl, String paramsJson) {
        logger.info("正在发送POST（Jason）请求...");
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        try {
            //设置头信息
            //httpPost.addHeader("Authorization", "");
            // 设置json参数
            if (paramsJson != null && paramsJson.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
                stringEntity.setContentType(Config.CONTENT_TYPE_JSON_URL);
                httpPost.setEntity(stringEntity);
            } else {
                logger.error("请求参数为空...");
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return client.sendPost(httpPost);
    }

    /**
     * post发送xml
     */
    public static String PostXml(String httpUrl, String paramsXml) {
        logger.info("开始发送POST(XML)请求...");
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            // 设置参数
            if (paramsXml != null && paramsXml.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(paramsXml, "UTF-8");
                stringEntity.setContentType(Config.CONTENT_TYPE_TEXT_HTML);
                httpPost.setEntity(stringEntity);
            } else {
                logger.error("请求参数为空...");
            }
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return client.sendPost(httpPost);
    }

    /**
     * 提交表单数据
     * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
     *
     * @param paramsMap 需要转化的键值对集合
     *                  需要自定义paramsMap并赋值传进来，方法如下：
     *                  Map<String, String> paramsMap = new HashMap<String, String>();
     *                  paramsMap.put("clientType", "1");
     */
    public static String PostForm(String url, Map<String, String> paramsMap) {
        logger.info("开始发送POST(Form)请求...");
        HttpPost httpPost = new HttpPost(url);
        try {
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            for (String key : paramsMap.keySet()) {
                pairList.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }
            UrlEncodedFormEntity formdata = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPost.setEntity(formdata);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return client.sendPost(httpPost);
    }

}

