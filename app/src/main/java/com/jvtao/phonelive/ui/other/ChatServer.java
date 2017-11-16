package com.jvtao.phonelive.ui.other;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;
import com.jvtao.phonelive.AppContext;

import com.jvtao.phonelive.bean.ChatBean;
import com.jvtao.phonelive.bean.SendGiftBean;
import com.jvtao.phonelive.bean.SimpleUserInfo;
import com.jvtao.phonelive.bean.UserBean;
import com.jvtao.phonelive.interf.ChatServerInterface;
import com.jvtao.phonelive.utils.LiveUtils;
import com.jvtao.phonelive.utils.SocketMsgUtils;
import com.jvtao.phonelive.utils.StringUtils;
import com.jvtao.phonelive.utils.TDevice;
import com.jvtao.phonelive.utils.TLog;
import com.jvtao.phonelive.widget.VerticalImageSpan;
import com.jvtao.phonlive.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 直播间业务逻辑处理
 */
public class ChatServer {

    public static final int[] heartImg = new int[]{R.drawable.plane_heart_cyan, R.drawable.plane_heart_pink, R.drawable.plane_heart_red, R.drawable.plane_heart_yellow, R.drawable.plane_heart_cyan};

    public static final String EVENT_NAME = "broadcast";

    private static final int SEND_CHAT = 2;//发言

    private static final int SYSTEM_NOT = 1;//系统消息

    private static final int NOTICE = 0;//提醒

    private static final int PRIVELEGE = 4;//特权操作

    private static final int JINHUA_GAME_MSG = 15;

    private static final int LUCKPAN = 16;

    private static final int HAIDAO = 18;

    private static final int KAIXINNIUZAI = 17;

    public static int LIVE_USER_NUMS = 0;

    private Socket mSocket;

    private Context context;

    private ChatServerInterface mChatServer;

    private Gson mGson;

