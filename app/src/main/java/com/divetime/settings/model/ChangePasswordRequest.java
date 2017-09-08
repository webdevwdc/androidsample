package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 27/4/17.
 */

public class ChangePasswordRequest {


    @SerializedName("result")
    @Expose
    private ChangePasswordResponse result;

    public ChangePasswordResponse getResult() {
        return result;
    }

    public void setResult(ChangePasswordResponse result) {
        this.result = result;
    }


}
