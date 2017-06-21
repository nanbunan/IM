package com.example.administrator.im;

import android.app.Application;
import android.content.Context;

import com.example.administrator.im.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by Administrator on 2017/6/4.
 */

public class IMApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化EaseUI
        EMOptions options=new EMOptions();
        options.setAcceptInvitationAlways(false);//设置需要统一后才能接受邀请
       options.setAutoAcceptGroupInvitation(false);//设置需要同意后才能接受群邀请
        EaseUI.getInstance().init(this,options);

        //初始化数据模型层数据
        Model.getInstance().init(this);
        mContext=this;//初始化全局上下文

    }
    //全局上下文对象
    public static Context getGlobalApplication(){
        return mContext;
    }
}
