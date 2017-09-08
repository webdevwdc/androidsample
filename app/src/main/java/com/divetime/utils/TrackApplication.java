package com.divetime.utils;

import android.app.Application;

import com.divetime.charters.model.CharterListingDatum;
import com.divetime.charters.model.CharterListingRequest;
import com.divetime.sites.model.SiteListingRequest;

/**
 * Created by android on 6/6/17.
 */

public class TrackApplication extends Application {
    private GoogleApiHelper googleApiHelper;
    private static TrackApplication mInstance;

    private SiteListingRequest filtered_site_listing;
    private CharterListingRequest charter_listing;
    private String pay_key=null;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized TrackApplication getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    public SiteListingRequest getFiltered_site_listing() {
        return filtered_site_listing;
    }

    public void setFiltered_site_listing(SiteListingRequest filtered_site_listing) {
        this.filtered_site_listing = filtered_site_listing;
    }

    public CharterListingRequest getCharter_listing() {
        return charter_listing;
    }

    public void setCharter_listing(CharterListingRequest charter_listing) {
        this.charter_listing = charter_listing;
    }

    public String getPay_key() {
        return pay_key;
    }

    public void setPay_key(String pay_key) {
        this.pay_key = pay_key;
    }
}
