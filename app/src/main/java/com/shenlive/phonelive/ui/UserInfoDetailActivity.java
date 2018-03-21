package com.shenlive.phonelive.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.adapter.ClassifyAdapter;
import com.shenlive.phonelive.api.remote.ApiUtils;
import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.ToolBarBaseActivity;
import com.shenlive.phonelive.bean.DirectoryBean;
import com.shenlive.phonelive.bean.UserBean;
import com.shenlive.phonelive.em.ChangInfo;
import com.shenlive.phonelive.ui.customviews.ActivityTitle;
import com.shenlive.phonelive.utils.LiveUtils;
import com.shenlive.phonelive.utils.UIHelper;
import com.shenlive.phonelive.widget.AvatarView;
import com.shenlive.phonelive.widget.BlackTextView;
import com.shenlive.phonlive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 用户信息详情页面
 */
public class UserInfoDetailActivity extends ToolBarBaseActivity {
    private List<String> listClassify;
    private List<String> listClassifyId;
    private DirectoryBean mDirectory;
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
    @InjectView(R.id.rl_classify)
    RelativeLayout mClassify;
    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;
    @InjectView(R.id.tv_classify)
    TextView mTvClassify;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {
        SharedPreferences preferences=getSharedPreferences("Directory", Context.MODE_PRIVATE);
        String classify=preferences.getString("classify", "默认分类");
        mTvClassify.setText(classify);

        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        mClassify.setOnClickListener(this);
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
                        Log.d("onResponsss: ", response);
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
        getClassify();
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
                case R.id.rl_classify:
                    showDialog();
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

    private void getClassify() {
        PhoneLiveApi.getDirectory(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                listClassify = new ArrayList<>();
                listClassifyId = new ArrayList<>();
                if (response != null) {
                    Log.d("onResponse: ", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectData = (JSONObject) jsonArray.get(i);
                            Gson gson = new Gson();
                            mDirectory = gson.fromJson(String.valueOf(jsonObjectData), DirectoryBean.class);
                            listClassify.add(mDirectory.getTitle());
                            listClassifyId.add(mDirectory.getId());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

        });


    }


    private void showDialog() {
        View bottomView = View.inflate(UserInfoDetailActivity.this, R.layout.dialog_layout, null);//填充ListView布局
        final ListView lvClassify = (ListView) bottomView.findViewById(R.id.list_dialog);//初始化ListView控件
        lvClassify.setAdapter(new ClassifyAdapter(this, listClassify));//ListView设置适配器

        final AlertDialog mDialog = new AlertDialog.Builder(this)
                .setTitle("选择分类").setView(bottomView)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        mDialog.show();
        lvClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setClassify(listClassify.get(position), listClassifyId.get(position), mDialog);
            }
        });
    }

    private void setClassify(final String type, String id, final AlertDialog mDialog) {
        PhoneLiveApi.setDirectory(id, AppContext.getInstance().getLoginUid(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("onResponse: ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.get("data").equals("0")) {
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = getApplication().getSharedPreferences("Directory", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("classify", type);
                        editor.commit();
                        mTvClassify.setText(type);
                        mDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
                    }
                    jsonObject.get("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
