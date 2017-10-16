package com.ylive.phonelive.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.ui.customviews.ActivityTitle;
import com.ylive.phonelive.utils.LiveUtils;
import com.ylive.phonelive.utils.UIHelper;
import com.ylive.phonelive.widget.AvatarView;
import com.google.gson.Gson;

import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.base.ToolBarBaseActivity;
import com.ylive.phonelive.em.ChangInfo;
import com.ylive.phonelive.widget.BlackTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 用户信息详情页面
 */
public class UserInfoDetailActivity extends ToolBarBaseActivity {

    @InjectView(R.id.et_info_birthday)
    TextView etInfoBirthday;
    private UserBean mUser;
    @InjectView(R.id.rl_userHead)
    RelativeLayout mRlUserHead;
    @InjectView(R.id.rl_userNick)
    RelativeLayout mRlUserNick;
    @InjectView(R.id.rl_userSign)
    RelativeLayout mRlUserSign;
    @InjectView(R.id.rl_userSex)
    RelativeLayout mRlUserSex;
    @InjectView(R.id.tv_userNick)
    BlackTextView mUserNick;
    @InjectView(R.id.tv_userSign)
    BlackTextView mUserSign;
    @InjectView(R.id.av_userHead)
    AvatarView mUserHead;
    @InjectView(R.id.iv_info_sex)
    ImageView mUserSex;
    @InjectView(R.id.rl_userBirthday)
    RelativeLayout rlUserBir;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {
        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        final Calendar c = Calendar.getInstance();
        rlUserBir.setOnClickListener(new View.OnClickListener() { //生日修改
            @Override
            public void onClick(View v) {
                showSelectBirthday(c);
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //生日选择
    private void showSelectBirthday(final Calendar c) {
        DatePickerDialog dialog = new DatePickerDialog(UserInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                if (c.getTime().getTime() > new Date().getTime()) {
                    showToast2("请选择正确的日期");
                    return;
                }
                final String birthday = DateFormat.format("yyy-MM-dd", c).toString();
                requestSaveBirthday(birthday);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        try {
            dialog.getDatePicker().setMinDate(new SimpleDateFormat("yyyy-MM-dd").parse("1950-01-01").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        dialog.show();
    }

    //保存生日
    private void requestSaveBirthday(final String birthday) {

        PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("birthday", birthday),
                AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast2(getString(R.string.editfail));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null != res) {
                            AppContext.showToastAppMsg(UserInfoDetailActivity.this, getString(R.string.editsuccess));
                            UserBean u = AppContext.getInstance().getLoginUser();
                            u.birthday = birthday;
                            AppContext.getInstance().updateUserInfo(u);
                            etInfoBirthday.setText(birthday);

                        }
                    }
                });
    }

    @Override
    public void initData() {
        setActionBarTitle(R.string.editInfo);
        sendRequiredData();
    }

    private void sendRequiredData() {
        PhoneLiveApi.getMyUserInfo(getUserID(), getUserToken(), callback);
    }

    @Override
    public void onClick(View v) {
        if (mUser != null) {
            switch (v.getId()) {
                case R.id.rl_userNick:
                    UIHelper.showEditInfoActivity(
                            this, "修改昵称",
                            getString(R.string.editnickpromp),
                            mUser.user_nicename,
                            ChangInfo.CHANG_NICK);
                    break;
                case R.id.rl_userSign:
                    UIHelper.showEditInfoActivity(
                            this, "修改签名",
                            getString(R.string.editsignpromp),
                            mUser.signature,
                            ChangInfo.CHANG_SIGN);
                    break;
                case R.id.rl_userHead:
                    UIHelper.showSelectAvatar(this, mUser.avatar);
                    break;
                case R.id.rl_userSex:
                    UIHelper.showChangeSex(this);
                    break;

            }
        }

    }

    @Override
    protected void onRestart() {
        sendRequiredData();
        super.onRestart();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String s, int id) {

            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (res != null) {
                try {
                    mUser = new Gson().fromJson(res.getString(0), UserBean.class);
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser != null) {
            fillUI();
        }
    }


    private void fillUI() {

        mUserNick.setText(mUser.user_nicename);
        mUserSign.setText(mUser.signature);
        mUserHead.setAvatarUrl(mUser.avatar);
        mUserSex.setImageResource(LiveUtils.getSexRes(mUser.sex));
        etInfoBirthday.setText(mUser.birthday);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getMyUserInfo");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
