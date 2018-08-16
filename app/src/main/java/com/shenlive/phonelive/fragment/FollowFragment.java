package com.shenlive.phonelive.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shenlive.phonelive.AppConfig;
import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.adapter.LiveUserAdapter;
import com.shenlive.phonelive.api.remote.ApiUtils;
import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.BaseFragment;
import com.shenlive.phonelive.bean.LiveJson;
import com.shenlive.phonelive.ui.VideoPlayerActivity;
import com.shenlive.phonelive.ui.other.OnItemEvent;
import com.shenlive.phonelive.utils.StringUtils;
import com.shenlive.phonelive.widget.SlideshowView;
import com.shenlive.phonelive.widget.WPSwipeRefreshLayout;
import com.shenlive.phonlive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 *
 * @dw 首页热门
 */
public class FollowFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.lv_live_room)
    ListView mListUserRoom;

    //默认提示
    @InjectView(R.id.fensi)
    LinearLayout mLlFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    private SlideshowView mSlideshowView;

    @InjectView(R.id.refreshLayout)
    WPSwipeRefreshLayout mSwipeRefreshLayout;

    private List<LiveJson> mUserList = new ArrayList<>();

    private LayoutInflater inflater;

    private LiveUserAdapter mHotUserListAdapter;

    private boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, null);
        ButterKnife.inject(this, view);
        this.inflater = inflater;

        initView();
        initData();

        return view;
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListUserRoom.setOnItemClickListener(new OnItemEvent(1000) {
            @Override
            public void singleClick(View v, int position) {
                if (AppContext.getInstance().getLoginUid() == null|| StringUtils.toInt(AppContext.getInstance().getLoginUid())==0) {
                    Toast.makeText(getContext(),"请登录..",Toast.LENGTH_SHORT).show();
                    return;
                }
                VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position));
            }

        });
    }

    @Override
    public void initData() {

        //2016.09.06 无数据不显示轮播修改 wp

//        View view = inflater.inflate(R.layout.view_hot_rollpic, null);
//        mSlideshowView = (SlideshowView) view.findViewById(R.id.slideshowView);
//        mListUserRoom.addHeaderView(view);
        mHotUserListAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);
        mListUserRoom.setAdapter(mHotUserListAdapter);
    }


    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

            mSwipeRefreshLayout.setRefreshing(false);
            mLlFensi.setVisibility(View.GONE);
            mLoad.setVisibility(View.VISIBLE);
            mListUserRoom.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String s, int id) {
            mSwipeRefreshLayout.setRefreshing(false);
            JSONArray res = ApiUtils.checkIsSuccess(s);
            Log.d("follow", String.valueOf(res));
            try {
                if (res != null) {

                    mUserList.clear();
                    mHotUserListAdapter.notifyDataSetChanged();
                    for (int i =0 ; i<res.length();i++){
                        LiveJson live = new Gson().fromJson(res.getJSONObject(i).toString(), LiveJson.class);
                        mUserList.add(live);
                    }

                    //轮播

//                    if (isFirst) {
//                        JSONArray rollPics = res.getJSONObject(0).getJSONArray("slide");
//                        mSlideshowView.addDataToUI(rollPics);
//                    }

                    isFirst = false;
                    fillUI();

                } else {
                    mLlFensi.setVisibility(View.VISIBLE);
                    mLoad.setVisibility(View.GONE);
                    mListUserRoom.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void fillUI() {
//        mTvPrompt.setVisibility(View.GONE);
//        mShibaiLoad.setVisibility(View.GONE);
//        mLvAttentions.setVisibility(View.VISIBLE);
//        mAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);
//        mLvAttentions.setAdapter(mAdapter);
        mLlFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mListUserRoom.setVisibility(View.VISIBLE);
        mHotUserListAdapter.notifyDataSetChanged();

    }

    public void selectTermsScreen() {
        PhoneLiveApi.getAttentionLive(AppContext.getInstance().getLoginUid(),callback);
    }


    @Override
    public void onResume() {
        super.onResume();
        selectTermsScreen();
    }

    //下拉刷新
    @Override
    public void onRefresh() {

        selectTermsScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("selectTermsScreen");
    }
}
