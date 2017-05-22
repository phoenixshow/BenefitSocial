package com.phoenix.social.model;

import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据模型层全局类
 */
public class Model {
    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();//线程池分四种，这种模式时间比较短，如果线程长时间不用就回收了

    //创建对象
    private static Model model = new Model();

    //私有化构造
    private Model() {
    }

    //获取单例对象
    public static Model getInstance(){
        return model;
    }

    //初始化的方法
    public void init(Context context){
        this.mContext = context;
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }
}
