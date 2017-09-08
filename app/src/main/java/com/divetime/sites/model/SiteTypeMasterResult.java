
package com.divetime.sites.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteTypeMasterResult {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("data")
    @Expose
    private List<SiteTypeMasterResponse> data = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<SiteTypeMasterResponse> getData() {
        return data;
    }

    public void setData(List<SiteTypeMasterResponse> data) {
        this.data = data;
    }

}
