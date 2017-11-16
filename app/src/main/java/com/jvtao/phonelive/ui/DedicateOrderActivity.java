package com.jvtao.phonelive.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jvtao.phonelive.AppConfig;

import com.jvtao.phonelive.adapter.OrderAdapter;
import com.jvtao.phonelive.api.remote.ApiUtils;
import com.jvtao.phonelive.api.remote.PhoneLiveApi;
import com.jvtao.phonelive.base.ToolBarBaseActivity;
import com.jvtao.phonelive.bean.OrderBean;
import com.jvtao.phonelive.utils.UIHelper;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonlive.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 贡献排行榜
 */
public class DedicateOrderActivity extends ToolBarBaseActivity {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    @InjectView(R.id.lv_order)
    ListView mOrderListView;
    @InjectView(R.id.tv_order_total)
    BlackTextView mOrderTotal;
    private String mTotal;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void initView() {
        mOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(DedicateOrderActivity.this,mOrderList.get(position).getUid());
            }
        });
    }
    @Override
    public void initData() {
        setActionBarTitle(AppConfig.TICK_NAME + "贡献榜");
        requestGetData();

    }

    private void requestGetData() {
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(null != res){
                    try {
                        JSONObject resJsonObj = res.getJSONObject(0);
                        Gson g = new Gson();
                        JSONArray uLsit = resJsonObj.getJSONArray("list");
                        mTotal = resJsonObj.getString("total");
                        for(int i = 0;i<uLsit.length(); i++){
                            OrderBean orderBean = g.fromJson(uLsit.getString(i), OrderBean.class);
                            orderBean.setOrderNo(String.valueOf(i));
                            mOrderList.add(orderBean);

                        }

                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        PhoneLiveApi.getYpOrder(getIntent().getIntExtra("uid", 0), callback);
    }

    private void fillUI() {
        mOrderTotal.setText(mTotal + AppConfig.TICK_NAME);
        if(0 < mOrderList.size()){
            mOrderListView.setAdapter(new OrderAdapter(mOrderList,getLayoutInflater(),this));
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getYpOrder");//BBB
    }
}
