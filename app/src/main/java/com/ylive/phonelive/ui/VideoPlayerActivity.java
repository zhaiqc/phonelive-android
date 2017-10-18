package com.ylive.phonelive.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.rtc.kit.RTCClient;
import com.ksyun.media.rtc.kit.RTCConstants;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ylive.phonelive.AppConfig;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.base.ShowLiveActivityBase;
import com.ylive.phonelive.bean.ChatBean;
import com.ylive.phonelive.bean.LiveJson;
import com.ylive.phonelive.bean.SimpleUserInfo;
import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.event.Event;
import com.ylive.phonelive.fragment.GiftListDialogFragment;
import com.ylive.phonelive.game.PokersGameControl;
import com.ylive.phonelive.ui.dialog.LiveCommon;
import com.ylive.phonelive.ui.other.ChatServer;
import com.ylive.phonelive.ui.other.LiveStream;
import com.ylive.phonelive.ui.other.SwipeAnimationController;
import com.ylive.phonelive.utils.LiveUtils;
import com.ylive.phonelive.utils.MD5;
import com.ylive.phonelive.utils.SocketMsgUtils;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.TDevice;
import com.ylive.phonelive.utils.TLog;
import com.ylive.phonelive.utils.UIHelper;
import com.ylive.phonelive.widget.LoadUrlImageView;
import com.ylive.phonelive.widget.VideoSurfaceView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.bean.SendGiftBean;
import com.ylive.phonelive.interf.ChatServerInterface;
import com.ylive.phonelive.utils.DialogHelp;
import com.ylive.phonelive.utils.ShareUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/*

* 直播播放页面
* */
public class VideoPlayerActivity extends ShowLiveActivityBase implements View.OnLayoutChangeListener {

    public final static String USER_INFO = "USER_INFO";

    @InjectView(R.id.video_view)
    VideoSurfaceView mVideoSurfaceView;

    //加载中的背景图
    @InjectView(R.id.iv_live_look_loading_bg)
    LoadUrlImageView mIvLoadingBg;

    @InjectView(R.id.iv_live_look_loading_bl)
    LoadUrlImageView mIvLoadingBl;

    @InjectView(R.id.tv_attention)
    TextView mIvAttention;

    @InjectView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    @InjectView(R.id.iv_stop_rtc)
    ImageView mStopLianmai;

    boolean RequestPermissions = false;
    private KSYMediaPlayer ksyMediaPlayer;

    private Surface mSurface = null;

    //视频流宽度
    private int mVideoWidth;

    //视频流高度
    private int mVideoHeight;

    //主播信息
    public LiveJson mEmceeInfo;

    private long mLitLastTime = 0;

    private View mLoadingView;

    private SurfaceHolder mHolder;

    private GiftListDialogFragment mGiftListDialogFragment;

    private SwipeAnimationController mSwipeAnimationController;

    public LiveStream mStreamer;

    public boolean mIsRegisted;

    public boolean mIsConnected;

    MediaPlayer player;

