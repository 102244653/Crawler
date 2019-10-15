package CrawlerArticle;

import Util.DateUtil;
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

public class SearchQiushibaike {


    private static Logger log = LoggerFactory.getLogger(SearchQiushibaike.class);

    //投资界网站文章爬取
    public List<Articles> searcbsQsbkList(int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        Document document =Jsoup.connect("http://www.lovehhy.net/Joke/Detail/QSBK/").header("Accept", "text/html, application/xhtml+xml, */*").get();
        Elements detail=document.select("div.cat_llb").select("h3");
        for(Element de:detail) {
            String url = de.select("a").attr("href");
            String newsid = url.substring(url.lastIndexOf("/") + 1,url.length()-1);
            //判断文章是否已爬取  80-20190912-348306.html
            IsCheck is = ArticleMapper.iscrawler(Integer.parseInt(newsid), "糗事百科");
            if (is != null) {
                log.warn("---糗事百科文章【" + newsid + "】已存在，自动跳过！---");
                continue;
            }
            Articles qsbk=new Articles();
            qsbk.setWebsource("糗事百科");
            qsbk.setTagname(tagid);//设置文章标签
            qsbk.setNewsid(Integer.valueOf(newsid));//id
            qsbk.setArthot(new Random().nextInt(500));
            qsbk.setArturl("http://www.lovehhy.net"+url);//文章链接
            if(de.text().length()>20){//标题
                qsbk.setTitle(de.text().split(" ")[0]);
            }else {
                qsbk.setTitle(de.text());
            }
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            qsbk.setCreattime(time);
            articles.add(qsbk);
        }
        log.info("本次从【糗事百科】共查询到"+articles.size()+"篇文章");
        return articles;
    }



    //解析html
    public  List<Articles> analysisqsbkHtml(List<Articles> articles ) throws Exception {
        for(Articles art:articles){
            try {
                Document document = Jsoup.connect(art.getArturl()).header("Accept", "text/html, application/xhtml+xml, */*").get();
                Elements detail = document.select("div#fontzoom");
                StringBuilder result = new StringBuilder();
                result.append( "[mgeimg:http://img.qsbk.com]\n");
                result.append(detail.text());
                art.setOlddetail(new String(result));
                art.setStatus(1);
            } catch (Exception e) { }
        }

        return articles;
    }
}
