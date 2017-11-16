package com.jvtao.phonelive.interf;

import com.jvtao.phonelive.bean.ChatBean;
import com.jvtao.phonelive.bean.SendGiftBean;
import com.jvtao.phonelive.bean.UserBean;
import com.jvtao.phonelive.utils.SocketMsgUtils;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface ChatServerInterface {
    void onMessageListen(SocketMsgUtils socketMsg,int type,ChatBean chatBean);
    void onConnect(boolean res);
    void onUserStateChange(SocketMsgUtils socketMsg,UserBean user, boolean upordown);
    void onSystemNot(int code);
    void onShowSendGift(SendGiftBean contentJson, ChatBean chatBean);
    void onPrivilegeAction(SocketMsgUtils socketMsg,ChatBean c);
    void onLit(SocketMsgUtils socketMsg);
    void onAddZombieFans(SocketMsgUtils socketMsg);
    void onError();
    void onJinhuaGameMessageListen(SocketMsgUtils socketMsg);
    void onLuckPanGame(SocketMsgUtils socketMsg);
    void onHaiDaoGame(SocketMsgUtils socketMsg);
    void onNiuZaiGame(SocketMsgUtils socketMsg);
}