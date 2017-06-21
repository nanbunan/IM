package com.example.administrator.im.model;

import android.content.Context;

import com.example.administrator.im.model.bean.UserInfo;
import com.example.administrator.im.model.dao.UserAccountDao;
import com.example.administrator.im.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/6/4.
 */
//数据模型层全局类
public class Model {
    private DBManager dbManager;
   private UserAccountDao userAccountDao;
    private  Context mContext;

    private ExecutorService executors= Executors.newCachedThreadPool();
    //创建对象
    private static Model model=new Model();
    private Model(){}
//获取单例对象
    public static Model getInstance(){
          return model;
    }
//初始化的方法
    public  void init(Context context){
        mContext=context;

        //创建用户数据库
         userAccountDao = new UserAccountDao(mContext);

        //开启全局监听
        new EventListener(mContext);
    }
    //获取全局线程池
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }

    public void loginSuccess(UserInfo account) {
        if (account==null){
            return;
        }
        if (dbManager!=null){
            dbManager.close();
        }
        //用户登录成功后的处理方法
        dbManager = new DBManager(mContext,account.getHxid() );
    }
    public DBManager getDbManager(){
        return dbManager;
    }
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

}
