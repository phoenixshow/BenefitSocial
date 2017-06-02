package com.phoenix.social.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.phoenix.social.R;
import com.phoenix.social.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择联系人的页面适配器
 */
public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();

    public PickContactAdapter(Context context, List<PickContactInfo> picks) {
        mContext = context;
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }
    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取ViewHolder
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_pick, null);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_pick);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_pick_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取当前Item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        //显示数据
        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());
        return convertView;
    }

    private class ViewHolder{
        private CheckBox cb;
        private TextView tv_name;
    }

    //获取选择的联系人
    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pick : mPicks) {
            //判断是否选中
            if (pick.isChecked()){
                picks.add(pick.getUser().getName());
            }
        }
        return picks;
    }
}
