package com.ylive.phonelive.ui;

import android.view.View;

import com.ylive.phonelive.R;
import com.ylive.phonelive.base.ToolBarBaseActivity;
import com.ylive.phonelive.widget.BlackTextView;

import butterknife.InjectView;

//支付宝回调页面
public class AlipayResultActivity extends ToolBarBaseActivity {
    @InjectView(R.id.tv_alipaypay_result)
    BlackTextView mAliPayResult;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_alipay_result;
    }

    @Override
    public void initView() {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.payresult));
        if(getIntent().getIntExtra("result",0) == 1){
            mAliPayResult.setText("ok");
        }else{
            mAliPayResult.setText("no");
        }
    }

    @Override
    public void onClick(View v) {

    }
}
