package CrawlerArticle;

import HttpUtils.HttpUtil;
import Util.DateUtil;
import Util.StringUtil;
import org.jsoup.Connection;
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

public class SearchTouzijie extends PostArticle{
    private static Logger log = LoggerFactory.getLogger(SearchTouzijie.class);

    //投资界网站文章爬取
    public List<Articles> searcbstzjList(int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        String html=HttpUtil.GetURL("https://www.pedaily.cn/all");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("ul#newslist-all").select("li");
        for(Element ele:detail){
            Elements de=ele.select("div.img").select("a");
            String url=de.get(0).attr("href");
            String newsid="";
            if(url.contains("newseed")){
                newsid=url.substring(url.lastIndexOf("/")+1);
            }
            if(url.contains("pedaily")){
                newsid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
            }

            //判断文章是否已爬取  446441.shtml
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"投资界");
            if(is!=null){
                log.warn("---投资界文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles tzj=new Articles();
            tzj.setWebsource("投资界");
            tzj.setTagname(tagid);//设置文章标签
            tzj.setNewsid(Integer.valueOf(newsid));//id
            tzj.setArthot(new Random().nextInt(1000));
            tzj.setArturl(url);//文章链接
            if(de.get(0).child(0).attr("alt").length()>20){//标题
                tzj.setTitle(de.get(0).child(0).attr("alt").substring(0,20));
            }else {
                tzj.setTitle(de.get(0).child(0).attr("alt"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(ele.select("div.desc").text()+"\n");
            result.append("[mgeimg:"+de.get(0).child(0).attr("data-src")+"]\n");
            tzj.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            tzj.setCreattime(time);
            articles.add(tzj);
        }
        log.info("本次从【投资界】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    public  List<Articles> analysisTzjHtml(List<Articles> artciles )throws Exception{
        for(Articles art:artciles) {
            try {
                String html = HttpUtil.GetURL(art.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("div.news-content");
                StringBuilder result = new StringBuilder(art.getOlddetail());
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
                                String picurl = ele.select("img").attr("src");
                                e.select("[src=" + picurl + "]").removeAttr("alt");
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
