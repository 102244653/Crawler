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

public class SearchMeizhuang {
    private static Logger log = LoggerFactory.getLogger(SearchMeizhuang.class);

    public List<Articles> searchMZList(int tagid)throws Exception {
        List<Articles> articles = new ArrayList<>();
        Document document = Jsoup.connect("https://www.shishang001.com/").header("Accept", "text/html, application/xhtml+xml, */*").get();
        Elements detail=document.select("h1.single");
        for(Element de:detail){
            String url=de.select("a").attr("href");
            String newsid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf(".")).trim();
            String title=de.select("a").text();
            //判断文章是否已爬取
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"美妆");
            if(is!=null){
                log.warn("---美妆文章【"+title+"】已存在，自动跳过！---");
                continue;
            }

            Articles mz=new Articles();
            mz.setWebsource("美妆");
            mz.setTagname(tagid);//设置文章标签
            mz.setNewsid(Integer.parseInt(newsid));//id
            mz.setArthot(new Random().nextInt(300));
            mz.setArturl(url);
            if(title.length()>20){
                mz.setTitle(title.substring(0,20));
            }else {
                mz.setTitle(title);
            }
            StringBuilder result = new StringBuilder();
            result.append("  \n");
            mz.setOlddetail(new String(result));
            //获取文章描述和封面图
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            mz.setCreattime(time);
            articles.add(mz);
        }
        log.info("本次从【美妆--深度】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    public List<Articles> analysisBSJHtml( List<Articles> Artcilelist) throws Exception {
        for(Articles artciles:Artcilelist ) {
            try {
                Document document = Jsoup.connect(artciles.getArturl()).header("Accept", "text/html, application/xhtml+xml, */*").get();
                Elements detail = document.select("div.jinsom-single-content").select("p");
                StringBuilder result = new StringBuilder(artciles.getOlddetail());
                for(Element de:detail){
                    if(de.getElementsByTag("img").size()>0){
                        result.append("[mgeimg:"+de.select("img").attr("src")+ "]\n");
                    }else {
                        result.append( de.text()+"\n");
                    }
                }
                artciles.setOlddetail(new String(result));
                artciles.setStatus(1);
            }catch (Exception e){}
        }
        return Artcilelist;
    }

}
