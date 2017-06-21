package com.example.administrator.im.controller.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.adapter.TabAdapter;
import com.example.administrator.im.controller.fragment.LoginFragment;
import com.example.administrator.im.controller.fragment.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public class LoginActivity extends AppCompatActivity {
    private TabLayout mTLLogin;
    private ViewPager mVPLogin;
    private String[] mTitle;
    private List<Fragment> mFragments;
    TabAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();

    }

    private void initData() {
        mTitle=getResources().getStringArray(R.array.title);
        mFragments=new ArrayList<>();
        LoginFragment loginFragment=new LoginFragment();
        RegisterFragment registerFragment=new RegisterFragment();

        mFragments.add(loginFragment);
        mFragments.add(registerFragment);


    }

    private void initView() {
        mTLLogin= (TabLayout) findViewById(R.id.tl_login);
        mVPLogin= (ViewPager) findViewById(R.id.vp_login);
        mAdapter=new TabAdapter(getSupportFragmentManager(),mTitle,mFragments);
        mVPLogin.setAdapter(mAdapter);
        mTLLogin.setupWithViewPager(mVPLogin);

    }
}
