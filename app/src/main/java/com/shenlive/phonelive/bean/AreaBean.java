package com.shenlive.phonelive.bean;


import com.google.gson.annotations.SerializedName;

public class AreaBean {
    @SerializedName("province")
    private String province;
    @SerializedName("total")
    private int total;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
