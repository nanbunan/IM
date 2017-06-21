package com.example.administrator.im.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

public class NewGroupActivity extends AppCompatActivity {
    private EditText et_newgroup_name,et_newgroup_desc;
    private CheckBox cb_newgroup_public,cb_newgroup_ivite;
    private Button bt_newgroup_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();
        initListener();
    }

    private void initListener() {
        //创建按钮点击事件的处理
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent,1);

            }
        });
    }
//初始view
    private void initView() {
        et_newgroup_name= (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc= (EditText) findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public= (CheckBox) findViewById(R.id.cb_newgroup_public);
        cb_newgroup_ivite= (CheckBox) findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create= (Button) findViewById(R.id.bt_newgroup_create);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取到联系热
        if (resultCode==RESULT_OK){
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }
    //创建群
    private void createGroup(final String[] memberses) {
        final String groupName = et_newgroup_name.getText().toString();
        final String groupDesc = et_newgroup_desc.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务创建群
                //参数一群名称参数二群描述参数三群成员参数四原因参数五群参数设置
                EMGroupManager.EMGroupOptions options=new EMGroupManager.EMGroupOptions();//创建群的类型
                options.maxUsers=200;//群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle=null;
                if (cb_newgroup_public.isChecked()){//公开
                    if (cb_newgroup_ivite.isChecked()){//开放群邀请
                        groupStyle= EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {
                        groupStyle= EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;

                    }
                }else {//没有公开
                    if (cb_newgroup_ivite.isChecked()) {//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;

                    }
                }
                options.style=groupStyle;
                    try {
                        EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,memberses,"申请加入去群",options);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();
                                //结束当前页面
                                finish();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        });
    }
}

