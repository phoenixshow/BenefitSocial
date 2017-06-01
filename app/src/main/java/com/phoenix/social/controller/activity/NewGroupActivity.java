package com.phoenix.social.controller.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.phoenix.social.R;
import com.phoenix.social.model.Model;

//创建新群
public class NewGroupActivity extends AppCompatActivity {
    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button btn_newgroup_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();
        initListener();
    }

    private void initListener() {
        //创建按钮的点击事件处理
        btn_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到选择联系人界面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取到联系人
        if (resultCode == RESULT_OK){
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    //创建群
    private void createGroup(final String[] members) {
        //群名称
        final String groupName = et_newgroup_name.getText().toString();
        //群描述
        final String groupDesc = et_newgroup_desc.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器创建群
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                options.maxUsers = 200;//群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;
                if (cb_newgroup_public.isChecked()){//公开
                    if (cb_newgroup_invite.isChecked()){//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;//开放群邀请，不需要权限
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;//需要权限
                    }
                }else {
                    if (cb_newgroup_invite.isChecked()){//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;//群成员也可邀请新人
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;//只有群主有权限
                    }
                }
                options.style = groupStyle;//创建群的类型
                try {
                    //参数一：群名称，二：群描述，三：群成员，四：创建群的原因，五：参数设置
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "申请加入群", options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //初始化View
    private void initView() {
        et_newgroup_name = (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText) findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = (CheckBox) findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = (CheckBox) findViewById(R.id.cb_newgroup_invite);
        btn_newgroup_create = (Button) findViewById(R.id.btn_newgroup_create);
    }
}
