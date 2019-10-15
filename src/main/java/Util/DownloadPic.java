package Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.net.URL;

public class DownloadPic {
    private static Logger log = LoggerFactory.getLogger(DownloadPic.class);

    //文章图片存放列表
    public static String path=System.getProperty("user.dir")+"/Article/";

    //链接url下载图片
    public static String downloadPicture(String urlList,String picname) throws Exception{
        URL url = null;
        new FileUtil().creatDir(path);
        if(picname==null){
            picname=(System.currentTimeMillis() / 1000)+".png";
        }
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path+picname));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            log.info("图片"+urlList+"下载完毕");
            return path+picname;
        } catch (MalformedURLException e) {
            log.info("---图片"+urlList+"下载失败---");
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            log.info("---图片"+urlList+"下载失败---");
            e.printStackTrace();
            return "";
        }
    }


}
