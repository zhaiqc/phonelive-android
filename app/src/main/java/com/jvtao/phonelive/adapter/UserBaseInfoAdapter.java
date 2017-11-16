package com.jvtao.phonelive.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jvtao.phonelive.bean.SimpleUserInfo;
import com.jvtao.phonelive.AppContext;

import com.jvtao.phonelive.api.remote.PhoneLiveApi;
import com.jvtao.phonelive.utils.LiveUtils;
import com.jvtao.phonelive.utils.SimpleUtils;
import com.jvtao.phonelive.utils.StringUtils;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonelive.widget.CircleImageView;
import com.jvtao.phonlive.R;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 *关注粉丝列表
 */
public class UserBaseInfoAdapter extends BaseAdapter {
    private List<SimpleUserInfo> users;
    public UserBaseInfoAdapter(List<SimpleUserInfo> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_attention_fans,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (BlackTextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mUSign = (BlackTextView) convertView.findViewById(R.id.tv_item_usign);
            viewHolder.mIsFollow = (ImageView) convertView.findViewById(R.id.iv_item_attention);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SimpleUserInfo u = users.get(position);


        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mUHead,u.avatar,0);

        if (u.id.equals(AppContext.getInstance().getLoginUid())){
            viewHolder.mIsFollow.setVisibility(View.GONE);
        }else {
            viewHolder.mIsFollow.setVisibility(View.VISIBLE);
        }
        viewHolder.mUSex.setImageResource(LiveUtils.getSexRes(u.sex));
        viewHolder.mIsFollow.setImageResource(StringUtils.toInt(u.isattention) == 1 ? R.drawable.me_following:R.drawable.me_follow);
        viewHolder.mULevel.setImageResource(LiveUtils.getLevelRes(u.level));
        viewHolder.mUNice.setText(u.user_nicename);
        viewHolder.mUSign.setText(u.signature);
        viewHolder.mIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(),u.id, AppContext.getInstance().getToken(),new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                    }

                    @Override
                    public void onResponse(String response,int id) {
                        if (StringUtils.toInt(u.isattention) == 1) {//1 已经关注 0未关注
                            u.isattention = "0";
                            ((ImageView)v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_follow);
                        } else {
                            u.isattention = "1";
                            ((ImageView)v.findViewById(R.id.iv_item_attention)).setImageResource(R.drawable.me_following);
                        }
                    }
                });
            }
        });
        return convertView;
    }
    private class ViewHolder{
        public CircleImageView mUHead;
        public ImageView mUSex,mULevel,mIsFollow;
        public BlackTextView mUNice,mUSign;
    }
}
