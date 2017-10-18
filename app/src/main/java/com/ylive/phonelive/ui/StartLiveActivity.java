package com.ylive.phonelive.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.util.NetUtils;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.rtc.kit.RTCClient;
import com.ksyun.media.rtc.kit.RTCConstants;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.base.ShowLiveActivityBase;
import com.ylive.phonelive.bean.ChatBean;
import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.bean.UserHomePageBean;
import com.ylive.phonelive.event.Event;
import com.ylive.phonelive.fragment.SearchMusicDialogFragment;
import com.ylive.phonelive.game.PokersGameControl;
import com.ylive.phonelive.ui.dialog.LiveCommon;
import com.ylive.phonelive.ui.other.ChatServer;
import com.ylive.phonelive.utils.InputMethodUtils;
import com.ylive.phonelive.utils.SocketMsgUtils;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.TDevice;
import com.ylive.phonelive.utils.TLog;
import com.ylive.phonelive.utils.ThreadManager;
import com.ylive.phonelive.widget.GridViewWithHeaderAndFooter;
import com.ylive.phonelive.widget.music.ILrcBuilder;
import com.ylive.phonelive.widget.music.LrcRow;
import com.ylive.phonelive.widget.music.LrcView;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.bean.SendGiftBean;
import com.ylive.phonelive.fragment.MusicPlayerDialogFragment;
import com.ylive.phonelive.interf.ChatServerInterface;
import com.ylive.phonelive.interf.DialogInterface;
import com.ylive.phonelive.ui.other.LiveStream;
import com.ylive.phonelive.utils.DialogHelp;
import com.ylive.phonelive.utils.LiveUtils;
import com.ylive.phonelive.utils.ShareUtils;
import com.ylive.phonelive.widget.music.DefaultLrcBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.ksyun.media.streamer.kit.StreamerConstants.VIDEO_RESOLUTION_480P;


/**
 * 直播页面
 * 本页面包括点歌 分享 直播 聊天 僵尸粉丝 管理 点亮 歌词等功能详细参照每个方法的注释
 * 本页面继承基类和观看直播属于同一父类
 */
public class StartLiveActivity extends ShowLiveActivityBase implements SearchMusicDialogFragment.SearchMusicFragmentInterface {

    //渲染视频
    @InjectView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    //歌词显示控件
    @InjectView(R.id.lcv_live_start)
    LrcView mLrcView;

    @InjectView(R.id.fl_bottom_menu)
    RelativeLayout mFlBottomMenu;

    @InjectView(R.id.rl_live_music)
    LinearLayout mViewShowLiveMusicLrc;

    @InjectView(R.id.btn_live_end_music)
    Button mEndMusic;

    @InjectView(R.id.iv_live_music)
    ImageView mMusic;

    @InjectView(R.id.iv_live_game)
    protected ImageView mIvGame;

    private String rtmpPushAddress;

    //直播结束魅力值数量
    private int mLiveEndYpNum;

    private Timer mTimer;

    //是否开启直播
    private boolean IS_START_LIVE = true;

    public LiveStream mStreamer;

    private int mPlayTimerDuration = 1000;

    private int pauseTime = 0;

    private MediaPlayer mPlayer;

    private boolean isFrontCameraMirro = false;

    public boolean mIsRegisted;

    public boolean mIsConnected;

    private UserHomePageBean mUserHomePageBean;
    private int lastX;
    private int lastY;
    boolean isCanRtc = true;

