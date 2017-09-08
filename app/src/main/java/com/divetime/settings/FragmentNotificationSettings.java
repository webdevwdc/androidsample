package com.divetime.settings;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.divetime.R;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.model.GetUserSettingsRequest;
import com.divetime.settings.model.NotiSettingsRequest;
import com.divetime.settings.model.SetUserSettingsRequest;
import com.divetime.utils.Constants;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class FragmentNotificationSettings extends Fragment implements View.OnClickListener, RetrofitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    RestHandler rest;
    ProgressDialog pDialog;
    LinearLayout btnSubmit;
    private TextView toolbar_header;
    Toolbar toolbar;
    CheckBox chk_can_booking, chk_discount,chk_event;
    private String can_booking;
    private String discount,events;

    public FragmentNotificationSettings() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentNotificationSettings newInstance(String param1, String param2) {
        FragmentNotificationSettings fragment = new FragmentNotificationSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_notification_settings, container, false);

        chk_can_booking=(CheckBox)view.findViewById(R.id.chk_can_booking);
        chk_discount=(CheckBox)view.findViewById(R.id.chk_discount);
        chk_event=(CheckBox)view.findViewById(R.id.chk_event);

        btnSubmit = (LinearLayout) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        toolbar_header= (TextView)view.findViewById(R.id.toolbar_header);
        toolbar_header.setText("Notification Settings");
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

//        btn_view_all_charters.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        rest=new RestHandler(getActivity(),this);

        pDialog=new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.dialog_msg));
        pDialog.setCancelable(false);




        getPrivacySettings();

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()!=null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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


            case R.id.btn_submit:

                if (chk_can_booking.isChecked()){
                    can_booking = "Yes";
                }else {
                    can_booking = "No";
                }

                if (chk_discount.isChecked()){

                    discount = "Yes";
                }else {
                    discount ="No";
                }

                if (chk_event.isChecked()){

                    events = "Yes";
                }else {
                    events ="No";
                }

                submitPrivacySettings(can_booking, discount,events);
        }

    }

    private void getPrivacySettings() {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getSettings(Constants.getUserId(getActivity())),"getNotificationSettings");
        pDialog.show();
    }


    private void submitPrivacySettings(String trip_can, String discount,String event) {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                notiSettings(Constants.getUserId(getActivity()),trip_can, discount,event),"setNotificationSettings");
        pDialog.show();
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {


        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if (method.equalsIgnoreCase("setNotificationSettings")) {
                NotiSettingsRequest req_Obj = (NotiSettingsRequest) response.body();
                if(!req_Obj.getResult().getError()) {

                    Toast.makeText(getActivity(), "Submitted successfully", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }

                else {
                    Toast.makeText(getActivity(), "Server Response Error.. ", Toast.LENGTH_LONG).show();
                }

//                getAllTeeTimes();


            }else if (method.equalsIgnoreCase("getNotificationSettings")) {
                GetUserSettingsRequest req_Obj = (GetUserSettingsRequest) response.body();
                if(!req_Obj.getResult().getError()) {
                    if (req_Obj.getResult().getData().getTripCancel().equalsIgnoreCase("Yes")){

                        chk_can_booking.setChecked(true);
                    }
                    else if(req_Obj.getResult().getData().getTripCancel().equalsIgnoreCase("no"))
                        chk_can_booking.setChecked(false);

                    if (req_Obj.getResult().getData().getDiscountDive().equalsIgnoreCase("Yes")){
                        chk_discount.setChecked(true);
                    }
                    else if(req_Obj.getResult().getData().getDiscountDive().equalsIgnoreCase("no"))
                        chk_discount.setChecked(false);

                    if (req_Obj.getResult().getData().getEvent().equalsIgnoreCase("Yes")){
                        chk_event.setChecked(true);
                    }
                    else if(req_Obj.getResult().getData().getEvent().equalsIgnoreCase("no"))
                        chk_event.setChecked(false);
                }
            }
        }
        else{
            try {
                response.errorBody().string();
//                label.setText(response.code()+" "+response.message());
                Constants.showAlert(getActivity(),response.code()+" "+response.message());
            } catch (IOException e) {
//                showAlertDialog("Alert","Server Response Error..");
                e.printStackTrace();
            }
            catch(NullPointerException e){
//                showAlertDialog("Alert","Server Response Error..");
            }
        }

    }

    @Override
    public void onFailure(String errorMessage) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        Constants.showAlert(getActivity(),errorMessage);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}



