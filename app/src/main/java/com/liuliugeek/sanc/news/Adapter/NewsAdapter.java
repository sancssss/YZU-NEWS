package com.liuliugeek.sanc.news.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.R;

import java.util.List;

/**
 * Created by 73732 on 2016/8/19.
 */
public class NewsAdapter extends BaseAdapter {
    private List<Data> mData;
    private Context mContext;

    public NewsAdapter(List<Data> mData, Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Data mData){
        this.mData.add(mData);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_content,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.txt_item_title);
            viewHolder.newsDate = (TextView) convertView.findViewById(R.id.txt_item_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.newsTitle.setText(mData.get(position).getNewTitle());
        viewHolder.newsDate.setText("日期："+mData.get(position).getNewDate());
        return convertView;
    }

    private class ViewHolder{
        TextView newsTitle;
        TextView newsDate;
    }

    public String subText(String text){
        if(text.length()>26){
            return text.substring(0,26)+"...";
        }else{
            return text;
        }
    }
}
