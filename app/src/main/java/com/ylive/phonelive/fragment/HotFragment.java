package com.ylive.phonelive.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ylive.phonelive.adapter.LiveUserAdapter;
import com.ylive.phonelive.bean.LiveJson;
import com.google.gson.Gson;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.base.BaseFragment;
import com.ylive.phonelive.ui.VideoPlayerActivity;
import com.ylive.phonelive.ui.other.OnItemEvent;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.widget.SlideshowView;
import com.ylive.phonelive.widget.WPSwipeRefreshLayout;
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
 * @author 魏鹏
 * @dw 首页热门
 */
public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
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
        View view = inflater.inflate(R.layout.fragment_index_hot, null);
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
                VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position - 1));
            }

        });
    }

    @Override
    public void initData() {

        //2016.09.06 无数据不显示轮播修改 wp
        mHotUserListAdapter = new LiveUserAdapter(getActivity().getLayoutInflater(), mUserList);
        View view = inflater.inflate(R.layout.view_hot_rollpic, null);
        mSlideshowView = (SlideshowView) view.findViewById(R.id.slideshowView);
        mListUserRoom.addHeaderView(view);
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

            try {
                if (res != null) {

                    mUserList.clear();
                    mHotUserListAdapter.notifyDataSetChanged();

                    //直播数据
                    JSONArray list = res.getJSONObject(0).getJSONArray("list");

                    for (int i = 0; i < list.length(); i++) {

                        LiveJson live = new Gson().fromJson(list.getJSONObject(i).toString(), LiveJson.class);
                        mUserList.add(live);
                    }

                    //轮播

                    if (isFirst) {
                        JSONArray rollPics = res.getJSONObject(0).getJSONArray("slide");
                        mSlideshowView.addDataToUI(rollPics);
                    }

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

        mLlFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mListUserRoom.setVisibility(View.VISIBLE);

        mHotUserListAdapter.notifyDataSetChanged();

    }

    public void selectTermsScreen() {
        PhoneLiveApi.requestHotData(callback);
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
