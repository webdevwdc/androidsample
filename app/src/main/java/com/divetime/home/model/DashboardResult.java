
package com.divetime.home.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardResult {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("data")
    @Expose
    private List<DashboardDatum> data = null;
    @SerializedName("article")
    @Expose
    private DashboardArticle article;
    @SerializedName("content")
    @Expose
    private List<DashboardContent> content = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<DashboardDatum> getData() {
        return data;
    }

    public void setData(List<DashboardDatum> data) {
        this.data = data;
    }

    public DashboardArticle getArticle() {
        return article;
    }

    public void setArticle(DashboardArticle article) {
        this.article = article;
    }

    public List<DashboardContent> getContent() {
        return content;
    }

    public void setContent(List<DashboardContent> content) {
        this.content = content;
    }

}
