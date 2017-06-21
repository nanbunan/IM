package com.example.administrator.im.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.administrator.im.R;
import com.example.administrator.im.utils.Constant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class ChatActivity extends AppCompatActivity {
    private EaseChatFragment easeChatFragment;
    private String mHxid;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String id = intent.getStringExtra(Constant.GROUP_ID);

            if (mHxid.equals(id)) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        initListener();
    }

    private void initListener() {
        easeChatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override
            public void onEnterToChatDetails() {
                Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                //传递群id
                intent.putExtra(Constant.GROUP_ID,mHxid);
                startActivity(intent);
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });
    }

    private void initData() {
        //创建一个会话的fragment
        easeChatFragment=new EaseChatFragment();
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        easeChatFragment.setArguments(getIntent().getExtras());

        //替换fragment

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat,easeChatFragment).commit();
        // 判断类型
        Log.e("type","执行");
        int type = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        Log.e("type",type+"");

        // 在群页面才进行退群注册
        if (type == EaseConstant.CHATTYPE_GROUP) {
            Log.e("type","未执行");
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
            broadcastManager.registerReceiver(receiver, new IntentFilter(Constant.EXIT_GROUP));
        }


    }
}
