package com.shenlive.phonelive.em;


import com.shenlive.phonelive.fragment.AttentionFragment;
import com.shenlive.phonelive.fragment.FindFragment;
import com.shenlive.phonelive.fragment.FollowFragment;
import com.shenlive.phonelive.fragment.UserInformationFragment;
import com.shenlive.phonelive.fragment.myFindFragment;
import com.shenlive.phonelive.ui.StartLiveActivity;
import com.shenlive.phonelive.viewpagerfragment.IndexPagerFragment;
import com.shenlive.phonlive.R;

/**
 * Created by Administrator on 2016/3/9.
 */
public enum  MainTab {
    LIVE(0, R.drawable.btn_tab_hot_background,0, IndexPagerFragment.class),
    FOLLOW(1, R.drawable.btn_tab_follow_background,1, AttentionFragment.class),
    STAR_LIVE(2, R.drawable.btn_tab_live_background,2, StartLiveActivity.class),
    FIND(3, R.drawable.btn_tab_find_background,3, FindFragment.class),
    MY(4, R.drawable.btn_tab_home_background,4, UserInformationFragment.class);
    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int idx, int resIcon,int resName, Class<?> clz) {
        this.idx = idx;
        this.resIcon = resIcon;
        this.resName = resName;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
