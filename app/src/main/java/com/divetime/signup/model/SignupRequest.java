
package com.divetime.signup.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignupRequest {

    @SerializedName("result")
    @Expose
    private SignupResult result;

    public SignupResult getResult() {
        return result;
    }

    public void setResult(SignupResult result) {
        this.result = result;
    }

}
