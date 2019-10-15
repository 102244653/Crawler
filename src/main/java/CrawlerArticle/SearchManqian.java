package CrawlerArticle;

import HttpUtils.HttpUtil;
import Util.DateUtil;
import Util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class SearchManqian {

    private static Logger log = LoggerFactory.getLogger(SearchManqian.class);

    public List<Articles> searcbsMqttList(int tagid)throws Exception {
        List<Articles> articles = new ArrayList<>();
        Document document = Jsoup.connect("http://toutiao.manqian.cn/channel_c030").header("Accept", "text/html, application/xhtml+xml, */*").get();
        Elements detail=document.select("li.news-list__item");
        for(int i=0;i<10;i++){
            Element ele=detail.get(i);
            String url=ele.select("a").get(0).attr("href");
            Articles mqtt=new Articles();
            mqtt.setWebsource("慢钱头条");
            mqtt.setTagname(tagid);//设置文章标签
            mqtt.setNewsid(new Random().nextInt(1000));//无id，随机取值不做校验
            mqtt.setArthot(new Random().nextInt(1000));
            mqtt.setArturl("http://toutiao.manqian.cn"+url);//文章链接
            if(ele.select("img").attr("alt").length()>20){//标题
                mqtt.setTitle(ele.select("img").attr("alt").substring(0,20));
            }else {
                mqtt.setTitle(ele.select("img").attr("alt"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(ele.select("[class=news-list__item__des]").text()+"\n");
//            result.append("[mgeimg:"+ele.select("img").attr("src")+"]\n");
            mqtt.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            mqtt.setCreattime(time);
            articles.add(mqtt);
        }
        log.info("本次从【慢钱头条--深度】共查询到"+articles.size()+"篇文章");
        return articles;
    }


    //解析html
    public  List<Articles>   analysisMqttHtml(List<Articles> Artcilelist)throws Exception{
        for(Articles art:Artcilelist){
            try {
                Document document = Jsoup.connect(art.getArturl()).header("Accept", "text/html, application/xhtml+xml, */*").get();
                Elements detail = document.select("section.article__content").select("p");
                StringBuilder result = new StringBuilder(art.getOlddetail());
                for (Element de : detail) {
                    //只包含一张图片
                    if (de.select("img").size() == 1 && de.text().trim().isEmpty()) {
                        if (de.select("img").attr("src").startsWith("/img?url=")) {
                            result.append("[mgeimg:" + de.select("img").attr("src").replace("/img?url=", "") + "]\n");
                        } else {
                            result.append("[mgeimg:" + de.select("img").attr("src") + "]\n");
                        }
                    } else //同时包含文章图片
                        if (!de.text().trim().isEmpty() && String.valueOf(de).contains("<img src=")) {
                            List<String> imgs = StringUtil.getMatchString(String.valueOf(de), "<img.*?>");
                            //匹配图片标签
                            for (String img : imgs) {
                                Document ele = Jsoup.parse(img);
                                String picurl = ele.select("img").attr("src");
                                if (picurl.startsWith("/img?url=")) {
                                    de.select("[src=" + picurl + "]").attr("src", picurl.replace("/img?url=", "")).removeAttr("title").removeAttr("alt");
                                } else {
                                    de.select("[src=" + picurl + "]").removeAttr("title").removeAttr("alt");
                                }
                            }
                            String phtml = Jsoup.parse(String.valueOf(de).replaceAll("<img src=\"", "[mgeimg:").replaceAll("\">", "]")).text();
                            result.append(phtml + "\n\r");
                        } else//没有图片
                        {
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
