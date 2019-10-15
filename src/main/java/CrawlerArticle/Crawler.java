package CrawlerArticle;

import Util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private static Logger log = LoggerFactory.getLogger(Crawler.class);
    static String emaildetail;

    //币世界文章数
    public static int bsj=20;
    //金色财经热度文章数
    public static int jscj_hot=10;
    //金色财经头条文章数
    public static int jscj_www=10;
    //金色财经新闻文章数
    public static int jscj_news=10;
    //金色财经行情文章数
    public static int jscj_hangqing=10;
    //金色财经政策文章数
    public static int jscj_zhengce=10;
    //金色财经技术文章数
    public static int jscj_jishu=10;


    //随机读取本次爬虫网站配置信息
    public List<CrawlerRecords> getsource()throws Exception{
        List<CrawlerRecords> websource=ArticleMapper.selectwebsource(DateUtil.format(DateUtil.COMMON_DATE_FORMAT));
        if(websource==null){
            emaildetail+="\n今日所有网站已爬取完毕！";
            log.info("今日所有网站已爬取完毕！");
            return null;
        }
        return websource;
    }

    //每天随机爬网页文字
    public static void main(String[] args)throws Exception{
        Crawler init=new Crawler();
        List<CrawlerRecords> resource=init.getsource();
        if(resource==null){return;}
        for(CrawlerRecords re:resource){
            switch (re.getWebsource()){
                case "币世界":
                    init.getfrombishijie(bsj,re.getTagid());
                    break;
                case "金色财经热度":
                    init.getfromjscj("热度",jscj_hot,re.getTagid());
                    break;
                case "金色财经头条":
                    init.getfromjscj("头条",jscj_www,re.getTagid());
                    break;
                case "金色财经新闻":
                    init.getfromjscj("新闻",jscj_news,re.getTagid());
                    break;
                case "金色财经行情":
                    init.getfromjscj("行情",jscj_hangqing,re.getTagid());
                    break;
                case "金色财经政策":
                    init.getfromjscj("政策",jscj_zhengce,re.getTagid());
                    break;
                case "金色财经技术":
                    init.getfromjscj("技术",jscj_jishu,re.getTagid());
                    break;
                case "56财经":
                    init.getfrom56cj(re.getTagid());
                    break;
                case "投资界":
                    init.getfromTZJ(re.getTagid());
                    break;
                case "投中网":
                    init.getfromTZW(re.getTagid());
                    break;
                case "大数据中国":
                    init.getfromDSJZG(re.getTagid());
                    break;
                case "中国数谷":
                    init.getfromZgsg(re.getTagid());
                    break;
                case "IT199":
                    init.getfromIT199(re.getTagid());
                    break;
                case "和讯理财":
                    init.getfromHexunlicai(re.getTagid());
                    break;
                case "天气加":
                    init.getfromTqjia(re.getTagid());
                    break;
//                case "慢钱头条":
//                    init.getfromMqtt(re.getTagid());
//                    break;
                case "今日头条热点":
                    init.getfromJinritoutiao("热点",re.getTagid());
                    break;
                case "今日头条科技":
                    init.getfromJinritoutiao("科技",re.getTagid());
                    break;
                case "今日头条游戏":
                    init.getfromJinritoutiao("游戏",re.getTagid());
                    break;
                case "今日头条体育":
                    init.getfromJinritoutiao("体育",re.getTagid());
                    break;
                case "今日头条财经":
                    init.getfromJinritoutiao("财经",re.getTagid());
                    break;
                case "今日头条时尚":
                    init.getfromJinritoutiao("时尚",re.getTagid());
                    break;
                case "今日头条搞笑":
                    init.getfromJinritoutiao("搞笑",re.getTagid());
                    break;
                case "今日头条美食":
                    init.getfromJinritoutiao("美食",re.getTagid());
                    break;
                case "今日头条旅游":
                    init.getfromJinritoutiao("旅游",re.getTagid());
                    break;
                case "今日头条军事":
                    init.getfromJinritoutiao("军事",re.getTagid());
                    break;
                case "今日头条养生":
                    init.getfromJinritoutiao("养生",re.getTagid());
                    break;
                case "今日头条育儿":
                    init.getfromJinritoutiao("育儿",re.getTagid());
                    break;
                case "今日头条娱乐":
                    init.getfromJinritoutiao("娱乐",re.getTagid());
                    break;
                case "糗事百科":
                    init.getfromqsbk(re.getTagid());
                    break;
                case "美妆":
                    init.getfromMz(re.getTagid());
                    break;
                default:
                    break;
            }
        }
    }

    //更新check表
    public void addcheck(Articles a)throws Exception{
        IsCheck check=new IsCheck();
        check.setWebsource(a.getWebsource());
        check.setNewsid(a.getNewsid());
        check.setUpdatetime(DateUtil.format(DateUtil.SHORT_DATE_FORMAT));
        check.setStatus(0);
        ArticleMapper.insertischeck(check);
    }

    //更新records表
    public void records(String websource)throws Exception{
        CrawlerRecords records=new CrawlerRecords();
        records.setWebsource(websource);
        records.setUpdatetime(DateUtil.format(DateUtil.COMMON_DATE_FORMAT));
        ArticleMapper.upwebsource(records);
    }

    //获取币世界文章
    public void getfrombishijie(int size,int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchBiShiJie bishijie=new SearchBiShiJie();
            arts=bishijie.searcbsjhList(size,tagid);
            arts=bishijie.analysisBSJHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                //更新check表
                this.addcheck(a);
            }
            this.records("币世界");
            emaildetail+="\n成功获取币世界深度文章"+arts.size()+"条";
            log.info("成功获取币世界深度文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取币世界深度文章失败，原因：\n"+e.getMessage();
            log.error("\n获取币世界深度文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取金色财经文章
    public void getfromjscj(String articletype,int size,int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchJinSeCaiJin jscj=new SearchJinSeCaiJin();
            arts=jscj.searcjscjhList(articletype,size,tagid);
            arts=jscj.analysisjscjHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("金色财经"+articletype);
            emaildetail+="\n成功获取金色财经"+articletype+"文章"+arts.size()+"条";
            log.info("成功获取金色财经"+articletype+"文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取金色财经"+articletype+"文章失败，原因：\n"+e.getMessage();
            log.error("\n获取金色财经"+articletype+"文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取56财经文章
    public void getfrom56cj( int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            Search56CJ cj56=new Search56CJ();
            arts=cj56.searc56cjhList(tagid);
            arts=cj56.analysis56CJHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("56财经");
            emaildetail+="\n成功获取56财经文章"+arts.size()+"条";
            log.info("成功获取56财经文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取56财经文章失败，原因：\n"+e.getMessage();
            log.error("\n获取56财经文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取投资界文章
    public void getfromTZJ(int tagid )throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchTouzijie tzj=new SearchTouzijie();
            arts=tzj.searcbstzjList(tagid);
            arts=tzj.analysisTzjHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("投资界");
            emaildetail+="\n成功获取投资界文章"+arts.size()+"条";
            log.info("成功获取投资界文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取投资界文章失败，原因：\n"+e.getMessage();
            log.error("\n获取投资界文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取投中网文章
    public void getfromTZW( int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchTouzhongwang tzj=new SearchTouzhongwang();
            arts=tzj.searcbstzwList(tagid);
            arts=tzj.analysisTZWHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("投中网");
            emaildetail+="\n成功获取投中网文章"+arts.size()+"条";
            log.info("成功获取投中网文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取投中网文章失败，原因：\n"+e.getMessage();
            log.error("\n获取投中网文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取大数据中国文章
    public void getfromDSJZG( int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchDashujuzhongguo dsj=new SearchDashujuzhongguo();
            arts=dsj.searcbsDsjzgList(tagid);
            arts=dsj.analysisDsjzgHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("大数据中国");
            emaildetail+="\n成功获取大数据中国文章"+arts.size()+"条";
            log.info("成功获取大数据中国文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取投大数据中国失败，原因：\n"+e.getMessage();
            log.error("\n获取投大数据中国失败，原因：\n"+e.getMessage());
        }
    }

    //获取中国数谷文章
    public void getfromZgsg(int tagid )throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchChinashugu Zgsg=new SearchChinashugu();
            arts=Zgsg.searcbsZgsgList(tagid);
            arts=Zgsg.analysisZgsgHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("中国数谷");
            emaildetail+="\n成功获取中国数谷文章"+arts.size()+"条";
            log.info("成功获取中国数谷文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取中国数谷文章失败，原因：\n"+e.getMessage();
            log.error("\n获取中国数谷文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取IT199文章
    public void getfromIT199( int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchIT199 it199=new SearchIT199();
            arts=it199.searcbsIT199List(tagid);
            arts=it199.analysisIT199Html(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("IT199");
            emaildetail+="\n成功获取IT199文章"+arts.size()+"条";
            log.info("成功获取IT199文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取IT199文章失败，原因：\n"+e.getMessage();
            log.error("\n获取IT199文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取和讯理财文章
    public void getfromHexunlicai(int tagid )throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchHexunlicai hxlc=new SearchHexunlicai();
            arts=hxlc.searcbsHxlcList(tagid);
            arts=hxlc.analysisHxlcHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("和讯理财");
            emaildetail+="\n成功获取和讯理财文章"+arts.size()+"条";
            log.info("成功获取和讯理财文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取和讯理财文章失败，原因：\n"+e.getMessage();
            log.error("\n获取和讯理财文章失败，原因：\n"+e.getMessage());
        }
    }

//    //获取慢钱头条文章
//    public void getfromMqtt( int tagid)throws Exception{
//        List<Articles> arts=new ArrayList<>();
//        try{
//            SearchManqian mqtt=new SearchManqian();
//            arts=mqtt.searcbsMqttList(tagid);
//            arts=mqtt.analysisMqttHtml(arts);
//            for(Articles a:arts){
//                if(a.getOlddetail().contains("mgeimg:http")){
//                    ArticleMapper.addarticle(a);
//                }
//                this.addcheck(a);
//            }
//            this.records("慢钱头条");
//            emaildetail+="\n成功获取慢钱头条文章"+arts.size()+"条";
//            log.info("成功获取慢钱头条文章"+arts.size()+"条");
//        }catch (Exception e){
//            emaildetail+="\n获取慢钱头条文章失败，原因：\n"+e.getMessage();
//            log.error("\n获取慢钱头条文章失败，原因：\n"+e.getMessage());
//        }
//    }

    //获取天气加旅游文章
    public void getfromTqjia(int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchTianqjia tqj=new SearchTianqjia();
            arts=tqj.searcbsTqjList(tagid);
            arts=tqj.analysisTqjHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("天气加");
            emaildetail+="\n成功获取天气加文章"+arts.size()+"条";
            log.info("成功获取天气加文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取天气加文章失败，原因：\n"+e.getMessage();
            log.error("\n获取天气加文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取今日头条文章
    public void getfromJinritoutiao(String type,int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchToutiao jrtt=new SearchToutiao();
            arts=jrtt.searchTouTiaoList(type,tagid);
            arts=jrtt.analysisJRTTHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("今日头条"+type);
            emaildetail+="\n成功获取今日头条-"+type+"文章"+arts.size()+"条";
            log.info("成功获取今日头条-"+type+"文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取今日头条-"+type+"文章失败，原因：\n"+e.getMessage();
            log.error("\n获取今日头条-"+type+"文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取糗事百科文章
    public void getfromqsbk(int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchQiushibaike qsbk=new SearchQiushibaike();
            arts=qsbk.searcbsQsbkList(tagid);
            arts=qsbk.analysisqsbkHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("糗事百科");
            emaildetail+="\n成功获取糗事百科文章"+arts.size()+"条";
            log.info("成功获取糗事百科文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取糗事百科文章失败，原因：\n"+e.getMessage();
            log.error("\n获取糗事百科文章失败，原因：\n"+e.getMessage());
        }
    }

    //获取美妆文章
    public void getfromMz(int tagid)throws Exception{
        List<Articles> arts=new ArrayList<>();
        try{
            SearchMeizhuang mz=new SearchMeizhuang();
            arts=mz.searchMZList(tagid);
            arts=mz.analysisBSJHtml(arts);
            for(Articles a:arts){
                if(a.getOlddetail().contains("mgeimg:http")){
                    ArticleMapper.addarticle(a);
                }
                this.addcheck(a);
            }
            this.records("美妆");
            emaildetail+="\n成功获取美妆文章"+arts.size()+"条";
            log.info("成功获取美妆文章"+arts.size()+"条");
        }catch (Exception e){
            emaildetail+="\n获取美妆文章失败，原因：\n"+e.getMessage();
            log.error("\n获取美妆文章失败，原因：\n"+e.getMessage());
        }
    }
}
