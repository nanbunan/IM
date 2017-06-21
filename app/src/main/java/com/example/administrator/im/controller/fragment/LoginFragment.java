package com.example.administrator.im.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.activity.MainActivity;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/6/4.
 */

public class LoginFragment extends Fragment {
    private EditText log_pwd,log_phone;
    private ImageView bt_login;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_login,null);
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String logName=log_phone.getText().toString().trim();
                final String logPwd=log_pwd.getText().toString().trim();
                //账号或密码为空
                if (TextUtils.isEmpty(logName) || TextUtils.isEmpty(logPwd)){
                    Toast.makeText(getActivity(), "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //登陆逻辑处理
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //去环信服务器登陆
                        EMClient.getInstance().login(logName, logPwd, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //登陆成功
                                Model.getInstance().loginSuccess(new UserInfo(logName));
                                //保存用户账号信息到本地数据库
                                Model.getInstance().getUserAccountDao().addAccount(new UserInfo(logName));

                                //提示登陆成功
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
                                        //跳转到主页面
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });


                            }

                            @Override
                            public void onError(int i, final String s) {
                                //登陆失败
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "登陆失败"+s, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                });
            }

        });
    }

    private void initView(View view) {
        log_phone= (EditText) view.findViewById(R.id.log_phone);
        log_pwd= (EditText) view.findViewById(R.id.log_pw);
        bt_login= (ImageView) view.findViewById(R.id.bt_login);
    }
}
