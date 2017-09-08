package com.divetime.home.adapter;

import android.support.v13.app.FragmentStatePagerAdapter;

import com.divetime.home.FragmentLandingContent;
import com.divetime.home.model.DashboardRequest;

/**
 * Created by android on 20/6/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int NUM_ITEMS = 0;
    DashboardRequest req1;

    public ViewPagerAdapter(android.app.FragmentManager fragmentManager,DashboardRequest req) {

        super(fragmentManager);
        NUM_ITEMS=req.getResult().getContent()==null ? 0 :req.getResult().getContent().size();
        req1=req;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public android.app.Fragment getItem(int position) {
        switch (position) {

            default:
                return FragmentLandingContent.newInstance(req1.getResult().getContent().
                        get(position).getImage(),req1.getResult().getContent().get(position).getContent(),
                        req1.getResult().getContent().get(position).getUrl());
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}