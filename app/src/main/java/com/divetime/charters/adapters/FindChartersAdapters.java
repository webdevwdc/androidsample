package com.divetime.charters.adapters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.divetime.R;
import com.divetime.charters.FragmentCharterSelected;
import com.divetime.charters.model.CharterListingDatum;
import com.divetime.databinding.RowFindChartersBinding;

import java.util.List;

/**
 * Created by android on 9/6/17.
 */

public class FindChartersAdapters extends RecyclerView.Adapter<FindChartersAdapters.MyViewHolder> {
    private Context mContext;
    private List<CharterListingDatum> al_data;
    RowFindChartersBinding binding;

    public FindChartersAdapters(List<CharterListingDatum> _data, Context ctx) {
        if(ctx!=null) {
            mContext = ctx;
            this.al_data = _data;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RowFindChartersBinding mBinding;
        public MyViewHolder(View view) {
            super(view);
            mBinding= DataBindingUtil.bind(view);
            mBinding.setAdapter(FindChartersAdapters.this);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_find_charters, parent, false);
        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        CharterListingDatum cc=al_data.get(position);
        cc.setPosition(position+1);
        holder.mBinding.setCharters(cc);
        holder.mBinding.setHolder(holder);
    }

    @Override
    public int getItemCount() {
       return al_data ==null ?  0 : al_data.size();
    }

    public void onClick(View v,CharterListingDatum charters_data,MyViewHolder holder)
    {
//        Log.d("well bro",""+holder.mBinding.getCharters().getId());
//        Toast.makeText(mContext,"Row "+holder.getAdapterPosition()+" ID: "+holder.mBinding.getCharters().getId(),Toast.LENGTH_LONG).show();
        FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();
        ft.replace(R.id.contentContainer, FragmentCharterSelected.newInstance(charters_data.getId(),""));
        ft.addToBackStack(new FragmentCharterSelected().getClass().getName());
        ft.commit();
    }
}
