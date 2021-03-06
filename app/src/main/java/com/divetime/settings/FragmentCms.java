package com.divetime.settings;

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

public class FragmentCms extends Fragment implements View.OnClickListener,RetrofitListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String cms_type = "";

    RestHandler rest;
    ProgressDialog pDialog;
    TextView toolbar_header;

    Toolbar toolbar;

    WebView webview;

    private ProgressBar progressBar;


    public FragmentCms() {
        // Required empty public constructor
    }


    public static FragmentCms newInstance(String param1, String param2) {
        FragmentCms fragment = new FragmentCms();
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


        webview=(WebView)view.findViewById(R.id.wv_cms_desc);


        if (mParam1.equalsIgnoreCase("privacy-policy")){
            toolbar_header.setText("Privacy Policy");
        }else if (mParam1.equalsIgnoreCase("terms-conditions")){
            toolbar_header.setText("Terms of Services");
        }

        getCmsData(mParam1);

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
        webview.setBackgroundColor(Color.TRANSPARENT);

        WebSettings settings = webview.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        DisplayMetrics dm = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        webview.setMinimumHeight(dm.heightPixels);
        webview.setMinimumWidth(dm.widthPixels);
        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String pHtml = "<html><head><style> p { color: #000;} body { color: #000;} ol li{color: #000;} ul li{color: #000;}</style></head><body>"+webContent+"</body></html>";


        webview.loadDataWithBaseURL("", pHtml, mimeType, encoding, "");
        progressBar.setVisibility(View.GONE);
    }
}
