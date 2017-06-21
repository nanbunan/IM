package com.example.administrator.im.model.db;

import android.content.Context;

import com.example.administrator.im.model.dao.ContactTableDao;
import com.example.administrator.im.model.dao.InviteTabDao;

/**
 * Created by Administrator on 2017/6/5.
 */
//联系人和邀请信息表的操作类的管理类
public class DBManager {
private final DBHelper dbHelper;
    private final  ContactTableDao contactTableDao;
    private final InviteTabDao inviteTabDao;
    public DBManager(Context context,String name) {
        //创建数据库
        dbHelper = new DBHelper(context, name);
        //创建该数据库两张表中的操作类
         contactTableDao = new ContactTableDao(dbHelper);
         inviteTabDao = new InviteTabDao(dbHelper);
    }
//获取联系人表的操作类对象
    public ContactTableDao getContactTableDao(){
        return contactTableDao;
    }
    //获取邀请人表的操作类
    public InviteTabDao getInviteTabDao(){
        return inviteTabDao;
    }
//关闭数据库的方法
    public void close() {
        dbHelper.close();
    }
}
