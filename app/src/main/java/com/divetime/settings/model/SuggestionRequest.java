package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class SuggestionRequest {

    @SerializedName("result")
    @Expose
    private SuggestionResult result;

    public SuggestionResult getResult() {
        return result;
    }

    public void setResult(SuggestionResult result) {
        this.result = result;
    }
}
