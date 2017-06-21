package com.example.administrator.im.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddContactActvity extends AppCompatActivity {
    private UserInfo userInfo;
private TextView tv_find;
    private TextView tv_name;
    private EditText et_name;
    private RelativeLayout rl_add;
    private Button bt_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_actvity);
        initView();
        initListener();
    }

    private void initListener() {
        //查找点击事件
        tv_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find();
            }
        });
        //添加点击事件
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }
//查找处理
    private void find() {

        //获取输入用户名称
        final String name = et_name.getText().toString().trim();

        //校验输入的名称
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "输入的用户名称不能为空", Toast.LENGTH_SHORT).show();
        }
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去服务器判断当前查找的用户是否存在
                userInfo = new UserInfo(name);
                //更新ui显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_add.setVisibility(View.VISIBLE);
                        tv_name.setText(userInfo.getName());
                    }
                });
            }
        });
    }

    //添加处理
    private void add() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(),"添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActvity.this, "发送添加好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActvity.this, "发送添加好友邀请失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    private void initView() {
        tv_find= (TextView) findViewById(R.id.tv_find);
        et_name= (EditText) findViewById(R.id.et_name);
        rl_add= (RelativeLayout) findViewById(R.id.rl_add);
        bt_add= (Button) findViewById(R.id.bt_add);
        tv_name= (TextView) findViewById(R.id.tv_name);
    }
}
