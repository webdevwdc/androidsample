package com.divetime.sites.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.divetime.R;
import com.divetime.sites.FragmentSiteSelected;
import com.divetime.sites.model.SiteListingDatum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 14/6/17.
 */

public class SitesAdapter extends RecyclerView.Adapter<SitesAdapter.MyViewHolder> implements Filterable{
    private ProgressDialog pDialog;
    MyViewHolder holder;
    Context mContext;
    List<SiteListingDatum> al_data,filter_list=new ArrayList<>();
    private final CustomFilter mFilter;
    boolean isFirstTime;

    public SitesAdapter(List<SiteListingDatum> al_data, Context ctx) {

        mFilter=new CustomFilter(this);
        if (ctx != null){
            mContext = ctx;
            this.al_data=new ArrayList<>(al_data);
            isFirstTime = true;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_site_name,tv_site_details,tv_reef_details,tv_depth;

        public MyViewHolder(View view) {
            super(view);

            tv_site_name=(TextView)view.findViewById(R.id.tv_site_name);
            tv_site_details=(TextView)view.findViewById(R.id.tv_site_details);
            tv_reef_details=(TextView)view.findViewById(R.id.tv_reef_details);
            tv_depth=(TextView)view.findViewById(R.id.tv_depth);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentTransaction ft = ((Activity) mContext).getFragmentManager().beginTransaction();

                    ft.replace(R.id.contentContainer, FragmentSiteSelected.newInstance(al_data.get(getAdapterPosition()).getId(),""));
                    ft.addToBackStack(new FragmentSiteSelected().getClass().getName());
                    ft.commit();
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sites, parent, false);
        holder=new MyViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_site_name.setText(al_data.get(position).getName());
        holder.tv_site_details.setText(al_data.get(position).getCity());
        holder.tv_reef_details.setText(al_data.get(position).getDeep()+"' "+al_data.get(position).getSiteType());
        holder.tv_depth.setText(al_data.get(position).getDeep()+"'");

    }

    @Override
    public int getItemCount() {
        if(al_data==null)
            return 0;
        else
            return al_data.size();
    }


  /*  public void removeAt(int position) {
        al_data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, al_data.size());

        if(al_data.size()==0)
            mCallback.onUIupdate();
    }*/


    public class CustomFilter extends Filter {
        private SitesAdapter mAdapter;
        private CustomFilter(SitesAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;

        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(isFirstTime) {
                filter_list.addAll(al_data);
                isFirstTime=false;
            }
            al_data.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                al_data.addAll(filter_list);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final SiteListingDatum mWords : filter_list) {
                    if (mWords.getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                        al_data.add(mWords);
                    }
                }
            }
            System.out.println("Count Number " + al_data.size());
            results.values = al_data;
            results.count = al_data.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<SiteListingDatum>) results.values).size());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


    public void animateTo(List<SiteListingDatum> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }


    private void applyAndAnimateRemovals(List<SiteListingDatum> newModels) {
        if(al_data!=null) {
            for (int i = al_data.size() - 1; i >= 0; i--) {
                final SiteListingDatum model = al_data.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }
    }

    private void applyAndAnimateAdditions(List<SiteListingDatum> newModels) {
        if(al_data!=null) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final SiteListingDatum model = newModels.get(i);
                if (!al_data.contains(model)) {
                    addItem(i, model);
                }
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SiteListingDatum> newModels) {
        if(al_data!=null) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final SiteListingDatum model = newModels.get(toPosition);
                final int fromPosition = al_data.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }


    public SiteListingDatum removeItem(int position) {
        final SiteListingDatum model = al_data.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SiteListingDatum model) {
        if(al_data!=null) {
            al_data.add(position, model);
            notifyItemInserted(position);
        }
    }

    public void moveItem(int fromPosition, int toPosition) {
        if(al_data!=null) {
            final SiteListingDatum model = al_data.remove(fromPosition);
            al_data.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

}
