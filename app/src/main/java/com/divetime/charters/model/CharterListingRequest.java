
package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CharterListingRequest {

    @SerializedName("result")
    @Expose
    private CharterListingResult result;

    public CharterListingResult getResult() {
        return result;
    }

    public void setResult(CharterListingResult result) {
        this.result = result;
    }

}
