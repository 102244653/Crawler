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

public class SearchDashujuzhongguo extends PostArticle{
    private static Logger log = LoggerFactory.getLogger(SearchDashujuzhongguo.class);

    //大数据中国网站文章爬取
    public List<Articles> searcbsDsjzgList(int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        String html=HttpUtil.GetURL("https://www.bigdatas.cn/news/");
        Document document = Jsoup.parse(html);
        Elements detail = document.select("ul.pagelist").select("li.subject_4");
        for(Element ele:detail){
            try {
                Elements content = ele.select("p.txt");//链接a标签
                String herf = content.select("a").attr("href");
                //判断文章是否已爬取  article-3311-1.html
                String newsid = herf.substring(herf.lastIndexOf("aid=") + 4, herf.lastIndexOf("&"));
                IsCheck is = ArticleMapper.iscrawler(Integer.parseInt(newsid), "大数据中国");
                if (is != null) {
                    log.warn("---大数据中国文章【" + newsid + "】已存在，自动跳过！---");
                    continue;
                }
                Articles dsj = new Articles();
                dsj.setWebsource("大数据中国");
                dsj.setTagname(tagid);//设置文章标签
                dsj.setNewsid(Integer.valueOf(newsid));//id
                dsj.setArturl("https://www.bigdatas.cn/article-" + newsid + "-1.html");//文章链接
                if (content.select("a").text().length() > 20) {//标题
                    dsj.setTitle(content.select("a").text().substring(0, 20));
                } else {
                    dsj.setTitle(content.select("a").text());
                }
                dsj.setArthot(new Random().nextInt(1000));
                StringBuilder result = new StringBuilder();
                //获取文章描述和封面图
                result.append("  ");
//            if(ele.select("img").get(0).attr("src")!=null){
//                result.append("[mgeimg:https://www.bigdatas.cn/"+ele.select("img").get(0).attr("src")+"]\n");
//            }
                dsj.setOlddetail(new String(result));
                String time = DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
                dsj.setCreattime(time);
                articles.add(dsj);
            }catch (Exception e){}
        }
        log.info("本次从【大数据中国】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    public  List<Articles> analysisDsjzgHtml(List<Articles> artciles ){
        for(Articles art:artciles) {
            try {
                String html = HttpUtil.GetURL(art.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("[class=message portal_view]");
                StringBuilder result = new StringBuilder(art.getOlddetail());
                for (Element e : detail.select("div")) {
                    if (e.attr("id").equals("myshare")) {
                        break;
                    }
                    //只包含一张图片
                    if (e.select("img").size() >= 1 && e.text().trim().isEmpty()) {
                        result.append("[mgeimg:https://www.bigdatas.cn/" + e.select("img").attr("src") + "]\n");
                    } else //同时包含文章图片
                        if (!e.text().trim().isEmpty() && e.select("img").size() >= 1) {
                            List<String> imgs = StringUtil.getMatchString(String.valueOf(e), "<img.*?>");
                            //匹配图片标签
                            for (String img : imgs) {
                                Document ele = Jsoup.parse(img);
                                String picurl = ele.select("img").attr("src");
                                e.select("[src=" + picurl.substring(picurl.lastIndexOf("/") + 1, picurl.lastIndexOf(".")) + "]");
                            }
                            String phtml = Jsoup.parse(String.valueOf(e).replaceAll("<img src=\"", "[mgeimg:https://www.bigdatas.cn/").replaceAll("\">", "]")).text();
                            result.append(phtml + "\n\r");
                        } else//没有图片
                        {
                            result.append(e.text() + "\n\r");
                        }
                }
                art.setOlddetail(new String(result).replaceAll("\" 或 \" \" 菜单 , 选择 \"分享\"", ""));
                art.setStatus(1);
            }catch (Exception e){}
        }
        return artciles;
    }

}
