package com.shenlive.phonelive.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.adapter.LiveRecordAdapter;
import com.shenlive.phonelive.api.remote.ApiUtils;
import com.shenlive.phonelive.api.remote.PhoneLiveApi;
import com.shenlive.phonelive.base.ToolBarBaseActivity;
import com.shenlive.phonelive.bean.LiveRecordBean;
import com.google.gson.Gson;

import com.shenlive.phonelive.ui.customviews.ActivityTitle;
import com.shenlive.phonlive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends ToolBarBaseActivity {
    @InjectView(R.id.lv_live_record)
    ListView mLiveRecordList;
    private Dialog dialog;

    @InjectView(R.id.fensi)
    LinearLayout mFensi;

    @InjectView(R.id.load)
    LinearLayout mLoad;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();
    private int lastPress = 0;
    private boolean delState = false;
    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;
    private LiveRecordAdapter mLiveRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    public void initView() {
        mLiveRecordList.setDividerHeight(1);
        mLiveRecordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLiveRecordBean = mRecordList.get(i);showLiveRecord();
            }
        });

        mLiveRecordAdapter = new LiveRecordAdapter(mRecordList);
        mLiveRecordList.setAdapter(mLiveRecordAdapter);

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLiveRecordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position", String.valueOf(position));

                showDel(position);

                return true;
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.liverecord));
        requestData();
    }

    //打开回放记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...", false);
        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(), showLiveByIdCallback);

    }

    private StringCallback showLiveByIdCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            hideWaitDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            hideWaitDialog();
            JSONArray res = ApiUtils.checkIsSuccess(response);

            if (res != null) {
                try {
                    mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                    VideoBackActivity.startVideoBack(LiveRecordActivity.this, mLiveRecordBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    private StringCallback requestLiveRecordDataCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            mFensi.setVisibility(View.GONE);
            mLoad.setVisibility(View.VISIBLE);
            mLiveRecordList.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String response, int id) {
            JSONArray liveRecordJsonArray = ApiUtils.checkIsSuccess(response);
            if (null != liveRecordJsonArray) {
                try {

                    mRecordList.clear();
                    mLiveRecordAdapter.notifyDataSetChanged();
                    if (0 < liveRecordJsonArray.length()) {
                        Gson g = new Gson();
                        for (int i = 0; i < liveRecordJsonArray.length(); i++) {
                            mRecordList.add(g.fromJson(liveRecordJsonArray.getString(i), LiveRecordBean.class));
                        }
                    }

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                mFensi.setVisibility(View.VISIBLE);
                mLoad.setVisibility(View.GONE);
                mLiveRecordList.setVisibility(View.INVISIBLE);
            }

        }
    };


    //请求数据
    private void requestData() {

        PhoneLiveApi.getLiveRecord(getIntent().getStringExtra("uid"), requestLiveRecordDataCallback);
    }

    private void fillUI() {
        mFensi.setVisibility(View.GONE);
        mLoad.setVisibility(View.GONE);
        mLiveRecordList.setVisibility(View.VISIBLE);
        mLiveRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getLiveRecordById");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }


    private void showDel(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("是否删除改文件!");
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {

                StringCallback callback =new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }
                };
                PhoneLiveApi.delVideo(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mRecordList.get(position).getId(),callback);
//                Log.d("showid",   mRecordList.get(position).getShowid());
                mRecordList.get(position).getShowid();
                mRecordList.remove(position);
                mLiveRecordAdapter.notifyDataSetChanged();
            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

}
