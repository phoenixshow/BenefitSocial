package com.phoenix.social.controller.fragment;

import android.content.Intent;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.phoenix.social.controller.activity.ChatActivity;

import java.util.List;

/**
 * 会话列表
 */
public class ChatFragment extends EaseConversationListFragment {
    @Override
    protected void initView() {
        super.initView();
        //跳转到会话详情页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //传递参数，会话的ID也就是环信ID
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());
                //是否是群聊
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat){
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });

        //清空集合数据（针对低于5.0版本的模拟器出现重复会话条目的情况）
        conversationList.clear();

        //监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        //接收到消息
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            //设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);

            //刷新页面
            refresh();
        }
        //接收到Cmd消息
        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
        }
        //接收到新的已读回执
        @Override
        public void onMessageRead(List<EMMessage> list) {
        }
        //接收到新的发送回执
        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }
        //接收到消息的状态改变
        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }
    };
}
