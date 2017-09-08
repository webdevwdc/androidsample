package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class GetUserSettingsData {

    @SerializedName("event")
    @Expose
    private String event;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("discount_dive")
    @Expose
    private String discountDive;
    @SerializedName("trip_cancel")
    @Expose
    private String tripCancel;
    @SerializedName("mile")
    @Expose
    private String mile;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDiscountDive() {
        return discountDive;
    }

    public void setDiscountDive(String discountDive) {
        this.discountDive = discountDive;
    }

    public String getTripCancel() {
        return tripCancel;
    }

    public void setTripCancel(String tripCancel) {
        this.tripCancel = tripCancel;
    }

    public String getMile() {
        return mile;
    }

    public void setMile(String mile) {
        this.mile = mile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
