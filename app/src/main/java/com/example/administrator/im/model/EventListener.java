package com.example.administrator.im.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.administrator.im.model.bean.GroupInfo;
import com.example.administrator.im.model.bean.InvationInfo;
import com.example.administrator.im.model.bean.UserInfo;
import com.example.administrator.im.utils.Constant;
import com.example.administrator.im.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/6/5.
 */
//全局事件监听类
public class EventListener {
    private Context mContext;
  private  LocalBroadcastManager mLBM;
    public EventListener(Context mContext) {
        this.mContext = mContext;
        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //注册群变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangedListener);
    }
    private final EMContactListener emContactListener=new EMContactListener() {
        //联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {

            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid),true);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //联系人删除后执行的方法
        @Override
        public void onContactDeleted(String s) {


            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(s);
            Model.getInstance().getDbManager().getInviteTabDao().removeInvitation(s);

            //发送联系人变化广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //接收到联系人的新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {


            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);//新邀请
            Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
        //别人同意了你的好友邀请
        @Override
        public void onContactAgreed(String s) {
            //数据更新
            InvationInfo invitationInfo=new InvationInfo();
            invitationInfo.setUser(new UserInfo(s));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);//别人同意了你的邀请
            Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
        //别人拒绝了你的好友邀请
        @Override
        public void onContactRefused(String s) {

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));


        }
    };
    //群组变化的监听
   private EMGroupChangeListener eMGroupChangedListener=new EMGroupChangeListener() {
        //收到群邀请
       @Override
       public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据更新
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(reason);
           invitationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
       }
//收到 群申请通知
       @Override
       public void onApplicationReceived(String groupId, String groupName, String applicant, String reason) {
           //数据更新
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(reason);
           invitationInfo.setGroup(new GroupInfo(groupName,groupId,applicant));
            invitationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
            //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
       }
//收到 群申请被接受
       @Override
       public void onApplicationAccept(String groupId, String groupName, String accepter) {
           //更新数据
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

       }
//收到 群申请被拒绝
       @Override
       public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
           //更新数据
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(reason);
           invitationInfo.setGroup(new GroupInfo(groupName,groupId,decliner));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
       }
//收到 群邀请被同意
       @Override
       public void onInvitationAccepted(String groupId, String inviter, String reason) {
           //更新数据
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(reason);
           invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

       }
//收到 群邀请被邀请
       @Override
       public void onInvitationDeclined(String groupId, String inviter, String reason) {
           //更新数据
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(reason);
           invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

       }
//群成员被删除
       @Override
       public void onUserRemoved(String s, String s1) {

       }
//群被解散
       @Override
       public void onGroupDestroyed(String s, String s1) {

       }
//收到群邀请被自动接受
       @Override
       public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
           //更新数据
           InvationInfo invitationInfo=new InvationInfo();
           invitationInfo.setReason(inviteMessage);
           invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
           invitationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
           Model.getInstance().getDbManager().getInviteTabDao().addInvitation(invitationInfo);
           //红点处理
           SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

           //发送广播
           mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

       }
   };
}
