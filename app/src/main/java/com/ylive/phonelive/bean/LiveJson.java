package com.ylive.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weipeng on 2017/1/17.
 */

public class LiveJson implements Parcelable {


    /**
     * uid : 7671
     * avatar : http://live.yunbaozhibo.com/api/public/upload/avatar/default.jpg
     * avatar_thumb : http://live.yunbaozhibo.com/api/public/upload/avatar/default_thumb.jpg
     * user_nicename : 测试
     * title :
     * city : 好像在火星
     * stream : 7671_1484145728
     * nums : 0
     */

    @SerializedName("uid")
    public String uid;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("avatar_thumb")
    public String avatar_thumb;
    @SerializedName("user_nicename")
    public String user_nicename;
    @SerializedName("title")
    public String title;
    @SerializedName("city")
    public String city;
    @SerializedName("stream")
    public String stream;
    @SerializedName("nums")
    public String nums;
    @SerializedName("distance")
    public String distance;
    @SerializedName("pull")
    public String pull;
    @SerializedName("thumb")
    public String thumb;
//    @SerializedName("label")
//    public String label;
    @SerializedName("admission")
    public String admission;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeString(this.user_nicename);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.stream);
        dest.writeString(this.nums);
        dest.writeString(this.distance);
        dest.writeString(this.pull);
        dest.writeString(this.thumb);
//        dest.writeString(this.label);
        dest.writeString(this.admission);
    }

    public LiveJson() {
    }

    protected LiveJson(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.user_nicename = in.readString();
        this.title = in.readString();
        this.city = in.readString();
        this.stream = in.readString();
        this.nums = in.readString();
        this.distance = in.readString();
        this.pull = in.readString();
        this.thumb = in.readString();
//        this.label = in.readString();
        this.admission = in.readString();
    }

    public static final Parcelable.Creator<LiveJson> CREATOR = new Parcelable.Creator<LiveJson>() {
        @Override
        public LiveJson createFromParcel(Parcel source) {
            return new LiveJson(source);
        }

        @Override
        public LiveJson[] newArray(int size) {
            return new LiveJson[size];
        }
    };
}
