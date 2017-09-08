package com.divetime.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by android on 6/6/17.
 */

public class GPSCapture implements LocationListener {

    Context mContext;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1500 * 10;
    private static final long FASTEST_INTERVAL = 2000 * 5;

    public GPSCapture(Context mContext) {

        this.mContext=mContext;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(mContext instanceof Activity)
        settingsrequest();
    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d("GPS_TRACKER ",location.getLatitude()+","+location.getLongitude());

        if(location!=null) {

            Intent intent2 = new Intent("gps_callback");

            //put whatever data you want to send, if any
            intent2.putExtra("latitude", location.getLatitude() + "");
            intent2.putExtra("longitude", location.getLongitude() + "");
            intent2.putExtra("address", getAddress(location.getLatitude(), location.getLongitude()));
            //send broadcast
            mContext.sendBroadcast(intent2);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
            //Toast.makeText(VisitorHomePageActivity.this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }


    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(TrackApplication.getGoogleApiHelper().getGoogleApiClient()!=null
                && TrackApplication.getGoogleApiHelper().getGoogleApiClient().isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    TrackApplication.getGoogleApiHelper().getGoogleApiClient(), mLocationRequest, this);
            Log.d("TAG", "Location update started ..............: ");
        }
    }

    public void stopLocationUpdates() {
        if(TrackApplication.getGoogleApiHelper().getGoogleApiClient()!=null
                && TrackApplication.getGoogleApiHelper().getGoogleApiClient().isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    TrackApplication.getGoogleApiHelper().getGoogleApiClient(), this);
            Log.d("TAG", "Location update stopped .......................");
        }
    }


    private String getAddress(double mLatitude,double mLongitude)
    {

        String  gps_address="";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        // Get the current location from the input parameter list
        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
				/*
				 * Return 1 address.
				 */
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

        } catch (IOException e1) {
            e1.printStackTrace();
            return ("IO Exception trying to get address:" + e1);
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments "
                    + Double.toString(mLatitude) + " , "
                    + Double.toString(mLongitude)
                    + " passed to address service";
            e2.printStackTrace();
            return errorString;
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
           gps_address = String.format(
                    "%s, %s, %s",
                    // If there's a street address, add it
                    address.getMaxAddressLineIndex() > 0 ? address
                            .getAddressLine(0) : "",
                    // Locality is usually a city
                    address.getLocality(),
                    // The country of the address
                    address.getCountryName());
            // Return the text
            Log.d("adddddreessss",gps_address);
            return gps_address;
        } else {
            Log.d("adddddreessss",gps_address);
            return "No address found by the service: Note to the developers," +
                    " If no address is found by google itself, there is nothing you can do about it. :(";
        }
//        } else {
//            return "Location Not available";
//        }


    }

    public void settingsrequest()
    {
        Log.e("settingsrequest","Comes");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(TrackApplication.getGoogleApiHelper().getGoogleApiClient(), builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // Log.e("Application","Button Clicked");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Log.e("Application","Button Clicked1");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) mContext, 005);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("Applicationsett",e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //Log.e("Application","Button Clicked2");
                        Toast.makeText((Activity)mContext, "Location is Enabled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


}
