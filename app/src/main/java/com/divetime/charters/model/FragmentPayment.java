package com.divetime.charters.model;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.charters.FragmentCharters;
import com.divetime.charters.adapters.ChartersAdapters;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCharters.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCharters#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPayment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Toolbar toolbar;
    TextView toolbar_header;
    LinearLayoutManager mLayoutManager;

    RecyclerView mRecycler;

    ChartersAdapters adapterList;

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
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.activity_payment_gateway, container, false);

        initView(view);

        return view;
    }

    private void initView(View view)
    {
        progressDialog=new ProgressDialog(getActivity());
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);

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
            }
        });

        toolbar_header.setText("Payment");

       /* Intent oIntent  = getIntent();

        if(oIntent.getExtras()!=null) {
            //itemName = oIntent.getExtras().getString("Name");
            total_price = oIntent.getExtras().getString("TotalAmount");
            orderID = oIntent.getExtras().getString("OrderID");
            //orderDescription = oIntent.getExtras().getString("Description");
            email_add = oIntent.getExtras().getString("EmailAdd");
        }*/
        //webView = new WebView(this);
        webView = (WebView) view.findViewById(R.id.webview);

        webView.setWebViewClient(new MyWebViewClient(){

            boolean loadingFinished = true;
            boolean redirect = false;
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("https://www.sandbox.paypal.com/webapps/adaptivepayment/flow/corepay?execution=e2s2")){
                    /*Intent intent = new Intent(PayMentGateWay.this, LandingActivity.class);
                    intent.putExtra("strUrl","payfastcancel");
                    finish();
                    progressDialog = null;
                    startActivity(intent);*/

                    //updateOrderPaymentStatus(orderID, "cancel");

                    //Toast.makeText(getActivity(), "Payment Successfull", Toast.LENGTH_LONG).show();
                    showAlert(getActivity(),"Payment Successfull");
                    if (!loadingFinished) {
                        redirect = true;
                    }


                }
                if (url.contains("https://www.sandbox.paypal.com/webapps/adaptivepayment/flow/closewindow")){

                    //updateOrderPaymentStatus(orderID, "complete");
                    getActivity().onBackPressed();
                    if (!loadingFinished) {
                        redirect = true;
                    }
                    //Toast.makeText(getActivity(), "Payment Successfull", Toast.LENGTH_LONG).show();


                }
                if (url.contains("payfastnotify")){

                }

                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;
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
                    if (progressDialog!=null && getActivity()!=null)
                        progressDialog.dismiss();
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
                    progressDialog.show();
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
        //String strPayFastUrl = makeParams();
        String strPayFastUrl="https://www.sandbox.paypal.com/webapps/adaptivepayment/flow/pay?paykey="+mParam1+"&expType=mini";
        webView.loadUrl(strPayFastUrl);


    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url.startsWith("http")){
                //Toast.makeText(getApplicationContext(),url ,Toast.LENGTH_LONG).show();
                progressDialog.show();
                view.loadUrl(url);
                System.out.println("myresult "+url);
                //return true;
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

    public void showAlert(Context con, String msg)
    {

        if(con==null)
            return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        getActivity().onBackPressed();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
