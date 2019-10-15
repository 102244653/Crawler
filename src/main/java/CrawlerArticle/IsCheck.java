package CrawlerArticle;

public class IsCheck {
    public int id;
    public String websource;
    public int newsid;
    public String updatetime;
    public int status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNewsid(int newsid) {
        this.newsid = newsid;
    }

    public int getNewsid() {
        return newsid;
    }

    public String getWebsource() {
        return websource;
    }

    public void setWebsource(String websource) {
        this.websource = websource;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
