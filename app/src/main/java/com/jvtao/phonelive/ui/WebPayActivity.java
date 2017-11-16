package com.jvtao.phonelive.ui;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;


import com.jvtao.phonelive.AppConfig;
import com.jvtao.phonelive.AppContext;

import com.jvtao.phonelive.base.ToolBarBaseActivity;
import com.jvtao.phonelive.ui.customviews.ActivityTitle;
import com.jvtao.phonlive.R;

import butterknife.InjectView;

/**
 * Created by zqc on 2017/9/29.
 */

public class WebPayActivity extends ToolBarBaseActivity {
    @InjectView(R.id.webView)
    WebView mWbView;
    @InjectView(R.id.activity_title)
    ActivityTitle mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_web_pay;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {
        mTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        Log.d("initData: ", intent.getStringExtra("position"));
        WebSettings settings = mWbView.getSettings();
        settings.setJavaScriptEnabled(true);
        Log.d("initData: ", AppContext.getInstance().getLoginUid()  );
        mWbView.loadUrl(AppConfig.MAIN_URL + "/index.php?g=cz&m=index&a=index&uid=" + AppContext.getInstance().getLoginUid() + "&rules=" + intent.getStringExtra("position"));

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        mWbView.destroy();
        super.onDestroy();
    }
}
