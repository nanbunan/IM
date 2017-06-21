package com.example.administrator.im.controller.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.administrator.im.R;
import com.example.administrator.im.controller.fragment.ChatFragment;
import com.example.administrator.im.controller.fragment.ContactFragment;
import com.example.administrator.im.controller.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment[] fragments;
    private Button[] mTabs;
    private int mIndex;//当前选中frgment和底部选项卡的索引
    private int mCurrentIndex=1;//frgment和底部选项卡的索引
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTabs=new Button[3];
        fragments=new Fragment[3];
        fragments[0]=new ChatFragment();
        fragments[1]=new ContactFragment();
        fragments[2]=new SettingFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragments[mCurrentIndex]).commit();
        mTabs[0]= (Button) findViewById(R.id.btn_shouye);
        mTabs[1]=(Button) findViewById(R.id.btn_jiaoyi);
        mTabs[2]=(Button) findViewById(R.id.btn_wode);
        mTabs[mCurrentIndex].setSelected(true);
    }
    public void onTabClicked(View v){
        switch (v.getId()){
            case R.id.btn_shouye:
                mIndex=0;
                break;
            case R.id.btn_jiaoyi:
                mIndex=1;
                break;
            case R.id.btn_wode:
                mIndex=2;
                break;
        }
        if(mCurrentIndex!=mIndex){
            mTabs[mIndex].setSelected(true);
            mTabs[mCurrentIndex].setSelected(false);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,fragments[mIndex]).commit();
            mCurrentIndex=mIndex;

        }

    }
}
