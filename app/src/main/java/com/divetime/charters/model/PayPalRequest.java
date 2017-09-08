
package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayPalRequest {

    @SerializedName("result")
    @Expose
    private PayPalResult result;

    public PayPalResult getResult() {
        return result;
    }

    public void setResult(PayPalResult result) {
        this.result = result;
    }

}
