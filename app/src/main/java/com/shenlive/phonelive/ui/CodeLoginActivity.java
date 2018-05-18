package com.shenlive.phonelive.ui;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.api.remote.ApiUtils;
import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.ToolBarBaseActivity;
import com.shenlive.phonelive.bean.UserBean;
import com.shenlive.phonelive.ui.customviews.ActivityTitle;
import com.shenlive.phonelive.utils.LoginUtils;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonelive.utils.UIHelper;
import com.shenlive.phonelive.widget.BlackEditText;
import com.shenlive.phonlive.R;
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
public class CodeLoginActivity extends ToolBarBaseActivity implements PlatformActionListener {

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @InjectView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;
    @InjectView(R.id.et_logincode)
    BlackEditText mEtCode;


//    @InjectView(R.id.et_password)
//    BlackEditText mEtUserPassword;

//    //QQ登录
//    @InjectView(R.id.iv_other_login_qq)
//    ImageView mIvQQLogin;

//    @InjectView(R.id.iv_other_login_wechat)
//    ImageView mIvWechatLogin;

//    @InjectView(R.id.ll_other_login)
//    LinearLayout mLlOtherLogin;

//    private String type;
//    private String[] names = {QQ.NAME,Wechat.NAME, SinaWeibo.NAME};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_code_login;
    }

    @Override
    public void initView() {

        //微信登录
//        mIvWechatLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showWaitDialog("正在登录...",false);
//                type = "wx";
//                otherLogin(names[1]);
//            }
//        });
        //QQ登录
//        mIvQQLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showWaitDialog("正在登录...",false);
//                type = "qq";
//                otherLogin(names[0]);
//
//            }
//        });

        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMobilLogin(CodeLoginActivity.this);
            }
        });
    }

    @Override
    public void initData() {
//        PhoneLiveApi.requestOtherLoginStatus(new StringCallback() {
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }

//            @Override
//            public void onResponse(String response, int id) {
//
//                JSONArray res = ApiUtils.checkIsSuccess(response);
//                if (res != null) {
//                    try {
//                        JSONObject object = res.getJSONObject(0);
//                        if (object.getInt("login_qq") == 1) {
//                            mIvQQLogin.setVisibility(View.VISIBLE);
//                        } else {
//                            mIvQQLogin.setVisibility(View.GONE);
//                        }
//                        if (object.getInt("login_wx") == 1) {
//                            mIvWechatLogin.setVisibility(View.VISIBLE);
//                        } else {
//                            mIvWechatLogin.setVisibility(View.GONE);
//                        }
//                        if (object.getInt("login_qq") != 1&&object.getInt("login_wx") != 1){
//                            mLlOtherLogin.setVisibility(View.GONE);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }


    @OnClick({R.id.btn_login,R.id.btn_phone_login_send_code})//,R.id.btn_doReg,R.id.tv_findPass
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_phone_login_send_code) {

            if (prepareForLogin()) {
                return;
            }
            String mUserName = mEtUserPhone.getText().toString();
            PhoneLiveApi.getLoginCode(mUserName,getCodeCallback);
            showWaitDialog(R.string.loading);


        }else  if (v.getId()==R.id.btn_login){
            if (prepareForLogin()) {
                return;
            }
            String mUserName = mEtUserPhone.getText().toString();
            String mCode = mEtCode.getText().toString();
            PhoneLiveApi.doCodeLogin(mUserName,mCode, loginCallback);
            showWaitDialog(R.string.loading);
        }

    }



    //登录回调
    private final StringCallback loginCallback = new StringCallback() {
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

                   LoginUtils.getInstance().OtherInit(CodeLoginActivity.this);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

        }
    };
//获取验证码回调
    StringCallback getCodeCallback =new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            hideWaitDialog();
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String response, int id) {
            hideWaitDialog();
            JSONArray requestRes = ApiUtils.checkIsSuccess(response);
            if (requestRes!=null){
//                Gson gson =new Gson();

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

//        //HHH 2016-09-09
//        if (mEtUserPassword.length() == 0) {
//            mEtUserPassword.setError("请输入密码");
//            mEtUserPassword.requestFocus();
//            return true;
//        }


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
//            PhoneLiveApi.otherLogin(type,platDB,callback);
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
