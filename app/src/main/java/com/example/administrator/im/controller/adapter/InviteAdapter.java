package com.example.administrator.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.im.R;
import com.example.administrator.im.model.bean.InvationInfo;
import com.example.administrator.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class InviteAdapter extends BaseAdapter {
    private List<InvationInfo> mInvitationInfos=new ArrayList<>();
    private Context mContext;
    private OnInviteListener mOnInviteListener;
    public InviteAdapter(Context context,OnInviteListener OnInviteListener) {
        mContext=context;
        this.mOnInviteListener=OnInviteListener;

    }
    //刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos){
        if (invationInfos!=null&&invationInfos.size()>=0){
            mInvitationInfos.clear();
            mInvitationInfos.addAll(invationInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitationInfos==null ? 0 : mInvitationInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvitationInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //获取或创建一个viewHolder
        ViewHolder holder=null;
                if(view==null){
                    holder=new ViewHolder();
                    view=View.inflate(mContext, R.layout.item_invite,null);
                    holder.bt_accept= (Button) view.findViewById(R.id.bt_accept);
                    holder.bt_reject= (Button) view.findViewById(R.id.bt_reject);
                    holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                    holder.tv_reason= (TextView) view.findViewById(R.id.tv_reason);
                    view.setTag(holder);
                }else {

                            holder= (ViewHolder) view.getTag();
                }
        //获取当前item数据
        final InvationInfo invationInfo = mInvitationInfos.get(i);
        //显示当前item数据
        UserInfo user = invationInfo.getUser();
        if (user!=null){//当前是联系人的邀请信息
            holder.tv_name.setText(user.getName());
            holder.bt_accept.setVisibility(View.GONE);
            holder.bt_reject.setVisibility(View.GONE);

            //原因
            if (invationInfo.getStatus()==InvationInfo.InvitationStatus.NEW_INVITE){//新的邀请

                if (invationInfo.getReason()==null){
                    holder.tv_reason.setText("添加好友");
                }else {
                    holder.tv_reason.setText(invationInfo.getReason());                }
                holder.bt_accept.setVisibility(View.VISIBLE);
                holder.bt_reject.setVisibility(View.VISIBLE);

            }else if (invationInfo.getStatus()==InvationInfo.InvitationStatus.INVITE_ACCEPT){//接受邀请
                if (invationInfo.getReason()==null){
                    holder.tv_reason.setText("接受邀请");
                }else {
                    holder.tv_reason.setText(invationInfo.getReason());                }

            }else if (invationInfo.getStatus()==InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER){//邀请被接受
                if (invationInfo.getReason()==null){
                    holder.tv_reason.setText("邀请被接受");
                }else {
                    holder.tv_reason.setText(invationInfo.getReason());                }
            }
            //按钮的处理
            holder.bt_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
               mOnInviteListener.onReject(invationInfo);

                }
            });
            holder.bt_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnInviteListener.onAccept(invationInfo);
                }
            });
        }else {//群组的邀请信息
            holder.bt_accept.setVisibility(View.GONE);
            holder.bt_reject.setVisibility(View.GONE);
            //显示名称
            holder.tv_name.setText(invationInfo.getGroup().getInvitePerson());
            //显示原因
            switch (invationInfo.getStatus()){
                //你的群申请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.tv_reason.setText("你的群申请已经被接受");
                    break;
                //你的群邀请已经被接受
                case GROUP_INVITE_ACCEPTED:
                    holder.tv_reason.setText("你的群邀请已经被接受");
                    break;
                //你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.tv_reason.setText("你的群申请已经被拒绝");
                    break;
                //你的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.tv_reason.setText("你的群邀请已经被拒绝");
                    break;
                //你收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.bt_accept.setVisibility(View.VISIBLE);
                    holder.bt_reject.setVisibility(View.VISIBLE);
                    //接受邀请
                    holder.bt_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onInviteAccept(invationInfo);
                        }
                    });
                    //拒绝邀请
                    holder.bt_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onInviteReject(invationInfo);

                        }
                    });
                    holder.tv_reason.setText("你收到了群邀请");
                    break;
                //你收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.tv_reason.setText("你收到了群申请");
                    holder.bt_accept.setVisibility(View.VISIBLE);
                    holder.bt_reject.setVisibility(View.VISIBLE);
                    //接受申请
                    holder.bt_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onApplicationAccept(invationInfo);

                        }
                    });
                    //拒绝申请
                    holder.bt_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onApplicationReject(invationInfo);

                        }
                    });
                    break;
                //你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.tv_reason.setText("你接受了群邀请");
                    break;
                //你批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    holder.tv_reason.setText("你批准了群加入");
                    break;
            }

        }
        //返回view
        return view;
    }
    private class ViewHolder{

        TextView tv_name,tv_reason;
        Button bt_accept,bt_reject;

    }
    public interface OnInviteListener{
        //联系人接受按钮点击事件
        void onAccept(InvationInfo invationInfo);
        //联系人拒绝按钮点击事件
        void onReject(InvationInfo invationInfo);

        //接受邀请按钮处理
        void onInviteAccept(InvationInfo invationInfo);
        //拒绝邀请按钮处理
        void onInviteReject(InvationInfo invationInfo);
        //接受申请按钮处理
        void onApplicationAccept(InvationInfo invationInfo);
        //拒绝申请按钮处理
        void onApplicationReject(InvationInfo invationInfo);
    }
}
