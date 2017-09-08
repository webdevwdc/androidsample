
package com.divetime.settings.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivacyPolicyData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cms_title")
    @Expose
    private String cmsTitle;
    @SerializedName("cms_slug")
    @Expose
    private String cmsSlug;
    @SerializedName("cms_desc")
    @Expose
    private String cmsDesc;
    @SerializedName("cms_meta_title")
    @Expose
    private String cmsMetaTitle;
    @SerializedName("cms_meta_key")
    @Expose
    private String cmsMetaKey;
    @SerializedName("cms_meta_desc")
    @Expose
    private String cmsMetaDesc;
    @SerializedName("cms_status")
    @Expose
    private String cmsStatus;
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

    public String getCmsTitle() {
        return cmsTitle;
    }

    public void setCmsTitle(String cmsTitle) {
        this.cmsTitle = cmsTitle;
    }

    public String getCmsSlug() {
        return cmsSlug;
    }

    public void setCmsSlug(String cmsSlug) {
        this.cmsSlug = cmsSlug;
    }

    public String getCmsDesc() {
        return cmsDesc;
    }

    public void setCmsDesc(String cmsDesc) {
        this.cmsDesc = cmsDesc;
    }

    public String getCmsMetaTitle() {
        return cmsMetaTitle;
    }

    public void setCmsMetaTitle(String cmsMetaTitle) {
        this.cmsMetaTitle = cmsMetaTitle;
    }

    public String getCmsMetaKey() {
        return cmsMetaKey;
    }

    public void setCmsMetaKey(String cmsMetaKey) {
        this.cmsMetaKey = cmsMetaKey;
    }

    public String getCmsMetaDesc() {
        return cmsMetaDesc;
    }

    public void setCmsMetaDesc(String cmsMetaDesc) {
        this.cmsMetaDesc = cmsMetaDesc;
    }

    public String getCmsStatus() {
        return cmsStatus;
    }

    public void setCmsStatus(String cmsStatus) {
        this.cmsStatus = cmsStatus;
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

}
