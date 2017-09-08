package com.divetime.charters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.Manifest;
import com.divetime.R;
import com.divetime.charters.model.CharterDetailsData;
import com.divetime.charters.model.CharterDetailsRequest;
import com.divetime.charters.model.PaymentRequest;
import com.divetime.charters.model.Restsss;
import com.divetime.databinding.FragmentCharterSelectedBinding;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.sites.FragmentSiteSelected;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCharters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCharterSelected extends Fragment implements RetrofitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mCharterID;
    private String mParam2;

    CharterDetailsRequest charter_dtls_obj;
    Toolbar toolbar;
    TextView toolbar_header,tv_site_name,tv_site_details,tv_date,tv_time,tv_address,tv_mobile_no;
    LinearLayout lin_btn_container;
    RestHandler rest;

    FragmentCharterSelectedBinding mBinding;
    ProgressDialog pDialog;
    private Integer dive_id;
    LinearLayout btn_signin;

    View empty_view;

    FragmentCharterSelectedBinding charterSelectedBinding;

    private OnFragmentInteractionListener mListener;

    public FragmentCharterSelected() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCharterSelected newInstance(int param1, String param2) {
        FragmentCharterSelected fragment = new FragmentCharterSelected();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCharterID = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View view= inflater.inflate(R.layout.fragment_charter_selected, container, false);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_charter_selected,container,false);

        mBinding.setFragment(this);



       View view =mBinding.getRoot();

        init(view);
        getCharterDetails();
        return view;
    }

    private void init(View view)
    {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        empty_view=view.findViewById(R.id.empty_view);
//        setSupportActionBar(toolbar);

        lin_btn_container=(LinearLayout)view.findViewById(R.id.lin_btn_container);

        tv_site_name=(TextView)view.findViewById(R.id.tv_site_name);
        tv_site_details=(TextView)view.findViewById(R.id.tv_site_details);
        tv_date=(TextView)view.findViewById(R.id.tv_date);
        tv_time=(TextView)view.findViewById(R.id.tv_time);
        tv_address=(TextView)view.findViewById(R.id.tv_address);
        tv_mobile_no=(TextView)view.findViewById(R.id.tv_mobile_no);

        toolbar_header.setText("Charter");

        pDialog=Constants.getDialog(getActivity());

        rest=new RestHandler(getActivity(),this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btn_signin=(LinearLayout)view.findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPayment(dive_id);
            }
        });
    }

    private void doPayment(int dive_id) {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).doPayment(
                Constants.getUserId(getActivity()), dive_id, mBinding.getTr().getCalculatedDiscount()),"do_payment");
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getCharterDetails()
    {
        pDialog.show();
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).getCharterDetails(mCharterID,
                Constants.getUserId(getActivity())),"get_charter_details");
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("get_charter_details"))
            {
                charter_dtls_obj=(CharterDetailsRequest) response.body();
                if(!charter_dtls_obj.getResult().getError())
                {
                    setData(charter_dtls_obj);
                }
                else
                    Constants.showAlert(getActivity(),charter_dtls_obj.getResult().getMessage());
            }
            else if(method.equalsIgnoreCase("do_payment")){
                PaymentRequest payment_request=(PaymentRequest) response.body();

                if(!payment_request.getResult().getError())
                {
                    FragmentTransaction ft = ((Activity) getActivity()).getFragmentManager().beginTransaction();
                    ft.replace(R.id.contentContainer, FragmentPayment.newInstance(payment_request.getResult().getData().getPayKey(),""));
                    ft.addToBackStack(new FragmentPayment().getClass().getName());
                    ft.commit();
                    TrackApplication.getInstance().setPay_key(payment_request.getResult().getData().getPayKey());
                }
                else
                    Constants.showAlert(getActivity(),payment_request.getResult().getMessage());
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

    private void setData(CharterDetailsRequest charter_dtls_obj)
    {
//        tv_site_name.setText(charter_dtls_obj.getResult().getData().getFirstName()+" "+
//                charter_dtls_obj.getResult().getData().getLastName());
//
//        tv_site_details.setText(charter_dtls_obj.getResult().getData().getCity());
//        tv_date.setText(Constants.changeDateFormat(charter_dtls_obj.getResult().getData().getDate(),
//                Constants.web_date_only_format,Constants.app_display_date_format));
//        tv_time.setText(Constants.changeDateFormat(charter_dtls_obj.getResult().getData().getTime(),
//                Constants.web_time_only_format,Constants.app_display_time_format));
//        tv_address.setText(charter_dtls_obj.getResult().getData().getAddress1());
//        tv_mobile_no.setText(charter_dtls_obj.getResult().getData().getPhone());

//        CharterDetailsData cd=charter_dtls_obj.getResult().getData();

        mBinding.setTr(charter_dtls_obj.getResult().getData());

        dive_id=charter_dtls_obj.getResult().getData().getId();
        addRows(charter_dtls_obj);

        empty_view.setVisibility(View.GONE);

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

        getActivity().findViewById(R.id.tv_btm_home).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_charters).setSelected(true);
        getActivity().findViewById(R.id.tv_btm_sites).setSelected(false);
        getActivity().findViewById(R.id.tv_btm_boats).setSelected(false);

    }

    private void addRows(CharterDetailsRequest charter_dtls_obj) {

        if (lin_btn_container.getChildCount() > 0) {
            lin_btn_container.removeAllViews();
        }

        for (int i = 0; i < charter_dtls_obj.getResult().getData().getSite().size(); i++) {

           View childBtn = getActivity().getLayoutInflater().inflate(R.layout.row_buttons, lin_btn_container, false);

            TextView b1=(TextView)childBtn.findViewById(R.id.btn_site_type);
            b1.setText(charter_dtls_obj.getResult().getData().getSite().get(i).getName()+": "+
            charter_dtls_obj.getResult().getData().getSite().get(i).getDeep()+"' "+charter_dtls_obj.getResult().getData().getSite().get(i).getSiteType());
            b1.setOnClickListener(new Value1ClickListener(b1,charter_dtls_obj.getResult().getData().getSite().get(i).getId()));


            lin_btn_container.addView(childBtn);
        }
    }

    public void showMap(View v)
    {
        if(charter_dtls_obj.getResult().getData().getAddress1()!=null &&
                charter_dtls_obj.getResult().getData().getAddress1().length()>0){
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+charter_dtls_obj.getResult().getData().getAddress1());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
    }

    public void callPhoneNo(View v)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CALL_PHONE);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + charter_dtls_obj.getResult().getData().getPhone()));
            startActivity(intent);
        }
        else{
            getPermission();
        }
    }


    private class Value1ClickListener implements View.OnClickListener
    {
        TextView mButton;
        int id;

        Value1ClickListener(TextView b,int site_id)
        {
            mButton=b;
           id=site_id;
        }
        @Override
        public void onClick(View v) {
//
//            Toast.makeText(getActivity(),"Site ID: "+id,Toast.LENGTH_LONG).show();

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.contentContainer, FragmentSiteSelected.newInstance(id,""));
            ft.addToBackStack(new FragmentSiteSelected().getClass().getName());
            ft.commit();
        }

    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_address:




                break;
        }
    }

    @TargetApi(23)
    private void getPermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(new String[]{ android.Manifest.permission.CALL_PHONE},
                        1547);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1547:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhoneNo(null);
                } else {
                    Toast.makeText(getActivity(), "Permission Denied, Phone call could not be completed.", Toast.LENGTH_LONG).show();
                    //splashHandlar(SPLASH_TIME_OUT);
                }
                //splashHandlar(SPLASH_TIME_OUT);
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
