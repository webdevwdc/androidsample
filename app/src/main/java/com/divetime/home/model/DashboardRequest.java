
package com.divetime.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardRequest {

    @SerializedName("result")
    @Expose
    private DashboardResult result;

    public DashboardResult getResult() {
        return result;
    }

    public void setResult(DashboardResult result) {
        this.result = result;
    }

}
