package com.shenlive.phonelive.bean;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/4/13.
 */
public class PrivateChatUserBean extends UserBean {
    @SerializedName("lastMessage")
    public String lastMessage;
    @SerializedName("unreadMessage")
    public boolean unreadMessage;
    @SerializedName("isattention2")
    public int isattention2;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.lastMessage);
        dest.writeByte(this.unreadMessage ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isattention2);
    }

    public PrivateChatUserBean() {
    }

    protected PrivateChatUserBean(Parcel in) {
        super(in);
        this.lastMessage = in.readString();
        this.unreadMessage = in.readByte() != 0;
        this.isattention2 = in.readInt();
    }

    public static final Creator<PrivateChatUserBean> CREATOR = new Creator<PrivateChatUserBean>() {
        @Override
        public PrivateChatUserBean createFromParcel(Parcel source) {
            return new PrivateChatUserBean(source);
        }

        @Override
        public PrivateChatUserBean[] newArray(int size) {
            return new PrivateChatUserBean[size];
        }
    };
}
