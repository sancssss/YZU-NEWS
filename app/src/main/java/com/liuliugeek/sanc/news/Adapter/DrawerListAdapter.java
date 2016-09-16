package com.liuliugeek.sanc.news.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuliugeek.sanc.news.Model.DrawerListData;
import com.liuliugeek.sanc.news.R;

import java.util.List;

/**
 * Created by 73732 on 2016/9/15.
 */
public class DrawerListAdapter extends BaseAdapter{
    private List<DrawerListData> mData;
    private Context context;
    public DrawerListAdapter(List<DrawerListData> mData, Context context){
        this.mData = mData;
        this.context = context;
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

    public void addItem(DrawerListData mData){
        this.mData.add(mData);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.drawer_list_text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.drawer_list_image);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemTitle.setText(mData.get(position).getItemName());
        //viewHolder.imageView.setImageResource(mData.get(position).getItemImage());
        return convertView;
    }

    private class ViewHolder{
        TextView itemTitle;
       ImageView imageView;
    }


}