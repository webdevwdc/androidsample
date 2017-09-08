package com.divetime;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.divetime.boats.FragmentBoats;
import com.divetime.charters.FragmentCharters;
import com.divetime.charters.FragmentPayment;
import com.divetime.charters.model.PayPalRequest;
import com.divetime.home.FragmentHome;
import com.divetime.home.model.LocationUpdateRequest;
import com.divetime.retrofit.RestHandler;
import com.divetime.retrofit.RetrofitListener;
import com.divetime.settings.FragmentSettings;
import com.divetime.settings.adapter.SettingsAdapter;
import com.divetime.sites.FragmentSites;
import com.divetime.utils.Constants;
import com.divetime.utils.GPSCapture;
import com.divetime.utils.TrackApplication;

import retrofit2.Call;
import retrofit2.Response;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener,RetrofitListener{

    RelativeLayout btm_charters,btm_sites,btm_home,btm_boats;
    RestHandler rest;
    String address="",latitude="",longitude="",push_type="";
    GPSCapture gps_tracker;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        gps_tracker=new GPSCapture(this);
        rest=new RestHandler(this,this);

        pDialog=Constants.getDialog(this);

        btm_charters=(RelativeLayout)findViewById(R.id.btm_charters);
        btm_sites=(RelativeLayout)findViewById(R.id.btm_sites);
        btm_home=(RelativeLayout)findViewById(R.id.btm_home);
        btm_boats=(RelativeLayout)findViewById(R.id.btm_boats);

        btm_charters.setOnClickListener(this);
        btm_sites.setOnClickListener(this);
        btm_home.setOnClickListener(this);
        btm_boats.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        Log.d("jhdjfhjhg",extras.getString("type"));
        else
            Log.d("jhdjfhjhg","null");

        if (extras != null && extras.getString("type").equalsIgnoreCase("Discount")) {
            findViewById(R.id.btm_charters).performClick();
        }
        else {
            findViewById(R.id.btm_home).performClick();
        }

        if (GPSCapture.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this, this)) {
            gps_tracker.startLocationUpdates();

            registerReceiver(mMessageReceiver, new IntentFilter("gps_callback"));

        } else {
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
                    registerReceiver(mMessageReceiver, new IntentFilter("gps_callback"));

                } else {
                    Toast.makeText(this, "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();
                    //splashHandlar(SPLASH_TIME_OUT);
                }
                //splashHandlar(SPLASH_TIME_OUT);
                break;
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras=intent.getExtras();
            if(extras!=null && extras.containsKey("latitude"))
            {
                latitude=extras.getString("latitude");
                longitude=extras.getString("longitude");
                address=extras.getString("address");
                postLocationUpdates();
            }
        }
    };

    @Override
    public void onBackPressed() {

        Fragment fragment = getFragmentManager().findFragmentById(R.id.contentContainer);

        int count = getFragmentManager().getBackStackEntryCount();
        if (count < 2)
            showConfirmDialog();
        else if (fragment instanceof FragmentPayment) {

            if(TrackApplication.getInstance().getPay_key()!=null && TrackApplication.getInstance().getPay_key().length()>0)
            {
                callService(TrackApplication.getInstance().getPay_key());
            }
            else
                super.onBackPressed();
//            showConfirmDialog2(fragment);
        }
        else
            super.onBackPressed();
    }

    private void callService(String pay_key)
    {
        if(pDialog!=null)
            pDialog.show();

        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).callService(pay_key,""),"paypal_back");
    }

    private void showConfirmDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder
                .setMessage("Do you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showConfirmDialog2(final Fragment frag) {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder
                .setMessage("Do you want to cancel payment?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        getFragmentManager().beginTransaction().remove(frag).commit();
                        getFragmentManager().popBackStack();
                        findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
                        TrackApplication.getInstance().setPay_key(null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        findViewById(R.id.bottom_bar).setVisibility(View.GONE);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        Fragment curr_fragment = getFragmentManager().findFragmentById(R.id.contentContainer);

        switch(v.getId())
        {

            case R.id.btm_charters:

                if (!(curr_fragment instanceof FragmentCharters)) {
                    FragmentCharters newFragment = new FragmentCharters();
                    replaceFragment(newFragment);
                    TrackApplication.getInstance().setCharter_listing(null);
                }

                break;

            case R.id.btm_sites:

                if (!(curr_fragment instanceof FragmentSites)) {
                    FragmentSites newFragment1 = new FragmentSites();
                    replaceFragment(newFragment1);
                    TrackApplication.getInstance().setFiltered_site_listing(null);
                }

                break;

            case R.id.btm_home:

                if (!(curr_fragment instanceof FragmentHome)) {
                    FragmentHome newFragment3 = new FragmentHome();
                    replaceFragment(newFragment3);
                }

                break;

            case R.id.btm_boats:

                if (!(curr_fragment instanceof FragmentBoats)) {
                    FragmentBoats newFragment4 = new FragmentBoats();
                    replaceFragment(newFragment4);
                }

                break;
        }
    }

    public void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate (backStateName, 0);
        if (!fragmentPopped){ //fragment not in back stack, create it.
               FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.contentContainer, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


    @Override
    public void onSuccess(Call call, Response response, String method) {

        if(pDialog!=null && pDialog.isShowing())
            pDialog.dismiss();

        if (response != null && response.code() == 200) {

            if (method.equalsIgnoreCase("location_update")) {

                LocationUpdateRequest obj=(LocationUpdateRequest)response.body();

                if(!obj.getResult().getError()) {
                    gps_tracker.stopLocationUpdates();

                    try {
                        unregisterReceiver(mMessageReceiver);
                    }
                    catch(Exception e)
                    {
//            Toast.makeText(this,"Not Registered",Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if(method.equalsIgnoreCase("paypal_back"))
            {
                PayPalRequest paypal_obj=(PayPalRequest)response.body();
                if(!paypal_obj.getResult().getError())
                {
                    if(!paypal_obj.getResult().getData().getMessage().equalsIgnoreCase("payment-not-done"))
                    {

                            getFragmentManager().popBackStack();
                            findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
                    }
                    else{

                        showConfirmDialog2(null);
                    }
                }
                else{
//                    Toast.makeText(SignupActivity.this,signup_obj.getResult().getMessage(),Toast.LENGTH_LONG).show();
//                    Constants.showAlert(LoginActivity.this,signup_obj.getResult().getMessage());
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FragmentSettings.callbackManager != null) {
            FragmentSettings.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (SettingsAdapter.callbackManager != null) {
            SettingsAdapter.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }

    public void postLocationUpdates()
    {
        rest.makeHttpRequest(rest.retrofit.create(RestHandler.RestInterface.class).
                locationUpdate(Constants.getUserId(this),address,latitude,longitude),"location_update");
    }
}
