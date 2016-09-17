package com.liuliugeek.sanc.news.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.liuliugeek.sanc.news.Activity.Fragment.NewsListFragment;


/**
 * Created by 73732 on 2016/9/17.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int countTabs;

    public PagerAdapter(FragmentManager fragmentManager, int countTabs){
        super(fragmentManager);
        this.countTabs = countTabs;
    }
    @Override
    public Fragment getItem(int position) {
        NewsListFragment newsListFragment = new NewsListFragment();
        switch (position){
            case 0:
                return newsListFragment;
            default:
                return newsListFragment;
        }
    }

    @Override
    public int getCount() {
        return countTabs;
    }
}
