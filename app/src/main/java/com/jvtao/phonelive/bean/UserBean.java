package com.jvtao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class UserBean extends SimpleUserInfo implements Parcelable {
    @SerializedName("birthday")
    public String birthday;
    @SerializedName("coin")
    public String coin;
    @SerializedName("token")
    public String token;
    @SerializedName("votes")
    public String votes;
    @SerializedName("consumption")
    public String consumption;
    @SerializedName("uType")
    public String uType;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.birthday);
        dest.writeString(this.coin);
        dest.writeString(this.token);
        dest.writeString(this.votes);
        dest.writeString(this.consumption);
        dest.writeString(this.uType);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        super(in);
        this.birthday = in.readString();
        this.coin = in.readString();
        this.token = in.readString();
        this.votes = in.readString();
        this.consumption = in.readString();
        this.uType = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
