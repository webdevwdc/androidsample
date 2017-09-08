package com.divetime.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by webskitters on 1/18/2017.
 */


    public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = GoogleApiHelper.class.getSimpleName();
            Context context;
            GoogleApiClient mGoogleApiClient;

    public GoogleApiHelper(Context context) {
        this.context = context;
            buildGoogleApiClient();
            connect();

            }

    public GoogleApiClient getGoogleApiClient() {
            return this.mGoogleApiClient;
            }

    public void connect() {
            if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            }
            }

    public void disconnect() {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            }
            }

    public boolean isConnected() {
            if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
            } else {
            return false;
            }
            }

    private void buildGoogleApiClient() {
        if(context!=null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(context)
//                .enableAutoManage( this, 0, this )
//                    .addApi(Places.GEO_DATA_API)
//                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i("onConnected", "Google Places API connected.");
//        new PlaceArrayAdapter(context,0).setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
//        mPlaceArrayAdapter.setGoogleApiClient(null);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
