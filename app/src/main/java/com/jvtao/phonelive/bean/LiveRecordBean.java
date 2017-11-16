package com.jvtao.phonelive.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LiveRecordBean implements Parcelable {
    @SerializedName("uid")
    private int uid;
    @SerializedName("showid")
    private String showid;
    @SerializedName("islive")
    private int islive;
    @SerializedName("starttime")
    private String starttime;
    @SerializedName("endtime")
    private String endtime;
    @SerializedName("nums")
    private String nums;
    @SerializedName("title")
    private String title;
    @SerializedName("datetime")
    private String datetime;
    @SerializedName("video_url")
    private String video_url;
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.showid);
        dest.writeInt(this.islive);
        dest.writeString(this.starttime);
        dest.writeString(this.endtime);
        dest.writeString(this.nums);
        dest.writeString(this.title);
        dest.writeString(this.datetime);
        dest.writeString(this.video_url);
    }

    protected LiveRecordBean(Parcel in) {
        this.uid = in.readInt();
        this.showid = in.readString();
        this.islive = in.readInt();
        this.starttime = in.readString();
        this.endtime = in.readString();
        this.nums = in.readString();
        this.title = in.readString();
        this.datetime = in.readString();
        this.video_url = in.readString();
    }

    public static final Parcelable.Creator<LiveRecordBean> CREATOR = new Parcelable.Creator<LiveRecordBean>() {
        @Override
        public LiveRecordBean createFromParcel(Parcel source) {
            return new LiveRecordBean(source);
        }

        @Override
        public LiveRecordBean[] newArray(int size) {
            return new LiveRecordBean[size];
        }
    };
}
