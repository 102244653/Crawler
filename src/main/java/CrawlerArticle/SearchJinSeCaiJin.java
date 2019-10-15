package CrawlerArticle;

import HttpUtils.HttpUtil;
import Util.DateUtil;
import Util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchJinSeCaiJin extends PostArticle{
    private static Logger log = LoggerFactory.getLogger(SearchJinSeCaiJin.class);

    static String jscjversion="9.9.9";

    //获取文章列表
    /**  金色财经文章列表查询链接
     * 热度文章查询链接
     *  https://api.jinse.com/v6/information/list?catelogue_key=hot&limit=2&flag=down&version=9.9.9
     *  头条文章查询链接
     *  https://api.jinse.com/v6/information/list?catelogue_key=www&limit=7&information_id=0&flag=down&version=9.9.9
     *  json文章类型--5
     *  catelogue_key --文章类型，hot--热度，www--头条
     *  limit --文章数，50以内
     * */
    public List<Articles> searcjscjhList(String articletype,int size,int tagid) throws Exception {
        List<Articles> articles = new ArrayList<>();
        String res="";
        switch (articletype){
            case "热度":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=hot&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "头条":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=www&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "新闻":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=news&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "行情":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=fenxishishuo&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "政策":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=zhengce&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "投研":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=capitalmarket&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "技术":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=tech&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
            case "百科":
                res=HttpUtil.GetURL("https://api.jinse.com/v6/information/list?catelogue_key=baike&limit="+size+"&information_id=0&flag=down&version="+jscjversion);
                break;
        }
        JSONObject article=JSONObject.parseObject(res);
        JSONArray articlelist=article.getJSONArray("list");
        for (Object art:articlelist) {
            JSONObject artres = (JSONObject) art;
            //判断类型，1为正常文章
            if(artres.getInteger("type") != 1){
                continue;
            }
            //判断文章是否已爬取
            IsCheck is= ArticleMapper.iscrawler(artres.getJSONObject("extra").getInteger("topic_id"),"金色财经");
            if(is!=null){
                log.warn("---金色财经文章【"+artres.getString("title")+"】已存在，自动跳过！---");
                continue;
            }
            Articles jscj=new Articles();
            jscj.setWebsource("金色财经");
            //设置文章标签
            jscj.setTagname(tagid);
            jscj.setNewsid(artres.getJSONObject("extra").getInteger("topic_id"));
            jscj.setArthot(artres.getJSONObject("extra").getInteger("read_number"));
            jscj.setArturl(artres.getJSONObject("extra").getString("topic_url"));
            if(artres.getString("short_title").length()>20){
                jscj.setTitle(artres.getString("short_title").substring(0,20));
            }else {
                jscj.setTitle(artres.getString("short_title"));
            }
            StringBuilder result = new StringBuilder();
            result.append(artres.getJSONObject("extra").getString("summary")+"\n\r");
            result.append("[mgeimg:"+artres.getJSONObject("extra").getString("thumbnail_pic")+"]\n");
            jscj.setOlddetail(new String(result));
            String time=DateUtil.format(DateUtil.DEFAULT_DATE_FORMAT);
            jscj.setCreattime(time);
            articles.add(jscj);

        }
        log.info("本次从【金色财经】共查询到"+articles.size()+"篇<"+articletype+">文章");
        return articles;
    }

    //解析html
    /**  币世界深度文章查询链接
     *   https://www.bishijie.com/shendu_50928
     *   shendu_50928 --文章类型_文章id
     * */
    public  List<Articles> analysisjscjHtml(List<Articles> artcilelist ) throws Exception {
        for(Articles artciles:artcilelist){
            try {
                String html = HttpUtil.GetURL(artciles.getArturl());
                Document document = Jsoup.parse(html);
                Elements detail = document.select("div.js-article-detail");
                StringBuilder result = new StringBuilder(artciles.getOlddetail());
                for (Element e : detail.select("p")) {
                    //只包含一张图片
                    if (e.select("img").size() == 1 && e.text().trim().isEmpty()) {
                        result.append("[mgeimg:" + e.select("img").attr("src") + "]\n");
                    } else //同时包含文章图片
                        if (!e.text().trim().isEmpty() && String.valueOf(e).contains("<img src=")) {
                            List<String> imgs = StringUtil.getMatchString(String.valueOf(e), "<img.*?>");
                            //匹配图片标签
                            for (String img : imgs) {
                                String picurl = Jsoup.parse(img).select("img").attr("src");
                                e.select("[src=" + picurl + "]").removeAttr("title").removeAttr("alt");
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
        return artcilelist;
    }


}
