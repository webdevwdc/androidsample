package com.divetime.charters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 28/6/17.
 */

public class PaymentData {

    @SerializedName("Ack")
    @Expose
    private String ack;
    @SerializedName("Build")
    @Expose
    private String build;
    @SerializedName("CorrelationID")
    @Expose
    private String correlationID;
    @SerializedName("Timestamp")
    @Expose
    private String timestamp;
    @SerializedName("PayKey")
    @Expose
    private String payKey;
    @SerializedName("PaymentExecStatus")
    @Expose
    private String paymentExecStatus;
    @SerializedName("RedirectURL")
    @Expose
    private String redirectURL;
    @SerializedName("XMLRequest")
    @Expose
    private String xMLRequest;
    @SerializedName("XMLResponse")
    @Expose
    private String xMLResponse;



    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    public String getPaymentExecStatus() {
        return paymentExecStatus;
    }

    public void setPaymentExecStatus(String paymentExecStatus) {
        this.paymentExecStatus = paymentExecStatus;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getXMLRequest() {
        return xMLRequest;
    }

    public void setXMLRequest(String xMLRequest) {
        this.xMLRequest = xMLRequest;
    }

    public String getXMLResponse() {
        return xMLResponse;
    }

    public void setXMLResponse(String xMLResponse) {
        this.xMLResponse = xMLResponse;
    }
}
