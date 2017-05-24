package com.phoenix.social.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.phoenix.social.R;
import com.phoenix.social.model.Model;
import com.phoenix.social.model.bean.UserInfo;

//登录页面
public class LoginActivity extends AppCompatActivity {
    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button btn_login_register;
    private Button btn_login_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        initView();
        
        //初始化监听
        initListener();
    }

    private void initListener() {
        //注册按钮的点击事件处理
        btn_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //登录按钮的点击事件处理
        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    //登录按钮的业务逻辑处理
    private void login() {
        //1、获取输入的用户名和密码
        final String loginName = et_login_name.getText().toString().trim();
        final String loginPwd = et_login_pwd.getText().toString().trim();

        //2、校验输入的用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)){
            Toast.makeText(LoginActivity.this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //3、登录逻辑处理
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器登录
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    //登录成功后的处理
                    @Override
                    public void onSuccess() {
                        //对模型层数据的处理
                        Model.getInstance().loginSuccess();

                        //保存用户账号信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //提示登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                //跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    //登录失败后的处理
                    @Override
                    public void onError(int i, final String s) {
                        //提示登录失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败"+s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //登录过程中的处理
                    @Override
                    public void onProgress(int i, String s) {
                    }
                });
            }
        });
    }

    //注册按钮的业务逻辑处理
    private void register() {
        //1、获取输入的用户名和密码
        final String registerName = et_login_name.getText().toString().trim();
        final String registerPwd = et_login_pwd.getText().toString().trim();

        //2、校验输入的用户名和密码
        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPwd)){
            Toast.makeText(LoginActivity.this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //3、去服务器注册账号
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);// 进度条
        pd.setMessage("正在注册中");
        pd.show();

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //去环信服务器注册账号
                    EMClient.getInstance().createAccount(registerName, registerPwd);

                    //更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    pd.cancel();//隐藏pd
                }
            }
        });
    }

    private void initView() {
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        btn_login_register = (Button) findViewById(R.id.btn_login_register);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
    }
}
