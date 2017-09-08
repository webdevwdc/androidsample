
package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CharterDetailsRequest {

    @SerializedName("result")
    @Expose
    private CharterDetailsDataResult result;

    public CharterDetailsDataResult getResult() {
        return result;
    }

    public void setResult(CharterDetailsDataResult result) {
        this.result = result;
    }

}
