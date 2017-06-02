package com.phoenix.social.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.phoenix.social.model.bean.GroupInfo;
import com.phoenix.social.model.bean.InvitationInfo;
import com.phoenix.social.model.bean.UserInfo;
import com.phoenix.social.utils.Constant;
import com.phoenix.social.utils.SpUtils;

import java.util.List;

/**
 * 全局事件监听类
 */
public class EventListener {
    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        this.mContext = context;

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }

    //注册一个联系人变化的监听
    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid), true);

            //发送联系人变化的广播————联系人增加
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //联系人删除后执行的方法
        @Override
        public void onContactDeleted(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);

            //发送联系人变化的广播————联系人减少
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //接收到联系人的新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            updateDB(hxid, reason, null, null, null, InvitationInfo.InvitationStatus.NEW_INVITE);//新邀请
            showReadAndSendBroadcast(Constant.CONTACT_INVITE_CHANGED);//发送邀请信息变化的广播
        }
        //别人同意了你的好友邀请
        @Override
        public void onFriendRequestAccepted(String hxid) {
            updateDB(hxid, null, null, null, null, InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);//别人同意了你的邀请
            showReadAndSendBroadcast(Constant.CONTACT_INVITE_CHANGED);//发送邀请信息变化的广播
        }
        //别人拒绝了你的好友邀请
        @Override
        public void onFriendRequestDeclined(String s) {
            showReadAndSendBroadcast(Constant.CONTACT_INVITE_CHANGED);//发送邀请信息变化的广播
        }
    };

    //群信息变化的监听
    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //接收到群组加入邀请
            updateDB(null, reason, groupName, groupId, inviter, InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);//inviter邀请人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
            //用户申请加入群
            updateDB(null, reason, groupName, groupId, applyer, InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);//applyer申请人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            //加群申请被同意
            updateDB(null, null, groupName, groupId, accepter, InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);//accepter接收人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            //加群申请被拒绝
            updateDB(null, reason, groupName, groupId, decliner, InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);//decliner拒绝人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //群组邀请被同意
            updateDB(null, reason, groupId, groupId, inviter, InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);//groupId作为群名称，inviter邀请人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //群组邀请被拒绝
            updateDB(null, reason, groupId, groupId, invitee, InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);//groupId作为群名称，invitee受邀者
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //收到 群成员被删除
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            //收到 群被解散
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            //接收邀请时自动加入到群组的通知
            updateDB(null, inviteMessage, groupId, groupId, inviter, InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);//groupId作为群名称，inviter邀请人
            showReadAndSendBroadcast(Constant.GROUP_INVITE_CHANGED);
        }

        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            //成员禁言的通知
        }

        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            //成员从禁言列表里移除通知
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            //增加管理员的通知
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            //管理员移除的通知
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            //群所有者变动通知
        }
        @Override
        public void onMemberJoined(final String groupId,  final String member){
            //群组加入新成员通知
        }
        @Override
        public void onMemberExited(final String groupId, final String member) {
            //群成员退出通知
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            //群公告变动通知
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            //增加共享文件的通知
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            //群共享文件删除通知
        }
    };

    //数据更新
    private void updateDB(String hxid, String reason, String groupName, String groupId, String inviter, InvitationInfo.InvitationStatus status) {
        InvitationInfo invitationInfo = new InvitationInfo();
        if (hxid != null){
            invitationInfo.setUser(new UserInfo(hxid));
        }
        if (reason != null) {
            invitationInfo.setReason(reason);
        }
        if (groupName != null && groupId != null && inviter != null) {
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));//groupId作为群名称，inviter邀请人
        }
        invitationInfo.setStatus(status);
        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
    }

    private void showReadAndSendBroadcast(String action) {
        //红点处理
        SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        //发送广播
        mLBM.sendBroadcast(new Intent(action));
    }
}
