package com.phoenix.social.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoenix.social.R;
import com.phoenix.social.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flashing on 2017/6/4.
 */

public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIsCanModify;//是否允许添加和删除群成员
    private List<UserInfo> mUsers = new ArrayList<>();
    private boolean mIsDeleteModel;//删除模式，true表示可以删除，false表示不可以删除
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    //获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    //设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    //刷新数据
    public void refresh(List<UserInfo> users){
        if (users != null && users.size() >= 0) {
            mUsers.clear();

            //添加加号和减号
            initUsers();

            mUsers.addAll(0, users);
        }
        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取或创建ViewHolder
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_groupdetail, null);
            holder.photo = (ImageView) convertView.findViewById(R.id.iv_groupdetail_avatar);
            holder.delete = (ImageView) convertView.findViewById(R.id.iv_groupdetail_delete);
            holder.name = (TextView) convertView.findViewById(R.id.tv_groupdetail_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取当前Item数据
        final UserInfo userInfo = mUsers.get(position);

        //显示数据
        if (mIsCanModify){//群主或开放了群权限
            //布局的处理
            if (position == getCount() -1){//减号处理
                //删除模式判断
                if (mIsDeleteModel){//删除模式
                    convertView.setVisibility(View.INVISIBLE);//隐藏减号条目
                }else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else if (position == getCount() -2){//加号处理
                //删除模式判断
                if (mIsDeleteModel){//删除模式
                    convertView.setVisibility(View.INVISIBLE);//隐藏加号条目
                }else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else {//群成员
                convertView.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);

                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.ease_default_avatar);

                if (mIsDeleteModel){
                    holder.delete.setVisibility(View.VISIBLE);
                }else {
                    holder.delete.setVisibility(View.GONE);
                }
            }

            //点击事件的处理
            if (position == getCount() -1){//减号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteModel){
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if (position == getCount() -2){//加号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }else {//群成员
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onDeleteMembers(userInfo);
                    }
                });
            }
        }else {//普通的群成员
            if (position == getCount() - 1 || position == getCount() - 2){
                convertView.setVisibility(View.GONE);
            }else {
                convertView.setVisibility(View.VISIBLE);
                holder.name.setText(userInfo.getName());//名称
                holder.photo.setImageResource(R.drawable.em_default_avatar);//头像
                holder.delete.setVisibility(View.GONE);//删除
            }
        }
        //返回View
        return convertView;
    }

    private class ViewHolder{
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }

    public interface OnGroupDetailListener{
        //添加群成员方法
        void onAddMembers();

        //删除群成员方法
        void onDeleteMembers(UserInfo user);
    }
}
