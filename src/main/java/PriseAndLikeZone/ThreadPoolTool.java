package PriseAndLikeZone;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static BaseApi.InitApi.InitUser;
import static PriseAndLikeZone.LikeVideoOrArticle.basehost;

public class ThreadPoolTool<T> {
    private static Logger log = LoggerFactory.getLogger(ThreadPoolTool.class);

    //单个线程处理的数据量
    private int singleCount;
    //处理数据的总数
    private int listSize;
    //开启的线程数
    private int runSize;
    //操作的数据集
    private List<T> list;
    //计数器
    private CountDownLatch begin,end;
    //线程池
    private ExecutorService executorService;
    //回调方法
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public ThreadPoolTool(int singleCount, List<T> list){
        this.singleCount = singleCount;
        this.list = list;
        if (list != null){
            this.listSize = list.size();
            this.runSize = (this.listSize/this.singleCount) + 1;
        }
    }

    public void excute() throws InterruptedException {
        executorService = Executors.newFixedThreadPool(runSize);
        begin = new CountDownLatch(1);
        end = new CountDownLatch(runSize);
        //创建线程
        int startIndex = 0;
        int endIndex = 0;
        List<T> newList = null;
        for (int i = 0; i < runSize; i++) {
            //计算每个线程池对应的数据
            if (i < (runSize - 1)){
                startIndex = i * singleCount;
                endIndex = (i + 1) * singleCount;
                newList = list.subList(startIndex,endIndex);
            }else {
                startIndex = i * singleCount;
                endIndex = listSize;
                newList = list.subList(startIndex,endIndex);
            }
            //创建线程处理数据
            MyThread<T> myThread = new MyThread(newList, begin, end) {
                @Override
                public void method(List list) {
                    callBack.method(list);
                }
            };
            //执行线程
            executorService.execute(myThread);
        }
        //计数器减1
        begin.countDown();
        end.await();
        //关闭线程池
        executorService.shutdown();
    }

    //抽象线程类
    public abstract class MyThread<T> implements Runnable{

        private List<T> list;
        private CountDownLatch begin,end;

        public MyThread(List<T> list, CountDownLatch begin, CountDownLatch end){
            this.list = list;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                //执行程序
                method(list);
                begin.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                //计数器减一
                end.countDown();
            }
        }
        public abstract void method(List<T> list);
    }

    //回调接口定义
    public interface CallBack<T>{
        public void method(List<T> list);
    }


    //多线程执行动态评论+点赞
    public static void main(String[] agrs){
        List<String> list = new ArrayList<>();
        list=InitUser(1,899,50);//获取用户
        ThreadPoolTool<String> tool = new ThreadPoolTool(10,list);

        tool.setCallBack(new CallBack<String>() {
            @Override
            public void method(List<String> list) {
                LikeVideoOrArticle like = new LikeVideoOrArticle();
                for (int i = 0; i < list.size(); i++) {
                    log.info("线程"+Thread.currentThread().getId()+"分配账号："+list.get(i)+"\n");
                    try {
                        like.likeandreview(String.valueOf(list.get(i)),"000000",basehost);
                    } catch (Exception e) {
                        log.error("\n"+list.get(i)+"操作异常：\n"+e.getMessage());
                    }
                }
            }
        });
        try {
            tool.excute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}