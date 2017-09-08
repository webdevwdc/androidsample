
package com.divetime.sites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteTypeMasterRequest {

    @SerializedName("result")
    @Expose
    private SiteTypeMasterResult result;

    public SiteTypeMasterResult getResult() {
        return result;
    }

    public void setResult(SiteTypeMasterResult result) {
        this.result = result;
    }

}
