package com.ylive.phonelive.ui;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.ylive.phonelive.AppConfig;
import com.ylive.phonelive.R;
import com.ylive.phonelive.adapter.RechangeAdapter;
import com.ylive.phonelive.alipay.Keys;
import com.ylive.phonelive.bean.RechargeJson;
import com.ylive.phonelive.ui.customviews.ActivityTitle;
import com.ylive.phonelive.utils.DialogHelp;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.WxPay.WChatPay;
import com.ylive.phonelive.alipay.AliPay;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.base.ToolBarBaseActivity;
import com.ylive.phonelive.bean.RechargeBean;
import com.ylive.phonelive.utils.UIHelper;
import com.ylive.phonelive.widget.BlackTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * 我的钻石
 */
public class UserDiamondsActivity extends ToolBarBaseActivity {

    @InjectView(R.id.lv_select_num_list)
    ListView mSelectNumListItem;

    @InjectView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private List<RechargeBean> mRechargeList = new ArrayList<>();

    private final int WX_PAY    = 1;
    private final int ALI_PAY   = 2;

    private int PAY_MODE = WX_PAY;

    private BlackTextView mCoin;
    private View mHeadView;

    private WChatPay mWChatPay;
    private AliPay mAliPayUtils;

    private RechangeAdapter mRechangeAdapter;
    private RechargeJson mRechargeJson;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_diamonds;
    }

    @Override
    public void initView() {

        mActivityTitle.setTitle("我的"+ AppConfig.CURRENCY_NAME);
        mHeadView = getLayoutInflater().inflate(R.layout.view_diamonds_head,null);
        mCoin     = (BlackTextView) mHeadView.findViewById(R.id.tv_coin);

        mSelectNumListItem.addHeaderView(mHeadView);

        mSelectNumListItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                UIHelper.showWebPay(getBaseContext(), String.valueOf(position));

//                DialogHelp.getSelectDialog(UserDiamondsActivity.this, new String[]{"支付宝", "微信"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        PAY_MODE = i == 0 ? ALI_PAY : WX_PAY;
//
//                        actionPay(String.valueOf(mRechargeList.get(position - 1).money), mRechargeList.get(position - 1).coin
//                                ,mRechargeList.get(position - 1).id);
//                    }
//                }).create().show();


            }
        });
        mSelectNumListItem.setDivider(null);
        mRechangeAdapter = new RechangeAdapter(mRechargeList);
        mSelectNumListItem.setAdapter(mRechangeAdapter);

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void initData() {
        requestData();

        mAliPayUtils = new AliPay(this);
        mWChatPay    = new WChatPay(this);


    }

    private void actionPay(String money, String num,String changeid) {

        if (PAY_MODE == ALI_PAY && checkPayMode()) {

            mAliPayUtils.initPay(money, num,changeid);

        }else if(checkPayMode()){

            mWChatPay.initPay(money, num,changeid);
        }
    }

    //检查支付配置
    private boolean checkPayMode(){

        if(PAY_MODE == ALI_PAY){
            if(mRechargeJson.aliapp_switch.equals("1")){
                return true;
            }else{

                showToast3("支付宝未开启",0);
                return false;
            }
        }else if(PAY_MODE == WX_PAY){
            if(mRechargeJson.wx_switch.equals("1")){
                return true;
            }else{

                showToast3("微信未开启",0);
                return false;
            }
        }

        return false;

    }

    private void requestData() {

        PhoneLiveApi.requestBalance(getUserID(),getUserToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray array = ApiUtils.checkIsSuccess(response);

                if(array != null){

                    try {
                        mRechargeJson = new Gson().fromJson(array.getString(0),RechargeJson.class);
                        mRechargeList.clear();
                        mRechargeList.addAll(mRechargeJson.rules);
                        mRechangeAdapter.notifyDataSetChanged();
                        mCoin.setText(mRechargeJson.coin);

                        //微信支付appid
                        AppConfig.GLOBAL_WX_KEY = mRechargeJson.wx_appid;

                        //支付宝
                        Keys.DEFAULT_PARTNER    = mRechargeJson.aliapp_partner;
                        Keys.DEFAULT_SELLER     = mRechargeJson.aliapp_seller_id;
                        Keys.PRIVATE            = mRechargeJson.aliapp_key_android;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View v) {


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    //充值结果
    public void rechargeResult(boolean isOk, String rechargeMoney) {
        if(isOk){
            mCoin.setText(String.valueOf(StringUtils.toInt(mCoin.getText().toString()) +
                    StringUtils.toInt(rechargeMoney)));
        }
    }




}
