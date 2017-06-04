package com.phoenix.social.controller.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.phoenix.social.R;
import com.phoenix.social.controller.adapter.GroupDetailAdapter;
import com.phoenix.social.model.Model;
import com.phoenix.social.model.bean.UserInfo;
import com.phoenix.social.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static com.hyphenate.easeui.model.EaseDefaultEmojiconDatas.getData;

//群详情页面
public class GroupDetailActivity extends AppCompatActivity {
    private GridView gv_groupdetail;
    private Button btn_groupdetail_out;
    private EMGroup mGroup;
    private List<UserInfo> mUsers;
    private GroupDetailAdapter groupDetailAdapter;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {

        }

        @Override
        public void onDeleteMembers(UserInfo user) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();
        getData();
        initData();
    }

    private void initData() {
        //初始化Button显示
        initButtonDisplay();

        //初始化GridView
        initGridView();

        //从环信服务器获取所有群成员
        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器获取所有群成员
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    List<String> members = emGroup.getMembers();

                    if (members != null && members.size() >= 0){
                        mUsers = new ArrayList<UserInfo>();

                        //转换
                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新适配器
                            groupDetailAdapter.refresh(mUsers);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        //当前用户是群主 || 群公开了
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);
        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        //判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {//群主
            btn_groupdetail_out.setText("解散群");
            btn_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送退群的广播
                                exitGroupBroatCase();
                                
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else {//群成员
            btn_groupdetail_out.setText("退群");
            btn_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //告诉环信服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                //发送退群广播
                                exitGroupBroatCase();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    //发送退群和解散群广播
    private void exitGroupBroatCase() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    //获取传递过来的数据
    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);

        if (groupId == null){
            return;
        }else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    private void initView() {
        gv_groupdetail = (GridView) findViewById(R.id.gv_groupdetail);
        btn_groupdetail_out = (Button) findViewById(R.id.btn_groupdetail_out);
    }
}