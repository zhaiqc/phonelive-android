package com.ylive.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weipeng on 2017/1/18.
 */

public class SimpleUserInfo implements Parcelable {

    public String id;
    public String user_nicename;
    public String avatar;
    public String avatar_thumb;
    public String sex;
    public String signature;
    public String level;
    public String isattention;
    public String city;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeString(this.sex);
        dest.writeString(this.signature);
        dest.writeString(this.level);
        dest.writeString(this.isattention);
        dest.writeString(this.city);
    }

    public SimpleUserInfo() {
    }

    protected SimpleUserInfo(Parcel in) {
        this.id = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.sex = in.readString();
        this.signature = in.readString();
        this.level = in.readString();
        this.isattention = in.readString();
        this.city = in.readString();
    }

    public static final Creator<SimpleUserInfo> CREATOR = new Creator<SimpleUserInfo>() {
        @Override
        public SimpleUserInfo createFromParcel(Parcel source) {
            return new SimpleUserInfo(source);
        }

        @Override
        public SimpleUserInfo[] newArray(int size) {
            return new SimpleUserInfo[size];
        }
    };
}
