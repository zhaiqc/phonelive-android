package com.shenlive.phonelive.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenlive.phonelive.AppContext;

import com.shenlive.phonelive.bean.LiveJson;
import com.shenlive.phonelive.utils.SimpleUtils;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonlive.R;

import java.util.List;

/**
 * Created by weipeng on 2016/12/23.
 */

public class NewestAdapter extends BaseAdapter {

    private List<LiveJson> mUserList;
    private int wh;

    public NewestAdapter(List<LiveJson> userList) {

        mUserList = userList;
        int w = TDevice.getDisplayMetrics().widthPixels;
        wh = w / 2;

    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_newest_user, null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (ImageView) convertView.findViewById(R.id.iv_newest_item_user);
            viewHolder.mUHead.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, wh));

            viewHolder.iv_bg= (ImageView) convertView.findViewById(R.id.iv_news_bg);
            viewHolder.iv_bg.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, wh));
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
            viewHolder.mIvLabel = (ImageView) convertView.findViewById(R.id.iv_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LiveJson u = mUserList.get(position);
        viewHolder.tv_name.setText(u.user_nicename);
        if (u.admission.equals("") || u.admission == null) {
            viewHolder.mIvLabel.setVisibility(View.GONE);
        } else if (u.admission.equals("收费")) {
            viewHolder.mIvLabel.setVisibility(View.VISIBLE);
        }
        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mUHead,u.thumb,0);
        return convertView;
    }

    class ViewHolder {
        ImageView mUHead, iv_bg, mIvLabel;
        TextView tv_name;
    }
}
