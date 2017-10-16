package com.ylive.phonelive.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.adapter.ManageListAdapter;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.bean.ManageListBean;
import com.ylive.phonelive.widget.BlackTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 管理员列表
 */
public class ManageListDialogFragment extends DialogFragment {
    private List<ManageListBean> mManageList = new ArrayList<>();
    @InjectView(R.id.lv_manage_list)
    ListView mListViewManageList;
    @InjectView(R.id.tv_manage_title)
    BlackTextView mTvManageTitle;
    @InjectView(R.id.iv_close)
    ImageView mIvClose;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_list,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    public void initView(View view) {
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void initData() {
        mTvManageTitle.setText("管理员列表");
        PhoneLiveApi.getManageList(AppContext.getInstance().getLoginUid(),callback);
    }
    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            AppContext.showToastAppMsg(getActivity(),"获取管理员列表失败");
        }

        @Override
        public void onResponse(String response,int id) {
            JSONArray manageListJsonObject = ApiUtils.checkIsSuccess(response);
            if(null != manageListJsonObject){
                try {
                    Gson g = new Gson();
                    if(!(manageListJsonObject.length() > 0))return;
                    for(int i = 0; i<manageListJsonObject.length();i++){
                        mManageList.add(g.fromJson(manageListJsonObject.getString(i),ManageListBean.class));
                    }

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void fillUI() {
        mListViewManageList.setAdapter(new ManageListAdapter(mManageList));
    }


}
