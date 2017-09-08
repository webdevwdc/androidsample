package com.divetime.charters;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.charters.adapters.ChartersAdapters;
import com.divetime.charters.model.CharterListingDatum;
import com.divetime.charters.model.CharterListingRequest;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCharters.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCharters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCharters extends Fragment implements
        com.divetime.charters.DatePickerDialog.DialogSubmitListner,View.OnClickListener,RetrofitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Toolbar toolbar;
    TextView toolbar_header,tv_dive_timing;
    LinearLayoutManager mLayoutManager;
    ProgressDialog pDialog;

    RestHandler rest;

    RecyclerView mRecycler;

    ChartersAdapters adapterList;

    List<CharterListingDatum> al_charters_data =new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private String selected_date="";

    public FragmentCharters() {
        // Required empty public constructor
    }

    public static FragmentCharters newInstance(String param1, String param2) {
        FragmentCharters fragment = new FragmentCharters();
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

        View view= inflater.inflate(R.layout.fragment_charters, container, false);

        initView(view);

        return view;
    }

    private void initView(View view)
    {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);
        tv_dive_timing=(TextView)view.findViewById(R.id.tv_site_name);
        mRecycler=(RecyclerView)view.findViewById(R.id.mRecycler);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_header.setText("Charters");

        pDialog =Constants.getDialog(getActivity());

        adapterList = new ChartersAdapters(al_charters_data, getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setAdapter(adapterList);
        rest=new RestHandler(getActivity(),this);

        tv_dive_timing.setOnClickListener(this);


        if(TrackApplication.getInstance().getCharter_listing()==null)
        getAllCharterList();
        else{
            al_charters_data.clear();
            al_charters_data.addAll(TrackApplication.getInstance().getCharter_listing().getResult().getData());
            adapterList.notifyDataSetChanged();
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

    private void showCalendar() {
        DatePickerDialog.DialogBuilder gDialog = new DatePickerDialog.DialogBuilder();
        gDialog.setContext(getActivity())
                .setSubmitListner(FragmentCharters.this)
                .showDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDialogSubmit(String date) {

        Log.d("test","");
        selected_date=Constants.changeDateFormat(date,"yyyy-MM-dd","MMM dd");
        getChartersByDate(date);

    }

    private void getChartersByDate(String date)
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                diveWhen(Constants.getUserId(getActivity()),date),"get_charters_by_date");

    }

    private void getAllCharterList()
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getCharterList(Constants.getUserId(getActivity())),"get_all_charters");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tv_site_name:

                showCalendar();

                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("get_charters_by_date"))
            {
                CharterListingRequest charter_obj=(CharterListingRequest)response.body();

                if(!charter_obj.getResult().getError() && charter_obj.getResult().getData().size()>0)
                {
                    TrackApplication.getInstance().setCharter_listing(charter_obj);
                    ((LandingActivity)getActivity()).replaceFragment(FragmentFindChartersByDate.newInstance(selected_date,""));

                }
                else{
                    Constants.showAlert(getActivity(),charter_obj.getResult().getMessage());
                }
            }
            else if(method.equalsIgnoreCase("get_all_charters"))
            {
                CharterListingRequest charter_obj=(CharterListingRequest)response.body();

                if(!charter_obj.getResult().getError())
                {
                    al_charters_data.clear();
                    al_charters_data.addAll(charter_obj.getResult().getData());

                   /* for(int i=65;i<81;i++)
                    {
                        CharterListingDatum cc=new CharterListingDatum();
                        cc.setId(i);
                        List<CharterListingDatum> aaaa=new ArrayList<>();
                        aaaa.add(cc);
                        al_charters_data.addAll(aaaa);
                    }*/

                    adapterList.notifyDataSetChanged();
                }  else{
                    Constants.showAlert(getActivity(),charter_obj.getResult().getMessage());
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

    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.img_btm_home).setSelected(false);
        getActivity().findViewById(R.id.img_btm_charters).setSelected(true);
        getActivity().findViewById(R.id.img_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.img_btm_boats).setSelected(false);
//        getActivity().findViewById(R.id.btm_meetups).setSelected(false);

        getActivity().findViewById(R.id.tv_btm_home).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);

    }


  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);

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
    }*/
}
