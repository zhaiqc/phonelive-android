package com.ylive.phonelive.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.adapter.NewestAdapter;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.base.BaseFragment;
import com.ylive.phonelive.bean.LiveJson;
import com.ylive.phonelive.ui.VideoPlayerActivity;
import com.ylive.phonelive.ui.other.OnItemEvent;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.TDevice;
import com.ylive.phonelive.widget.HeaderGridView;
import com.google.gson.Gson;
import com.ylive.phonelive.R;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
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
 * 首页最新直播
 */
public class NewestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    List<LiveJson> mUserList = new ArrayList<>();

    @InjectView(R.id.gv_newest)
    HeaderGridView mNewestLiveView;

    @InjectView(R.id.sl_newest)
    SwipeRefreshLayout mRefresh;
    //默认提示
    @InjectView(R.id.newest_fensi)
    LinearLayout mFensi;

    @InjectView(R.id.newest_load)
    LinearLayout mLoad;

    private int wh;

    private NewestAdapter newestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newest, null);

        ButterKnife.inject(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void initData() {

        newestAdapter = new NewestAdapter(mUserList);
        mNewestLiveView.setAdapter(newestAdapter);//BBB
    }

    @Override
    public void initView(View view) {
        mNewestLiveView.setOnItemClickListener(new OnItemEvent(1000) {
            @Override
            public void singleClick(View v, int position) {
                if (AppContext.getInstance().getLoginUid() == null|| StringUtils.toInt(AppContext.getInstance().getLoginUid())==0) {
                    Toast.makeText(getContext(),"请登录..",Toast.LENGTH_SHORT).show();
                    return;
                }
                VideoPlayerActivity.startVideoPlayerActivity(getContext(), mUserList.get(position ));
            }

        });
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);
    }

    //最新主播数据请求
    private void requestData() {

        PhoneLiveApi.getNewestUserList(new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {

                if(mRefresh!=null) {

                    mRefresh.setRefreshing(false);
                    mFensi.setVisibility(View.GONE);
                    mLoad.setVisibility(View.VISIBLE);
                    mNewestLiveView.setVisibility(View.INVISIBLE );
                }

            }

            @Override
            public void onResponse(String response,int id) {

                if(mRefresh!=null) {
                    mRefresh.setRefreshing(false);
                }

                JSONArray resUserListJsonArr = ApiUtils.checkIsSuccess(response);

                if (null != resUserListJsonArr) {

                    try {
                        mUserList.clear();
                        Gson g = new Gson();
                        for (int i = 0; i < resUserListJsonArr.length(); i++) {
                            mUserList.add(g.fromJson(resUserListJsonArr.getString(i), LiveJson.class));
                        }

                        if (mUserList.size() > 0){
                            fillUI();
                        }else{

                            mFensi.setVisibility(View.VISIBLE);
                            mLoad.setVisibility(View.GONE);
                            mNewestLiveView.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }else{
                    mFensi.setVisibility(View.VISIBLE);
                    mLoad.setVisibility(View.GONE);
                    mNewestLiveView.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void fillUI() {

        mFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mNewestLiveView.setVisibility(View.VISIBLE);
        if (getActivity() != null) {
            //设置每个主播宽度
            int w = (int) TDevice.getScreenWidth();
            wh = w / 2;
            mNewestLiveView.setColumnWidth(wh);
            newestAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getNewestUserList");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
