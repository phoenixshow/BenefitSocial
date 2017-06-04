package com.phoenix.social.controller.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.phoenix.social.R;
import com.phoenix.social.model.Model;
import com.phoenix.social.model.bean.UserInfo;

import java.util.ArrayList;

/**
 * 欢迎页面
 */

public class SplashActivity extends AppCompatActivity {
    private boolean isGrant = true;
    private final int SDK_PERMISSION_REQUEST = 127;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //如果当前Activity已经退出，那么就不处理Handler中的消息
            if (isFinishing()){
                return;
            }
            getPersimmions();
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

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.CAMERA);
            }
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }else {
                //判断进入主页面还是登录页面
                toMainOrLogin();
            }
        }else {
            //判断进入主页面还是登录页面
            toMainOrLogin();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case SDK_PERMISSION_REQUEST:
                if (grantResults.length>0){
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            isGrant = false;
                            break;
                        }
                    }
                }else{
                    isGrant = false;
                }
                if (isGrant){
                    // 允许
                    //判断进入主页面还是登录页面
                    toMainOrLogin();
                }else {
                    // 不允许
                    Toast.makeText(this, "已拒绝授权", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
