
package com.divetime.sites.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityRequest {

    @SerializedName("result")
    @Expose
    private CityResult result;

    public CityResult getResult() {
        return result;
    }

    public void setResult(CityResult result) {
        this.result = result;
    }

}
