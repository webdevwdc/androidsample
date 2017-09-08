
package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivacyPolicyRequest {

    @SerializedName("result")
    @Expose
    private PrivacyPolicyResult result;

    public PrivacyPolicyResult getResult() {
        return result;
    }

    public void setResult(PrivacyPolicyResult result) {
        this.result = result;
    }

}