    boolean isRequst;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_look;
    }

    @Override
    public void initView() {
        super.initView();

        mLiveChat.setVisibility(View.VISIBLE);
        mVideoSurfaceView.addOnLayoutChangeListener(this);
        mRoot.addOnLayoutChangeListener(this);

        SurfaceHolder mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mVideoSurfaceView.setOnTouchListener(mTouchListener);
        mVideoSurfaceView.setKeepScreenOn(true);

        mDanmuControl.show();

        mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendLit(event);
                return mSwipeAnimationController.processEvent(event);
            }
        });

        mSwipeAnimationController = new SwipeAnimationController(this);

        mSwipeAnimationController.setAnimationView(mLiveContent);

        mPokersGameControl = new PokersGameControl(mJinHuaPokersLayout, mLucklyPanLayout, mHaiDaoPokers, mNiuZaiPokersLayout, this, false);
        mPokersGameControl.setOnGameListen(new PokersGameControl.GameInterface() {
            @Override
            public void onStartLicensing(int i) {

            }

            @Override
            public void onStartCountDown(int i) {
                mPokersGameControl.setIsVisibleBettingView(true, i);
                if (i == 3) {
                    mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(40));
                }
            }

            @Override
            public void onEndCountDown(int i) {

            }

            @Override
            public void onClickBetting(int index, int i) {
                mPokersGameControl.requestBetting(index, mUser, mChatServer, i);
            }

            @Override
            public void onShowResultEnd(final int i) {
                //查看结果
                requestGetGameResult();
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPokersGameControl.initGameView(VideoPlayerActivity.this, i);
                            if (i == 3) {
                                mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(20));
                            }
                            mPokersGameControl.setCoin(AppContext.getInstance().getLoginUser().coin);
                        }
                    }, 10000);
                }
            }

            @Override
            public void onClickStartGame(int i) {

            }

            @Override
            public void onClickCloseGame(int i) {

            }

            @Override
            public void onStartGameCommit(int i) {

            }

            @Override
            public void onSelectBettingNum(int coin, int i) {

                mPokersGameControl.setBettingCoin(coin);
            }

            @Override
            public void onInitGameView(int i) {

                mPokersGameControl.setCoin(AppContext.getInstance().getLoginUser().coin);
            }
        });
    }


    @Override
    public void initData() {
        super.initData();

        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        //获取用户登陆信息
        mUser = AppContext.getInstance().getLoginUser();
        //获取主播信息
        mEmceeInfo = bundle.getParcelable("USER_INFO");

        mStreamName = mEmceeInfo.stream;

        mRoomNum = mEmceeInfo.uid;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mPokersGameControl.setCoin(mUser.coin);
        //初始化房间信息
        initRoomInfo();
    }

    private void initRoomInfo() {

        //设置背景图
        mIvLoadingBg.setVisibility(View.VISIBLE);
        mIvLoadingBg.setImageLoadUrl(mEmceeInfo.avatar);

        mTvLiveNumber.setText("房间: " + mEmceeInfo.uid);
        mEmceeHead.setAvatarUrl(mEmceeInfo.avatar);

        requestRoomInfo();
        //初始化直播播放器参数配置
        if (mIvLoadingBg != null)
            initLive();

    }

    private void initLive() { //HHH 2016-09-13

        //视频播放器init
        ksyMediaPlayer = new KSYMediaPlayer.Builder(this).build();
        /*if (ksyMediaPlayer != null && mHolder != null) {
            final Surface newSurface = mHolder.getSurface();
            ksyMediaPlayer.setDisplay(mHolder);
            ksyMediaPlayer.setScreenOnWhilePlaying(true);
            //设置视频缩放模式
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (mSurface != newSurface) {
                mSurface = newSurface;
                ksyMediaPlayer.setSurface(mSurface);
            }
        }*/

        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);

        ksyMediaPlayer.setBufferTimeMax(5);
        try {
            ksyMediaPlayer.setDataSource(mEmceeInfo.pull);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startRtc();
    }

    /**
     * @dw 获取房间信息
     */
    private void requestRoomInfo() {
        //请求服务端获取房间基本信息
        PhoneLiveApi.enterRoom(mUser.id
                , mEmceeInfo.uid
                , mUser.token
                , AppContext.address
                , mEmceeInfo.stream
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String s, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);

                        if (res != null) {

                            try {
                                JSONObject data = res.getJSONObject(0);

                                //用户列表
                                mUserList.addAll(ApiUtils.formatDataToList2(data.getJSONArray("userlists")
                                        , SimpleUserInfo.class));

                                //房间人数
                                ChatServer.LIVE_USER_NUMS = data.getInt("nums");

                                mTvLiveNum.setText(ChatServer.LIVE_USER_NUMS + "人观看");

                                //弹幕价格
                                barrageFee = data.getInt("barrage_fee");
                                //映票数量
                                mTvYpNum.setText(data.getString("votestotal"));
                                LiveUtils.sortUserList(mUserList);
                                mUserListAdapter.setUserList(mUserList);

                                if (data.getInt("isattention") == 0) {
                                    mIvAttention.setVisibility(View.VISIBLE);

                                } else {
                                    mIvAttention.setVisibility(View.GONE);

                                }
                                connectToSocketService(data.getString("chatserver"));

                                //游戏信息
                                int gameAction = StringUtils.toInt(data.getString("gameaction"));
                                if (gameAction != 3) {
                                    mPokersGameControl.setGameTime(data.getString("gametime"));
                                    mPokersGameControl.setGameId(data.getString("gameid"));
                                    mPokersGameControl.setBetting1Total(data.getJSONArray("game").getInt(0));
                                    mPokersGameControl.setBetting2Total(data.getJSONArray("game").getInt(1));
                                    mPokersGameControl.setBetting3Total(data.getJSONArray("game").getInt(2));
                                    if (gameAction == 1) {
                                        mJinHuaPokersLayout.setVisibility(View.VISIBLE);
                                    } else if (gameAction == 2) {
                                        mHaiDaoPokers.setVisibility(View.VISIBLE);
                                    }
                                    mPokersGameControl.setGameStatusOnHaveInHand(gameAction);
                                } else {
                                    mPokersGameControl.setGameTime(data.getString("gametime"));
                                    mPokersGameControl.setGameId(data.getString("gameid"));
                                    mPokersGameControl.setBetting1Total(data.getJSONArray("game").getInt(0));
                                    mPokersGameControl.setBetting2Total(data.getJSONArray("game").getInt(1));
                                    mPokersGameControl.setBetting3Total(data.getJSONArray("game").getInt(2));
                                    mPokersGameControl.setBetting4Total(data.getJSONArray("game").getInt(3));
                                    mPokersGameControl.setGameStatusOnHaveInHand(gameAction);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });


    }


    @OnClick({R.id.iv_stop_rtc, R.id.iv_live_meiyan, R.id.iv_live_camera_control, R.id.iv_live_rtc, R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.iv_live_shar, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.ll_live_room_info, R.id.iv_live_chat, R.id.iv_live_look_loading_bg, R.id.bt_send_chat, R.id.iv_live_gift, R.id.tv_attention})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;
            case R.id.iv_live_shar:
                ShareUtils.showSharePopWindow(this, v);
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            //退出直播间
            case R.id.iv_live_back:
                finish();
                break;
            //票数排行榜
            case R.id.ll_yp_labe:
                OrderWebViewActivity.startOrderWebView(this, mEmceeInfo.uid);
                break;
            //发言框
            case R.id.iv_live_chat:
                changeEditStatus(true);
                break;
            //开启关闭弹幕
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.bt_send_chat:
                //等待优化，可能会造成卡顿
                mUser = AppContext.getInstance().getLoginUser();
                //弹幕判断 HHH
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    sendChat();
                }
                break;
            case R.id.iv_live_look_loading_bg:
                changeEditStatus(false);
                break;
            case R.id.iv_live_gift:
                if (mGiftListDialogFragment == null) {
                    mGiftListDialogFragment = new GiftListDialogFragment();
                }
                mGiftListDialogFragment.show(getSupportFragmentManager(), "GiftListDialogFragment");
                break;
            case R.id.ll_live_room_info://左上角点击主播信息
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;
            case R.id.tv_attention:
                //关注主播
                PhoneLiveApi.showFollow(mUser.id, mEmceeInfo.uid, AppContext.getInstance().getToken(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null != res) {
                            mIvAttention.setVisibility(View.GONE);
                            showToast2("关注成功");
                        }
                    }
                });
                mChatServer.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser);
                break;
            case R.id.iv_live_rtc:
                if (mIsConnected) return;
                if (!isRequst) {
                    if (!mIsConnected && android.os.Build.VERSION.SDK_INT >= 23) {
                        ActivityCompat.requestPermissions(VideoPlayerActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                                5);
                    } else {
                        if (!mIsConnected && mIsRegisted) {
                            showToast3("请求连麦", 0);
                            mCameraPreview.setVisibility(View.VISIBLE);
                            mIvLoadingBl.setVisibility(View.GONE);
//                            mStreamer.getRtcClient().startCall(mRoomNum);
                            isRequst = true;
                        }
                    }
                } else {
                    showToast3("正在请求连麦，请稍后...", 0);
                }


                break;
            case R.id.iv_live_camera_control:
                showSettingPopUp(v);
                break;
            case R.id.iv_live_meiyan:
                if (!mBeauty) {
                    mBeauty = true;
                    mBeautyChooseView.setVisibility(View.VISIBLE);
                } else {
                    mBeauty = false;
                    mBeautyChooseView.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_stop_rtc:
                if (mIsConnected) {
                    try {
//                        mStreamer.getRtcClient().stopCall();
                        mRtcView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }
                    mIsConnected = false;
                    isRequst = false;
                } else {
                    showToast3("还没有建立连麦", 1);
                }
                break;
            default:
                break;
        }
    }

    private void onRTCRegisterClick() {
        PhoneLiveApi.getRtc(mUser.id, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                doAuthFailed();
            }

            @Override
            public void onResponse(String response, int id) {
                //JSONArray res = ApiUtils.checkIsSuccess(response);
                if (response != null) {
                    if (!mIsRegisted) {
                        //注册
                        if (mRoomNum == null || mRoomNum.equals("")) {
                            Toast.makeText(VideoPlayerActivity.this, "you must set the local uri before register", Toast
                                    .LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            // doRegister(res.getJSONObject(0).getString("authString"),res.getJSONObject(0).getString("uniqname"),res.getJSONObject(0).getString("url"));
                            doRegister(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        doUnRegister();
                    }
                }

            }
        });
    }

    private void doUnRegister() {
//        mStreamer.getRtcClient().unRegisterRTC();
    }

    private void doAuthFailed() {
        //can register again
        mIsRegisted = false;
    }

    private void doRegister(String authString) {
        if (mCameraPreview != null)
            mCameraPreview.setVisibility(View.VISIBLE);
        mStreamer.setDisplayPreview(mCameraPreview);
//        mStreamer.setRTCSubScreenRect(0.65f, 0.1f, 0.35f, 0.3f,
//                RTCConstants.SCALING_MODE_CENTER_CROP);
//        mStreamer.getRtcClient().setRTCAuthInfo(RTC_AUTH_URI, authString,
//                mUser.id);
//        mStreamer.getRtcClient().setRTCUniqueName(RTC_UINIQUE_NAME);
//        mStreamer.setRTCMainScreen(RTCConstants.RTC_MAIN_SCREEN_REMOTE);
//        mStreamer.getRtcClient().openChattingRoom(false);
//        mStreamer.getRtcClient().setRTCResolutionScale(0.5f);
//        mStreamer.getRtcClient().setRTCFps(15);
//        mStreamer.getRtcClient().setRTCMode(0);
//        mStreamer.getRtcClient().registerRTC();
    }

    //请求连麦 成为辅播 推流
    private void startRtc() {
        mStreamer = new LiveStream(this);
        mStreamer.startCameraPreview();
        mStreamer.setOnErrorListener(mOnErrorListener1);
//        mStreamer.getRtcClient().setRTCErrorListener(mRTCErrorListener);
//        mStreamer.getRtcClient().setRTCEventListener(mRTCEventListener);
        onRTCRegisterClick();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (!isRequst) {
                        if (!mIsConnected && mIsRegisted) {
                            showToast3("请求连麦", 0);
                            mCameraPreview.setVisibility(View.VISIBLE);
                            mIvLoadingBl.setVisibility(View.GONE);
//                            mStreamer.getRtcClient().startCall(mRoomNum);
                            isRequst = true;
                        }
                    } else {
                        showToast3("正在请求连麦，请稍后...", 0);
                    }
                    return;
                }
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您已拒绝使用摄像头权限,无法连麦,请去设置中修改", 0);
                    RequestPermissions = false;
                }

                return;
            }
        }
    }

    //连麦监听
    private RTCClient.RTCEventChangedListener mRTCEventListener = new RTCClient.RTCEventChangedListener() {
        @Override
        public void onEventChanged(int event, final Object arg1) {
            switch (event) {
                case RTCClient.RTC_EVENT_REGISTED:
                    mIsRegisted = true;
                    mCameraPreview.setVisibility(View.GONE);
                    break;
                case RTCClient.RTC_EVENT_STARTED:
                    mVideoSurfaceView.setVisibility(View.GONE);
                    if (ksyMediaPlayer != null)
                        ksyMediaPlayer.pause();
                    mIvLiveMeiyan.setVisibility(View.VISIBLE);
                    mCameraPreview.setVisibility(View.VISIBLE);
                    initBeautyUI(mStreamer);
                    mBeautyChooseView.setVisibility(View.INVISIBLE);
                    mRtcView.setVisibility(View.GONE);
                    mStopLianmai.setVisibility(View.VISIBLE);
                    mIsConnected = true;
                    break;
                case RTCClient.RTC_EVENT_CALL_COMMING:
                    break;
                case RTCClient.RTC_EVENT_STOPPED:
                    mIsConnected = false;
                    isRequst = false;
                    mBeautyChooseView.setVisibility(View.INVISIBLE);
                    mIvLiveMeiyan.setVisibility(View.GONE);
                    mIvCameraControl.setVisibility(View.GONE);
                    mCameraPreview.setVisibility(View.GONE);
                    mVideoSurfaceView.setVisibility(View.VISIBLE);
                    if (ksyMediaPlayer != null) {
                        ksyMediaPlayer.start();
                    }
                    mStopLianmai.setVisibility(View.GONE);
                    mRtcView.setVisibility(View.VISIBLE);
                    break;
                case RTCClient.RTC_EVENT_UNREGISTED:
                    break;
                default:
                    break;
            }

        }
    };
    private RTCClient.RTCErrorListener mRTCErrorListener = new RTCClient.RTCErrorListener() {
        @Override
        public void onError(int errorType, int arg1) {
            switch (errorType) {
                case RTCClient.RTC_ERROR_AUTH_FAILED:
                    mIsRegisted = false;
                    Toast.makeText(VideoPlayerActivity.this, "连麦Auth信息获取失败，注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case RTCClient.RTC_ERROR_REGISTED_FAILED:
                    Toast.makeText(VideoPlayerActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
//                    mStreamer.getRtcClient().registerRTC();
                    break;
                case RTCClient.RTC_ERROR_SERVER_ERROR:
                    isRequst = false;
                    Toast.makeText(VideoPlayerActivity.this, "连麦异常断开", Toast.LENGTH_SHORT).show();
                    mRtcView.setVisibility(View.VISIBLE);
                    mStopLianmai.setVisibility(View.GONE);
                case RTCClient.RTC_ERROR_CONNECT_FAIL:
                    Toast.makeText(VideoPlayerActivity.this, "网络不稳定，请重新连麦", Toast.LENGTH_SHORT).show();
                    isRequst = false;
                    mRtcView.setVisibility(View.VISIBLE);
                    mStopLianmai.setVisibility(View.GONE);
                    break;
                case RTCClient.RTC_ERROR_STARTED_FAILED:
                    mIsConnected = false;
                    if (arg1 == 603) {
                        Toast.makeText(VideoPlayerActivity.this, "主播拒绝连麦", Toast.LENGTH_SHORT).show();
                    }
                    isRequst = false;
                    break;
                default:
                    break;
            }
        }
    };
    private KSYStreamer.OnInfoListener mOnInfoListener1 = new KSYStreamer.OnInfoListener() {
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    TLog.log("初始化完成");
                    mStreamer.startStream();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                    TLog.log("推流成功");
                    break;
                case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:
                    showToast3("网络状况不好", 0);
                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:

                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_DROP:

                    break;
                default:
                    TLog.log("OnInfo: " + what + " msg1: " + msg1 + " msg2: " + msg2);
                    break;
            }
        }
    };
    private KSYStreamer.OnErrorListener mOnErrorListener1 = new KSYStreamer.OnErrorListener() {
        //
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED:
                    //url域名解析失败
                    TLog.log("url域名解析失败");
                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED:
                    //网络连接失败，无法建立连接
                    TLog.log("网络连接失败");
                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED:
                    //跟RTMP服务器完成握手后,向{streamname}推流失败)
                    TLog.log("跟RTMP服务器完成握手后,向{streamname}推流失败)");

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED:
                    //网络连接断开
                    TLog.log("网络连接断开");

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
                    //音视频采集pts差值超过5s
                    TLog.log("KSY_STREAMER_ERROR_AV_ASYNC " + msg1 + "ms");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                    //编码器初始化失败
                    TLog.log("编码器初始化失败");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                    //视频编码失败
                    TLog.log("视频编码失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
                    //音频初始化失败
                    TLog.log("音频初始化失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
                    //音频编码失败
                    TLog.log("音频编码失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:

                    //录音开启失败
                    TLog.log("录音开启失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    //录音开启未知错误
                    TLog.log("录音开启未知错误");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                    //摄像头未知错误
                    TLog.log("摄像头未知错误");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                    //打开摄像头失败
                    TLog.log("打开摄像头失败");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    //系统Camera服务进程退出
                    TLog.log("系统Camera服务进程退出");
                    break;
                default:
                    break;
            }

            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    mStreamer.stopCameraPreview();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStreamer.startCameraPreview();
                        }
                    }, 5000);
                    break;
                //重连
                default:
                    if (mStreamer != null) {
//                        mStreamer.startStream();
                    }
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mStreamer.startStream();
                            }
                        }, 3000);
                    }

                    break;
            }

        }
    };


    //分享操作
    public void share(View v) {
        ShareUtils.share(this, v.getId(), LiveUtils.getSimleUserInfo(mEmceeInfo));
    }

    //弹幕发送
    @Override
    protected void sendBarrageOnResponse(String response) {
        JSONArray s = ApiUtils.checkIsSuccess(response);
        if (s != null) {
            try {
                JSONObject tokenJson = s.getJSONObject(0);
                mUser.coin = tokenJson.getString("coin");
                mUser.level = tokenJson.getString("level");
                mChatServer.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                mChatInput.setText("");
                mChatInput.setHint("开启大喇叭，" + barrageFee + AppConfig.CURRENCY_NAME + "/条");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //更新ui
    private void connectToSocketService(String chatUrl) {

        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), this, chatUrl);
            mChatServer.connectSocketServer(mUser, mEmceeInfo.stream, mEmceeInfo.uid);//连接到socket服务端

        } catch (URISyntaxException e) {
            e.printStackTrace();
            TLog.log("connect error");
        }
    }

    //socket客户端事件监听处理start
    private class ChatListenUIRefresh implements ChatServerInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (type == 1) {
                        addDanmu(c);
                    } else if (type == 2) {

                        if (StringUtils.toInt(socketMsg.getRetcode()) == 409002) {
                            showToast3("你已经被禁言", 0);
                            return;
                        }
                        addChatMessage(c);
                    }
                }
            });
        }

        @Override
        public void onConnect(final boolean res) {
            //连接结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectRes(res);
                }
            });
        }

        //用户状态改变
        @Override
        public void onUserStateChange(SocketMsgUtils socketMsg, final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        //主播关闭直播
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    showLiveEndDialog(mUser.id, 0, "");
                    videoPlayerEnd();
                }
            });

        }

        //送礼物展示
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }


        //特权操作
        @Override
        public void onPrivilegeAction(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (socketMsg.get2Uid().equals(mUser.id)) {

                        //禁言
                        if (socketMsg.getAction().equals("1")) {

                            changeEditStatus(false);

                        } else if (socketMsg.getAction().equals("2")) {
                            //踢人
                            videoPlayerEnd();

                            AlertDialog alertDialog = DialogHelp.getMessageDialog(VideoPlayerActivity.this, "您已被踢出房间",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .create();
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);

                            alertDialog.show();
                        } else if (socketMsg.getAction().equals("13")) {

                            DialogHelp.getMessageDialog(VideoPlayerActivity.this, socketMsg.getCt()).show();
                        }
                    }
                    addChatMessage(c);
                }
            });
        }

        //点亮
        @Override
        public void onLit(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLit(mRandom.nextInt(3));
                }
            });

        }

        //添加僵尸粉丝
        @Override
        public void onAddZombieFans(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addZombieFans(socketMsg.getCt());
                }
            });
        }

        //服务器连接错误
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppContext.showToastAppMsg(VideoPlayerActivity.this, "服务器连接错误");
                }
            });
        }

        @Override
        public void onJinhuaGameMessageListen(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (StringUtils.toInt(socketMsg.getAction())) {

                        case PokersGameControl.POKERS_OPEN_VIEW:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mJinHuaPokersLayout.setVisibility(View.VISIBLE);
                            break;

                        case PokersGameControl.POKERS_START_GAME:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mPokersGameControl.startGame(1);
                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:

                            mPokersGameControl.setGameId(socketMsg.getParam("gameid", "0"));
                            mPokersGameControl.setGameTime(socketMsg.getParam("time", "0"));
                            mPokersGameControl.startCountDown(1);
                            break;
                        case PokersGameControl.POKERS_RESULT_GAME:

                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.guesssuccesssound);
                            player.start();
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 1);
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.buttonclick);
                            player.start();

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 1);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:

                            mJinHuaPokersLayout.setVisibility(View.GONE);
                            mPokersGameControl.initGameView(VideoPlayerActivity.this, 1);
                            mPokersGameControl.setCoin(AppContext.getInstance().getLoginUser().coin);
                            break;

                    }

                }
            });
        }

        @Override
        public void onLuckPanGame(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (StringUtils.toInt(socketMsg.getAction())) {

                        case PokersGameControl.POKERS_OPEN_VIEW:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(20));
                            mLlPan.setTranslationY(-TDevice.dpToPixel(180));
                            mLucklyPanLayout.setVisibility(View.VISIBLE);
                            break;
                        case PokersGameControl.POKERS_START_GAME:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mPokersGameControl.startGame(3);
                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:

                            mPokersGameControl.setGameId(socketMsg.getParam("gameid", "0"));
                            mPokersGameControl.setGameTime(socketMsg.getParam("time", "0"));
                            mPokersGameControl.startCountDown(3);
                            break;
                        case PokersGameControl.POKERS_RESULT_GAME:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.guesssuccesssound);
                            player.start();
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 3);
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 3);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:

                            mLucklyPanLayout.setVisibility(View.GONE);
                            mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(170));
                            mLlPan.setTranslationY(TDevice.dpToPixel(20));
                            mPokersGameControl.initGameView(VideoPlayerActivity.this, 3);
                            mPokersGameControl.setCoin(AppContext.getInstance().getLoginUser().coin);
                            break;

                    }

                }
            });
        }

        @Override
        public void onHaiDaoGame(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (StringUtils.toInt(socketMsg.getAction())) {

                        case PokersGameControl.POKERS_OPEN_VIEW:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mHaiDaoPokers.setVisibility(View.VISIBLE);
                            break;

                        case PokersGameControl.POKERS_START_GAME:
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.beginguesssound);
                            player.start();
                            mPokersGameControl.startGame(2);
                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:

                            mPokersGameControl.setGameId(socketMsg.getParam("gameid", "0"));
                            mPokersGameControl.setGameTime(socketMsg.getParam("time", "0"));
                            mPokersGameControl.startCountDown(2);
                            break;
                        case PokersGameControl.POKERS_RESULT_GAME:
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 2);
                            player = MediaPlayer.create(VideoPlayerActivity.this, R.raw.guesssuccesssound);
                            player.start();
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 2);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:

                            mHaiDaoPokers.setVisibility(View.GONE);
                            mPokersGameControl.initGameView(VideoPlayerActivity.this, 2);
                            mPokersGameControl.setCoin(AppContext.getInstance().getLoginUser().coin);
                            break;

                    }

                }
            });
        }

        @Override
        public void onNiuZaiGame(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (StringUtils.toInt(socketMsg.getAction())) {

                        case PokersGameControl.POKERS_OPEN_VIEW:

                            mNiuZaiPokersLayout.setVisibility(View.VISIBLE);
                            break;

                        case PokersGameControl.POKERS_START_GAME:


                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:
                            mPokersGameControl.startCountDown(4);
                            break;

                        case PokersGameControl.POKERS_RESULT_GAME:
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 4);
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 4);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:
                            mPokersGameControl.initGameView(VideoPlayerActivity.this, 4);
                            mNiuZaiPokersLayout.setVisibility(View.GONE);
                            break;
                    }

                }
            });
        }


    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            //直播开始
            if (null != mLoadingView) {
                mRoot.removeView(mLoadingView);
                mLoadingView = null;
            }
            mIvLoadingBg.setVisibility(View.GONE);

            ksyMediaPlayer.start();
        }
    };
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    // maybe we could call scaleVideoView here.
                    if (mVideoSurfaceView != null) {
                        mVideoSurfaceView.setVideoDimension(mVideoWidth, mVideoHeight);
                        mVideoSurfaceView.requestLayout();
                    }
                }
            }
        }
    };

    //视频播放完成
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            // 重新链接
            ksyMediaPlayer.reload(mEmceeInfo.pull, true);
        }
    };

    //错误异常监听
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    //TLog.log("OnErrorListener, Error:" + what + ",extra:" + extra);
            }
            return false;
        }
    };
    //视频播放信息
    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int info, int i1) {
            if (info == IMediaPlayer.MEDIA_INFO_RELOADED)
                //重连成功
                TLog.log("重连成功");
            if (info == IMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD) {
                //建议此时重连
                ksyMediaPlayer.reload(mEmceeInfo.pull, true);
            }
            return false;
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            changeEditStatus(false);
            return false;
        }
    };

    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mHolder = holder;
            if (ksyMediaPlayer != null) {
                final Surface newSurface = holder.getSurface();
                ksyMediaPlayer.setDisplay(holder);
                ksyMediaPlayer.setScreenOnWhilePlaying(true);
                //设置视频缩放模式
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                    ksyMediaPlayer.setSurface(mSurface);
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (ksyMediaPlayer != null) {
                mSurface = null;
            }
        }
    };

    //直播结束释放资源
    private void videoPlayerEnd() {
        if (mShowGiftAnimator != null) {
            mShowGiftAnimator.removeAllViews();
        }

        if (mGiftListDialogFragment != null) {
            mGiftListDialogFragment.dismissAllowingStateLoss();
        }

        if (mButtonMenuFrame != null && mLvChatList != null) {
            mButtonMenuFrame.setVisibility(View.GONE);//隐藏菜单栏
            mLvChatList.setVisibility(View.GONE);
        }

        if (mChatServer != null) {
            mChatServer.close();
        }

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mRoot != null && mGiftView != null) {
            mRoot.removeView(mGiftView);
        }

        if (mDanmuControl != null) {
            mDanmuControl.hide();//关闭弹幕 HHH
        }


    }

    //切换房间释放资源
    private void switchRoomRelease() {
        mLitLastTime = 0;
        mGiftShowQueue.clear();
        mLuxuryGiftShowQueue.clear();
        mListChats.clear();
        mShowGiftAnimator.removeAllViews();
        mDanMuIsOpen = false;
        mBtnDanMu.setBackgroundResource(R.drawable.tanmubutton);
        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }
        if (mGiftListDialogFragment != null) {
            mGiftListDialogFragment.dismiss();
        }
        mDanmuControl.hide();
        if (mChatServer != null) {
            mChatServer.close();
        }
        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * @dw 当每个聊天被点击显示该用户详细信息弹窗
     */
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() != 13) {

            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }


    //点亮
    private void sendLit(MotionEvent event) {

        //按下并且当前屏幕不是清屏状态下
        if (event.getAction() == MotionEvent.ACTION_DOWN && !(mLiveContent.getLeft() > 10)) {
            int index = mRandom.nextInt(3);
            if (mLitLastTime == 0 || (System.currentTimeMillis() - mLitLastTime) > 500) {
                if (mLitLastTime == 0) {
                    //第一次点亮请求服务端纪录
                    //PhoneLiveApi.showLit(mUser.id, mUser.token, mEmceeInfo.uid);
                    mChatServer.doSendLitMsg(mUser, index);
                }
                mLitLastTime = System.currentTimeMillis();
                if (mChatServer != null) {
                    mChatServer.doSendLit(index);
                }

            } else {
                showLit(mRandom.nextInt(3));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.VideoEvent event) {

        if (event.action == 0 && mChatServer != null) {

            mUser = AppContext.getInstance().getLoginUser();
            //送礼物
            mChatServer.doSendGift(event.data[0], mUser, event.data[1]);

        } else if (event.action == 1 && mChatServer != null) {

            //关注
            mChatServer.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser);
            mIvAttention.setVisibility(View.GONE);
        }

    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (v.getId() == R.id.video_view) {
            if (bottom != 0) {
                //防止聊天软键盘挤压屏幕导致视频变形
                //mVideoSurfaceView.setVideoDimension(mScreenWidth,mScreenHeight);
            }
        } else if (v.getId() == R.id.rl_live_root) {
            if (bottom > oldBottom) {
                //如果聊天窗口开启,收起软键盘时关闭聊天输入框
                changeEditStatus(false);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.start();

        }
        if (mStreamer!=null){
            mStreamer.startCameraPreview();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.pause();
            //mPause = true;
        }
    }

    @Override
    protected void onDestroy() {//释放
        super.onDestroy();
        videoPlayerEnd();
        if (mStreamer != null && mIsConnected) {
//            mStreamer.getRtcClient().stopCall();
        }
        if (mStreamer != null && mIsRegisted) {
//            mStreamer.getRtcClient().unRegisterRTC();
        }
        if (mStreamer != null) {
            mStreamer.release();
        }
        //解除广播
        OkHttpUtils.getInstance().cancelTag("initRoomInfo");
        ButterKnife.reset(this);
    }


    public static void startVideoPlayerActivity(final Context context, final LiveJson live) {
        PhoneLiveApi.checkoutRoom(AppContext.getInstance().getLoginUid()
                , AppContext.getInstance().getToken(), live.stream, live.uid, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (res != null) {
                            try {
                                final JSONObject data = res.getJSONObject(0);

                                if (data.getInt("type") == 2) {
                                    DialogHelp.getMessageDialog(context, data.getString("type_msg"), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(),
                                                    live.uid, live.stream, new StringCallback() {

                                                        @Override
                                                        public void onError(Call call, Exception e, int id) {

                                                        }

                                                        @Override
                                                        public void onResponse(String response, int id) {
                                                            JSONArray res = ApiUtils.checkIsSuccess(response);

                                                            if (res != null) {

                                                                UserBean userBean = AppContext.getInstance().getLoginUser();
                                                                try {
                                                                    userBean.coin = res.getJSONObject(0).getString("coin");
                                                                    AppContext.getInstance().saveUserInfo(userBean);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                Bundle bundle = new Bundle();

                                                                bundle.putParcelable("USER_INFO", live);
                                                                UIHelper.showLookLiveActivity(context, bundle);
                                                            }
                                                        }
                                                    });

                                        }
                                    }).create().show();

                                } else if (data.getInt("type") == 1) {
                                    LiveCommon.showInputContentDialog(context, "请输入房间密码", new com.ylive.phonelive.interf.DialogInterface() {
                                        @Override
                                        public void cancelDialog(View v, Dialog d) {
                                            d.dismiss();
                                        }

                                        @Override
                                        public void determineDialog(View v, Dialog d) {
                                            try {
                                                EditText et = (EditText) d.findViewById(R.id.et_input);
                                                if (!data.getString("type_msg").equals(MD5.getMD5(et.getText().toString()))
                                                        && !data.getString("type_msg").contains(MD5.getMD5(et.getText().toString()))) {
                                                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Bundle bundle = new Bundle();

                                                bundle.putParcelable("USER_INFO", live);
                                                UIHelper.showLookLiveActivity(context, bundle);
                                                d.dismiss();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("USER_INFO", live);
                                    UIHelper.showLookLiveActivity(context, bundle);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }


    //显示设置列表
    private void showSettingPopUp(View v) {
        View popView = getLayoutInflater().inflate(R.layout.pop_view_camera_control, null);
        LinearLayout llLiveCameraControl = (LinearLayout) popView.findViewById(R.id.ll_live_camera_control);
        llLiveCameraControl.measure(0, 0);
        int height = llLiveCameraControl.getMeasuredHeight();
        popView.findViewById(R.id.iv_live_flashing_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingLightOn = !flashingLightOn;
                mStreamer.toggleTorch(flashingLightOn);
            }
        });
        popView.findViewById(R.id.iv_live_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamer.switchCamera();
            }
        });
        popView.findViewById(R.id.iv_live_shar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSharePopWindow(VideoPlayerActivity.this, mIvCameraControl);
            }
        });
        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - height);
    }
}
