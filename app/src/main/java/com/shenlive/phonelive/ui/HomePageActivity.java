package com.shenlive.phonelive.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.shenlive.phonelive.AppConfig;
import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.adapter.LiveRecordAdapter;
import com.shenlive.phonelive.api.remote.ApiUtils;
import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.ToolBarBaseActivity;
import com.shenlive.phonelive.bean.LiveRecordBean;
import com.shenlive.phonelive.bean.PrivateChatUserBean;
import com.shenlive.phonelive.bean.UserHomePageBean;
import com.shenlive.phonelive.utils.LiveUtils;
import com.shenlive.phonelive.utils.StringUtils;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonelive.utils.UIHelper;
import com.shenlive.phonelive.widget.AvatarView;
import com.shenlive.phonelive.widget.BlackTextView;
import com.shenlive.phonlive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 他人信息
 */
public class HomePageActivity extends ToolBarBaseActivity {


    //昵称
    @InjectView(R.id.tv_home_page_uname)
    BlackTextView mUNice;

    @InjectView(R.id.iv_home_page_sex)
    ImageView mUSex;

    @InjectView(R.id.iv_home_page_level)
    ImageView mULevel;

    //头像
    @InjectView(R.id.av_home_page_uhead)
    AvatarView mUHead;

    //关注数
    @InjectView(R.id.tv_home_page_follow)
    BlackTextView mUFollowNum;

    //粉丝数
    @InjectView(R.id.tv_home_page_fans)
    BlackTextView mUFansNum;

    //个性签名
    @InjectView(R.id.tv_home_page_sign)
    BlackTextView mUSign;

    @InjectView(R.id.tv_home_page_sign2)
    BlackTextView mUSign2;

    @InjectView(R.id.tv_home_page_num)
    BlackTextView mUNum;

    @InjectView(R.id.ll_default_video)
    LinearLayout mDefaultVideoBg;

    @InjectView(R.id.ll_home_page_index)
    LinearLayout mHomeIndexPage;

    @InjectView(R.id.ll_home_page_video)
    LinearLayout mHomeVideoPage;

    @InjectView(R.id.tv_home_page_index_btn)
    BlackTextView mPageIndexBtn;

    @InjectView(R.id.tv_home_page_video_btn)
    BlackTextView mPageVideoBtn;

    @InjectView(R.id.tv_home_page_menu_follow)
    BlackTextView mFollowState;

    @InjectView(R.id.tv_home_page_black_state)
    BlackTextView mTvBlackState;

    @InjectView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;

    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;

    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    @InjectView(R.id.view_1)
    View mViewLine1;

    @InjectView(R.id.view_2)
    View mViewLine2;

    @InjectView(R.id.rl_live_status)
    RelativeLayout mRlLiveStatusView;

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;

    private String uid;

    private AvatarView[] mOrderTopNoThree = new AvatarView[3];

    private UserHomePageBean mUserHomePageBean;

    private ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

