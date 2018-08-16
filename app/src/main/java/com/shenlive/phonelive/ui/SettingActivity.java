package com.shenlive.phonelive.ui;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.shenlive.phonelive.AppConfig;
import com.shenlive.phonelive.AppContext;

import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.ToolBarBaseActivity;
import com.shenlive.phonelive.ui.customviews.ActivityTitle;
import com.shenlive.phonelive.ui.customviews.LineControllerView;
import com.shenlive.phonelive.utils.LoginUtils;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonelive.utils.UIHelper;
import com.shenlive.phonelive.utils.UpdateManager;
import com.shenlive.phonlive.R;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 设置
 */
public class SettingActivity extends ToolBarBaseActivity {


    @InjectView(R.id.ll_check_update)
    LineControllerView mTvVersion;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;
    @InjectView(R.id.switch_btn)
    Switch mSwich;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        Log.d("settingyinsi",AppContext.getInstance().getLoginUser().yinsi );
        if(AppContext.getInstance().getLoginUser().yinsi.equals("1")){
            mSwich.setChecked(true);

        }else if (AppContext.getInstance().getLoginUser().yinsi.equals("0")){
            mSwich.setChecked(false);
        }

        mSwich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            StringCallback callback =new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {

                }
            };

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
//                    mText.setText("开启");
                    PhoneLiveApi.getPrivacy(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),"1",callback);
//                    AppContext.getInstance().getLoginUser().yinsi="1";
                }else {
//                    mText.setText("关闭");
                    PhoneLiveApi.getPrivacy(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),"0",callback);
//                    AppContext.getInstance().getLoginUser().yinsi="0";

                }
            }
        });
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

        mTvVersion.setContent(String.format(Locale.CHINA, "V" + TDevice.getVersionName()));
    }

    @OnClick({R.id.ll_login_out, R.id.ll_room_setting, R.id.ll_clearCache, R.id.ll_push_manage, R.id.ll_about, R.id.ll_feedback, R.id.ll_blank_list, R.id.rl_change_pass, R.id.ll_check_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_login_out:
                LoginUtils.outLogin(this);
                finish();
                break;
            //房间设置
            case R.id.ll_room_setting:

                break;
            case R.id.ll_clearCache:
                clearCache();
                break;
            case R.id.ll_push_manage:
                UIHelper.showPushManage(this);
                break;
            case R.id.ll_about:
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists", "服务条款");
                break;
            //用户反馈
            case R.id.ll_feedback:
                String model = android.os.Build.MODEL;
                String release = android.os.Build.VERSION.RELEASE;
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=newslist&uid="
                        + getUserID() + "&version=" + release + "&model=" + model, "");
                break;
            case R.id.ll_blank_list:
                UIHelper.showBlackList(SettingActivity.this);
                break;
            case R.id.rl_change_pass:
                UIHelper.showPhoneChangePassActivity(SettingActivity.this);
                break;
            case R.id.ll_check_update:
                checkNewVersion();
                break;
//            case R.id.switch_btn:
//
//
//                break;
        }
    }

    private void checkNewVersion() {
        UpdateManager manager;
        if (!AppConfig.USER_VERSION.equals(TDevice.getVersionName())) {
            manager = new UpdateManager(this, true);
            manager.checkUpdate(AppConfig.APK_URL);
//            Log.d("checkNewVersion: ", AppConfig.APK_URL);
        } else {
            manager = new UpdateManager(this, false);
            manager.checkUpdate(AppConfig.APK_URL);
        }
    }

    private void clearCache() {
        AppContext.getInstance().clearAppCache();
        AppContext.showToastAppMsg(this, "缓存清理成功");
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
