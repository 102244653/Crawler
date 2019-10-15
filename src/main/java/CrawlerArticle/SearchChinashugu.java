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

public class SearchChinashugu extends PostArticle{

    private static Logger log = LoggerFactory.getLogger(SearchDashujuzhongguo.class);

    //中国数谷网站文章爬取
    public List<Articles> searcbsZgsgList(int tagid)throws Exception {
        List<Articles> articles = new ArrayList<>();
        String html = HttpUtil.GetURL("http://www.cbdio.com/node_2570.htm");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("div.cb-media").select("li.am-g");
        for (Element ele : detail) {
            Element de=ele.select("p").get(0);
            String url=de.select("a").get(0).attr("href");
            //判断文章是否已爬取  BigData/2019-09/16/content_6151324.htm
            String newsid=url.substring(url.lastIndexOf("_")+1,url.lastIndexOf("."));
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"中国数谷");
            if(is!=null){
                log.warn("---中国数谷文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles zgsg=new Articles();
            zgsg.setWebsource("中国数谷");
            zgsg.setTagname(tagid);//设置文章标签
            zgsg.setNewsid(Integer.valueOf(newsid));//id
            zgsg.setArthot(new Random().nextInt(1000));
            zgsg.setArturl("http://www.cbdio.com/"+url);//文章链接
            if(de.select("img").attr("alt").length()>20){//标题
                zgsg.setTitle(de.select("img").attr("alt").substring(0,20));
            }else {
                zgsg.setTitle(de.select("img").attr("alt"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(ele.select("div").select("p.cb-media-summary").text()+"\n");
            if (ele.select("img").attr("src").trim().isEmpty()){
                result.append("[mgeimg:http://www.cbdio.com/assets/i/no_picture.jpg]\n");
            }else {
                result.append("[mgeimg:http://www.cbdio.com/"+ele.select("img").attr("src")+"]\n");
            }
            zgsg.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            zgsg.setCreattime(time);
            articles.add(zgsg);
        }
        log.info("本次从【中国数谷--深度】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    public  List<Articles>   analysisZgsgHtml(List<Articles> artciles){
        for(Articles art:artciles) {
            try {
                String html = HttpUtil.GetURL(art.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("div.cb-article");
                String content = String.valueOf(detail).replaceAll("center", "p");
                art.setArthot(new Random().nextInt(100));
                StringBuilder result = new StringBuilder(art.getOlddetail());
                for (Element e : Jsoup.parse(content).select("p")) {
                    if (e.attr("class").equals("cb-article-info") || e.attr("align").equals("center") ||
                            e.attr("align").equals("right") || e.attr("align").equals("p")) {
                        continue;
                    }
                    //只包含一张图片
                    if (e.select("img").size() >= 1 && e.text().trim().isEmpty()) {
                        result.append("[mgeimg:http://www.cbdio.com" + e.select("img").attr("src").substring(9) + "]\n");
                    } else//没有图片
                    {
                        result.append(e.text() + "\n\r");
                    }
                }
                art.setOlddetail(new String(result));
                art.setStatus(1);
            }catch (Exception e){}
        }
        return artciles;
    }

}
