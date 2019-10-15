package PriseAndLikeZone;

import Util.RandomNum;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.HashMap;

public class commentdata {
    public static  HashMap<String ,String[]> videocomment;
    public static  HashMap<String ,String[]> articlecomment;

    public commentdata() {
        videocomment=this.readtxt(System.getProperty("user.dir")+"/src/main/resources/video.txt");
        articlecomment=this.readtxt(System.getProperty("user.dir")+"/src/main/resources/article.txt");
    }
    /**
     * type 5-视频；0-文章
     * tag  标签名
     * */
    public  String sendcomment(int type,String tag){
        String commentvalue=""; String[] values={};
        switch (type){
            case 5:
                if(!videocomment.containsKey(tag)){
                    tag="其他";
                }
                values=videocomment.get(tag);
                commentvalue=values[Math.toIntExact(new RandomNum().getNumRandom(0, values.length))];
                break;
            case 0:
                if(!articlecomment.containsKey(tag)){
                    tag="其他";
                }
                values=articlecomment.get(tag);
                commentvalue=values[Math.toIntExact(new RandomNum().getNumRandom(0, values.length))];
                break;
        }
        return commentvalue;
    }

    //读取txt评论文本
    public HashMap<String, String[]> readtxt(String filePath) {
        HashMap<String ,String[]> comment =new HashMap<String ,String[]>();
        String text = "";
        File filename = new File(filePath);
        InputStreamReader reader=null;
        BufferedReader br=null;
        if (filename.exists() == true) {
            try {
                reader= new InputStreamReader(new FileInputStream(filename));
                br= new BufferedReader(reader);
                String tag=null;
                String[] values=new String[]{};
                while (!(text = br.readLine()).trim().contains("end")){
                    if(text.trim().length()<=0){
                        continue;
                    }
                    if(text.startsWith("#")){
                        tag = text.replace("#", "").trim();
                    }else if(text.equals("$$")){
                        comment.put(tag,values);
                        values=new String[]{};
                    } else {
                        values=(String[]) arrayGrow(values);
                        values[values.length-1]=text.trim();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(br!=null){
                    try{
                        br.close();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return comment;
    }
    //自增数组
    public Object arrayGrow(Object o) {
        Class cl = o.getClass();
        if (!cl.isArray()) return null;
        Class componentType = cl.getComponentType();
        int length = Array.getLength(o);
        int newLength = length +1;
        Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(o, 0, newArray, 0, length);
        return newArray;
    }

}
