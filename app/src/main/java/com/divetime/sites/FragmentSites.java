package com.divetime.sites;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.home.adapter.LandingAdapter;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.FragmentSettings;
import com.divetime.sites.adapter.SitesAdapter;
import com.divetime.sites.model.SiteListingDatum;
import com.divetime.sites.model.SiteListingRequest;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSites extends Fragment implements View.OnClickListener,RetrofitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "is_filter";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1="no";
    private String mParam2;
    SearchView searchViewAndroidActionBar;
    Toolbar toolbar;
    RecyclerView mRecycler;
    TextView toolbar_header,btn_filter;
    private OnFragmentInteractionListener mListener;
    SitesAdapter adapterList;
    ProgressDialog pDialog;
    RestHandler rest;

    List<SiteListingDatum> al_data=new ArrayList<>();

    public FragmentSites() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSites newInstance(String param1, String param2) {
        FragmentSites fragment = new FragmentSites();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_sites, container, false);

        initView(view);

        return view;
    }

    private void initView(View view)
    {
        mRecycler=(RecyclerView)view.findViewById(R.id.mRecycler);
        toolbar_header= (TextView)view.findViewById(R.id.toolbar_header);
        btn_filter=(TextView)view.findViewById(R.id.btn_filter);
        toolbar_header.setText("Sites");
        btn_filter.setOnClickListener(this);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().onBackPressed();

                if (searchViewAndroidActionBar!=null && !searchViewAndroidActionBar.isIconified()) {
                    searchViewAndroidActionBar.setIconified(true);
                    searchViewAndroidActionBar.onActionViewCollapsed();
                    toolbar_header.setVisibility(View.VISIBLE);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
                }
            }
        });

        rest=new RestHandler(getActivity(),this);
        pDialog= Constants.getDialog(getActivity());

        if(TrackApplication.getInstance().getFiltered_site_listing()==null)
        getSites();
        else {
            al_data.clear();
            al_data.addAll(TrackApplication.getInstance().getFiltered_site_listing().getResult().getData());
//            adapterList.notifyDataSetChanged();

            adapterList = new SitesAdapter(al_data, getActivity());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecycler.setLayoutManager(mLayoutManager);
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.setNestedScrollingEnabled(false);
            mRecycler.setAdapter(adapterList);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_filter:

                TrackApplication.getInstance().setFiltered_site_listing(null);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer, new FragmentSiteFilter());
                ft.addToBackStack(new FragmentSiteFilter().getClass().getName());
                ft.commit();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("site_listing"))
            {
                SiteListingRequest sites_obj=(SiteListingRequest)response.body();

                if(!sites_obj.getResult().getError())
                {
                  al_data.clear();
                    al_data.addAll(sites_obj.getResult().getData());
//                    adapterList.notifyDataSetChanged();

                    adapterList = new SitesAdapter(al_data, getActivity());
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecycler.setLayoutManager(mLayoutManager);
                    mRecycler.setItemAnimator(new DefaultItemAnimator());
                    mRecycler.setNestedScrollingEnabled(false);
                    mRecycler.setAdapter(adapterList);
                }
                else{
//                    Constants.showAlert(getActivity(),charter_obj.getResult().getMessage());
                }
            }
        }
        else{
            try {
                Constants.showAlert(getActivity(),response.code()+" "+response.message());
            } catch (Exception e) {
//                showAlertDialog("Alert","Server Response Error..");
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(String errorMessage) {

        if(pDialog !=null && pDialog.isShowing())
            pDialog.dismiss();

        Constants.showAlert(getActivity(),errorMessage);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getSites()
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getSiteListing(Constants.getUserId(getActivity())),"site_listing");
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.img_btm_home).setSelected(false);
        getActivity().findViewById(R.id.img_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.img_btm_sites).setSelected(true);
        getActivity().findViewById(R.id.img_btm_boats).setSelected(false);
//        getActivity().findViewById(R.id.btm_meetups).setSelected(false);

        getActivity().findViewById(R.id.tv_btm_home).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        EditText searchEditText = (EditText) searchViewAndroidActionBar.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.app_white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey));

        ImageView image_view=(ImageView)searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

       /* if(image_view!=null)
            image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolbar_header.setVisibility(View.VISIBLE);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

                    //Clear the text from EditText view
//                    et.setText("");

                    //Clear query
                    searchViewAndroidActionBar.setQuery("", false);
                    //Collapse the action view
                    searchViewAndroidActionBar.onActionViewCollapsed();
                    //Collapse the search widget
                }
            });*/


        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText!=null && newText.length()>=0 && adapterList!=null) {
                    final List<SiteListingDatum> filteredModelList = filter(al_data, newText.toString());
                    adapterList.animateTo(filteredModelList);
                    mRecycler.scrollToPosition(0);
                }

                return false;
            }
        });

        searchViewAndroidActionBar.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your code here
//                Toast.makeText(getActivity(),"has focus",Toast.LENGTH_LONG).show();
                toolbar_header.setVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        });

        searchViewAndroidActionBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //your code here
//                Toast.makeText(getActivity(),"close focus",Toast.LENGTH_LONG).show();
                toolbar_header.setVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
                return false;
            }
        });
    }


    private List<SiteListingDatum> filter(List<SiteListingDatum> models, String query) {
        query = query.toLowerCase();

        final List<SiteListingDatum> filteredModelList = new ArrayList<>();
        for (SiteListingDatum model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO put your code here to respond to the button tap
                Toast.makeText(getActivity(), "Settings!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
