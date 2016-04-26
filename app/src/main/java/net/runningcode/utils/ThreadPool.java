package net.runningcode.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ThreadPool {
    private ScheduledExecutorService delayExecutor;
    private static ThreadPool ourInstance = new ThreadPool();

    public static ThreadPool getInstance() {
        return ourInstance;
    }

    private static ScheduledExecutorService getExcutor(){
        return getInstance().delayExecutor;
    }
    private ThreadPool() {
        delayExecutor = Executors.newScheduledThreadPool(5);
    }

    public static void submit(Runnable r){
        getExcutor().schedule(r,0, TimeUnit.SECONDS);
    }

    public static void submitDelay(Runnable r,int s){
        getExcutor().schedule(r,s, TimeUnit.SECONDS);
    }

    public static void submitDelayAndRepeat(Runnable r,int s,int times){
        getExcutor().scheduleAtFixedRate(r,s,times,TimeUnit.SECONDS);
    }
}