    boolean isStartGame;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_show;
    }

    @Override
    public void initView() {
        super.initView();
        mBeautyChooseView.setVisibility(View.INVISIBLE);
        mLrcView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算移动的距离
                        int offX = x - lastX;
                        int offY = y - lastY;
                        //调用layout方法来重新放置它的位置
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
                        layoutParams.setMargins(mViewShowLiveMusicLrc.getLeft() + offX, mViewShowLiveMusicLrc.getTop() + offY, 0, 0);
                        mViewShowLiveMusicLrc.setLayoutParams(layoutParams);
                        break;
                }
                return true;
            }
        });
        //防止聊天软键盘挤压屏幕
        mRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > oldBottom && InputMethodUtils.isShowSoft(StartLiveActivity.this)) {
                    changeEditStatus(false);
                }
            }
        });

        mPokersGameControl = new PokersGameControl(mJinHuaPokersLayout, mLucklyPanLayout, mHaiDaoPokers, mNiuZaiPokersLayout, this, true);
        mPokersGameControl.setOnGameListen(new PokersGameControl.GameInterface() {
            @Override
            public void onStartLicensing(int i) {

            }

            @Override
            public void onStartCountDown(int i) {
                if (i == 1) {
                    mChatServer.doSendStartGameCountDown(mUser, mPokersGameControl.getJinhuaToken(),
                            mPokersGameControl.getGameId(), mUser.token, mPokersGameControl.getGameTime());
                } else if (i == 3) {
                    mChatServer.doSendStartPanGameCountDown(mUser, mPokersGameControl.getJinhuaToken(),
                            mPokersGameControl.getGameId(), mUser.token, mPokersGameControl.getGameTime());
                } else if (i == 2) {
                    mChatServer.doSendStartHaiDaoGameCountDown(mUser, mPokersGameControl.getJinhuaToken(),
                            mPokersGameControl.getGameId(), mUser.token, mPokersGameControl.getGameTime());
                } else if (i == 4) {
                    mChatServer.doSendStartNiuZaiGameCountDown(mUser, mPokersGameControl.getJinhuaToken(),
                            mPokersGameControl.getGameId(), mUser.token, mPokersGameControl.getGameTime());
                }
            }

            @Override
            public void onEndCountDown(int i) {

            }

            @Override
            public void onClickBetting(final int index, int i) {

                //mPokersGameControl.requestBetting(index,mUser,mChatServer);
            }

            @Override
            public void onShowResultEnd(final int i) {
                //查看结果
                requestGetGameResult();
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPokersGameControl.setCoin(mUser.coin);
                            mPokersGameControl.initGameView(StartLiveActivity.this, i);
                        }
                    }, 10000);
                }
            }

            @Override
            public void onClickStartGame(int i) {

                if (mPokersGameControl.getGameStatus() == 0) {

                    mPokersGameControl.startGame(mUser.id, mStreamName, mUser.token, i);
                } else {

                    mPokersGameControl.closeGame(mChatServer, mUser, mStreamName, i);
                }

            }

            @Override
            public void onClickCloseGame(int i) {
                mPokersGameControl.closeGame(mChatServer, mUser, mStreamName, i);
            }

            @Override
            public void onStartGameCommit(int i) {

                //发牌
                if (i == 1) {
                    mChatServer.doSendLicensing(mUser, mPokersGameControl.getGameId());
                } else if (i == 3) {
                    mChatServer.doSendPanLicensing(mUser, mPokersGameControl.getGameId());
                } else if (i == 2) {
                    mChatServer.doSendHaiDaoLicensing(mUser, mPokersGameControl.getGameId());
                } else if (i == 4) {
                    mChatServer.doSendNiuZaiLicensing(mUser, mPokersGameControl.getGameId());
                }
            }

            @Override
            public void onSelectBettingNum(int coin, int i) {
                mPokersGameControl.setBettingCoin(coin);
            }

            @Override
            public void onInitGameView(int i) {
                mPokersGameControl.setCoin(mUser.coin);
            }
        });
        mIvGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initmPopupDialog();
            }
        });


    }

    public void initmPopupDialog() {
        final Dialog dialog = new Dialog(StartLiveActivity.this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_game);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        GridViewWithHeaderAndFooter gridView = (GridViewWithHeaderAndFooter) dialog.findViewById(R.id.gv_game);


        gridView.setAdapter(new GameGridAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isStartGame) {
                    showToast3("请等待当前游戏结束", 0);
                    dialog.dismiss();
                    return;
                }
                if (position == 0) {
                    if (mNiuZaiPokersLayout.getVisibility() != View.VISIBLE && mConnectionState) {
                        mChatServer.doSendNiuZaiOpenGame(mUser);
                    }
                } else if (position == 1) {
                    if (mJinHuaPokersLayout.getVisibility() != View.VISIBLE && mConnectionState) {
                        mChatServer.doSendOpenGame(mUser);
                    }
                } else if (position == 2) {
                    if (mHaiDaoPokers.getVisibility() != View.VISIBLE && mConnectionState) {
                        mChatServer.doSendHaiDaoOpenGame(mUser);
                    }
                } else if (position == 3) {

                } else {
                    if (mLucklyPanLayout.getVisibility() != View.VISIBLE && mConnectionState) {
                        mChatServer.doSendPanOpenGame(mUser);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @Override
    public void initData() {
        super.initData();
        mUser = AppContext.getInstance().getLoginUser();
        mRoomNum = mUser.id;
        mTvLiveNumber.setText("房间: " + mUser.id);
        //流明
        mStreamName = getIntent().getStringExtra("stream"); //HHH 2016-09-13
        //推流地址
        rtmpPushAddress = getIntent().getStringExtra("push");
        //是否开启镜像
        isFrontCameraMirro = getIntent().getBooleanExtra("isFrontCameraMirro", false);

        mTvLiveNum.setText(ChatServer.LIVE_USER_NUMS + "人观看");
        //映票
        mTvYpNum.setText(getIntent().getStringExtra("votestotal"));
        //弹幕价格
        barrageFee = StringUtils.toInt(getIntent().getStringExtra("barrage_fee"));

        mPokersGameControl.setCoin(mUser.coin);
        //连接聊天服务器
        initChatConnection();
        initLivePlay();
    }

    /**
     * @dw 初始化连接聊天服务器
     */
    private void initChatConnection() {
        //连接socket服务器
        try {
            mChatServer = new ChatServer(new ChatListenUIRefresh(), this, getIntent().getStringExtra("chaturl"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //连麦监听
    private RTCClient.RTCEventChangedListener mRTCEventListener = new RTCClient.RTCEventChangedListener() {
        @Override
        public void onEventChanged(final int event, final Object arg1) {
            switch (event) {
                case RTCClient.RTC_EVENT_REGISTED:
                    mIsRegisted = true;
                    break;
                case RTCClient.RTC_EVENT_STARTED:
                    break;
                case RTCClient.RTC_EVENT_CALL_COMMING:
                    if (mRtcView != null)
                        mRtcView.setVisibility(View.VISIBLE);
                    int i = arg1.toString().indexOf(RTC_UINIQUE_NAME);
                    getUserInfo(arg1.toString().substring(0, i));
                    break;
                case RTCClient.RTC_EVENT_STOPPED:
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
                    Toast.makeText(StartLiveActivity.this, "RTC_ERROR_AUTH_FAILED", Toast.LENGTH_SHORT).show();
                    break;
                case RTCClient.RTC_ERROR_REGISTED_FAILED:
                    mIsRegisted = false;
//                    mStreamer.getRtcClient().registerRTC();
                    break;
                case RTCClient.RTC_ERROR_SERVER_ERROR:
                    Toast.makeText(StartLiveActivity.this, "RTC_ERROR_SERVER_ERROR", Toast.LENGTH_SHORT).show();
                case RTCClient.RTC_ERROR_CONNECT_FAIL:
                    break;
                case RTCClient.RTC_ERROR_STARTED_FAILED:
                    break;
                default:
                    break;
            }
        }
    };
    private CameraTouchHelper.OnTouchListener mRTCSubScreenTouchListener = new CameraTouchHelper.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //获取相对屏幕的坐标，即以屏幕左上角为原点
            mLastRawX = event.getRawX();
            mLastRawY = event.getRawY();
            // 预览区域的大小
            int width = view.getWidth();
            int height = view.getHeight();
            //小窗的位置信息
//            RectF subRect = mStreamer.getRTCSubScreenRect();
//            int left = (int) (subRect.left * width);
//            int right = (int) (subRect.right * width);
//            int top = (int) (subRect.top * height);
//            int bottom = (int) (subRect.bottom * height);
//            int subWidth = right - left;
//            int subHeight = bottom - top;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //只有在小屏区域才触发位置改变
//                    if (isSubScreenArea(event.getX(), event.getY(), left, right, top, bottom, mIsConnected)) {
//                        //获取相对sub区域的坐标，即以sub左上角为原点
//                        mSubTouchStartX = event.getX() - left;
//                        mSubTouchStartY = event.getY() - top;
//                        mLastX = event.getX();
//                        mLastY = event.getY();
//                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveX = (int) Math.abs(event.getX() - mLastX);
                    int moveY = (int) Math.abs(event.getY() - mLastY);
                    if (mSubTouchStartX > 0f && mSubTouchStartY > 0f && (
                            (moveX > SUB_TOUCH_MOVE_MARGIN) ||
                                    (moveY > SUB_TOUCH_MOVE_MARGIN))) {
                        //触发移动
                        mIsSubMoved = true;
//                        updateSubPosition(width, height, subWidth, subHeight, mStreamer);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //未移动并且在小窗区域，则触发大小窗切换
//                    if (!mIsSubMoved && isSubScreenArea(event.getX(), event.getY(), left, right,
//                            top, bottom, mIsConnected)) {
//                        mStreamer.switchRTCMainScreen();
//                    }
                    mIsSubMoved = false;
                    mSubTouchStartX = 0f;
                    mSubTouchStartY = 0f;
                    mLastX = 0f;
                    mLastY = 0f;
                    break;
            }
            return true;
        }
    };

    /**
     * @dw 初始化直播播放器
     */
    private void initLivePlay() {
        //直播参数配置start
        mStreamer = new LiveStream(this);
        mStreamer.setUrl(rtmpPushAddress);
        mStreamer.setDisplayPreview(mCameraPreview);
        mStreamer.setPreviewFps(20);
        mStreamer.setTargetFps(20);
        mStreamer.setVideoKBitrate(800 * 3 / 4, 800, 800 / 4);
        mStreamer.setPreviewResolution(VIDEO_RESOLUTION_480P);
        mStreamer.setTargetResolution(VIDEO_RESOLUTION_480P);
        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);

//        mStreamer.getRtcClient().setRTCErrorListener(mRTCErrorListener);
//        mStreamer.getRtcClient().setRTCEventListener(mRTCEventListener);

        mCameraTouchHelper = new CameraTouchHelper();
        mCameraTouchHelper.setCameraCapture(mStreamer.getCameraCapture());
        mCameraPreview.setOnTouchListener(mCameraTouchHelper);
        // set CameraHintView to show focus rect and zoom ratio
        // mCameraTouchHelper.setCameraHintView(mCameraHintView);
        mCameraTouchHelper.addTouchListener(mRTCSubScreenTouchListener);

        //默认美颜关闭
        //mStreamer.setBeautyFilter(RecorderConstants.FILTER_BEAUTY_DENOISE);
        //直播参数配置end

        mEmceeHead.setAvatarUrl(mUser.avatar);
        startAnimation(3);

        onRTCRegisterClick();
        initBeautyUI(mStreamer);
    }

    //开始直播
    private void startLiveStream() {
        //连接到socket服务端
        mChatServer.connectSocketServer(mUser, mStreamName, mUser.id);
    }

    @OnClick({R.id.iv_game_open,R.id.iv_live_rtc, R.id.btn_live_sound, R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.ll_live_room_info, R.id.btn_live_end_music, R.id.iv_live_music, R.id.iv_live_meiyan, R.id.iv_live_camera_control, R.id.camera_preview, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.iv_live_chat, R.id.bt_send_chat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            打开游戏
            case R.id.iv_game_open:
                if (mJinHuaPokersLayout.getVisibility() != View.VISIBLE && mConnectionState) {
                    mChatServer.doSendOpenGame(mUser);

                } else {
                    mPokersGameControl.closeGame(mChatServer, mUser, mStreamName,1);
                }

                break;
            //音效
            case R.id.btn_live_sound:
                showSoundEffectsDialog();
                break;
            //展示主播信息弹窗
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(mUser);
                break;
            //展示主播信息弹窗
            case R.id.ll_live_room_info:
                showUserInfoDialog(mUser);
                break;
            //展示点歌菜单
            case R.id.iv_live_music:
                showSearchMusicDialog();
                break;
            //美颜
            case R.id.iv_live_meiyan:
                if (!mBeauty) {
                    mBeauty = true;
                    mBeautyChooseView.setVisibility(View.VISIBLE);
                } else {
                    mBeauty = false;
                    mBeautyChooseView.setVisibility(View.GONE);
                }
                break;
            //设置
            case R.id.iv_live_camera_control:
                showSettingPopUp(v);
                break;
            //开启关闭弹幕
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.camera_preview:
                changeEditStatus(false);
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            case R.id.iv_live_back:
                onClickGoBack();
                break;
            //魅力值排行榜
            case R.id.ll_yp_labe:
                showDedicateOrder();
                break;
            //聊天输入框
            case R.id.iv_live_chat://chat gone or visble
                changeEditStatus(true);
                break;
            case R.id.bt_send_chat://send chat
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    sendChat();
                }
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.btn_live_end_music:
                stopMusic();
                mEndMusic.setVisibility(View.GONE);
                break;
            case R.id.iv_live_rtc:
                if (mIsConnected) {
                    try {
//                        mStreamer.getRtcClient().stopCall();
                        mMusic.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }
                    mIsConnected = false;
                } else {
                    showToast3("还没建立连麦", 1);
                }
                break;
        }
    }

    //连麦注册
    private void doRegister(String authString) {
        //小屏
//        mStreamer.setRTCSubScreenRect(0.65f, 0.1f, 0.35f, 0.3f,
//                RTCConstants.SCALING_MODE_CENTER_CROP);
//        mStreamer.getRtcClient().setRTCAuthInfo(RTC_AUTH_URI, authString,
//                mRoomNum);
//        mStreamer.getRtcClient().setRTCUniqueName(RTC_UINIQUE_NAME);
//        mStreamer.getRtcClient().openChattingRoom(false);
//        mStreamer.setRTCMainScreen(RTCConstants.RTC_MAIN_SCREEN_CAMERA);
//        mStreamer.getRtcClient().setRTCResolutionScale(0.5f);
//        mStreamer.getRtcClient().setRTCFps(20);
//        mStreamer.getRtcClient().setRTCMode(0);
//        mStreamer.getRtcClient().registerRTC();
    }

    private void onRTCRegisterClick() {
        PhoneLiveApi.getRtc(mRoomNum, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                doAuthFailed();
            }

            @Override
            public void onResponse(String response, int id) {
//                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (response != null) {
                    if (!mIsRegisted) {
                        //注册
                        if (mRoomNum == null || mRoomNum.equals("")) {
                            Toast.makeText(StartLiveActivity.this, "you must set the local uri before register", Toast
                                    .LENGTH_SHORT).show();
                            return;
                        }
                        try {
//                            doRegister(res.getJSONObject(0).getString("authString"), res.getJSONObject(0).getString("uniqname"), res.getJSONObject(0).getString("url"));
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
        mIsRegisted = false;
        Toast.makeText(this, "unregister waiting...", Toast
                .LENGTH_SHORT).show();
    }

    private void doAuthFailed() {
        Toast.makeText(this, "Auth failed, pls. try again", Toast
                .LENGTH_SHORT).show();
        mIsRegisted = false;
        mRtcView.setEnabled(true);
    }

    //打开魅力值排行
    private void showDedicateOrder() {
        DialogHelp.getMessageDialog(this, "正在直播点击排行会影响直播,是否继续", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                OrderWebViewActivity.startOrderWebView(StartLiveActivity.this, mUser.id);
            }
        }).show();
    }

    //当每个聊天被点击显示该用户详细信息弹窗
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() != 13) {
            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }

    /**
     * @dw 显示搜索音乐弹窗
     */
    private void showSearchMusicDialog() {
        SearchMusicDialogFragment musicFragment = new SearchMusicDialogFragment();
        musicFragment.setStyle(SearchMusicDialogFragment.STYLE_NO_TITLE, 0);
        musicFragment.show(getSupportFragmentManager(), "SearchMusicDialogFragment");
    }

    //音效调教菜单
    private void showSoundEffectsDialog() {
        MusicPlayerDialogFragment musicPlayerDialogFragment = new MusicPlayerDialogFragment();
        musicPlayerDialogFragment.show(getSupportFragmentManager(), "MusicPlayerDialogFragment");
    }

    //当主播选中了某一首歌,开始播放
    @Override
    public void onSelectMusic(Intent data) {
        startMusicStrem(data);
        mEndMusic.setVisibility(View.VISIBLE);
    }

    //发送弹幕回调方法
    @Override
    protected void sendBarrageOnResponse(String response) {
        JSONArray s = ApiUtils.checkIsSuccess(response);
        if (s != null) {
            try {
                JSONObject tokenJson = s.getJSONObject(0);
                mChatServer.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                mChatInput.setText("");
                mChatInput.setHint("开启大喇叭，" + barrageFee + "钻石/条");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //socket客户端事件监听处理
    private class ChatListenUIRefresh implements ChatServerInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (type == 1) {
                        addDanmu(c);
                    } else if (type == 2) {
                        if (StringUtils.toInt(socketMsg) == 409002) {
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

        //系统通知
        @Override
        public void onSystemNot(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {//后台关闭直播

                        videoPlayerEnd();
                        DialogHelp.getMessageDialog(StartLiveActivity.this, "直播内容涉嫌违规", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                showLiveEndDialog(mUser.id, mLiveEndYpNum, mStreamName);
                            }
                        }).show();

                    }
                }
            });

        }

        //送礼物
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {//送礼物展示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLiveEndYpNum += giftInfo.getTotalcoin();
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

                    if (!NetUtils.hasNetwork(StartLiveActivity.this)) {
                        mChatServer.close();
                    }

                }
            });
        }

        //扎金花
        @Override
        public void onJinhuaGameMessageListen(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (StringUtils.toInt(socketMsg.getAction())) {

                        case PokersGameControl.POKERS_OPEN_VIEW:

                            mJinHuaPokersLayout.setVisibility(View.VISIBLE);
                            isStartGame = true;
                            break;

                        case PokersGameControl.POKERS_START_GAME:


                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:
                            mPokersGameControl.startCountDown(1);
                            break;
                        case PokersGameControl.POKERS_RESULT_GAME:
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 1);
                            break;
                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 1);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:
                            mPokersGameControl.initGameView(StartLiveActivity.this, 1);
                            mJinHuaPokersLayout.setVisibility(View.GONE);
                            isStartGame = false;
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
                            mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(20));
                            mLlPan.setTranslationY(-TDevice.dpToPixel(180));
                            mLucklyPanLayout.setVisibility(View.VISIBLE);
                            isStartGame = true;
                            break;

                        case PokersGameControl.POKERS_START_GAME:


                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:

                            mPokersGameControl.startCountDown(3);
                            break;

                        case PokersGameControl.POKERS_RESULT_GAME:

                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 3);
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 3);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:
                            mPokersGameControl.initGameView(StartLiveActivity.this, 3);
                            mLucklyPanLayout.setVisibility(View.GONE);
                            mButtonMenuFrame.setTranslationY(TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(170));
                            mLlPan.setTranslationY(TDevice.dpToPixel(20));
                            isStartGame=false;
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
                            mHaiDaoPokers.setVisibility(View.VISIBLE);
                            isStartGame=true;
                            break;

                        case PokersGameControl.POKERS_START_GAME:


                            break;
                        case PokersGameControl.POKERS_COUNT_DOWN:
                            mPokersGameControl.startCountDown(2);
                            break;

                        case PokersGameControl.POKERS_RESULT_GAME:
                            mPokersGameControl.openGameResult(socketMsg.getCt(), mHandler, 2);
                            break;

                        case PokersGameControl.POKERS_BETTING_GAME:

                            mPokersGameControl.changeBettingCoin(mUser.id, socketMsg.getUid(), socketMsg.getParam("type", 1), socketMsg.getParam("money", 0), 2);

                            break;

                        case PokersGameControl.POKERS_CLOSE_GAME:
                            mPokersGameControl.initGameView(StartLiveActivity.this, 2);
                            mHaiDaoPokers.setVisibility(View.GONE);
                            isStartGame=false;
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
                            isStartGame=true;
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
                            mPokersGameControl.initGameView(StartLiveActivity.this, 4);
                            mNiuZaiPokersLayout.setVisibility(View.GONE);
                            isStartGame=false;
                            break;
                    }

                }
            });
        }
    }

    private void getUserInfo(String touid) {
        PhoneLiveApi.getHomePageUInfo(mRoomNum, touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast3("连麦失败", 1);
            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        mUserHomePageBean = new Gson().fromJson(res.getString(0), UserHomePageBean.class);
                        LiveCommon.showIRtcDialog(StartLiveActivity.this, "连麦请求"
                                , mUserHomePageBean.user_nicename + "请求连麦", new DialogInterface() {
                                    @Override
                                    public void cancelDialog(View v, Dialog d) {
                                        try {
//                                            mStreamer.getRtcClient().rejectCall();
                                        } catch (Exception e) {
                                            d.dismiss();
                                        }

                                        d.dismiss();
                                    }

                                    @Override
                                    public void determineDialog(View v, Dialog d) {
                                        try {
//                                            mStreamer.getRtcClient().answerCall();
                                        } catch (Exception e) {
                                            d.dismiss();
                                        }
                                        if (mPlayer != null) {
                                            if (mPlayer.isPlaying()) {
                                                Toast.makeText(StartLiveActivity.this, "为保证连麦效果，需结束音乐伴奏", Toast.LENGTH_LONG).show();
                                                stopMusic();
                                            }
                                        }
                                        Toast.makeText(StartLiveActivity.this, "连麦成功", Toast.LENGTH_SHORT).show();
                                        mIsConnected = true;
                                        d.dismiss();
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    //播放音乐
    private void startMusicStrem(Intent data) {
        //停止音乐
        mStreamer.stopBgm();
        mViewShowLiveMusicLrc.setVisibility(View.VISIBLE);
        //获取音乐路径
        String musicPath = data.getStringExtra("filepath");
        //获取歌词字符串
        String lrcStr = LiveUtils.getFromFile(musicPath.substring(0, musicPath.length() - 3) + "lrc");
        mStreamer.getAudioPlayerCapture().getMediaPlayer()
                .setOnCompletionListener(new KSYMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(IMediaPlayer iMediaPlayer) {
                        TLog.log("音乐初始化完毕");
                    }

                });
        mStreamer.getAudioPlayerCapture().getMediaPlayer()
                .setOnErrorListener(new KSYMediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                        TLog.log("音乐初始化失败");
                        return false;
                    }

                });
        mStreamer.getAudioPlayerCapture().getMediaPlayer().setVolume(1, 1);
        mStreamer.startBgm(musicPath, true);
        mStreamer.setHeadsetPlugged(true);

        //插入耳机
        //mStreamer.setHeadsetPlugged(true)
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(musicPath);
            mPlayer.setLooping(true);
            mPlayer.setVolume(0, 0);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            long beginTime = -1;

                            @Override
                            public void run() {
                                if (beginTime == -1) {
                                    beginTime = System.currentTimeMillis();
                                }

                                if (null != mPlayer) {
                                    final long timePassed = mPlayer.getCurrentPosition();
                                    StartLiveActivity.this.runOnUiThread(new Runnable() {

                                        public void run() {
                                            mLrcView.seekLrcToTime(timePassed);
                                        }
                                    });
                                }

                            }
                        }, 0, mPlayTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrcStr);

        //设置歌词
        mLrcView.setLrc(rows);
    }

    //停止歌词滚动
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //停止播放音乐
    private void stopMusic() {
        if (mEndMusic != null) {
            mEndMusic.setVisibility(View.GONE);
        }
        if (mPlayer != null && null != mStreamer) {
            mStreamer.stopBgm();
            mPlayer.stop();
            mViewShowLiveMusicLrc.setVisibility(View.GONE);
            if (mTimer != null) {
                mTimer.cancel();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CommonEvent event) {

        if (event.action == 1) {

            EventBus.getDefault().unregister(this);
            if (!NetUtils.hasNetwork(StartLiveActivity.this)) {

                videoPlayerEnd();
                new AlertDialog.Builder(StartLiveActivity.this)
                        .setTitle("提示")
                        .setMessage("网络断开连接,请检查网络后重新开始直播")
                        .setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {

                                showLiveEndDialog(mUser.id, mLiveEndYpNum, mStreamName);
                            }
                        })
                        .create()
                        .show();
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

    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!IS_START_LIVE)) {
                return super.onKeyDown(keyCode, event);
            } else {
                onClickGoBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // 判断权限请求是否通过
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    showSoundEffectsDialog();
                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您拒绝写入文件权限,无法保存歌曲,请到设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您拒绝读取文件权限,无法读取歌曲,请到设置中修改", 0);
                }
                break;
            }
        }
    }

    //主播点击退出
    private void onClickGoBack() {

        DialogHelp.getConfirmDialog(this, getString(R.string.iscloselive), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                videoPlayerEnd();
                showLiveEndDialog(mUser.id, mLiveEndYpNum, mStreamName);
            }
        }).show();
    }

    //关闭直播
    private void videoPlayerEnd() {
        IS_START_LIVE = false;
        //停止播放音乐
        stopMusic();
        //停止直播
        if (mStreamer != null && mIsRegisted) {
//            mStreamer.getRtcClient().unRegisterRTC();
        }
        mStreamer.stopCameraPreview();
        mStreamer.stopStream();


        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                mStreamer.release();
            }
        });
        //请求接口改变直播状态
        PhoneLiveApi.closeLive(mUser.id, mUser.token, mStreamName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //showToast3("关闭直播失败" ,0);
            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
        mGiftShowQueue.clear();
        mLuxuryGiftShowQueue.clear();
        mListChats.clear();

        if (mLiveContent != null && mGiftView != null) {
            mLiveContent.removeView(mGiftView);
        }
        if (mShowGiftAnimator != null) {
            mShowGiftAnimator.removeAllViews();
        }
        if (mChatServer != null) {
            mChatServer.closeLive();
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        mDanmuControl.hide();//关闭弹幕
    }

    private KSYStreamer.OnInfoListener mOnInfoListener = new KSYStreamer.OnInfoListener() {
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    TLog.log("初始化完成");
                    mStreamer.startStream();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                    TLog.log("推流成功");
                    PhoneLiveApi.changeLiveState(mUser.id, mUser.token, mStreamName, "1", null);
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
    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
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
                    isCanRtc = false;
                    //录音开启失败
                    TLog.log("录音开启失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    isCanRtc = false;
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
                    if (mStreamer != null && IS_START_LIVE) {
                        mStreamer.startStream();
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

    @Override
    public void onPause() {
        super.onPause();

        mStreamer.onPause();

        if (IS_START_LIVE) {
            mStreamer.stopCameraPreview();
        }
        mStreamer.stopStream();
        if (IS_START_LIVE && mHandler != null) {
            mHandler.postDelayed(pauseRunnable, 1000);
        }
        //提示
        mChatServer.doSendSystemMessage("主播暂时离开一下,马上回来!", mUser);

    }


    private Runnable pauseRunnable = new Runnable() {
        @Override
        public void run() {
            pauseTime++;
            if (pauseTime >= 60) {
                if (mHandler != null)
                    mHandler.removeCallbacks(this);
                videoPlayerEnd();

                return;
            }
            TLog.log(pauseTime + "定时器");
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 一般可以在onResume中开启摄像头预览
        mStreamer.startCameraPreview();
        // 调用KSYStreamer的onResume接口
        mStreamer.onResume();
        //重置时间,如果超过预期则关闭直播

        if (pauseTime >= 60) {
            showLiveEndDialog(mUser.id, mLiveEndYpNum, mStreamName);
        } else if (mHandler != null) {
            mHandler.removeCallbacks(pauseRunnable);
        }
        pauseTime = 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mStreamer != null) {
//            mStreamer.release();
//        }
        mChatServer.close();
        mChatServer = null;
        ButterKnife.reset(this);
    }

    /**
     * @param num 倒数时间
     * @dw 开始直播倒数计时
     */
    private void startAnimation(final int num) {
        final TextView tvNum = new TextView(this);
        tvNum.setTextColor(getResources().getColor(R.color.white));
        tvNum.setText(num + "");
        tvNum.setTextSize(30);
        mRoot.addView(tvNum);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNum.setLayoutParams(params);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvNum, "scaleX", 5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvNum, "scaleY", 5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRoot == null) return;
                mRoot.removeView(tvNum);
                if (num == 1) {
                    startLiveStream();
                    return;
                }
                startAnimation(num == 3 ? 2 : 1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();

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
                ShareUtils.showSharePopWindow(StartLiveActivity.this, mIvCameraControl);
            }
        });
        popView.findViewById(R.id.iv_live_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchMusicDialog();
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

    public void share(View v) {
        ShareUtils.share(this, v.getId(), mUser);
    }

    public static void startLiveActivity(Context context, String stream, String barrage_fee, String votestotal, String push, String chaturl, boolean isFrontCameraMirro) { //HHH 2016-09-13
        Intent intent = new Intent(context, StartLiveActivity.class);
        intent.putExtra("stream", stream);
        intent.putExtra("barrage_fee", barrage_fee);
        intent.putExtra("votestotal", votestotal);
        intent.putExtra("push", push);
        intent.putExtra("chaturl", chaturl);
        intent.putExtra("isFrontCameraMirro", isFrontCameraMirro);
        context.startActivity(intent);

    }

    private class GameGridAdapter extends BaseAdapter {
        int[] img = {R.drawable.kaixinniuzai, R.drawable.zhiyongsanzhang, R.drawable.haodaochuanzhang, R.drawable.erbabei, R.drawable.xinyunzhuanpan
        };
        String[] name = {
                "开心牛仔", "志勇三张", "海盗船长", "二八贝", "幸运转盘"
        };

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return img[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(AppContext.getInstance(), R.layout.item_dialog_game, null);
                viewHolder = new GameGridAdapter.ViewHolder();
                viewHolder.mGameIcon = (ImageView) convertView.findViewById(R.id.iv_gamaicon);
                viewHolder.mGameName = (TextView) convertView.findViewById(R.id.tv_gamename);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mGameIcon.setBackgroundResource(img[position]);
            viewHolder.mGameName.setText(name[position]);

            return convertView;
        }

        class ViewHolder {
            ImageView mGameIcon;
            TextView mGameName;
        }
    }

}
