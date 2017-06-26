package com.wenjiehe.sendsms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenjiehe.sendsms.R;
import com.wenjiehe.sendsms.entity.SMSText;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */

public class SMSTextAdapter extends BaseAdapter {

    private List<SMSText> mData;
    private Context mContext;

    public SMSTextAdapter(List<SMSText> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sms_text, parent, false);
            holder = new ViewHolder();
            holder.txt_aName = (TextView) convertView.findViewById(R.id.tv_sms_text);
            convertView.setTag(holder);   //将Holder存储到convertView中
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_aName.setText(mData.get(position).getText());
        return convertView;
    }

    static class ViewHolder {
        TextView txt_aName;
    }
}
