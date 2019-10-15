package CrawlerArticle;

import Util.DateUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ArticleMapper {
    private static Logger log = LoggerFactory.getLogger(ArticleMapper.class);

    public static SqlSession getSqlSession() throws IOException {
        //获取配置的资源文件
        Reader reader = Resources.getResourceAsReader("database.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        //sqlSession就是能够执行配置文件中的sql语句。
        SqlSession sqlSession = factory.openSession(true);
        return sqlSession;
    }

    //判断文章是否已爬取【Y】
    public  static IsCheck  iscrawler(int newsid,String websource)throws Exception{
        IsCheck is=new IsCheck();
        is.setNewsid(newsid);is.setWebsource(websource);
        IsCheck isnew=getSqlSession().selectOne("selectcheck",is);
        return isnew;
    }

    //根据newsid/websource查询文章【X】
    public static Articles selectarrticle(int newsid,String websource)throws Exception{
        Articles is=new Articles();
        is.setNewsid(newsid);is.setWebsource(websource);
        Articles isnew;
        try {
            isnew=getSqlSession().selectOne("selectarticle",is);
        }catch (Exception e){
            isnew=null;
        }
        return isnew;
    }

    //根据id读取文章【Y】
    public static Articles getarticle(int id)throws Exception{
        Articles is=new Articles();
        is.setId(id);
        Articles isnew;
        try {
            isnew=getSqlSession().selectOne("selectarticlebyid",is);
        }catch (Exception e){
            isnew=null;
        }
        return isnew;
    }

    //根据状态查询文章id【Y】
    public static List<Articles> selectarticleid(int status)throws Exception{
        return getSqlSession().selectList("selectarticlebystatus",status);
    }

    //查询未发布的文章【Y】
    public static List<Articles> selectarticleby12()throws Exception {
        return getSqlSession().selectList("selectarticleby12");
    }

        //新增文章【Y】
    public static void addarticle(Articles articles)throws Exception {
        getSqlSession().insert("insertarticle", articles);
//        getSqlSession().commit();
        Thread.sleep(1000);
    }

    //修改文章内容和状态【Y】
    public static void uparticledetail(Articles articles)throws Exception{
        getSqlSession().update("updatearticle",articles);
    }

    //修改文章状态【Y】
    public static void uparticlestatus(Articles articles)throws Exception{
        getSqlSession().update("updatearticlestatus",articles);
        getSqlSession().commit();
    }

    //更新网站爬取时间【Y】
    public static void upwebsource(CrawlerRecords crawlerRecords)throws Exception{
        getSqlSession().update("updaterecord",crawlerRecords);
        getSqlSession().commit();
    }

    //查询未爬取过的网站【Y】
    public static List<CrawlerRecords> selectwebsource(String date)throws Exception{
        CrawlerRecords crawlerRecords=new CrawlerRecords();
        crawlerRecords.setUpdatetime(date);
       return getSqlSession().selectList("selectrecord",crawlerRecords);
    }

    //更新文章爬取记录【Y】
    public static void upischeck(IsCheck isCheck)throws Exception{
        getSqlSession().update("updatecheck",isCheck);
        getSqlSession().commit();
    }

    //插入文章爬取记录【Y】
    public static int insertischeck(IsCheck isCheck)throws Exception{
       int id= getSqlSession().insert("insertcheck",isCheck);
//        getSqlSession().commit();
        Thread.sleep(1000);
        return id;
    }

    //更新接口版本
    public static void updateversion(appversion appversion)throws Exception{
        getSqlSession().update("updateappversion",appversion);
        getSqlSession().commit();
    }

    //获取接口版本
    public static String selectversion(appversion appversion)throws Exception{
        return  getSqlSession().selectOne("selectappversion",appversion);
    }
}
