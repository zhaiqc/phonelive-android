package com.shenlive.phonelive.viewpagerfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.shenlive.phonelive.AppContext;

import com.shenlive.phonelive.adapter.ViewPageFragmentAdapter;
import com.shenlive.phonelive.base.BaseFragment;
import com.shenlive.phonelive.fragment.FoodFragment;
import com.shenlive.phonelive.fragment.MasterFragment;
import com.shenlive.phonelive.fragment.OutDoorsFragment;
import com.shenlive.phonelive.fragment.ScoreFragment;
import com.shenlive.phonelive.fragment.StartShowFragment;
import com.shenlive.phonelive.fragment.HotFragment;
import com.shenlive.phonelive.interf.ListenMessage;
import com.shenlive.phonelive.interf.PagerSlidingInterface;
import com.shenlive.phonelive.ui.other.PhoneLivePrivateChat;
import com.shenlive.phonelive.utils.StringUtils;
import com.shenlive.phonelive.utils.UIHelper;
import com.shenlive.phonelive.widget.PagerSlidingTabStrip;
import com.shenlive.phonlive.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IndexPagerFragment extends BaseFragment implements ListenMessage {

    private View view;
    @InjectView(R.id.mviewpager)
    ViewPager pager;

    @InjectView(R.id.iv_hot_select_region)
    ImageView mRegion;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;


    @InjectView(R.id.fl_tab_container)
    RelativeLayout flTabContainer;

    @InjectView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;

    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    public static int mSex = 0;

    public static String mArea = "";

    private EMMessageListener mMsgListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hot, null);
            ButterKnife.inject(this, view);

            initView();
            initData();

        } else {
            mIvNewMessage.setVisibility(View.GONE);
            tabs.setPageChangeListener();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt("position");
            pager.setCurrentItem(pos, true);

        }
    }

    @Override
    public void initData() {


    }


    @OnClick({R.id.iv_hot_private_chat, R.id.iv_hot_search})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hot_private_chat:
                String uid = AppContext.getInstance().getLoginUid();
                if (0 < StringUtils.toInt(uid)) {
                    mIvNewMessage.setVisibility(View.GONE);
                    UIHelper.showPrivateChatSimple(getActivity(), uid);
                }

                break;
            case R.id.iv_hot_search:
                UIHelper.showScreen(getActivity());
                break;
        }
    }

    private void initView() {
//    <string name="score">颜值</string>

//        <string name="starshow">星秀</string>
//    <string name="food">美食</string>
//    <string name="outdoors">户外</string>
//    <string name="master">达人</string>

        mIvNewMessage.setVisibility(View.GONE);
        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getFragmentManager(), pager);
        viewPageFragmentAdapter.addTab(getString(R.string.attention), "gz", HotFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.score), "gz", ScoreFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.starshow), "gz", StartShowFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.food), "gz", FoodFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.outdoors), "gz", OutDoorsFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.master), "gz", MasterFragment.class, getBundle());
//        viewPageFragmentAdapter.addTab(getString(R.string.hot), "rm", HotFragment.class, getBundle());
//        viewPageFragmentAdapter.addTab(getString(R.string.daren), "dr", NewestFragment.class, getBundle());

//        viewPageFragmentAdapter.addTab(getString(R.string.charge), "rm", ChargeFragment.class, getBundle());


        pager.setAdapter(viewPageFragmentAdapter);

        pager.setOffscreenPageLimit(2);

        tabs.setViewPager(pager);
        tabs.setUnderlineColor(getResources().getColor(R.color.white));
        tabs.setDividerColor(getResources().getColor(R.color.white));
        tabs.setTextColor(Color.BLACK);
        tabs.setTextSize(35);
        tabs.setTabPaddingLeftRight(10);
        tabs.setSelectedTextColor(getResources().getColor(R.color.global));
        tabs.setIndicatorHeight(4);
        tabs.setZoomMax(0.2f);
        tabs.setIndicatorColorResource(R.color.global);
        tabs.setPagerSlidingListen(new PagerSlidingInterface() {
            @Override
            public void onItemClick(View v, int currentPosition, int position) {


            }
        });

        pager.setCurrentItem(0);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                mRegion.setVisibility(3 == position ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        //获取私信未读数量
        if (PhoneLivePrivateChat.getUnreadMsgsCount() > 0) {
            mIvNewMessage.setVisibility(View.VISIBLE);
        }
    }

    public void listenMessage() {

        mMsgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvNewMessage.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMsgListener);

    }

    public void unListen() {
        EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        listenMessage();
    }

    @Override
    public void onPause() {
        super.onPause();
        unListen();
    }

    @Override
    public void onDestroy() {
        //注销广播
        super.onDestroy();
        unListen();

    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();

        return bundle;
    }

}
