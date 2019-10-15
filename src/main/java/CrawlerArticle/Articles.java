package CrawlerArticle;

public class Articles {
    //文章id
    public int id;
    //文章来源平台
    public String websource;
    //文章原平台id
    public int newsid;
    //文章原链接
    public String arturl;
    //文章热度
    public int arthot;
    //文章所属脉果儿标签
    public int tagname;
    //文章标题
    public String title;
    //原文章详情
    public String olddetail;
    //格式化之后的文章详情
    public String newdetail;
    //创建时间
    public String creattime;
    //文章状态
    public int status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOlddetail() {
        return olddetail;
    }

    public void setOlddetail(String olddetail) {
        this.olddetail = olddetail;
    }

    public String getNewdetail() {
        return newdetail;
    }

    public void setNewdetail(String newdetail) {
        this.newdetail = newdetail;
    }

    public void delDetail(){
        this.setOlddetail("");
    }

    public int getNewsid() {
        return newsid;
    }

    public void setNewsid(int newsid) {
        this.newsid = newsid;
    }

    public void setWebsource(String websource) {
        this.websource = websource;
    }

    public String getWebsource() {
        return websource;
    }

    public String getArturl() {
        return arturl;
    }

    public void setArturl(String arturl) {
        this.arturl = arturl;
    }

    public int getArthot() {
        return arthot;
    }

    public void setArthot(int arthot) {
        this.arthot = arthot;
    }

    public int getTagname() {
        return tagname;
    }

    public void setTagname(int tagname) {
        this.tagname = tagname;
    }

    public void setCreattime(String creattime) {
        this.creattime = creattime;
    }

    public String getCreattime() {
        return creattime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
