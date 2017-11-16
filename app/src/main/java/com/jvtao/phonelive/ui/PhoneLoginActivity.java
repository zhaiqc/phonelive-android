package com.jvtao.phonelive.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jvtao.phonelive.AppContext;
import com.jvtao.phonelive.api.remote.ApiUtils;
import com.jvtao.phonelive.api.remote.PhoneLiveApi;
import com.jvtao.phonelive.base.ToolBarBaseActivity;
import com.jvtao.phonelive.bean.UserBean;
import com.jvtao.phonelive.ui.customviews.ActivityTitle;
import com.jvtao.phonelive.utils.LoginUtils;
import com.jvtao.phonelive.utils.TDevice;
import com.jvtao.phonelive.utils.UIHelper;
import com.google.gson.Gson;

import com.jvtao.phonelive.widget.BlackEditText;
import com.jvtao.phonlive.R;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

/**
 *手机登陆
 */
public class PhoneLoginActivity extends ToolBarBaseActivity implements PlatformActionListener {

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @InjectView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;

    @InjectView(R.id.et_password)
    BlackEditText mEtUserPassword;

    //QQ登录
    @InjectView(R.id.iv_other_login_qq)
    ImageView mIvQQLogin;

    @InjectView(R.id.iv_other_login_wechat)
    ImageView mIvWechatLogin;

    @InjectView(R.id.ll_other_login)
    LinearLayout mLlOtherLogin;

    private String type;
    private String[] names = {QQ.NAME,Wechat.NAME, SinaWeibo.NAME};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {

        //微信登录
        mIvWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...",false);
                type = "wx";
                otherLogin(names[1]);
            }
        });
        //QQ登录
        mIvQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...",false);
                type = "qq";
                otherLogin(names[0]);

            }
        });

        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMobileRegLogin(PhoneLoginActivity.this);
            }
        });
    }

    @Override
    public void initData() {
        PhoneLiveApi.requestOtherLoginStatus(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        JSONObject object = res.getJSONObject(0);
                        if (object.getInt("login_qq") == 1) {
                            mIvQQLogin.setVisibility(View.VISIBLE);
                        } else {
                            mIvQQLogin.setVisibility(View.GONE);
                        }
                        if (object.getInt("login_wx") == 1) {
                            mIvWechatLogin.setVisibility(View.VISIBLE);
                        } else {
                            mIvWechatLogin.setVisibility(View.GONE);
                        }
                        if (object.getInt("login_qq") != 1&&object.getInt("login_wx") != 1){
                            mLlOtherLogin.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @OnClick({R.id.btn_dologin,R.id.btn_doReg,R.id.tv_findPass})
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_dologin) {

            if (prepareForLogin()) {
                return;
            }
            String mUserName = mEtUserPhone.getText().toString();
            String mPassword=  mEtUserPassword.getText().toString();

            showWaitDialog(R.string.loading);

            PhoneLiveApi.login(mUserName,mPassword, callback);

        }else  if (v.getId()==R.id.btn_doReg) {

            UIHelper.showMobileRegLogin(this);

        }else  if(v.getId()==R.id.tv_findPass) {

            UIHelper.showUserFindPass(this);
        }

    }

    private void otherLogin(String name){
        ShareSDK.initSDK(this);
        showWaitDialog("正在授权登录...",false);
        Platform other = ShareSDK.getPlatform(name);
        other.SSOSetting(true);  //设置false表示使用SSO授权方式
//        other.authorize();
        other.showUser(null);//执行登录，登录后在回调里面获取用户资料
        other.setPlatformActionListener(this);

    }


    //登录回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            hideWaitDialog();
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String s,int id) {

           hideWaitDialog();
           JSONArray requestRes = ApiUtils.checkIsSuccess(s);
           if(requestRes != null){

               Gson gson = new Gson();
               try {
                   UserBean user = gson.fromJson(requestRes.getString(0), UserBean.class);

                   AppContext.getInstance().saveUserInfo(user);

                   LoginUtils.getInstance().OtherInit(PhoneLoginActivity.this);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

        }
    };


    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }
        if (mEtUserPhone.length() != 11) {
            mEtUserPhone.setError("请输入11位的手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }

        //HHH 2016-09-09
        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }


        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    public void onComplete(Platform platform, final int i, HashMap<String, Object> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideWaitDialog();
                showToast3("授权成功正在登录....",0);
            }
        });

        //用户资源都保存到res
        //通过打印res数据看看有哪些数据是你想要的
        if (i == Platform.ACTION_USER_INFOR) {
            //showWaitDialog("正在登录...");
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            PhoneLiveApi.otherLogin(type,platDB,callback);
            //如果要删除授权信息，重新授权
            platform.removeAccount(true);
        }




    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        hideWaitDialog();
        showToast3("授权登录失败",0);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        hideWaitDialog();
        showToast3("授权已取消",0);
    }
}
