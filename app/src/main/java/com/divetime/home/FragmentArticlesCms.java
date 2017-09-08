package com.divetime.home;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.model.PrivacyPolicyRequest;
import com.divetime.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android on 3/5/17.
 */

public class FragmentArticlesCms extends Fragment implements View.OnClickListener,RetrofitListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String cms_type = "";

    RestHandler rest;
    ProgressDialog pDialog;
    boolean isFinished=false;
    TextView toolbar_header;

    Toolbar toolbar;

    WebView webView;

    private ProgressBar progressBar;


    public FragmentArticlesCms() {
        // Required empty public constructor
    }


    public static FragmentArticlesCms newInstance(String param1, String param2) {
        FragmentArticlesCms fragment = new FragmentArticlesCms();
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

        View view=inflater.inflate(R.layout.fragment_cms, container, false);

        rest=new RestHandler(getActivity(),this);

        progressBar=(ProgressBar)view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);

        pDialog=new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.dialog_msg));
        pDialog.setCancelable(false);

        toolbar=(Toolbar)view.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        webView=(WebView)view.findViewById(R.id.wv_cms_desc);


        if (mParam1.equalsIgnoreCase("privacy-policy")){
            toolbar_header.setText("Privacy Policy");
        }else if (mParam1.equalsIgnoreCase("terms-conditions")){
            toolbar_header.setText("Terms of Services");
        }

        if(mParam2.equalsIgnoreCase("content"))
        toolbar_header.setText("Content");
        else
            toolbar_header.setText("Article");

//        getCmsData(mParam1);

//        setWebview("");

        startWebView(mParam1);
        return view;
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
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {



        }
    }


    private void getCmsData(String type) {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                getPriavcyPolicy(type),"getPriavcyPolicy");
        pDialog.show();
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {


        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if (method.equalsIgnoreCase("getPriavcyPolicy")) {
                PrivacyPolicyRequest req_Obj = (PrivacyPolicyRequest) response.body();
                if(!req_Obj.getResult().getError()) {

                    String description = req_Obj.getResult().getData().getCmsDesc();
                    setWebview(description);

                }

                else {
                    Toast.makeText(getActivity(), "Server Response Error.. ", Toast.LENGTH_LONG).show();
                    // Constants.showAlert(getActivity(),req_Obj.getResult().getData().getCmsDesc());

                }



            }
        }
        else{
            try {
                response.errorBody().string();
//                label.setText(response.code()+" "+response.message());
//                showAlertDialog("Alert",response.code()+" "+response.message());
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
        Constants.showAlert(getActivity(),errorMessage);
    }


    private void setWebview(String webContent){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.androidexample.com/media/webview/login.html");
    }



    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null && !isFinished) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog!=null && progressDialog.isShowing()) {
                        isFinished=true;
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);

        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null);
         */

        //Load url in webview
        webView.loadUrl(url);


    }
}
