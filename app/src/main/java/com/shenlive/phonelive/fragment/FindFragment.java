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
import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.adapter.LiveUserAdapter;
import com.shenlive.phonelive.adapter.NearLiveAdapter;
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
import java.util.IllegalFormatCodePointException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * @author by zqc
 * @dw 发现
 */
public class FindFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
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

    private NearLiveAdapter mNearListAdapter;

    private boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, null);
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
//                Log.d("singleClick: ","点击了"+position );
            }

        });
    }

    @Override
    public void initData() {

        mNearListAdapter = new NearLiveAdapter(getActivity().getLayoutInflater(), mUserList);
        mListUserRoom.setAdapter(mNearListAdapter);
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
            Log.d("livebean", String.valueOf(res));

            try {
                if (res != null) {
                    if (res.length()<=0){
//                        Log.d("onResponse: ", String.valueOf(res.length()));
                        mLlFensi.setVisibility(View.VISIBLE);
                        mLoad.setVisibility(View.GONE);
                        mListUserRoom.setVisibility(View.INVISIBLE);
                    }else {
                        mUserList.clear();
                        mNearListAdapter.notifyDataSetChanged();
                        for (int i =0 ; i<res.length();i++){
                            LiveJson live = new Gson().fromJson(res.getJSONObject(i).toString(), LiveJson.class);
                            mUserList.add(live);

                        }


                        //轮播


                        fillUI();
                    }


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
        mNearListAdapter.notifyDataSetChanged();

    }

    public void selectTermsScreen() {
        PhoneLiveApi.getNearbyRoom(callback);
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
