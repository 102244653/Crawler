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

public class SearchTouzhongwang {
    private static Logger log = LoggerFactory.getLogger(SearchTouzhongwang.class);

    //投资界网站文章爬取
    public List<Articles> searcbstzwList(int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        String html=HttpUtil.GetURL("https://www.chinaventure.com.cn/news/80.html");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("ul.common_newslist_pc").select("li");
        for(int i=0;i<20;i++){
            Element ele=detail.get(i);
            Elements de=ele.select("a");
            String url=de.get(0).attr("href");
            String newsid=url.substring(url.lastIndexOf("-")+1,url.lastIndexOf("."));
            //判断文章是否已爬取  80-20190912-348306.html
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"投中网");
            if(is!=null){
                log.warn("---投中网文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles tzw=new Articles();
            tzw.setWebsource("投中网");
            tzw.setTagname(tagid);//设置文章标签
            tzw.setNewsid(Integer.valueOf(newsid));//id
            tzw.setArthot(new Random().nextInt(1000));
            tzw.setArturl("https://www.chinaventure.com.cn"+url);//文章链接
            if(de.select("img").attr("title").length()>20){//标题
                tzw.setTitle(de.select("img").attr("title").substring(0,20));
            }else {
                tzw.setTitle(de.select("img").attr("title"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(de.select("h2").text()+"\n");
            result.append("[mgeimg:"+de.select("img").attr("src")+"]\n");
            tzw.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            tzw.setCreattime(time);
            articles.add(tzw);
        }
        log.info("本次从【投中网】共查询到"+articles.size()+"篇文章");
        return articles;
    }


    //解析html
    public  List<Articles> analysisTZWHtml(List<Articles> artciles ){
        for(Articles art:artciles) {
            try{
            String html = HttpUtil.GetURL(art.getArturl());
            Document document = Jsoup.parse(html);
            Elements detail = document.select("[class=article_slice_pc clearfix]");
            StringBuilder result =new StringBuilder(art.getOlddetail());
            for (Element e : detail.select("P")) {
                //只包含一张图片
                if (e.select("img").size() == 1 && e.text().trim().isEmpty()) {
                    result.append("[mgeimg:" + e.select("img").attr("src") + "]\n");
                } else //同时包含文章图片
                    if (!e.text().trim().isEmpty() && String.valueOf(e).contains("<img src=")) {
                        List<String> imgs = StringUtil.getMatchString(String.valueOf(e), "<img.*?>");
                        //匹配图片标签
                        for (String img : imgs) {
                            Document ele = Jsoup.parse(img);
                            String picurl =ele.select("img").attr("title");
                            e.select("[src=" + picurl.substring(0,picurl.length()-4) + "]").removeAttr("alt");
                        }
                        String phtml = Jsoup.parse(String.valueOf(e).replaceAll("<img src=\"", "[mgeimg:").replaceAll("\">", "]")).text();
                        result.append(phtml + "\n\r");
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
