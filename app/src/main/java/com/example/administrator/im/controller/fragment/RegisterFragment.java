package com.example.administrator.im.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.activity.MainActivity;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Administrator on 2017/6/4.
 */

public class RegisterFragment extends Fragment {
    private EditText rg_pw1,rg_pw2,rg_phone;
    ImageView bt_register;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_register,null);
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        bt_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String rgName=rg_phone.getText().toString().trim();
                final String rgPw1=rg_pw1.getText().toString().trim();
                String rgPw2=rg_pw2.getText().toString().trim();
                if (TextUtils.isEmpty(rgName)|| TextUtils.isEmpty(rgPw1)|| TextUtils.isEmpty(rgPw2)){
                    //输入为空提醒
                    Toast.makeText(getActivity(), "输不能为空", Toast.LENGTH_SHORT).show();
                }else if (!rgPw1.equals(rgPw2)){
                    Toast.makeText(getActivity(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                }else {
                    //去服务器注册账号
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //去环信服务器注册账号
                            try {
                                EMClient.getInstance().createAccount(rgName,rgPw1);

                                //更新页面显示

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                                        Model.getInstance().loginSuccess(new UserInfo(rgName));
                                        //保存用户账号信息到本地数据库
                                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(rgName));
                                        //跳转到主页面
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();

                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "注册失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void initView(View view) {
        rg_phone= (EditText) view.findViewById(R.id.rg_phone);
        rg_pw1= (EditText) view.findViewById(R.id.rg_pwd1);
        rg_pw2= (EditText) view.findViewById(R.id.rg_pwd2);
        bt_register= (ImageView) view.findViewById(R.id.bt_register);
    }
}
