package com.phoenix.social.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.chat.EMClient;
import com.phoenix.social.R;
import com.phoenix.social.model.Model;
import com.phoenix.social.model.bean.UserInfo;

/**
 * 欢迎页面
 */

public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //如果当前Activity已经退出，那么就不处理Handler中的消息
            if (isFinishing()){
                return;
            }

            //判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    //判断进入主页面还是登录页面
    private void toMainOrLogin() {
        /*//开启子线程
        new Thread(){
            @Override
            public void run() {
                //判断当前账号是否已经登录过
                if (EMClient.getInstance().isLoggedInBefore()){//登录过
                    //获取到当前登录用户的信息

                    //跳转到主页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {//没登录过
                    //跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                //结束当前页面
                finish();
            }
        }.start();*/

        //使用线程池
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前账号是否已经登录过
                if (EMClient.getInstance().isLoggedInBefore()){//登录过
                    //获取到当前登录用户的信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    if (account == null){
                        //跳转到登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        //登录成功后的方法
                        Model.getInstance().loginSuccess(account);

                        //跳转到主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }else {//没登录过
                    //跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                //结束当前页面
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //发送2S钟的延时消息
        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
