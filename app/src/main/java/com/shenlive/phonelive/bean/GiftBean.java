package com.shenlive.phonelive.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/3/28.
 */
public class GiftBean {

    /**
     * id : 27
     * type : 1
     * sid : 0
     * giftname : 啤酒
     * needcoin : 20
     * gifticon_mini :
     * gifticon : http://yy.yunbaozhibo.com/public/appcmf/data/upload/20160525/57450c7d614ce.png
     * orderno : 0
     * addtime : 1464142983
     * experience : 200
     */

    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private int type;
    @SerializedName("sid")
    private int sid;
    @SerializedName("giftname")
    private String giftname;
    @SerializedName("needcoin")
    private int needcoin;
    @SerializedName("gifticon_mini")
    private String gifticon_mini;
    @SerializedName("gifticon")
    private String gifticon;
    @SerializedName("experience")
    private String experience;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public int getNeedcoin() {
        return needcoin;
    }

    public void setNeedcoin(int needcoin) {
        this.needcoin = needcoin;
    }

    public String getGifticon_mini() {
        return gifticon_mini;
    }

    public void setGifticon_mini(String gifticon_mini) {
        this.gifticon_mini = gifticon_mini;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
