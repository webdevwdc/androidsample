
package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayPalResult {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("data")
    @Expose
    private PayPalData data;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public PayPalData getData() {
        return data;
    }

    public void setData(PayPalData data) {
        this.data = data;
    }

}
