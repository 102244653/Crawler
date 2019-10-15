package CrawlerArticle;

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

public class SearchHexunlicai {

    private static Logger log = LoggerFactory.getLogger(SearchHexunlicai.class);

    public List<Articles> searcbsHxlcList(int tagid) throws Exception {
        List<Articles> articles = new ArrayList<>();
        Document document = Jsoup.connect("http://money.hexun.com/").header("Accept", "text/html, application/xhtml+xml, */*").get();
        Elements detail = document.select("ul#hidList").select("li");
        int i = 0;
        for (Element ele : detail) {
            if (i >= 20) {
                break;
            }
            String url = ele.select("a").attr("href");
            String newsid = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            //判断文章是否已爬取  BigData/2019-09/16/content_6151324.htm
            IsCheck is = ArticleMapper.iscrawler(Integer.parseInt(newsid), "和讯理财");
            if (is != null) {
                log.warn("---和讯理财文章【" + newsid + "】已存在，自动跳过！---");
                continue;
            }
            Articles it199 = new Articles();
            it199.setWebsource("和讯理财");
            it199.setTagname(tagid);//设置文章标签
            it199.setNewsid(Integer.valueOf(newsid));//id
            it199.setArthot(new Random().nextInt(1000));
            it199.setArturl(url);//文章链接
            if (ele.text().length() > 20) {//标题
                it199.setTitle(ele.text().substring(0, 20));
            } else {
                it199.setTitle(ele.text());
            }
            String time = DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            it199.setCreattime(time);
            articles.add(it199);
            i++;
        }
        log.info("本次从【和讯理财】共查询到" + articles.size() + "篇文章");
        return articles;
    }

    //解析html
    public List<Articles> analysisHxlcHtml(List<Articles> artciles) throws Exception {
        for (Articles art : artciles) {
            try {

            String url = art.getArturl();
            Document document;
            try {
                document = Jsoup.connect(url).header("Accept", "text/html, application/xhtml+xml, */*").get();

            } catch (Exception e) {
                continue;
            }
            Elements detail = document.select("div.art_contextBox");
            StringBuilder result = new StringBuilder();
            for (Element de : detail.select("p")) {
                //只包含一张图片
                if (de.select("img").size() == 1 && de.text().trim().isEmpty()) {
                    result.append("[mgeimg:" + de.select("img").attr("src") + "]\n");
                } else //同时包含文章图片
                    if (!de.text().trim().isEmpty() && String.valueOf(de).contains("<img src=")) {
                        List<String> imgs = StringUtil.getMatchString(String.valueOf(de), "<img.*?>");
                        //匹配图片标签
                        for (String img : imgs) {
                            Document ele = Jsoup.parse(img);
                            String picurl = ele.select("img").attr("src");
                            de.select("[src=" + picurl + "]").removeAttr("style");
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
        return artciles;
    }

}
