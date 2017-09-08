package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class PaymentRequest {

    @SerializedName("result")
    @Expose
    private PaymentResponse result;

    public PaymentResponse getResult() {
        return result;
    }

    public void setResult(PaymentResponse result) {
        this.result = result;
    }
}
