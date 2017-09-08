package com.divetime.sites;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.charters.FragmentCharters;
import com.divetime.charters.FragmentFindCharters;
import com.divetime.charters.model.CharterListingRequest;
import com.divetime.databinding.FragmentSiteSelectedBinding;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.sites.model.SiteDetailsRequest;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

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
public class FragmentSiteSelected extends Fragment implements RetrofitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SITE_ID = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    Toolbar toolbar;
    TextView toolbar_header;
    Button btn_view_all_charters;
    private OnFragmentInteractionListener mListener;
    RestHandler rest;
    ProgressDialog pDialog;

    String sitename="";
    View white_view;

    FragmentSiteSelectedBinding mBinding;

    public FragmentSiteSelected() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSiteSelected newInstance(int param1, String param2) {
        FragmentSiteSelected fragment = new FragmentSiteSelected();
        Bundle args = new Bundle();
        args.putInt(ARG_SITE_ID, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SITE_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View view= inflater.inflate(R.layout.fragment_site_selected, container, false);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_site_selected,container,false);
        mBinding.setFragment(this);
        View view=mBinding.getRoot();
        init(view);
        return view;
    }


    private void init(View view)
    {
        btn_view_all_charters=(Button)view.findViewById(R.id.btn_view_all_charters);
        toolbar_header= (TextView)view.findViewById(R.id.toolbar_header);
        toolbar_header.setText("Sites");
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        white_view=view.findViewById(R.id.view);

//        btn_view_all_charters.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        rest=new RestHandler(getActivity(),this);
        pDialog= Constants.getDialog(getActivity());

        getSiteDetails();
    }


    private void getSiteDetails()
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).getSiteDetails
                (mParam1,Constants.getUserId(getActivity())),"site_details");
    }

    private void getChartersList()
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).getSiteWiseCharterListing
                (mParam1,Constants.getUserId(getActivity())),"charter_details");
    }

    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_view_all_charters:

                getChartersList();

           /* FragmentTransaction ft = getFragmentManager().beginTransaction();

//                int id=mBinding.getSite().getId();
            ft.replace(R.id.contentContainer, new FragmentFindCharters());
            ft.addToBackStack(new FragmentFindCharters().getClass().getName());
            ft.commit();*/

            break;
        }

    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("site_details"))
            {
                SiteDetailsRequest site_dtls_obj=(SiteDetailsRequest) response.body();
                if(!site_dtls_obj.getResult().getError())
                {
                    mBinding.setSite(site_dtls_obj.getResult().getData());
                    sitename=site_dtls_obj.getResult().getData().getName();
                    white_view.setVisibility(View.GONE);
                }
//                else
//                    Constants.showAlert(getActivity(),charter_dtls_obj.getResult().getMessage());
            }
            else if(method.equalsIgnoreCase("charter_details"))
            {

                CharterListingRequest site_dtls_obj=(CharterListingRequest) response.body();
                if(!site_dtls_obj.getResult().getError())
                {
                    TrackApplication.getInstance().setCharter_listing(site_dtls_obj);
                    ((LandingActivity)getActivity()).replaceFragment(FragmentFindCharters.newInstance("yes",sitename));
                }
                else
                    Constants.showAlert(getActivity(),site_dtls_obj.getResult().getMessage());
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
//        inflater.inflate(R.menu.home_menu, menu);

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
}
