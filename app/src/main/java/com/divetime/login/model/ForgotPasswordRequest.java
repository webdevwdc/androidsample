
package com.divetime.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {

    @SerializedName("result")
    @Expose
    private ForgotPasswordResult result;

    public ForgotPasswordResult getResult() {
        return result;
    }

    public void setResult(ForgotPasswordResult result) {
        this.result = result;
    }

}
