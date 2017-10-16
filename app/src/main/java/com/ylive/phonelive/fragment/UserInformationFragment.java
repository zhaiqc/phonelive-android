package com.ylive.phonelive.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ylive.phonelive.AppConfig;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.base.BaseFragment;
import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.ui.customviews.LineControllerView;
import com.ylive.phonelive.utils.LiveUtils;
import com.ylive.phonelive.utils.LoginUtils;
import com.ylive.phonelive.utils.UIHelper;
import com.ylive.phonelive.widget.AvatarView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 登录用户中心页面
 */
public class UserInformationFragment extends BaseFragment{

    //头像
    @InjectView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    //昵称
    @InjectView(R.id.tv_name)
    TextView mTvName;

    @InjectView(R.id.ll_user_container)
    View mUserContainer;

    //退出登陆
    @InjectView(R.id.ll_loginout)
    LinearLayout mLoginOut;

    //直播记录
    @InjectView(R.id.tv_info_u_live_num)
    TextView mLiveNum;

    //关注
    @InjectView(R.id.tv_info_u_follow_num)
    TextView mFollowNum;

    //粉丝
    @InjectView(R.id.tv_info_u_fans_num)
    TextView mFansNum;

    //id
    @InjectView(R.id.tv_id)
    TextView mUId;

    @InjectView(R.id.iv_sex)
    ImageView mIvSex;

    @InjectView(R.id.iv_level)
    ImageView mIvLevel;

    private UserBean mInfo;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_information,
                container, false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();

    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {

        view.findViewById(R.id.ll_live).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);
        view.findViewById(R.id.ll_fans).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_level).setOnClickListener(this);
        view.findViewById(R.id.ll_diamonds).setOnClickListener(this);
        view.findViewById(R.id.ll_about).setOnClickListener(this);
        view.findViewById(R.id.ll_authenticate).setOnClickListener(this);
        view.findViewById(R.id.tv_edit_info).setOnClickListener(this);

        mLoginOut.setOnClickListener(this);

        ((LineControllerView)view.findViewById(R.id.ll_diamonds)).setName("我的" + AppConfig.CURRENCY_NAME);
    }

    private void fillUI() {
        if (mInfo == null)
            return;

        mIvAvatar.setAvatarUrl(mInfo.avatar);
        //昵称
        mTvName.setText(mInfo.user_nicename);

        mUId.setText("ID:" + mInfo.id);

        mIvSex.setImageResource(LiveUtils.getSexRes(mInfo.sex));
        mIvLevel.setImageResource(LiveUtils.getLevelRes(mInfo.level));
    }

    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {

            sendRequestData();
        }

    }

    private void sendRequestData() {

        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),stringCallback);
    }

    private StringCallback stringCallback = new StringCallback() {
       @Override
       public void onError(Call call, Exception e,int id) {

       }

       @Override
       public void onResponse(String s,int id) {
           JSONArray res = ApiUtils.checkIsSuccess(s);
           if(res != null){

               try {
                   JSONObject object = res.getJSONObject(0);
                   mInfo = new Gson().fromJson(object.toString(),UserBean.class);
                   AppContext.getInstance().updateUserInfo(mInfo);

                   mLiveNum.setText(object.getString("lives"));
                   mFollowNum.setText(object.getString("follows"));
                   mFansNum.setText(object.getString("fans"));

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }


       }
    };

    @Override
    public void onClick(View v) {

        final int id = v.getId();
        switch (id) {

            case R.id.ll_authenticate://申请认证

                UIHelper.showWebView(getActivity(),
                        AppConfig.MAIN_URL + "/index.php?g=Appapi&m=auth&a=index&uid=" +
                                mInfo.id,"");
                break;
            case R.id.iv_avatar:
                break;
            case R.id.ll_live:
                UIHelper.showLiveRecordActivity(getActivity(),mInfo.id);
                break;
            case R.id.ll_following:
                UIHelper.showAttentionActivity(getActivity(), mInfo.id);
                break;
            case R.id.ll_fans:
                UIHelper.showFansActivity(getActivity(),mInfo.id);
                    break;
            case R.id.ll_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds:
                //我的钻石
                UIHelper.showMyDiamonds(getActivity());
                break;
            case R.id.ll_level:
                //我的等级
                UIHelper.showLevel(getActivity());
                break;

            //退出登录
            case R.id.ll_loginout:
                LoginUtils.outLogin(getActivity());
                getActivity().finish();
                break;

            case R.id.ll_profit:
                //收益
                UIHelper.showProfitActivity(getActivity());
                break;
            //编辑资料
            case R.id.tv_edit_info:
                UIHelper.showMyInfoDetailActivity(getContext());
                break;
            case R.id.ll_about:
                UIHelper.showWebView(getContext(),AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists","");
                break;

            default:
                break;
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        sendRequestData();
    }


}
