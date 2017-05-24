package com.phoenix.social.controller.fragment;

import android.content.Intent;
import android.view.View;

import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.phoenix.social.R;
import com.phoenix.social.controller.activity.AddContactActivity;

/**
 * 联系人列表
 */

public class ContactFragment extends EaseContactListFragment {
    @Override
    protected void initView() {
        super.initView();

        //头布局显示右侧+号
        titleBar.setRightImageResource(R.drawable.em_add);

        //添加头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contact, null);
        listView.addHeaderView(headerView);
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        //添加按钮的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });
    }
}
