package com.xrtz.xrlive.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */

public class MyAdapter extends FragmentPagerAdapter {
    List<String> list;
    List<Fragment>fragments;
    public MyAdapter(FragmentManager fm, List<String> list,List<Fragment>fragments) {
        super(fm);
        this.list = list;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);//返回标题
    }
}