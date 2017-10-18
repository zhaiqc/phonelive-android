package com.ylive.phonelive.bean;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 奖项实体
 * Created by bakumon on 16-11-12.
 */

public class PrizeVo implements Serializable {
    @SerializedName("id")
    public String id;//
    @SerializedName("title")
    public String title;// 奖品名称,奖品名称,
    @SerializedName("rate")
    public String rate;// 倍率，大于1的整数,倍率，大于1的整数,
    @SerializedName("img")
    public Bitmap img;
}
