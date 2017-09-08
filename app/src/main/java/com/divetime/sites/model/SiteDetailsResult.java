
package com.divetime.sites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteDetailsResult {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("data")
    @Expose
    private SiteDetailsData data;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public SiteDetailsData getData() {
        return data;
    }

    public void setData(SiteDetailsData data) {
        this.data = data;
    }

}
