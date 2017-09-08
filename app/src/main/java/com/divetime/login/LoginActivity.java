package com.divetime.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.databinding.ActivityLoginBinding;
import com.divetime.login.model.ForgotPasswordRequest;
import com.divetime.login.model.LoginRequest;
import com.divetime.login.model.User;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.signup.SignupActivity;
import com.divetime.signup.model.SignupRequest;
import com.divetime.utils.Constants;
import com.divetime.utils.GPSCapture;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements OnClickListener,RetrofitListener {

    private CallbackManager callbackManager;
    ProgressDialog pDialog;
    private String fb_id="",fb_profile_pic_url="",first_name="",last_name="",email="";

    ActivityLoginBinding activity_binding;
    RestHandler rest;
    GPSCapture gps_tracker;
    Activity a;
    boolean isFirstTime=true;
    private String gps_address="",latitude="",longitude="";

    EditText edt_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity_binding=DataBindingUtil.setContentView(this,R.layout.activity_login);
        activity_binding.setUser(new User());
        activity_binding.setActivity(this);
        init();
    }

    private void init()
    {
        gps_tracker=new GPSCapture(this);
        rest=new RestHandler(this,this);
        pDialog=Constants.getDialog(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras=intent.getExtras();
            if(extras!=null && extras.containsKey("latitude"))
            {
                latitude=extras.getString("latitude");
                longitude=extras.getString("longitude");
                gps_address=extras.getString("address");
            }

        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        if (GPSCapture.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this, this)) {
            gps_tracker.startLocationUpdates();

            registerReceiver(mMessageReceiver, new IntentFilter("gps_callback"));

        } else {
            if(isFirstTime)
            GPSCapture.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 5444, this, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 5444:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps_tracker.startLocationUpdates();


                } else {
                    isFirstTime=false;
                    Toast.makeText(this, "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();
                    //splashHandlar(SPLASH_TIME_OUT);
                }
                //splashHandlar(SPLASH_TIME_OUT);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gps_tracker.stopLocationUpdates();


        try {
            unregisterReceiver(mMessageReceiver);
        }
        catch(Exception e)
        {
//            Toast.makeText(this,"Not Registered",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(callbackManager!=null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onFblogin()
    {
        pDialog.show();
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email",
                "user_friends", "public_profile"/*, "user_birthday"*/));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                getFacebookData(object);

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, " +
                                "email,gender, birthday, location"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");

                        if(pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        if(exception!=null) {

                            AccessToken.setCurrentAccessToken(null);
                            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "user_friends", "public_profile"));
                        }
                        if(pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
    }

    private void getFacebookData(JSONObject object) {

        try {
            fb_id = object.getString("id");


            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + fb_id + "/picture?width=200&height=200");

                fb_profile_pic_url=profile_pic.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (object.has("first_name")) {
                first_name=object.getString("first_name");
            }
            if (object.has("last_name")) {
                last_name=object.getString("last_name");
            }
            if (object.has("email")) {
                email=object.getString("email");
            }
            if (object.has("gender")) {
            }
            if (object.has("birthday")) {
            }
            if (object.has("location")) {
            }

            doFbRegistrationStep1(0);

//            Toast.makeText(this,first_name+" "+last_name,Toast.LENGTH_LONG).show();

            if(pDialog.isShowing())
                pDialog.dismiss();
        }
        catch(JSONException e) {
            Log.d("Dsdsd","Error parsing JSON");
        }
    }

    private boolean validate(User user)
    {

        if(user.email.get()==null || user.email.get().trim().length()==0)
        {
            Constants.showAlert(this,"Please enter email.");
            return false;
        }
        else if(!Constants.emailValidator(user.email.get().trim()))
        {
            Constants.showAlert(this,"Please enter valid email.");
            return false;
        }
        else if(user.password.get()==null || user.password.get().trim().length()==0)
        {
            Constants.showAlert(this,"Please enter password.");
            return false;
        }
       /* else if(user.password.get()==null || user.password.get().trim().length()<6)
        {
            Constants.showAlert(this,"Password must be of atleast 5 characters.");
            return false;
        }*/

        return true;
    }

    private void doFbRegistrationStep1(int register_done) {

        pDialog.show();

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                register(Constants.getFCMToken(this),
                        fb_id,
                        first_name,
                        last_name,
                        email,
                        "",
                        Constants.registration_type_fb,
                        Constants.device_type,register_done,
                        gps_address,latitude,longitude),"Fbregistration");

        pDialog.show();
    }

    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_register:

                Intent intent=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

                break;

            case R.id.btn_signin:

           /*     if(validate(activity_binding.getUser())) {
                    Intent intent_landing = new Intent(LoginActivity.this, LandingActivity.class);
                    startActivity(intent_landing);
                    finish();
                }*/
                if(validate(activity_binding.getUser()))
                    doLogin(activity_binding.getUser());

                break;

            case R.id.fb_login:

                onFblogin();

                break;

            case R.id.tv_forgot_pwd:

                showResetPwdDialog();

                break;
        }

    }

    private void doLogin(User user)
    {
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                login(Constants.getFCMToken(this),user.email.get().trim(),
                        user.password.get().trim(),Constants.device_type,
                        gps_address,latitude,longitude),"login");

        pDialog.show();
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("login"))
            {
                LoginRequest signup_obj=(LoginRequest)response.body();
                if(!signup_obj.getResult().getError())
                {
                    // login succesfull
                    hideSoftKeyboard();
                    storeUserDetails(signup_obj);
                    Toast.makeText(LoginActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Intent intentLanding=new Intent(LoginActivity.this, LandingActivity.class);
                    startActivity(intentLanding);
                    finish();
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Constants.showAlert(LoginActivity.this,signup_obj.getResult().getMessage());
                }
            }

            else if(method.equalsIgnoreCase("Fbregistration"))
            {
                SignupRequest signup_obj=(SignupRequest)response.body();
                if(!signup_obj.getResult().getError())
                {

                    FbSignUpRequest(signup_obj);

                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Constants.showAlert(LoginActivity.this,signup_obj.getResult().getMessage());
                }
            }
            else if(method.equalsIgnoreCase("forgot_pwd"))
            {
                ForgotPasswordRequest forgot_obj=(ForgotPasswordRequest)response.body();

//                    if(!forgot_obj.getResult().getError())
//                    {

                Constants.showAlert(LoginActivity.this,forgot_obj.getResult().getMessage());

//                    }
            }

//                else if(method.equalsIgnoreCase("registration"))
//                {
//                    SignupRequest signup_obj=(SignupRequest)response.body();
//                    processResponse(signup_obj);
//                }

          /*  else if(method.equalsIgnoreCase("Fbregistration"))
            {
                SignupRequest signup_obj=(SignupRequest)response.body();
                if(!signup_obj.getResult().getError())
                {

                    // signup succesfull
                    FbSignUpRequest(signup_obj);
//                        Intent intentLanding=new Intent(LoginActivity.this, LandingActivity.class);
//                        startActivity(intentLanding);
//                        finish();
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Constants.showAlert(LoginActivity.this,signup_obj.getResult().getMessage());
                }
            }*/
           /* else if(method.equalsIgnoreCase("forgot_pwd"))
            {
                ForgotPasswordRequest forgot_obj=(ForgotPasswordRequest)response.body();

//                    if(!forgot_obj.getResult().getError())
//                    {

                Constants.showAlert(LoginActivity.this,forgot_obj.getResult().getMessage());

//                    }
            }*/
        }
        else{
            try {
                Constants.showAlert(this,response.code()+" "+response.message());
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

        Constants.showAlert(this,errorMessage);


    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void FbSignUpRequest(SignupRequest signup_obj) {

        Log.d("test","");


        if(signup_obj.getResult().getData().getCreatedAt()==null || signup_obj.getResult().getData().getCreatedAt()=="" ||
                signup_obj.getResult().getData().getCreatedAt().isEmpty()) {

            Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
            intent.putExtra("registration_type",Constants.registration_type_fb);
            intent.putExtra("fbId",fb_id);
            intent.putExtra("first_name",first_name);
            intent.putExtra("last_name",last_name);
            intent.putExtra("email",email);

            intent.putExtra("address",gps_address);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);

            startActivity(intent);

        }
        else {
            //======================

            //parse data and store response in shared pref & go go dashboard

            storeFbUserDetails(signup_obj);
            Intent intentLanding=new Intent(LoginActivity.this, LandingActivity.class);
            Toast.makeText(LoginActivity.this,"Successfully Logged in..",Toast.LENGTH_LONG).show();
            startActivity(intentLanding);
            finish();
        }

    }

    private void storeUserDetails(LoginRequest req)
    {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).edit();

        editor.putString(Constants.strShPrefUserType,req.getResult().getData().getUserType());
        editor.putString(Constants.strShPrefRegistrationType,req.getResult().getData().getRegistrationType());
        editor.putString(Constants.strShPrefUserLocationSet,"No");
        editor.putBoolean(Constants.strShPrefUserloginStatus, true);
        editor.putString(Constants.strShPrefUserEmail, req.getResult().getData().getEmail());
        editor.putString(Constants.strShPrefUserFirstName, req.getResult().getData().getFirstName());
        editor.putString(Constants.strShPrefUserLastName, req.getResult().getData().getLastName());
      /*  editor.putString(Constants.strShPrefUserZip, edtAddress.getEditableText().toString().trim());
        editor.putString(Constants.strShPrefUserCity,edt_city.getEditableText().toString().trim());
        editor.putString(Constants.strShPrefUserLanguage,req.getResult().getData().getLanguageId());*/
        editor.putInt(Constants.strShPrefUserID,req.getResult().getData().getId());

        editor.commit();

    }

    private void storeFbUserDetails(SignupRequest req)
    {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).edit();

        editor.putString(Constants.strShPrefUserType,req.getResult().getData().getUserType());
        editor.putString(Constants.strShPrefRegistrationType,req.getResult().getData().getRegistrationType());
        editor.putString(Constants.strShPrefUserLocationSet,"No");
        editor.putBoolean(Constants.strShPrefUserloginStatus, true);
        editor.putString(Constants.strShPrefUserEmail, req.getResult().getData().getEmail());
        editor.putString(Constants.strShPrefUserFirstName, req.getResult().getData().getFirstName());
        editor.putString(Constants.strShPrefUserLastName, req.getResult().getData().getLastName());
        editor.putInt(Constants.strShPrefUserID,req.getResult().getData().getId());

        editor.commit();

    }

    private void submitRequest(String email) {


        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                forgotPwd(email),"forgot_pwd");

        pDialog.show();
    }


    private void showResetPwdDialog() {
        String keyword="";
        final Dialog dialog = new Dialog(this);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_reset);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView header=(TextView)dialog.findViewById(R.id.header);
        TextView header2=(TextView)dialog.findViewById(R.id.header2);
        edt_email=(EditText) dialog.findViewById(R.id.edt_cnf_new_pwd);
        TextView btn_search=(TextView)dialog.findViewById(R.id.btn_search);
        TextView btn_cancel=(TextView)dialog.findViewById(R.id.btn_cancel);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edt_email.getText().toString().trim().length()==0)
                    Constants.showAlert(LoginActivity.this,"Please enter email");
                else {
                    submitRequest(edt_email.getText().toString());
                    dialog.dismiss();

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}

