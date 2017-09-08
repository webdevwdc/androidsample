package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class GetUserSettingsResult {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("data")
    @Expose
    private GetUserSettingsData data;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public GetUserSettingsData getData() {
        return data;
    }

    public void setData(GetUserSettingsData data) {
        this.data = data;
    }


}
