package com.example.administrator.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.im.R;
import com.example.administrator.im.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIsCanModify;//是否允许添加和删除群成员
    private String mOwner;//群主
    // 是否是删除模式: true:删除模式; false:非删除模式
    private boolean mIsDeleteMode = false;

    private List<UserInfo> mUserInfos;//群成员数据
    public GroupDetailAdapter(Context mContext, boolean mIsCanModify,String owner,OnMembersChangeListener onMembersChangeListener) {
        this.mOwner=owner;
        this.mContext = mContext;
        this.mIsCanModify = mIsCanModify;
        mUserInfos=new ArrayList<>();
        mOnMembersChangeListener = onMembersChangeListener;
    }

    //刷新数据
    public void refresh(List<UserInfo> userInfos){
        if (userInfos==null){
            return;
        }
        //清楚原有数据
        mUserInfos.clear();
        //添加加减号
        initUser();
        //添加群成员
        mUserInfos.addAll(0,userInfos);

        notifyDataSetChanged();
    }
    private void initUser() {
        mUserInfos.add(new UserInfo("remove"));
        mUserInfos.add(0, new UserInfo("add"));
    }

    @Override
    public int getCount() {
        return mUserInfos == null ? 0 : mUserInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mUserInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView = View.inflate(mContext, R.layout.adapter_group_members, null);
            holder.mIvMemberDelete= (ImageView) convertView.findViewById(R.id.iv_member_delete);
            holder.mIvMemberPhoto= (ImageView) convertView.findViewById(R.id.iv_member_photo);
            holder.mTvMemberName= (TextView) convertView.findViewById(R.id.tv_member_name);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        /**
         *  处理ItemView, 即按情况分别处理加号,减号和群成员
         *
         *  群主: 除群成员外, 加号和减号都可见, 即有权限踢人和加人
         *  群成员: 只可见群成员
         */

        if (EMClient.getInstance().getCurrentUser().equals(mOwner)) {

            /**
             * 当前用户是群主时
             */

            if (position == mUserInfos.size() - 1) {
                // 最后一个位置的itemView: 减号

                if (mIsDeleteMode) {
                    // 隐藏删除模式下的减号
                    convertView.setVisibility(View.GONE);

                } else {
                    // 展示整个减号
                    convertView.setVisibility(View.VISIBLE);
                    // 隐藏群成员上的减号
                    holder.mIvMemberDelete.setVisibility(View.GONE);
                    // 隐藏群成员的名字
                    holder.mTvMemberName.setVisibility(View.INVISIBLE);
                    // 设置图片
                    holder.mIvMemberPhoto.setImageResource(R.drawable.smiley_minus_btn_pressed);

                }

            } else if (position == mUserInfos.size() - 2) {
                // 倒数第二个位置的itemView: 加号

                if (mIsDeleteMode) {
                    // 隐藏删除模式下的加号
                    convertView.setVisibility(View.GONE);

                } else {
                    // 展示整个加号
                    convertView.setVisibility(View.VISIBLE);
                    // 隐藏群成员上的减号
                    holder.mIvMemberDelete.setVisibility(View.GONE);
                    // 隐藏名字
                    holder.mTvMemberName.setVisibility(View.INVISIBLE);
                    // 设置图片
                    holder.mIvMemberPhoto.setImageResource(R.drawable.smiley_add_btn_pressed);
                }

            } else {
                // 其他位置的itemView: 群成员

                convertView.setVisibility(View.VISIBLE);
                holder.mTvMemberName.setVisibility(View.VISIBLE);

                // 根据删除模式决定是否显示群成员上的小减号
                if (mIsDeleteMode) {
                    // 删除模式下可见
                    holder.mIvMemberDelete.setVisibility(View.VISIBLE);
                } else {
                    // 不是删除模式不可见
                    holder.mIvMemberDelete.setVisibility(View.GONE);
                }

                holder.mTvMemberName.setText(mUserInfos.get(position).getName());
                //holder.mIvMemberPhoto.setImageResource(R.mipmap.em_default_avatar);
            }

            /**
             * 监听事件
             */
            if (position == mUserInfos.size() - 1) {
                // 最后位置的减号

                holder.mIvMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteMode) {
                            // 设置为删除模式
                            mIsDeleteMode = true;
                            // 刷新
                            notifyDataSetChanged();
                        }
                    }
                });

            } else if (position == mUserInfos.size() - 2) {
                // 倒数第二位置的加号

                holder.mIvMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMembersChangeListener != null) {
                            mOnMembersChangeListener.onAddGroupMember(mUserInfos.get(position));
                        }
                    }
                });

            } else {
                // 群成员上的减号

                holder.mIvMemberDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMembersChangeListener != null) {
                            mOnMembersChangeListener.onRemoveGroupMember(mUserInfos.get(position));
                        }
                    }
                });
            }

        } else {

            /**
             * 当前用户是群成员时
             */
            if (position == mUserInfos.size() - 1) {
                // 隐藏最后位置的减号
                convertView.setVisibility(View.GONE);

            } else if (position == mUserInfos.size() - 2) {

                if (mIsCanModify) {
                    // 显示加号
                    convertView.setVisibility(View.VISIBLE);
                    // 隐藏群成员上的减号
                    holder.mIvMemberDelete.setVisibility(View.GONE);
                    // 隐藏名字
                    holder.mTvMemberName.setVisibility(View.INVISIBLE);
                    // 设置图片
                    //viewHolder.mIvMemberPhoto.setImageResource(R.mipmap.em_smiley_add_btn_pressed);

                    holder.mIvMemberPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnMembersChangeListener != null) {
                                mOnMembersChangeListener.onAddGroupMember(mUserInfos.get(position));
                            }
                        }
                    });
                } else {
                    // 隐藏倒数第二位置的加号
                    convertView.setVisibility(View.GONE);
                }

            } else {
                // 其他的群成员
                convertView.setVisibility(View.VISIBLE);
                holder.mTvMemberName.setText(mUserInfos.get(position).getName());

                // 隐藏群成员上的减号, 这是群主在删除模式下才可见的
                holder.mIvMemberDelete.setVisibility(View.GONE);
            }

            // 监听

        }
        return null;
    }
    private class ViewHolder{
        ImageView mIvMemberPhoto;
        TextView mTvMemberName;
        ImageView mIvMemberDelete;

    }
    // 接口回调
    private OnMembersChangeListener mOnMembersChangeListener;

    public interface OnMembersChangeListener {

        // 移除成员
        void onRemoveGroupMember(UserInfo userInfo);

        // 添加成员
        void onAddGroupMember(UserInfo userInfo);

    }
}
