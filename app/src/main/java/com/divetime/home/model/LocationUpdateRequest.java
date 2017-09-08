
package com.divetime.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationUpdateRequest {

    @SerializedName("result")
    @Expose
    private LocationUpdateResult result;

    public LocationUpdateResult getResult() {
        return result;
    }

    public void setResult(LocationUpdateResult result) {
        this.result = result;
    }

}
