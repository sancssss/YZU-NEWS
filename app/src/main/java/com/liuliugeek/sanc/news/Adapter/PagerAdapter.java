package com.liuliugeek.sanc.news.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.liuliugeek.sanc.news.Activity.Fragment.NewsListFragment;
import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.Model.DrawerListData;

import java.util.ArrayList;


/**
 * Created by 73732 on 2016/9/17.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<DrawerListData> listdatas;
    private ArrayList<Data> datas;
    private NewsDbManager dbManager;
    private Context context;
    private FragmentManager fragmentManager;
    public PagerAdapter(FragmentManager fragmentManager, ArrayList<DrawerListData> datas, Context context){
        super(fragmentManager);
        this.listdatas = datas;
        this.context = context;
        this.fragmentManager = fragmentManager;
        dbManager = new NewsDbManager(context);
    }
    @Override
    public Fragment getItem(int position) {
        int typeid = listdatas.get(position).getTypeId();
        Log.v("pager_position", String.valueOf(position));
        if(!dbManager.isListEmpty(listdatas.get(position).getTypeId())){
            Log.v("is_from_db", "now datas is from db");
            ArrayList<Data> datas = new ArrayList<Data>();
            Log.v("typeid", String.valueOf(typeid));
            datas = dbManager.query(typeid);
            NewsListFragment newsListFragment = new NewsListFragment();
            newsListFragment.setTypeID(listdatas.get(position).getTypeId());
            newsListFragment.setDatas(datas);
            return  newsListFragment;
        }else {
            Log.v("is_from_network", "now datas is from network");
            ArrayList<Data> datas = new ArrayList();
            NewsListFragment newsListFragment = new NewsListFragment(fragmentManager, datas);
            newsListFragment.setTypeID(typeid);
            return newsListFragment;
        }

    }

    @Override
    public int getCount() {
        return listdatas.size();
    }

    public void setDatas(ArrayList<Data> datas) {
        this.datas = datas;
    }
}
