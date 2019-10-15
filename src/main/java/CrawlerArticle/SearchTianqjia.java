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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchTianqjia {

    private static Logger log = LoggerFactory.getLogger(SearchTianqjia.class);

    //投资界网站文章爬取
    public  List<Articles> searcbsTqjList(int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        Document document =Jsoup.connect("https://www.tianqi.com/toutiao/lvyou/").header("Accept", "text/html, application/xhtml+xml, */*").get();
        Elements detail=document.select("ul.toutiao600").select("li");
        for(int i=0;i<10;i++){
            Element de=detail.get(i);
            String url=de.select("a").attr("href");
            String newsid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
            //判断文章是否已爬取  80-20190912-348306.html
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"天气+");
            if(is!=null){
                log.warn("---天气+文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles tqj=new Articles();
            tqj.setWebsource("天气加");
            tqj.setTagname(tagid);//设置文章标签
            tqj.setNewsid(Integer.valueOf(newsid));//id
            tqj.setArthot(new Random().nextInt(1000));
            tqj.setArturl(url);//文章链接
            if(de.select("a").attr("title").length()>20){//标题
                tqj.setTitle(de.select("a").attr("title").split(" ")[0]);
            }else {
                tqj.setTitle(de.select("a").attr("title"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append("[mgeimg:"+de.select("img").attr("src")+"]\n");
            tqj.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            tqj.setCreattime(time);
            articles.add(tqj);
        }
        log.info("本次从【投中网】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    public  List<Articles> analysisTqjHtml( List<Articles> articles) throws Exception{
        for (Articles art : articles) {
            try {
                Document document = Jsoup.connect(art.getArturl()).header("Accept", "text/html, application/xhtml+xml, */*").get();
                Elements detail = document.select("div.texts").select("p");
                StringBuilder result = new StringBuilder();
                for (Element e : detail.select("P")) {
                    //只包含一张图片
                    if (e.select("img").size() >= 1 && e.text().trim().isEmpty()) {
                        if (e.select("img").size() > 1) {
                            for (Element img : e.select("img")) {
                                result.append("[mgeimg:" + img.attr("src") + "]\n");
                            }
                        } else {
                            result.append("[mgeimg:" + e.select("img").attr("src") + "]\n");
                        }
                    } else//没有图片
                    {
                        result.append(e.text() + "\n\r");
                    }
                }
                art.setOlddetail(new String(result));
                art.setStatus(1);
            }catch (Exception e){}
        }
        return articles;
    }
}
