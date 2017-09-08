package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class GetUserSettingsRequest {

    @SerializedName("result")
    @Expose
    private GetUserSettingsResult result;

    public GetUserSettingsResult getResult() {
        return result;
    }

    public void setResult(GetUserSettingsResult result) {
        this.result = result;
    }
}
