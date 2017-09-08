package com.divetime.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.R;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.adapter.SettingsAdapter;
import com.divetime.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class FragmentSettings extends Fragment implements View.OnClickListener, RetrofitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static RecyclerView mDialogRecyclerView;
   /* private static InviteFbFriendsAdapter mAdapter;*/

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView toolbar_header;
    Toolbar toolbar;
    private OnFragmentInteractionListener mListener;


    static RecyclerView mRecyclerView;
    LinearLayout img_back2;

    RestHandler rest;
    ProgressDialog pDialog;


    public static List<HashMap<String, String>> fbFriendList = new ArrayList();
    public static CallbackManager callbackManager;

    public static String currentFbId = "";
    private List<String> mDataArray;

    public FragmentSettings() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();
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

        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        mRecyclerView=(RecyclerView)view.findViewById(R.id.mRecyclerView);
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mDataArray= Arrays.asList(getResources().getStringArray(R.array.settings_items));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SettingsAdapter(mDataArray,getActivity()));
        toolbar_header=(TextView)view.findViewById(R.id.toolbar_header);

        rest=new RestHandler(getActivity(),this);
        pDialog=new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.dialog_msg));
        pDialog.setCancelable(false);

        toolbar_header.setText("Settings");

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

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

        }

    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){
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

        if(pDialog.isShowing())
            pDialog.dismiss();

        Constants.showAlert(getActivity(),errorMessage);

    }

    static ProgressDialog progressDialog;
    public static void getFbFriendList(){

        final Activity activity = (Activity)mRecyclerView.getContext();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

        callbackManager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "user_friends", "public_profile"/*, "user_birthday"*/));
//        LoginManager.logInWithReadPermissions(FbLoginActivity.this, Arrays.asList("email", "user_friends", "public_profile"/*, "user_birthday"*/));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("onSuccess");
//                       ProgressDialog progressDialog = new ProgressDialog(FbLoginActivity.this);
//                        progressDialog.setMessage("Procesando datos...");
//                        progressDialog.show();
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());
                                // Get facebook data from login
                                //Bundle bFacebookData = getFacebookData(object);

                                try {
                                    currentFbId = object.get("id").toString();
                                    JSONArray jsonArrayFriends = object.getJSONObject("friends").getJSONArray("data");
                                    String countFriends = object.getJSONObject("friends").getJSONObject("summary").get("total_count").toString();
                                    if (jsonArrayFriends!=null){
                                        for (int i=0; i<jsonArrayFriends.length();i++) {
                                            JSONObject friendlistObject = jsonArrayFriends.getJSONObject(i);
                                            String friendListID = friendlistObject.getString("id");
                                            fbFriendDtl(friendListID);
                                        }
                                        //if (fbFriendList.size()>0){
                                            /*dialogFbFriend(activity);*/
                                        //}
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, friends"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();

                        if(progressDialog.isShowing())
                            progressDialog.dismiss();



                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                        Toast.makeText(activity, "You have cancelled the authentication.", Toast.LENGTH_LONG).show();

                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        if(exception!=null) {
                            System.out.println("onError");
//                            Log.v("LoginActivity", exception.getCause().toString());
                            Toast.makeText(activity, "Facebook Login Error. "+exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }


    public static void getFbFriends() {
        getFbFriendList();
    }

    private static void fbFriendDtl(String friendlistId) {
        final String graphPath = "/"+friendlistId+"/members/";
        AccessToken token = AccessToken.getCurrentAccessToken();

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                //"/"+friendlistId+"/friends",
                "/"+friendlistId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        JSONObject object = response.getJSONObject();

                        try {

                            HashMap<String, String> map = new HashMap<>();
                            String id = "";
                            if (object.has("id")) {
                                id = object.getString("id");
                            }
                            map.put("id",id);
                            String name = "";
                            if (object.has("name")) {
                                name = object.getString("name");
                            }
                            map.put("name", name);
                            String fName = "";
                            if (object.has("first_name")) {
                                fName = object.getString("first_name");
                            }
                            map.put("first_name", fName);
                            String lName = "";
                            if (object.has("last_name")) {
                                lName = object.getString("last_name");
                            }
                            map.put("last_name", lName);
                            String mail = "";
                            if (object.has("email")) {
                                mail = object.getString("email");
                            }
                            map.put("email", mail);
                            String gender = "";
                            if (object.has("gender")) {
                                gender = object.getString("gender");
                            }
                            map.put("gender", gender);
                            if (object.has("birthday")) {
                                String bDay = object.getString("birthday");
                            }
                            if (object.has("location")) {
                                String location = object.getString("location");
                            }

                            fbFriendList.add(map);
                           /* mAdapter.notifyDataSetChanged();*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        Bundle param = new Bundle();
        param.putString("fields", "name, id, first_name, last_name, email,gender, birthday");
        request.setParameters(param);
        request.executeAsync();


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

    }

/*    private static void dialogFbFriend(Activity activity) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite_fb_users);
        activity.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setCancelable(false);
        mDialogRecyclerView=(RecyclerView)dialog.findViewById(R.id.my_recycler_view);
        ImageView img_back_dialog=(ImageView)dialog.findViewById(R.id.img_back_dialog);

        TextView txt_done=(TextView)dialog.findViewById(R.id.txt_done);
        EditText et_search = (EditText) dialog.findViewById(R.id.et_search);

        mAdapter=new InviteFbFriendsAdapter(fbFriendList, activity);
        mDialogRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mDialogRecyclerView.setAdapter(mAdapter);

        img_back_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fbFriendList.clear();
            }
        });
        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fbFriendList.clear();
            }
        });

        dialog.show();
    }*/
}
