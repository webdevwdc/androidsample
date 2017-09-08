
package com.divetime.charters.model;

import com.divetime.utils.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CharterListingDatum {

    int position=-1;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("capacity")
    @Expose
    private String capacity;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("booked_qty")
    @Expose
    private String bookedQty;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("site_all")
    @Expose
    private String siteAll;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("discount")
    @Expose
    private String discount;

    @SerializedName("dive_discount")
    @Expose
    private DiveDiscount diveDiscount;


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBookedQty() {
        return bookedQty;
    }

    public void setBookedQty(String bookedQty) {
        this.bookedQty = bookedQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSiteAll() {
        return siteAll;
    }

    public void setSiteAll(String siteAll) {
        this.siteAll = siteAll;
    }

    public String getDateFormatted(){
        return  Constants.changeDateFormat(getDate(),
                Constants.web_date_only_format,Constants.app_display_date_format);
    }

    public String getTimeFormatted(){
        return  Constants.changeDateFormat(getTime(),
                Constants.web_time_only_format,Constants.app_display_time_format);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiveDiscount getDiveDiscount() {
        return diveDiscount;
    }

    public void setDiveDiscount(DiveDiscount diveDiscount) {
        this.diveDiscount = diveDiscount;
    }

    public String getCalculatedDiscount()
    {
        double final_amt=0;

        if(diveDiscount==null || diveDiscount.getDiscount()==null)
            return amount;
        else {
           float disc=Float.parseFloat(diveDiscount.getDiscount())/100;
            final_amt=Float.parseFloat(amount)-(Float.parseFloat(amount)*disc)+0.5;
        }

        return (int)final_amt+"";

    }
}
