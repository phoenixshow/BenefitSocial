package com.phoenix.social.controller.activity;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.phoenix.social.R;
import com.phoenix.social.controller.fragment.ChatFragment;
import com.phoenix.social.controller.fragment.ContactFragment;
import com.phoenix.social.controller.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_main_chat://会话列表页面
                        fragment = chatFragment;
                        break;
                    case R.id.rb_main_contact://联系人列表页面
                        fragment = contactFragment;
                        break;
                    case R.id.rb_main_setting://设置页面
                        fragment = settingFragment;
                        break;
                }

                //实现Fragment切换的方法
                switchFragment(fragment);
            }
        });

        //默认选择会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initData() {
        //创建三个Fragment对象
        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        settingFragment = new SettingFragment();
    }

    private void initView() {
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
    }
}
