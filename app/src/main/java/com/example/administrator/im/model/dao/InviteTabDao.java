package com.example.administrator.im.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.im.model.bean.GroupInfo;
import com.example.administrator.im.model.bean.InvationInfo;
import com.example.administrator.im.model.bean.UserInfo;
import com.example.administrator.im.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class InviteTabDao {
    private DBHelper mHelper;
    public InviteTabDao(DBHelper helper) {
        mHelper=helper;
    }

    //添加邀请
    public void addInvitation(InvationInfo invationInfo){
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行添加语句
        ContentValues values=new ContentValues();
        values.put(InviteTab.COL_REASON,invationInfo.getReason());
        values.put(InviteTab.COL_STATUS,invationInfo.getStatus().ordinal());//枚举的序号从0开始


        UserInfo user=invationInfo.getUser();
        if (user!=null){//联系人
            values.put(InviteTab.COL_USER_HXID,invationInfo.getUser().getHxid());
            values.put(InviteTab.COL_USER_NAME,invationInfo.getUser().getName());
        }else {//群组
            values.put(InviteTab.COL_GROUP_HXID,invationInfo.getGroup().getGroupId());
            values.put(InviteTab.COL_GROUP_NAME,invationInfo.getGroup().getGroupName());
            values.put(InviteTab.COL_USER_HXID,invationInfo.getGroup().getInvitePerson());
        }

        db.replace(InviteTab.TAB_NAME,null,values);

    }

    //获取所有邀请信息
    public List<InvationInfo> getInvitations(){
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询
        String sql="select * from "+InviteTab.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        List<InvationInfo> invationInfos=new ArrayList<>();
        while (cursor.moveToNext()){
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTab.COL_REASON)));
            invationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTab.COL_STATUS))));

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_HXID));
            if (groupId==null){//联系人的邀请信息
                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTab.COL_USER_HXID)));
                invationInfo.setUser(userInfo);

            }else {//群组的邀请信息
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InviteTab.COL_GROUP_HXID)));
                invationInfo.setGroup(groupInfo);

            }
            invationInfos.add(invationInfo);

        }

        //关闭
        cursor.close();
        //返回
        return invationInfos;
    }

    //将int类型转化为邀请的状态
    private InvationInfo.InvitationStatus int2InviteStatus(int intStatus){
        if (intStatus==InvationInfo.InvitationStatus.NEW_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.NEW_INVITE;
        }
        if (intStatus==InvationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()){
            return InvationInfo.InvitationStatus.INVITE_ACCEPT;
        }
        if (intStatus==InvationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }
        if (intStatus==InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()){
            return InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }
        if (intStatus==InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }
        if (intStatus==InvationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }
            return null;
    }

    //删除邀请
    public void removeInvitation(String hxId){
        if (hxId==null){
            return;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.delete(InviteTab.TAB_NAME,InviteTab.COL_USER_HXID,new String[]{hxId});

    }

    //更新邀请状态
    public void updateInvitationStatus(InvationInfo.InvitationStatus invitationStatus,String hxId){
        if (hxId==null){
            return;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put(InviteTab.COL_STATUS,invitationStatus.ordinal());
        db.update(InviteTab.TAB_NAME,values,InviteTab.COL_USER_HXID+"=?",new String[]{hxId});
        //执行更新
    }
}
