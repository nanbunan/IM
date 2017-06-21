package com.example.administrator.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.im.R;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/6/4.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.sendMessageDelayed(Message.obtain(),2000);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //如果当前activity已经退出，不处理handler中的消息
            if (isFinishing()){
                return;
            }
            //判断进入主页面还是登陆页面
            toMainOrLogin();
        }
    };
    //判断进入主页面还是登陆页面
    private void toMainOrLogin() {
        /*new Thread(){
            //判断是否登陆过

            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()){


                    //获取当前用户信息

                    //登陆过，跳转到主页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    //m没登陆过，跳转到登陆面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                //结束当前页面
                finish();
            }
        }.start();*/

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()){


                    //获取当前用户信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    //校验，如果本地数据库被删掉进行验证
                    if (account==null){
                        //跳转到登陆界面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {//登陆过，跳转到主页面
                        //登陆成功后的方法
                        Model.getInstance().loginSuccess(account);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);}
                }else {//没登陆过
                    //跳转到登陆界面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                //结束当前页面
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
