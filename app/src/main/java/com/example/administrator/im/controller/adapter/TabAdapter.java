package com.example.administrator.im.controller.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

public class TabAdapter extends FragmentPagerAdapter {
    String [] mTitle;
    List<Fragment> mFragments;

    public TabAdapter(FragmentManager fm, String[] mTitle, List<Fragment> mFragments) {
        super(fm);
        this.mTitle=mTitle;
        this.mFragments=mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
