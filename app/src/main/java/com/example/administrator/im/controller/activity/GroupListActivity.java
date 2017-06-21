package com.example.administrator.im.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.adapter.GroupListAdapter;
import com.example.administrator.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class GroupListActivity extends AppCompatActivity {
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_group_list;
    private ListView lv_group_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list_activiy);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //listview条目点击事件
        lv_group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    return;
                }
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                //传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                //群id
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(i - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

                startActivity(intent);
            }
        });
        ll_group_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        groupListAdapter = new GroupListAdapter(this);
        lv_group_list.setAdapter(groupListAdapter);

        //从欢鑫服务器获取所有群组信息
        getGroupFromServer();
    }

    private void initView() {

        lv_group_list= (ListView) findViewById(R.id.lv_group_list);
        View headView=View.inflate(this,R.layout.header_grouplist,null);
        ll_group_list= (LinearLayout)headView.findViewById(R.id.ll_group_list);
        lv_group_list.addHeaderView(headView);
    }

    public void getGroupFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                    final List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                            refresh();

                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refresh() {
        //groupListAdapter.refresh(mGroups);这个可以
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());//这个也可以
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新页面
        refresh();
    }
}
