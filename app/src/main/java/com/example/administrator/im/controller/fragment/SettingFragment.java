package com.example.administrator.im.controller.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.im.R;
import com.example.administrator.im.controller.activity.LoginActivity;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.utils.DataCleanManger;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/6/4.
 */

public class SettingFragment extends Fragment {
    private LinearLayout mClearCache;
    private TextView mCurrentCache;
    private Button bt_setting_out;
    private String mTotalCacheSize;
    private TextView mCurVerName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, null);
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        //清理缓存
        mClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCleanManger.clearAllCache(getActivity());  //清理app缓存
                new MaterialDialog.Builder(getActivity())
                        .title(getResources().getString(R.string.clear_tip))
                        .content(getResources().getString(R.string.clear_success))
                        .positiveText(getResources().getString(R.string.okk))
                        .onPositive((dialog, which) -> mCurrentCache.setText("0 KB")).show();
            }
        });
        bt_setting_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //登陆环信服务器
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //退出成功

                                //关闭数据库 DBHelper
                                Model.getInstance().getDbManager().close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //回到登陆页面
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });



                            }

                            @Override
                            public void onError(int i, final String s) {
                                //退出失败
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出失败"+s, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onProgress(int i, String s) {
                                //正在退出

                            }
                        });
                    }
                });
            }
        });
    }

    private void initView(View view) {
        mCurVerName = (TextView) view.findViewById(R.id.version_name);
        mCurrentCache = (TextView) view.findViewById(R.id.current_cache);
        mClearCache = (LinearLayout) view.findViewById(R.id.clear_cache);
        bt_setting_out= (Button) view.findViewById(R.id.bt_setting_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        //在button上显示用户名称
        bt_setting_out.setText("退出登录（"+ EMClient.getInstance().getCurrentUser()+"）");
    }

    @Override
    public void onResume() {
        super.onResume();
        mTotalCacheSize = DataCleanManger.getTotalCacheSize(getActivity());
        if (mTotalCacheSize != null) {
            mCurrentCache.setText(mTotalCacheSize);
        }
        String versionName = null;
        try {
            versionName = getVersionName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mCurVerName.setText(versionName);
    }
    private String getVersionName() throws PackageManager.NameNotFoundException {
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }
}
