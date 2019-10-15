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

public class SearchIT199 {
    private static Logger log = LoggerFactory.getLogger(SearchIT199.class);

    public List<Articles> searcbsIT199List(int tagid)throws Exception {
        List<Articles> articles = new ArrayList<>();
        String html = HttpUtil.GetURL("http://www.199it.com/archives/category/sharingeconomy");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("article");
        for(int i=0;i<3;i++){
            Elements de=detail.get(i).select("div.post-img");
            String url=de.select("a").attr("href");
            String newsid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
            //判断文章是否已爬取  BigData/2019-09/16/content_6151324.htm
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"IT199");
            if(is!=null){
                log.warn("---IT199文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles it199=new Articles();
            it199.setWebsource("IT199");
            it199.setTagname(tagid);//设置文章标签
            it199.setNewsid(Integer.valueOf(newsid));//id
            it199.setArthot(new Random().nextInt(1000));
            it199.setArturl(url);//文章链接
            if(de.select("a").attr("title").length()>20){//标题
                it199.setTitle(de.select("a").attr("title").substring(0,20));
            }else {
                it199.setTitle(de.select("a").attr("title"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append("[mgeimg:"+de.select("img").attr("src")+"]\n");
            it199.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            it199.setCreattime(time);
            articles.add(it199);
        }
        log.info("本次从【IT199】共查询到"+articles.size()+"篇文章");
        return  articles;
    }

    //解析html
    public List<Articles>  analysisIT199Html(List<Articles> artciles){
        for(Articles art:artciles) {
            try {
                String html = HttpUtil.GetURL(art.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("[itemprop=articleBody]");
                StringBuilder result = new StringBuilder(art.getOlddetail());
                for (Element ele : detail.select("p")) {
                    //只包含一张图片
                    if (ele.select("img").size() >= 1 && ele.text().trim().isEmpty()) {
                        for (Element img : ele.select("img")) {
                            result.append("[mgeimg:" + img.attr("src") + "]\n");
                        }
                    } else//没有图片
                    {
                        result.append(ele.text() + "\n\r");
                    }
                }
                art.setOlddetail(new String(result));
                art.setStatus(1);
            }catch (Exception e){}
        }
        return artciles;
    }
}
