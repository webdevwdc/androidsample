package com.divetime.charters;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.divetime.R;
import com.divetime.charters.model.PayPalRequest;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.utils.Constants;
import com.divetime.utils.TrackApplication;

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
public class FragmentPayment extends Fragment implements RetrofitListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Toolbar toolbar;
    TextView toolbar_header;
    ProgressBar progress_bar;
    ProgressDialog pDialog;
    RestHandler rest;

    private OnFragmentInteractionListener mListener;
    private WebView webView;

    ProgressDialog progressDialog;
    public FragmentPayment() {
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
    public static FragmentPayment newInstance(String param1, String param2) {
        FragmentPayment fragment = new FragmentPayment();
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

        View view= inflater.inflate(R.layout.activity_payment_gateway, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        progressDialog= Constants.getDialog(getActivity());
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);
        progress_bar=(ProgressBar)view.findViewById(R.id.progress_bar);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        pDialog=Constants.getDialog(getActivity());

        rest=new RestHandler(getActivity(),this);
        getActivity().findViewById(R.id.bottom_bar).setVisibility(View.GONE);

        if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                callService();
            }
        });

        toolbar_header.setText("Payment");
        webView = (WebView) view.findViewById(R.id.webview);

        webView.setWebViewClient(new MyWebViewClient(){

            boolean loadingFinished = true;
            boolean redirect = false;
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

               if (url.contains("http://www.divetimeapp.com/payment_cancel")){
                   showAlert2("Payment has been cancelled.");
                }
                if (url.contains("http://divetimeapp.com/payment_success")){
                    showAlert2("Payment successful.");
                }

                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;

                if (!(url.contains("http://divetimeapp.com/payment_cancel")) && !(url.equalsIgnoreCase("http://divetimeapp.com/payment_success")))
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);


                if(!redirect){
                    loadingFinished = true;
                }
                if(loadingFinished && !redirect){
                 /*   if (progressDialog!=null && getActivity()!=null)
                        progressDialog.dismiss();*/

                 if(progress_bar!=null)
                     progress_bar.setVisibility(View.GONE);
                }
                else{
                    redirect = false;
                }
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                loadingFinished = false;
                //make sure dialog is showing
                if(progressDialog!=null && !progressDialog.isShowing()){
//                    progressDialog.show();
                    progress_bar.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(2);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);
        String strPayPalUrl="http://divetimeapp.com/proceed-to-payment/"+mParam1;
        webView.loadUrl(strPayPalUrl);
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("paypal_back"))
            {
                PayPalRequest paypal_obj=(PayPalRequest)response.body();
                if(!paypal_obj.getResult().getError())
                {
                    if(!paypal_obj.getResult().getData().getMessage().equalsIgnoreCase("payment-not-done"))
                    {
                        if(getActivity()!=null) {
                            getFragmentManager().popBackStack();
                            getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        getActivity().onBackPressed();
                    }


                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
//                    Constants.showAlert(LoginActivity.this,signup_obj.getResult().getMessage());
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
        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url.startsWith("http")){
                progress_bar.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                System.out.println("myresult "+url);
            } else {
                return false;
            }

            return true;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public void showAlertzzzz(String msg)
    {
        if(getActivity()==null)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Thank You");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        if(getActivity()!=null) {
//                            getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
                            getActivity().onBackPressed();
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    public void showAlert2(String msg)
    {
        if(getActivity()==null)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Thank You");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        if(getActivity()!=null) {
                            getFragmentManager().popBackStack();
                            getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callService()
    {
        if(pDialog!=null)
            pDialog.show();

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).callService(mParam1,""),"paypal_back");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("fdffg","");
        TrackApplication.getInstance().setPay_key(null);

    }
}
