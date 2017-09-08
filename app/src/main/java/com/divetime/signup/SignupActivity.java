package com.divetime.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.divetime.LandingActivity;
import com.divetime.R;
import com.divetime.databinding.ActivitySignupBinding;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.signup.model.SignupRequest;
import com.divetime.signup.model.SignupUserBinding;
import com.divetime.utils.Constants;

import retrofit2.Call;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements RetrofitListener{

    EditText edt_first_name,edt_last_name,edt_email,edt_password,edt_cnf_password;

    LinearLayout btn_register;

    RestHandler rest;
    ActivitySignupBinding signup_binding;
    private String facebookId="",first_name,last_name,email;
    private String registration_type=Constants.registration_type_nrml;
    private String gps_address="",latitude="",longitude="";

    ImageView pwd_ico,cnf_pwd_ico;
    View pwd_btn_view,cnf_pwd_btm_view;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signup_binding= DataBindingUtil.setContentView(this,R.layout.activity_signup);
        signup_binding.setUser(new SignupUserBinding());
        signup_binding.setActivity(this);

        init();
    }

    private void init()
    {
        edt_first_name=(EditText)findViewById(R.id.edt_old_pwd);
        edt_last_name=(EditText)findViewById(R.id.edt_new_pwd);
        edt_email=(EditText)findViewById(R.id.edt_cnf_new_pwd);
        edt_password=(EditText)findViewById(R.id.edt_password);
        edt_cnf_password=(EditText)findViewById(R.id.edt_cnf_password);
        btn_register=(LinearLayout)findViewById(R.id.btn_register);

        pwd_ico=(ImageView)findViewById(R.id.pwd_ico);
        cnf_pwd_ico=(ImageView)findViewById(R.id.cnf_pwd_ico);
        pwd_btn_view=findViewById(R.id.pwd_btn_view);
        cnf_pwd_btm_view=findViewById(R.id.cnf_pwd_btm_view);

        pDialog=Constants.getDialog(this);

        rest=new RestHandler(this,this);

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
            registration_type=Constants.registration_type_fb;
            facebookId=extras.getString("fbId");
            first_name=extras.getString("first_name");
            last_name=extras.getString("last_name");
            email=extras.getString("email");

            latitude=extras.getString("latitude");
            longitude=extras.getString("longitude");
            gps_address=extras.getString("address");

            edt_password.setVisibility(View.GONE);
            edt_cnf_password.setVisibility(View.GONE);
            edt_first_name.setText(first_name);
            edt_last_name.setText(last_name);
            edt_email.setText(email);

            pwd_ico.setVisibility(View.GONE);
            cnf_pwd_ico.setVisibility(View.GONE);
            cnf_pwd_btm_view.setVisibility(View.GONE);
            pwd_btn_view.setVisibility(View.GONE);

            signup_binding.getUser().firstName.set(first_name);
            signup_binding.getUser().lastName.set(last_name);
            signup_binding.getUser().email.set(email);
        }
    }

    private boolean validate(SignupUserBinding user)
    {

        if(user.firstName.get()==null || user.firstName.get().trim().length()==0)
        {
            Constants.showAlert(this,"Please enter first name.");
            return false;
        }
        else if(user.lastName.get()==null || user.lastName.get().trim().length()==0)
        {
            Constants.showAlert(this,"Please enter last name.");
            return false;
        }
        else if(user.email.get()==null || user.email.get().trim().length()==0)
        {
            Constants.showAlert(this,"Please enter email.");
            return false;
        }
        else if(!Constants.emailValidator(user.email.get().trim()))
        {
            Constants.showAlert(this,"Please enter valid email.");
            return false;
        }
        else if(registration_type.equalsIgnoreCase(Constants.registration_type_nrml) && (user.password.get()==null || user.password.get().trim().length()==0))
        {
            Constants.showAlert(this,"Please enter password.");
            return false;
        }
        else if(registration_type.equalsIgnoreCase(Constants.registration_type_nrml) && (user.password.get()==null || user.password.get().trim().length()<5))
        {
            Constants.showAlert(this,"Password must be of atleast 5 characters.");
            return false;
        }
        else if(registration_type.equalsIgnoreCase(Constants.registration_type_nrml) && !user.password.get().trim().equals(user.cnfPassword.get().trim()))
        {
            Constants.showAlert(this,"Passwords not match.");
            return false;
        }

        return true;
    }


    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_register:

                /*Intent intent=new Intent(this, SignupActivity.class);
                startActivity(intent);*/

                if(validate(signup_binding.getUser()))
                {
                    /*Intent intent_landing = new Intent(this, LandingActivity.class);
                    startActivity(intent_landing);
                    finish();*/

                    if(registration_type.equalsIgnoreCase(Constants.registration_type_nrml))
                        doRegistration();
                    else
                        doFbRegistration();
                }

                break;

        }

    }

    private void doRegistration() {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                register(Constants.getFCMToken(this),
                        facebookId,
                        edt_first_name.getText().toString().trim(),
                        edt_last_name.getText().toString().trim(),
                        edt_email.getText().toString().trim(),
                        edt_password.getText().toString().trim(),
                        registration_type,
                        Constants.device_type,0,"","",""),"registration");

        pDialog.show();
    }

    private void doFbRegistration() {

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                register(Constants.getFCMToken(this),
                        facebookId,
                        first_name,
                        last_name,
                        email,
                        "",
                        registration_type,
                        Constants.device_type,1,
                        gps_address,latitude,longitude),"Fb1registration");

        pDialog.show();
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if(response!=null && response.code()==200){

            if(method.equalsIgnoreCase("registration"))
            {
                SignupRequest signup_obj=(SignupRequest)response.body();
                if(!signup_obj.getResult().getError())
                {

                    hideSoftKeyboard();
                    storeUserDetails(signup_obj);

                    Toast.makeText(SignupActivity.this,"Registration Successful !!", Toast.LENGTH_SHORT).show();

                    Intent intentLanding=new Intent(SignupActivity.this, LandingActivity.class);
                    startActivity(intentLanding);
                    finish();
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Constants.showAlert(SignupActivity.this,signup_obj.getResult().getMessage());
                }
            }
            else if(method.equalsIgnoreCase("Fb1registration"))
            {
                SignupRequest signup_obj=(SignupRequest)response.body();
                if(!signup_obj.getResult().getError())
                {

                    hideSoftKeyboard();
                    storeUserDetails(signup_obj);

                    Toast.makeText(SignupActivity.this,"Registration Successful !!", Toast.LENGTH_SHORT).show();

                    Intent intentLanding=new Intent(SignupActivity.this, LandingActivity.class);
                    startActivity(intentLanding);
                    finish();
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
                    Constants.showAlert(SignupActivity.this,signup_obj.getResult().getMessage());
                }
            }

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


    private void storeUserDetails(SignupRequest req)
    {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE).edit();

        editor.putString(Constants.strShPrefUserType,req.getResult().getData().getUserType());
        editor.putString(Constants.strShPrefRegistrationType,req.getResult().getData().getRegistrationType());
        editor.putString(Constants.strShPrefUserLocationSet,"No");
        editor.putBoolean(Constants.strShPrefUserloginStatus, true);
        editor.putString(Constants.strShPrefUserFirstName, edt_first_name.getText().toString().trim());
        editor.putString(Constants.strShPrefUserLastName, edt_last_name.getText().toString().trim());
        editor.putString(Constants.strShPrefUserEmail, edt_email.getEditableText().toString().trim());
      /*  editor.putString(Constants.strShPrefUserZip, edtAddress.getEditableText().toString().trim());
        editor.putString(Constants.strShPrefUserCity,edt_city.getEditableText().toString().trim());
        editor.putString(Constants.strShPrefUserLanguage,req.getResult().getData().getLanguageId());*/
        editor.putInt(Constants.strShPrefUserID,req.getResult().getData().getId());

        editor.commit();

    }
}
