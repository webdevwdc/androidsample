
package com.divetime.sites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteListingRequest {

    @SerializedName("result")
    @Expose
    private SiteListingResult result;

    public SiteListingResult getResult() {
        return result;
    }

    public void setResult(SiteListingResult result) {
        this.result = result;
    }

}
