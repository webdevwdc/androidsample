
package com.divetime.sites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteDetailsRequest {

    @SerializedName("result")
    @Expose
    private SiteDetailsResult result;

    public SiteDetailsResult getResult() {
        return result;
    }

    public void setResult(SiteDetailsResult result) {
        this.result = result;
    }

}
