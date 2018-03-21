package com.shenlive.phonelive.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zqc on 2018/1/15.
 */

public class DirectoryBean {

    /**
     * id : 1
     * title : 颜值
     */
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
