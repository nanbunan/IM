package com.example.administrator.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.im.R;
import com.example.administrator.im.controller.activity.AddContactActvity;
import com.example.administrator.im.controller.activity.ChatActivity;
import com.example.administrator.im.controller.activity.GroupListActivity;
import com.example.administrator.im.controller.activity.InviteActivity;
import com.example.administrator.im.model.Model;
import com.example.administrator.im.model.bean.UserInfo;
import com.example.administrator.im.utils.Constant;
import com.example.administrator.im.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/4.
 */

public class ContactFragment extends EaseContactListFragment {
    private String mHxid;
    private LinearLayout ll_invite;
    private LocalBroadcastManager mLBM;

    private ImageView iv_red;
    private BroadcastReceiver ContactInviteChangeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
        }
    };
    private BroadcastReceiver ContactChangeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新界面
            refreshContact();
        }
    };
    private BroadcastReceiver GroupChangeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //显示红点
            iv_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
        }
    };

    @Override
    protected void initView() {
        super.initView();
        //添加加号
        titleBar.setRightImageResource(R.drawable.search);
        //添加头布局
        View headerView=View.inflate(getActivity(),R.layout.header_fragment_contact,null);
        listView.addHeaderView(headerView);

        //获取红点对象
        iv_red = (ImageView) headerView.findViewById(R.id.iv_red);
        
        //获取邀请信息条目的对象
         ll_invite = (LinearLayout) headerView.findViewById(R.id.ll_invite);

        //设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if (user==null){
                    return;
                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                startActivity(intent);
            }
        });
        LinearLayout ll_group = (LinearLayout) headerView.findViewById(R.id.ll_group);
        //跳转到群组页面
        ll_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddContactActvity.class);
                startActivity(intent);
            }
        });
        //初始化红点显示
        Boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        iv_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //邀请信息条目的点击事件
        ll_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //红点处理
                iv_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,false);
                //跳转到邀请列表页面
                Intent intent=new Intent(getActivity(),InviteActivity.class);

                startActivity(intent);
            }
        });

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver,new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(GroupChangeReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
        //从环信服务器获取所有人联系信息
        getContactFromServer();

        //绑定listview和contextmenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();
        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.contact_delete){
            deleteContact(mHxid);


            return true;
        }
        return super.onContextItemSelected(item);
    }
//执行删除选中的联系人
    private void deleteContact(final String hxId) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(hxId);
                    //刷新本地数据库
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);
                    if (getActivity()==null){
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toast提示
                            Toast.makeText(getActivity(), "删除"+mHxid+"成功", Toast.LENGTH_SHORT).show();
                            //刷新界面
                            refreshContact();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity()==null){
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toast提示
                            Toast.makeText(getActivity(), "删除"+mHxid+"失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //从环信服务器获取所有人联系信息
    private void getContactFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取所有好友的环信id
                    List<String> hxIds = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    //校验
                    if (hxIds!=null &&hxIds.size()>=0){
                        //转换
                        List<UserInfo> contacts=new ArrayList<UserInfo>();
                        for (String hxId : hxIds){
                        contacts.add(new UserInfo(hxId));}
                        //保存好友信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts,true);
                        if (getActivity()==null){
                            return;
                        }
                        //刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新页面的方法
                                refreshContact();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity()==null){
                        return;
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "删除"+mHxid+"失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
    //刷新页面的方法
    private void refreshContact() {

        //获取数据
        List<UserInfo> contacts=Model.getInstance().getDbManager().getContactTableDao().getContacts();
        if (contacts!=null &&contacts.size()>=0){
            //设置数据
            Map<String, EaseUser> contactsMap=new HashMap<>();
            //数据转换
            for (UserInfo contact : contacts){
                EaseUser easeUser=new EaseUser(contact.getHxid());
                contactsMap.put(contact.getHxid(),easeUser);
            }
            setContactsMap(contactsMap);

            //刷新界面
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
        mLBM.unregisterReceiver(GroupChangeReceiver);
    }
}
