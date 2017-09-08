
package com.divetime.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutRequest {

    @SerializedName("result")
    @Expose
    private LogoutResult result;

    public LogoutResult getResult() {
        return result;
    }

    public void setResult(LogoutResult result) {
        this.result = result;
    }

}
