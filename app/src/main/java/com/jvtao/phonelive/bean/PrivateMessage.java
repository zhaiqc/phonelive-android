package com.jvtao.phonelive.bean;

import com.google.gson.annotations.SerializedName;
import com.hyphenate.chat.EMMessage;

/**
 * Created by weipeng on 16/8/15.
 */
public class PrivateMessage {
    @SerializedName("message")
    public EMMessage message;
    @SerializedName("uHead")
    public String uHead;

    public static PrivateMessage crateMessage(EMMessage message,String uHead){
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.message = message;
        privateMessage.uHead = uHead;
        return privateMessage;
    }



}
