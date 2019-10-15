package CrawlerArticle;

public class CrawlerRecords {
    public int id;

    public String websource;

    public int tagid;

    public int status;

    public String updatetime;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setWebsource(String websource) {
        this.websource = websource;
    }

    public String getWebsource() {
        return websource;
    }

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdatetime() {
        return updatetime;
    }
}