    //服务器连接关闭监听
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            TLog.log("socket断开连接");
        }
    };

    //服务器连接失败监听
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mChatServer.onError();
            TLog.log("socket连接Error");
        }
    };

    //服务器消息监听
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONArray jsonArray = (JSONArray) args[0];
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getString(i).equals("stopplay")) {
                        mChatServer.onSystemNot(1);
                        return;
                    }
                    SocketMsgUtils socketMsg = SocketMsgUtils.getFormatJsonMode(jsonArray.getString(i));

                    int action = StringUtils.toInt(socketMsg.getAction());

                    //获取用户动作
                    switch (StringUtils.toInt(socketMsg.getMsgtype())) {
                        case SEND_CHAT://聊天
                            if (action == 0) {//公聊
                                onMessage(socketMsg);
                            }
                            break;
                        case SYSTEM_NOT://系统
                            if (action == 0) {
                                //发送礼物
                                onSendGift(socketMsg);

                            } else if (action == 18) {
                                //房间关闭
                                mChatServer.onSystemNot(0);
                            } else if (action == 7) {
                                //弹幕
                                onDanmuMessage(socketMsg);
                            } else if (action == 19) {
                                //房间关闭
                                mChatServer.onSystemNot(1);
                            }
                            break;
                        case NOTICE://通知

                            if (action == 0) {
                                //上下线
                                ChatServer.LIVE_USER_NUMS += 1;
                                mChatServer.onUserStateChange(socketMsg, mGson.fromJson(socketMsg.getCt(), UserBean.class), true);
                            } else if (action == 1) {
                                ChatServer.LIVE_USER_NUMS -= 1;
                                mChatServer.onUserStateChange(socketMsg, mGson.fromJson(socketMsg.getCt(), UserBean.class), false);
                            } else if (action == 2) {
                                //点亮
                                mChatServer.onLit(socketMsg);
                            } else if (action == 3) {
                                //僵尸粉丝推送
                                mChatServer.onAddZombieFans(socketMsg);
                            }
                            break;
                        case PRIVELEGE:
                            onPrivate(socketMsg);
                            break;

                        case JINHUA_GAME_MSG:
                            onJinhuaGameMsg(socketMsg);
                            break;
                        case LUCKPAN:
                            onLuckPanGame(socketMsg);
                            break;
                        case HAIDAO:
                            mChatServer.onHaiDaoGame(socketMsg);
                            break;
                        case KAIXINNIUZAI:
                            mChatServer.onNiuZaiGame(socketMsg);
                            break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //游戏1
    private void onJinhuaGameMsg(SocketMsgUtils socketMsg) {

        mChatServer.onJinhuaGameMessageListen(socketMsg);

    }

    //游戏2
    private void onLuckPanGame(SocketMsgUtils socketMsg) {
        mChatServer.onLuckPanGame(socketMsg);

    }

    //特权
    private void onPrivate(SocketMsgUtils msgUtils) throws JSONException {
        SpannableStringBuilder msg = new SpannableStringBuilder(msgUtils.getCt());
        SpannableStringBuilder name = new SpannableStringBuilder("系统消息 : ");
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#DFA640")), 0, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setSpan(new ForegroundColorSpan(Color.parseColor("#A98BE3")), 0, msg.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ChatBean c = new ChatBean();
        c.setType(13);
        c.setSendChatMsg(msg);
        c.setUserNick(name);
        mChatServer.onPrivilegeAction(msgUtils, c);
    }

    private void onDanmuMessage(SocketMsgUtils msgUtils) throws JSONException {
        String ct = msgUtils.getCt();
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);

        JSONObject jsonObject = new JSONObject(ct);

        c.setContent(jsonObject.getString("content"));
        mChatServer.onMessageListen(msgUtils, 1, c);
    }


    //礼物信息
    private void onSendGift(SocketMsgUtils msgUtils) throws JSONException {
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);

        SendGiftBean mSendGiftInfo = mGson.fromJson(msgUtils.getCt(), SendGiftBean.class);//gift info

        mSendGiftInfo.setAvatar(userInfo.avatar);
        mSendGiftInfo.setEvensend(msgUtils.getParam("evensend", "n"));
        mSendGiftInfo.setNicename(userInfo.user_nicename);
        String uname = "_ " + userInfo.user_nicename + " : ";
        SpannableStringBuilder msg = new SpannableStringBuilder("我送了" + mSendGiftInfo.getGiftcount() + "个" + mSendGiftInfo.getGiftname());
        SpannableStringBuilder name = new SpannableStringBuilder(uname);

        Drawable d = context.getResources().getDrawable(LiveUtils.getLevelRes(userInfo.level));
        d.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
        VerticalImageSpan is = new VerticalImageSpan(d);
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#f2b437")), 1, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        name.setSpan(is, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setSpan(new ForegroundColorSpan(Color.parseColor("#f16678")), 0, msg.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        c.setSendChatMsg(msg);
        c.setUserNick(name);


        mChatServer.onShowSendGift(mSendGiftInfo, c);
    }

    //消息信息
    private void onMessage(SocketMsgUtils msgUtils) throws JSONException {

        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);
        String uname = "_ " + userInfo.user_nicename + " : ";

        SpannableStringBuilder msg = new SpannableStringBuilder(msgUtils.getCt());
        SpannableStringBuilder name = new SpannableStringBuilder(uname);
        //添加等级图文混合
        Drawable levelDrawable = context.getResources().getDrawable(LiveUtils.getLevelRes(userInfo.level));
        levelDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
        VerticalImageSpan levelImage = new VerticalImageSpan(levelDrawable);
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#f2b437")), 1, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        name.setSpan(levelImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //获取被@用户id
        String touid = String.valueOf(msgUtils.get2Uid());
        //判断如果是@方式聊天,被@方用户显示粉色字体
        if ((!touid.equals("0") && (touid.equals(AppContext.getInstance().getLoginUid())))) {
            msg.setSpan(new ForegroundColorSpan(Color.rgb(232, 109, 130)), 0, msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //判断是否是点亮
        if (msgUtils.getParam("heart", 0) > 0) {

            int index = msgUtils.getParam("heart", 0);
            msg.append("❤");
            //添加点亮图文混合
            Drawable hearDrawable = context.getResources().getDrawable(heartImg[index]);
            hearDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(20), (int) TDevice.dpToPixel(20));
            VerticalImageSpan hearImage = new VerticalImageSpan(hearDrawable);
            msg.setSpan(hearImage, msg.length() - 1, msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        c.setSendChatMsg(msg);
        c.setUserNick(name);
        mChatServer.onMessageListen(msgUtils, 2, c);
    }


    //服务器连接结果监听
    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONArray jsonArray = (JSONArray) args[0];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (jsonArray.getString(i).equals("ok")) {
                        mChatServer.onConnect(true);
                    } else {
                        mChatServer.onConnect(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public ChatServer(ChatServerInterface chatServerInterface, Context context, String chaturl) throws URISyntaxException {
        this.mChatServer = chatServerInterface;
        this.context = context;

        mGson = new Gson();

        try {

            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket = IO.socket(chaturl, option);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param u 用户信息json格式
     * @dw 连接socket服务端
     */
    public void connectSocketServer(UserBean u, final String stream, String liveuid) {
        publicSocketInitAction(u, stream, liveuid);
    }

    /**
     * @dw 公共的初始化方法
     */
    public void publicSocketInitAction(final UserBean u, final String stream, final String liveuid) {

        if (null == mSocket) return;
        try {
            mSocket.connect();
            JSONObject dataJson = new JSONObject();
            dataJson.put("uid", u.id);
            dataJson.put("token", u.token);
            dataJson.put("roomnum", liveuid);
            dataJson.put("stream", stream);
            mSocket.emit("conn", dataJson);

            TLog.log(dataJson.toString());
            mSocket.on("conn", onConn);
            mSocket.on("broadcastingListen", onMessage);
            mSocket.on(mSocket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(mSocket.EVENT_ERROR, onError);
            mSocket.on(mSocket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    TLog.log("重连");
                    try {
                        JSONObject dataJson = new JSONObject();
                        dataJson.put("uid", u.id);
                        dataJson.put("token", u.token);
                        dataJson.put("roomnum", stream);
                        dataJson.put("liveuid", liveuid);
                        mSocket.emit("conn", dataJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
           /* mSocket.on(mSocket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    TLog.log("尝试重连");
                }
            });
            mSocket.on(mSocket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    TLog.log("尝试重连错误");
                }
            });
            mSocket.on(mSocket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    TLog.log("尝试重连失败");
                }
            });
            mSocket.on(mSocket.EVENT_RECONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    TLog.log("尝试重连chengong");
                }
            });*/
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    //关闭房间
    public void closeLive() {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("StartEndLive")
                .setAction("18")
                .setMsgtype("1")
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);
    }

    //超管关闭直播
    public void doSetCloseLive() {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("stopLive")
                .setAction("19")
                .setMsgtype("1")
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);

    }

    /**
     * @param mUser    用户信息
     * @param evensend 是否在连送规定时间内
     * @dw token 发送礼物凭证
     */
    public void doSendGift(String token, UserBean mUser, String evensend) {
        if (null != mSocket) {

            SocketMsgUtils.getNewJsonMode()
                    .set_method_("SendGift")
                    .setAction("0")
                    .setMsgtype("1")
                    .setMyUserInfo(mUser)
                    .addParamToJson1("uhead", mUser.avatar_thumb)
                    .setCt(token)
                    .addParamToJson1("evensend", evensend)
                    .build()
                    .sendMessage(mSocket);
        }

    }

    /**
     * @param mUser 用户信息
     * @dw token 发送弹幕凭证
     */
    public void doSendBarrage(String token, UserBean mUser) {
        if (null != mSocket) {
            SocketMsgUtils.getNewJsonMode()
                    .set_method_("SendBarrage")
                    .setAction("7")
                    .setMsgtype("1")
                    .setMyUserInfo(mUser)
                    .addParamToJson1("uhead", mUser.avatar_thumb)
                    .setCt(token)
                    .build()
                    .sendMessage(mSocket);

        }

    }

    /**
     * @param mUser   当前用户bean
     * @param mToUser 被操作用户bean
     * @dw 禁言
     */
    public void doSetShutUp(UserBean mUser, SimpleUserInfo mToUser) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("ShutUpUser")
                .setAction("1")
                .setMsgtype("4")
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被禁言5分钟")
                .build()
                .sendMessage(mSocket);


    }

    //踢人
    public void doSetKick(UserBean mUser, SimpleUserInfo mToUser) {

        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("KickUser")
                .setAction("2")
                .setMsgtype("4")
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被踢出房间")
                .build()
                .sendMessage(mSocket);
    }

    //设为管理员
    public void doSetOrRemoveManage(UserBean user, SimpleUserInfo touser, String content) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .setMsgtype("4")
                .setMyUserInfo(user)
                .set2UserInfo(touser)
                .setCt(content)
                .build()
                .sendMessage(mSocket);


    }

    //发送系统消息
    public void doSendSystemMessage(String msg, UserBean user) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .setMsgtype("4")
                .setMyUserInfo(user)
                .setCt(msg)
                .build()
                .sendMessage(mSocket);
    }

    /**
     * @param sendMsg 发言内容
     * @param user    用户信息
     * @dw 发言
     */
    public void doSendMsg(String sendMsg, UserBean user, int reply) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype("2")
                .setMyUserInfo(user)
                .setCt(sendMsg)
                .build()
                .sendMessage(mSocket);

    }

    //打开游戏画面
    public void doSendOpenGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startGame")
                .setAction("1")
                .setMsgtype("15")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);

    }

    //开始发牌
    public void doSendLicensing(UserBean user, String gameid) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("startGame")
                .setAction("2")
                .setMsgtype("15")
                .addParamToJson1("gameid", gameid)
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //开始倒计时
    public void doSendStartGameCountDown(UserBean user, String jinhua_token, String gameid, String token, String time) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startGame")
                .setAction("4")
                .setMsgtype("15")
                .setMyUserInfo(user)
                .addParamToJson1("liveuid", user.id)
                .addParamToJson1("token", jinhua_token)
                .addParamToJson1("gameid", gameid)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //下注
    public void doSendBetting(String coin, String type, UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startGame")
                .setAction("5")
                .setMsgtype("15")
                .setMyUserInfo(user)
                .addParamToJson1("money", coin)
                .addParamToJson1("type", type)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startGame")
                .setAction("3")
                .setMsgtype("15")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //打开游戏画面
    public void doSendPanOpenGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startRotationGame")
                .setAction("1")
                .setMsgtype("16")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);

    }

    //开始发牌
    public void doSendPanLicensing(UserBean user, String gameid) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startRotationGame")
                .setAction("2")
                .setMsgtype("16")
                .addParamToJson1("gameid", gameid)
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //开始倒计时
    public void doSendStartPanGameCountDown(UserBean user, String jinhua_token, String gameid, String token, String time) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startRotationGame")
                .setAction("4")
                .setMsgtype("16")
                .setMyUserInfo(user)
                .addParamToJson1("liveuid", user.id)
                .addParamToJson1("token", jinhua_token)
                .addParamToJson1("gameid", gameid)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //下注
    public void doSendPanBetting(String coin, String type, UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startRotationGame")
                .setAction("5")
                .setMsgtype("16")
                .setMyUserInfo(user)
                .addParamToJson1("money", coin)
                .addParamToJson1("type", type)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndPanGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startRotationGame")
                .setAction("3")
                .setMsgtype("16")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //打开游戏画面
    public void doSendHaiDaoOpenGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("1")
                .setMsgtype("18")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);

    }

    //开始发牌
    public void doSendHaiDaoLicensing(UserBean user, String gameid) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("2")
                .setMsgtype("18")
                .addParamToJson1("gameid", gameid)
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //开始倒计时
    public void doSendStartHaiDaoGameCountDown(UserBean user, String jinhua_token, String gameid, String token, String time) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("4")
                .setMsgtype("18")
                .setMyUserInfo(user)
                .addParamToJson1("liveuid", user.id)
                .addParamToJson1("token", jinhua_token)
                .addParamToJson1("gameid", gameid)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //下注
    public void doSendHaiDaoBetting(String coin, String type, UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("5")
                .setMsgtype("18")
                .setMyUserInfo(user)
                .addParamToJson1("money", coin)
                .addParamToJson1("type", type)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndHaiDaoGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("3")
                .setMsgtype("18")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //打开游戏画面
    public void doSendNiuZaiOpenGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startCattleGame")
                .setAction("1")
                .setMsgtype("17")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);

    }

    //开始发牌
    public void doSendNiuZaiLicensing(UserBean user, String gameid) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startCattleGame")
                .setAction("2")
                .setMsgtype("17")
                .addParamToJson1("gameid", gameid)
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //开始倒计时
    public void doSendStartNiuZaiGameCountDown(UserBean user, String jinhua_token, String gameid, String token, String time) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startCattleGame")
                .setAction("4")
                .setMsgtype("17")
                .setMyUserInfo(user)
                .addParamToJson1("liveuid", user.id)
                .addParamToJson1("token", jinhua_token)
                .addParamToJson1("gameid", gameid)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //下注
    public void doSendNiuZaiBetting(String coin, String type, UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startCattleGame")
                .setAction("5")
                .setMsgtype("17")
                .setMyUserInfo(user)
                .addParamToJson1("money", coin)
                .addParamToJson1("type", type)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndNiuZaiGame(UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("startLodumaniGame")
                .setAction("3")
                .setMsgtype("17")
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    /**
     * @param index
     * @param user  用户信息
     * @dw 我点亮了
     */
    public void doSendLitMsg(UserBean user, int index) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype("2")
                .setMyUserInfo(user)
                .setCt("我点亮了")
                .addParamToJson1("heart", String.valueOf(index + 1))
                .build()
                .sendMessage(mSocket);

    }

    //获取僵尸粉丝
    public void getZombieFans() {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestFans")
                .setAction("")
                .setMsgtype("")
                .build()
                .sendMessage(mSocket);

    }

    /**
     * @param index 点亮心在数组中的下标
     * @dw 点亮
     */
    public void doSendLit(int index) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("light")
                .setAction("2")
                .setMsgtype("0")
                .build()
                .sendMessage(mSocket);
    }

    //释放资源
    public void close() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("conn");
            mSocket.off("broadcastingListen");
            mSocket.off();
            mSocket.close();
            mSocket = null;
        }

    }


}