    private LiveRecordAdapter mLiveRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView()  {

        mLiveRecordList.setDividerHeight(1);
        mOrderTopNoThree [0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree [1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree [2] = (AvatarView) findViewById(R.id.av_home_page_order3);
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);
                //开始播放
                showLiveRecord();
            }
        });

        mLiveRecordAdapter = new LiveRecordAdapter(mRecordList);
        mLiveRecordList.setAdapter(mLiveRecordAdapter);

        ((TextView)findViewById(R.id.tv_home_tick_order)).setText(AppConfig.TICK_NAME + "排行榜");
    }

    @Override
    public void initData() {

        uid = getIntent().getStringExtra("uid");

        if(uid.equals(getUserID())){
            mLLBottomMenu.setVisibility(View.GONE);
        }

        //请求用户信息
        PhoneLiveApi.getHomePageUInfo(getUserID(), uid,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    try {
                        mUserHomePageBean = new Gson().fromJson(res.getString(0), UserHomePageBean.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillUIUserInfo();
                }
            }
        });
    }


    //ui填充
    private void fillUIUserInfo() {


        if(mUserHomePageBean.islive.equals("1")){
            mRlLiveStatusView.setVisibility(View.VISIBLE);
            mRlLiveStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppContext.getInstance().getLoginUid() == null|| StringUtils.toInt(AppContext.getInstance().getLoginUid())==0) {
                        Toast.makeText(HomePageActivity.this,"请登录..",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    VideoPlayerActivity.startVideoPlayerActivity(HomePageActivity.this,mUserHomePageBean.liveinfo);
                }
            });
        }else{
            mRlLiveStatusView.setVisibility(View.GONE);
        }

        mUHead.setAvatarUrl(mUserHomePageBean.avatar_thumb);
        mUNice.setText(mUserHomePageBean.user_nicename);
        mUSex.setImageResource(LiveUtils.getSexRes(mUserHomePageBean.sex));
        mULevel.setImageResource(LiveUtils.getLevelRes(mUserHomePageBean.level));
        mUFansNum.setText(  getString(R.string.fans) + ":" + mUserHomePageBean.fans);
        mUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.follows);
        mUSign.setText(mUserHomePageBean.signature);
        mUSign2.setText(mUserHomePageBean.signature);
        mUNum.setText(mUserHomePageBean.id);
        mFollowState.setText(StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));
        mTvBlackState.setText(StringUtils.toInt(mUserHomePageBean.isblack) == 0 ? getString(R.string.pullblack):getString(R.string.relieveblack));
        List<UserHomePageBean.ContributeBean> os =  mUserHomePageBean.contribute;
        for(int i = 0;i<os.size(); i++){
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }


        if(null != mUserHomePageBean.liverecord){
            mRecordList.clear();
            mRecordList.addAll(mUserHomePageBean.liverecord);

            if(mRecordList.size() != 0){
                mLiveRecordList.setVisibility(View.VISIBLE);
                mFensi.setVisibility(View.GONE);
                mLoad.setVisibility(View.GONE);
                mLiveRecordAdapter.notifyDataSetChanged();
            }else{
                mLiveRecordList.setVisibility(View.INVISIBLE);
                mFensi.setVisibility(View.VISIBLE);
                mLoad.setVisibility(View.GONE);
            }


        }else{
            mLiveRecordList.setVisibility(View.INVISIBLE);
            mFensi.setVisibility(View.VISIBLE);
            mLoad.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.ll_home_page_menu_lahei,R.id.ll_home_page_menu_privatechat,R.id.tv_home_page_menu_follow,R.id.rl_home_pager_yi_order,R.id.tv_home_page_follow,R.id.tv_home_page_index_btn,R.id.tv_home_page_video_btn,R.id.iv_home_page_back,R.id.tv_home_page_fans})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_home_page_menu_privatechat:
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
                pullTheBlack();
                break;
            case R.id.tv_home_page_menu_follow:
                followUserOralready();
                break;
            case R.id.tv_home_page_index_btn:
                changeLineStatus(true);
                mHomeIndexPage.setVisibility(View.VISIBLE);
                mHomeVideoPage.setVisibility(View.GONE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.global));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_home_page_video_btn:
                changeLineStatus(false);
                mHomeIndexPage.setVisibility(View.GONE);
                mHomeVideoPage.setVisibility(View.VISIBLE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.black));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.global));

                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                UIHelper.showFansActivity(this,uid);
                break;
            case R.id.tv_home_page_follow:
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order://魅力值排行榜
                OrderWebViewActivity.startOrderWebView(this,uid);
                break;
        }

    }

    private void changeLineStatus(boolean status) {
        if(status){
            mViewLine1.setBackgroundResource(R.color.global);
            mViewLine2.setBackgroundColor(Color.parseColor("#E2E2E2"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine1.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params2.height = 1;
            mViewLine2.setLayoutParams(params2);
        }else{
            mViewLine2.setBackgroundResource(R.color.global);
            mViewLine1.setBackgroundColor(Color.parseColor("#E2E2E2"));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine2.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params2.height = 1;
            mViewLine1.setLayoutParams(params2);
        }

    }


    //打开直播记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...",false);

        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(),new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response,int id) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(res != null){
                    try {
                        mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                        VideoBackActivity.startVideoBack(HomePageActivity.this,mLiveRecordBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    //拉黑
    private void pullTheBlack() {// black list
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(),uid,
                AppContext.getInstance().getToken(),
                new StringCallback(){

            @Override
            public void onError(Call call, Exception e,int id) {
                AppContext.showToastAppMsg(HomePageActivity.this,"操作失败");
            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(null == res)return;
                if(StringUtils.toInt(mUserHomePageBean.isblack) == 0){
                    //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                    try {
                        EMClient.getInstance().contactManager().addUserToBlackList(String.valueOf(mUserHomePageBean.id),true);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mUserHomePageBean.id));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }

                int isBlack = StringUtils.toInt(mUserHomePageBean.isblack);

                mUserHomePageBean.isblack = (isBlack == 0 ? "1" : "0");

                mTvBlackState.setText(isBlack == 0 ? getString(R.string.relieveblack) : getString(R.string.pullblack));
                showToast3( isBlack == 0?"拉黑成功":"解除拉黑",0);

            }
        });
    }

    //私信
    private void openPrivateChat() {

        if(StringUtils.toInt(mUserHomePageBean.isblack2) == 1){
            AppContext.showToastAppMsg(this,"你已被对方拉黑无法私信");
            return;
        }

        if(null != mUserHomePageBean){

            PhoneLiveApi.getPmUserInfo(getUserID(),mUserHomePageBean.id, new StringCallback() {
                @Override
                public void onError(Call call, Exception e,int id) {

                }

                @Override
                public void onResponse(String response,int id) {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if(null != res)
                        try {
                            UIHelper.showPrivateChatMessage(HomePageActivity.this,
                                    new Gson().fromJson(res.getString(0),PrivateChatUserBean.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            });

        }

    }


    private void followUserOralready() {

        PhoneLiveApi.showFollow(getUserID(),uid,getUserToken(),new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                mUserHomePageBean.isattention = (
                        StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? "1" : "0");

                if (StringUtils.toInt(mUserHomePageBean.isattention) == 0 ){
                    mFollowState.setText(getString(R.string.follow2));
                }else{

                    mFollowState.setText(getString(R.string.alreadyfollow));
                    if (StringUtils.toInt(mUserHomePageBean.isblack) == 0){
                        return;
                    }else {
                        pullTheBlack();
                    }
                }
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }


    @Override
    protected void onDestroy() {//BBB
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("showFollow");
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("pullTheBlack");
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }
}
