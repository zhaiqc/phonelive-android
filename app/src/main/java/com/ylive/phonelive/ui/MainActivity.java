package com.ylive.phonelive.ui;


import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.ylive.phonelive.AppConfig;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.AppManager;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.base.ToolBarBaseActivity;
import com.ylive.phonelive.bean.BonusBean;
import com.ylive.phonelive.em.MainTab;
import com.ylive.phonelive.fragment.LoginAwardDialogFragment;
import com.ylive.phonelive.interf.BaseViewInterface;
import com.ylive.phonelive.ui.dialog.LiveCommon;
import com.ylive.phonelive.utils.LoginUtils;
import com.ylive.phonelive.utils.SharedPreUtil;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.TDevice;
import com.ylive.phonelive.utils.TLog;
import com.ylive.phonelive.utils.UIHelper;
import com.ylive.phonelive.utils.UpdateManager;
import com.ylive.phonelive.widget.BlackTextView;
import com.ylive.phonelive.widget.MyFragmentTabHost;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;


//主页面
public class MainActivity extends ToolBarBaseActivity implements
        TabHost.OnTabChangeListener, BaseViewInterface,
        View.OnTouchListener, LoginAwardDialogFragment.onLoginAwardImgShow {
    @InjectView(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;
    ImageView cart;
    @InjectView(R.id.drawer_layout)
    RelativeLayout ml;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];
    RelativeLayout layout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public boolean isStartingLive = true;

    LoginAwardDialogFragment mAwardDialogFragment;

    @Override
    public void initView() {

        AppManager.getAppManager().addActivity(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        getSupportActionBar().hide();
        initTabs();

        mTabHost.setCurrentTab(100);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setNoTabChangedTag("1");


    }

    private void initTabs() {
        final MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        String[] title = new String[]{"首页", "", "我"};

        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];

            TabHost.TabSpec tab = mTabHost.newTabSpec(String.valueOf(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            ImageView tabImg = (ImageView) indicator.findViewById(R.id.tab_img);
            BlackTextView tabTv = (BlackTextView) indicator.findViewById(R.id.tv_wenzi);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            tabTv.setText(title[i]);
            if (i == 2) {
                cart = tabImg;
            }
            if (i == 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) TDevice.dpToPixel(50), ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 2, 0);
                tabImg.setLayoutParams(params);
                tabImg.setVisibility(View.GONE);
            }
            tabImg.setImageDrawable(drawable);
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });

            mTabHost.addTab(tab, mainTab.getClz(), null);

            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);

        }

        mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLive();
            }
        });
    }

    @Override
    public void initData() {
        updateConfig();
        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //检查是否有最新版本
//        checkNewVersion();


        mTabHost.setCurrentTab(0);

        Bundle bundle = getIntent().getBundleExtra("USER_INFO");

        if (bundle != null) {
            UIHelper.showLookLiveActivity(this, bundle);
        }


        initAMap();
    }



    private void updateConfig() {

        /*if(SharedPreUtil.getBoolean(this,"isSaveConfig")){

            AppConfig.TICK_NAME     = SharedPreUtil.getString(this,"name_votes");
            AppConfig.CURRENCY_NAME = SharedPreUtil.getString(this,"name_coin");
            AppConfig.JOIN_ROOM_ANIMATION_LEVEL = SharedPreUtil.getInt(this,"enter_tip_level");

            return;
        }*/
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        AppConfig.APK_URL = res.getJSONObject(0).getString("apk_url");
                        AppConfig.USER_VERSION = res.getJSONObject(0).getString("apk_ver");
                        AppConfig.TICK_NAME = res.getJSONObject(0).getString("name_votes");
                        AppConfig.CURRENCY_NAME = res.getJSONObject(0).getString("name_coin");
                        AppConfig.JOIN_ROOM_ANIMATION_LEVEL = res.getJSONObject(0).getInt("enter_tip_level");
                        AppConfig.ROOM_CHARGE_SWITCH = res.getJSONObject(0).getInt("live_cha_switch");
                        AppConfig.ROOM_PASSWORD_SWITCH = res.getJSONObject(0).getInt("live_pri_switch");
                        SharedPreUtil.put(MainActivity.this, "name_votes", AppConfig.TICK_NAME);
                        SharedPreUtil.put(MainActivity.this, "name_coin", AppConfig.CURRENCY_NAME);
                        SharedPreUtil.put(MainActivity.this, "enter_tip_level", AppConfig.JOIN_ROOM_ANIMATION_LEVEL);
                        SharedPreUtil.put(MainActivity.this, "isSaveConfig", true);
                        int maintain_switch = res.getJSONObject(0).getInt("maintain_switch");
                        if (maintain_switch == 1) {
                            String maintain_tips = res.getJSONObject(0).getString("maintain_tips");
                            LiveCommon.showMainTainDialog(MainActivity.this, maintain_tips);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //检查是否有最新版本
                checkNewVersion();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    requestStartLive();
                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("您已拒绝使用摄像头权限,将无法正常直播,请去设置中修改");
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("您已拒绝使用录音权限,将无法正常直播,请去设置中修改");
                } else if (grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您没有同意使用读写文件权限,无法正常直播,请去设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("定位权限未打开", 0);
                } else if (grantResults.length > 0 && grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                    showToast3("您没有同意使用定位权限,无法正常直播,请去设置中修改", 0);
                }
                return;
            }
        }
    }

    //请求服务端开始直播
    private void requestStartLive() {
        UIHelper.showStartLiveActivity(MainActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        isStartingLive = true;
    }

    //登录环信即时聊天
    private void loginIM() {
        String uid = String.valueOf(AppContext.getInstance().getLoginUid());

        EMClient.getInstance().login(uid,
                "fmscms" + uid, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                TLog.log("环信[登录聊天服务器成功]");
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        if (204 == code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.showToastAppMsg(MainActivity.this, "聊天服务器登录和失败,请重新登录");
                                }
                            });

                        }
                        TLog.log("环信[主页登录聊天服务器失败" + "code:" + code + "MESSAGE:" + message + "]");
                    }
                });


    }

    /**
     * @dw 注册极光推送
     */
    private void registerJpush() {
        JPushInterface.setAlias(this, AppContext.getInstance().getLoginUid() + "PUSH",
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        TLog.log("极光推送注册[" + i + "I" + "S:-----" + s + "]");
                    }
                });

    }

    /**
     * @dw 检查token是否过期
     */
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(null);
    }

    /**
     * @dw 检查是否有最新版本
     */
    private void checkNewVersion() {
        UpdateManager manager;
//        Log.d("checkNewVersion:", AppConfig.USER_VERSION + "hahahahaahahah");
        if (!AppConfig.USER_VERSION.equals(TDevice.getVersionName())) {
            manager = new UpdateManager(this, true);
            manager.checkUpdate(AppConfig.APK_URL);
//            Log.d("checkNewVersion: ", AppConfig.APK_URL);
        }

    }

    //开始直播初始化
    public void startLive() {
        if (Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        5);

            } else {
                requestStartLive();
            }
        } else {
            requestStartLive();
        }

    }


    @Override
    public void onTabChanged(String tabId) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return false;

    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLoginAwardImgShow(View view, String i) {

        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.setLayoutParams(layoutParams);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.banner);
        layoutParams = new RelativeLayout.LayoutParams((int) TDevice.dpToPixel(150), (int) TDevice.dpToPixel(50));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(layoutParams);
        layout.addView(imageView);
        TextView textView = new TextView(this);
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setText("X" + i);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setLayoutParams(layoutParams);
        layout.addView(textView);
        ml.addView(layout);
        layout.setAnimation(getAlphaAnimationOut());
        addCart(imageView);
    }

    public Animation getAlphaAnimationOut() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 0.2f, 1.5f, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        return scaleAnimation;
    }

    private void addCart(ImageView iv) {
//      一、创造出执行动画的主题---imageview
        //代码new一个imageview，图片资源是上面的imageview的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        final ImageView goods = new ImageView(this);
        goods.setImageResource(R.drawable.star2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        ml.addView(goods, params);

//        二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        ml.getLocationInWindow(parentLocation);

        //得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        iv.getLocationInWindow(startLoc);

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        cart.getLocationInWindow(endLoc);


//        三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = TDevice.getScreenWidth() / 2;
        float startY = TDevice.getScreenHeight() / 2;

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + cart.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

//        四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });
//      五、 开始执行动画
        valueAnimator.start();

//      六、动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // 把移动的图片imageview从父布局里移除
                ml.removeView(layout);
                ml.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        everyBonus();
    }

    private void everyBonus() {
        PhoneLiveApi.getBonus(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        BonusBean mBonus = new Gson().fromJson(res.getString(0), BonusBean.class);
                        if (StringUtils.toInt(mBonus.getBonus_switch()) == 1 && StringUtils.toInt(mBonus.getBonus_day()) > 0) {
                            mAwardDialogFragment = new LoginAwardDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("BonusBean", mBonus);
                            mAwardDialogFragment.setArguments(bundle);
                            if (!mAwardDialogFragment.isAdded())
                                mAwardDialogFragment.show(getSupportFragmentManager(), "LoginAwardDialogFragment");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
