package Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TxtUtil {
    private static Logger log = LoggerFactory.getLogger(TxtUtil.class);

    //读取
    public static StringBuilder readtxt(String path)   {
        BufferedReader bufferedReader = null;
        //result用来存储文件内容
        StringBuilder result = new StringBuilder();
        try {
            //构造一个BufferedReader类来读取文件
            bufferedReader = new BufferedReader(new FileReader(new File(path)));
            String linetxt = null;
            //按使用readLine方法，一次读一行
            while ((linetxt = bufferedReader.readLine()) != null) {
                result.append(linetxt+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //输出读出的所有数据（StringBuilder类型）
        return result;
    }

    //写入
    public static void writetxt(String path,String text){
        new FileUtil().creatFile(path);
        try {
            File filename = new File(path);
            //覆盖
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(text+"\n");
            out.flush();
            out.close();
        }catch (Exception e){
            log.error("写入失败："+text);
            e.printStackTrace();
        }
    }

    public static void addtxt(String path,String text){
        new FileUtil().creatFile(path);
        try {
            File filename = new File(path);
            //追加
            BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
            out.write(text+"\n");
            out.flush();
            out.close();
        }catch (Exception e){
            log.error("写入失败："+text);
            e.printStackTrace();
        }
    }

}
