package com.example.administrator.im.controller.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.adapter.GroupDetailAdapter;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.example.administrator.im.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;


//群详情页面
public class GroupDetailActivity extends AppCompatActivity {
    private GroupDetailAdapter groupDetailAdapter;
    private String groupId;
    private String mOwner;
    private EMGroup mGroup;
private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        initView();
        getData();
        
        initData();
    }

    private void initData() {
        //初始化button显示
        initButtonDisplay();

        //初始化gridview
        initGridView();
    }

    private void initGridView() {
        //当前用户是群主或群公开
        boolean isCanModify=EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())||mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOwner, new GroupDetailAdapter.OnMembersChangeListener() {
            @Override
            public void onRemoveGroupMember(final UserInfo userInfo) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 参数1: 群id
                         * 参数2: 用户id
                         */
                        // 网络删除用户
                        try {
                            EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), userInfo.getHxid());

                            // 网络获取群成员
                            getGroupMembers();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } catch (HyphenateException e) {
                            e.printStackTrace();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GroupDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });

            }

            @Override
            public void onAddGroupMember(UserInfo userInfo) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GroupDetailActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        // 跳转到选择好友界面
                        Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
                        intent.putExtra("groupid", mGroup.getGroupId());
                        startActivityForResult(intent, 2);
                    }
                });

            }
        });
        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        //判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())){//群组的处理
            bt_groupdetail_out.setText("解散群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());
                                
                                //发送退群的广播
                                exitGroupBroadCast();
                                
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else {//群成员的处理
            bt_groupdetail_out.setText("退群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                //发送退群广播
                                exitGroupBroadCast();
                                
                                //更新界面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败"+e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
    //发送退群和解散广播
    private void exitGroupBroadCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    //获取传递过来的数据
    private void getData() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId==null){
            return;
        }else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
            mOwner=mGroup.getOwner();
        }
    }

    private void initView() {
        gv_groupdetail= (GridView) findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out= (Button) findViewById(R.id.bt_groupdetail_out);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            // 添加选中的群成员
            addMembers(data);
        }
    }
    private void addMembers(Intent data) {
        final String[] members = data.getStringArrayExtra("members");

        if (members == null || members.length == 0) {
            return;
        }

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this,"添加群成员成功" , Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this,"添加群成员失败" , Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
    private void getGroupMembers() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 从网络获取群组
                try {
                    EMClient.getInstance().groupManager().getGroupFromServer(groupId);

                    // 获取群成员
                    List<String> members = mGroup.getMembers();

                    // 转类型
                    final List<UserInfo> userInfos = new ArrayList<>();

                    for (String hxid : members) {
                        userInfos.add(new UserInfo(hxid));
                    }

                    // 内存和网页
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.refresh(userInfos);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

