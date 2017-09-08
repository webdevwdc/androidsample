package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class SetUserSettingsRequest {

    @SerializedName("result")
    @Expose
    private SetUserSettingsResult result;

    public SetUserSettingsResult getResult() {
        return result;
    }

    public void setResult(SetUserSettingsResult result) {
        this.result = result;
    }
}
