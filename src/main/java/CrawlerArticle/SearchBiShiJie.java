package CrawlerArticle;

import HttpUtils.HttpUtil;
import Util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//币世界深度模块文章列表拉取
public class SearchBiShiJie extends PostArticle{
    private static Logger log = LoggerFactory.getLogger(SearchBiShiJie.class);

    //获取文章列表
    /**  币世界深度文章列表查询链接
     *  https://www.bishijie.com/api/Information/index?page=1&size=30
     *  page --页数，默认为1
     *  size --每页显示的文章数，50以内
     * */
    public  List<Articles> searcbsjhList(int size,int tagid)throws Exception{
        List<Articles> articles = new ArrayList<>();
        String res=HttpUtil.GetURL("https://www.bishijie.com/api/Information/index?page=1&size="+size);
        JSONObject article=JSONObject.parseObject(res);
        JSONArray articlelist =article.getJSONObject("data").getJSONArray("data");
        for (Object art:articlelist) {
            JSONObject artres=(JSONObject) art;
            //判断文章是否已爬取
            IsCheck is= ArticleMapper.iscrawler(artres.getInteger("news_id"),"币世界");
            if(is!=null){
                log.warn("---币世界文章【"+artres.getString("title")+"】已存在，自动跳过！---");
                continue;
            }

            Articles bsj=new Articles();
            bsj.setWebsource("币世界");
            bsj.setTagname(tagid);//设置文章标签
            bsj.setNewsid(artres.getInteger("news_id"));//id
            bsj.setArthot(artres.getInteger("visit"));
            bsj.setArturl("https://www.bishijie.com/shendu_"+artres.getInteger("news_id"));
            if(artres.getString("title").length()>20){
                bsj.setTitle(artres.getString("title").substring(0,20));
            }else {
                bsj.setTitle(artres.getString("title"));
            }
            StringBuilder result = new StringBuilder();
            //获取文章描述和封面图
            result.append(artres.getString("abstract")+"\n");
            result.append("[mgeimg:"+artres.getString("img_url")+"]\n");
            bsj.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            bsj.setCreattime(time);
            articles.add(bsj);
        }
        log.info("本次从【币世界--深度】共查询到"+articles.size()+"篇文章");
        return articles;
    }

    //解析html
    /**  币世界深度文章查询链接
     *   https://www.bishijie.com/shendu_50928
     *   shendu_50928 --文章类型_文章id
     * */
    public  List<Articles> analysisBSJHtml(List<Articles> Artcilelist ) throws Exception {
        for(Articles artciles:Artcilelist ) {
            try {
                String html = HttpUtil.GetURL(artciles.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("div.content");
                StringBuilder result = new StringBuilder(artciles.getOlddetail());
                for (Element e : detail.select("P")) {
                    //只包含一张图片
                    if (e.select("img").size() == 1 && e.text().trim().isEmpty()) {
                        result.append("[mgeimg:" + e.select("img").attr("data-src") + "]\n");
                    } else //同时包含文章图片
                        if (!e.text().trim().isEmpty() && String.valueOf(e).contains("<img src=")) {
                            List<String> imgs = StringUtil.getMatchString(String.valueOf(e), "<img.*?>");
                            //匹配图片标签
                            for (String img : imgs) {
//                    String reg_img="<img.*data-src=\".+(156749345930139).*\".*/>";///<img.*data-src=".+(156749345930139).*".*/>/
                                Document ele = Jsoup.parse(img);
                                String picurl = ele.select("img").attr("data-src");
                                e.select("[data-src=" + picurl + "]").attr("src", picurl).removeAttr("data-src");
                            }
                            String phtml = Jsoup.parse(String.valueOf(e).replaceAll("<img src=\"", "[mgeimg:").replaceAll("\">", "]")).text();
                            result.append(phtml + "\n\r");
                        } else//没有图片
                        {
                            result.append(e.text() + "\n\r");
                        }
                }
                artciles.setOlddetail(new String(result));
                artciles.setStatus(1);
            }catch (Exception e){}
        }
       return Artcilelist;
    }

}
