package com.divetime.home.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.divetime.R;
import com.divetime.charters.FragmentCharterSelected;
import com.divetime.databinding.RowLandingBinding;
import com.divetime.home.model.DashboardDatum;
import com.divetime.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 14/6/17.
 */

public class LandingAdapter extends RecyclerView.Adapter<LandingAdapter.MyViewHolder>{
    List<DashboardDatum> al_data=new ArrayList<>();
    Context mContext;
    RowLandingBinding mBinding;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RowLandingBinding mBinding;

        public MyViewHolder(View view) {
            super(view);
            mBinding= DataBindingUtil.bind(view);
            mBinding.setAdapter(LandingAdapter.this);
        }
    }

    public LandingAdapter(List<DashboardDatum> _data, Context ctx, boolean ismyMeetups) {
        if(ctx!=null) {
            this.al_data = _data;
            this.mContext = ctx;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mBinding= DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_landing, parent, false);
        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mBinding.setModel(al_data.get(position));
        holder.mBinding.setHolder(holder);
    }

    @Override
    public int getItemCount() {
       return al_data==null ? 0 : al_data.size();
    }

    public void onClick(View v,DashboardDatum dashboard_data,MyViewHolder holder)
    {
        Log.d("ddddd","");
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, FragmentCharterSelected.newInstance(holder.mBinding.getModel().getId(),""));
        ft.addToBackStack(new FragmentCharterSelected().getClass().getName());
        ft.commit();
    }
}
