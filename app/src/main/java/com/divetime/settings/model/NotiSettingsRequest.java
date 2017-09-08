package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class NotiSettingsRequest {

    @SerializedName("result")
    @Expose
    private NotiSettingsResponse result;

    public NotiSettingsResponse getResult() {
        return result;
    }

    public void setResult(NotiSettingsResponse result) {
        this.result = result;
    }
}
