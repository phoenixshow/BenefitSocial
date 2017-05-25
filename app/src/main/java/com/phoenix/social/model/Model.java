package com.phoenix.social.model;

import android.content.Context;

import com.phoenix.social.model.bean.UserInfo;
import com.phoenix.social.model.dao.UserAccountDao;
import com.phoenix.social.model.db.DBManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据模型层全局类
 */
public class Model {
    private Context mContext;
    private UserAccountDao userAccountDao;
    private ExecutorService executors = Executors.newCachedThreadPool();//线程池分四种，这种模式时间比较短，如果线程长时间不用就回收了
    //创建对象
    private static Model model = new Model();
    private DBManager dbManager;

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
        //创建用户账号数据库的操作类对象
        userAccountDao = new UserAccountDao(mContext);
        //开启全局监听
        EventListener eventListener = new EventListener(mContext);
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }

    //用户登录成功后的处理方法
    public void loginSuccess(UserInfo account) {
        //校验
        if (account == null){
            return;
        }

        if (dbManager != null){
            dbManager.close();
        }

        dbManager = new DBManager(mContext, account.getName());
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    //获取用户账号数据库的操作类对象
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }
}
