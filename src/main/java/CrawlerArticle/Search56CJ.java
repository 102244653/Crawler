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

public class Search56CJ extends PostArticle{
    private static Logger log = LoggerFactory.getLogger(Search56CJ.class);

    //日更新极少，建议每次爬取前5条即可
    public  List<Articles> searc56cjhList(int tagid) throws Exception {
        List<Articles> articles = new ArrayList<>();
        String html=HttpUtil.GetURL("https://www.56cj.com");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("[class=list mainList]");
        int i=0;
        for(Element de:detail.select("li")){
            if(i>=5){break;}
            i++;
            //文章id
            String url=de.select("div.title").select("a").attr("href");
            String newsid=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
            //判断文章是否已爬取
            IsCheck is= ArticleMapper.iscrawler(Integer.parseInt(newsid),"币世界");
            if(is!=null){
                log.warn("---56财经文章【"+newsid+"】已存在，自动跳过！---");
                continue;
            }
            Articles cj56=new Articles();
            cj56.setWebsource("56财经");
            cj56.setTagname(tagid);//设置文章标签
            cj56.setNewsid(Integer.valueOf(newsid));//id
            String view=de.select("[class=fr readCount]").text().trim();
            if(view!=null){
                cj56.setArthot(Integer.valueOf(view));
            }else {
                cj56.setArthot(new Random().nextInt(1000));
            }
            cj56.setArturl("https://www.56cj.com"+url);
            if(de.select("div.title").select("a").text().length()>20){
                cj56.setTitle(de.select("div.title").select("a").text().substring(0,20));
            }else {
                cj56.setTitle(de.select("div.title").select("a").text());
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(de.select("[class=content]").text()+"\n");
            result.append("[mgeimg:https://www.56cj.com"+de.select("img.lazy").attr("data-original")+"]\n");
            cj56.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            cj56.setCreattime(time);
            articles.add(cj56);
        }
        log.info("本次从【56财经--深度】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    public  List<Articles> analysis56CJHtml(List<Articles> artciles ){
        for(Articles art:artciles) {
            try {
                String html = HttpUtil.GetURL(art.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("div.content");
                StringBuilder result = new StringBuilder(art.getOlddetail());
                for (Element e : detail.select("P")) {
                    //只包含一张图片
                    if (e.select("img").size() == 1 && e.text().trim().isEmpty()) {
                        result.append("[mgeimg:https://www.56cj.com" + e.select("img").attr("src") + "]\n");
                    } else //同时包含文章图片
                        if (!e.text().trim().isEmpty() && String.valueOf(e).contains("<img src=")) {
                            List<String> imgs = StringUtil.getMatchString(String.valueOf(e), "<img.*?>");
                            //匹配图片标签
                            for (String img : imgs) {
                                Document ele = Jsoup.parse(img);
                                String picurl = ele.select("img").attr("src");
                                e.select("[src=" + picurl + "]").attr("src", "https://www.56cj.com" + picurl);
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
