package com.phoenix.social.controller.fragment;

import android.view.View;

import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.phoenix.social.R;

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
}
