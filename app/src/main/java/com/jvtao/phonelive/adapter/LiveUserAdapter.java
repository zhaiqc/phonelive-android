package com.jvtao.phonelive.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jvtao.phonelive.AppContext;
import com.jvtao.phonelive.bean.LiveJson;
import com.jvtao.phonelive.utils.SimpleUtils;
import com.jvtao.phonelive.widget.AvatarView;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonelive.widget.LoadUrlImageView;
import com.jvtao.phonlive.R;


import java.util.List;

//热门主播
public class LiveUserAdapter extends BaseAdapter {
    private List<LiveJson> mUserList;
    private LayoutInflater inflater;

    public LiveUserAdapter(LayoutInflater inflater, List<LiveJson> mUserList) {
        this.mUserList = mUserList;
        this.inflater = inflater;
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
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_hot_user,null);
            viewHolder = new ViewHolder();
            viewHolder.mUserNick = (BlackTextView) convertView.findViewById(R.id.tv_live_nick);
            viewHolder.mUserLocal = (BlackTextView) convertView.findViewById(R.id.tv_live_local);
            viewHolder.mUserNums = (BlackTextView) convertView.findViewById(R.id.tv_live_usernum);
            viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
            viewHolder.mUserPic = (LoadUrlImageView) convertView.findViewById(R.id.iv_live_user_pic);
            viewHolder.mRoomTitle = (BlackTextView) convertView.findViewById(R.id.tv_hot_room_title);
            viewHolder.mIvLabel = (ImageView) convertView.findViewById(R.id.iv_label);
            convertView.setTag(viewHolder);
        }
        LiveJson live = mUserList.get(position);
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mUserNick.setText(live.user_nicename);
        viewHolder.mUserLocal.setText(live.city);
        viewHolder.mUserHead.setAvatarUrl(live.avatar_thumb);
        viewHolder.mUserNums.setText(live.nums);

        if (live.admission != null)
            if (live.admission.equals("") ) {
                viewHolder.mIvLabel.setVisibility(View.GONE);
            } else if (live.admission.equals("收费")) {
                viewHolder.mIvLabel.setVisibility(View.VISIBLE);
            }
        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mUserPic,live.thumb,0);


        if(!TextUtils.isEmpty(live.title)){
            viewHolder.mRoomTitle.setVisibility(View.VISIBLE);
            viewHolder.mRoomTitle.setText(live.title);
        }else{
            viewHolder.mRoomTitle.setVisibility(View.GONE);
            viewHolder.mRoomTitle.setText("");
        }
        return convertView;
    }
    private class ViewHolder{
        public BlackTextView mUserNick,mUserLocal,mUserNums,mRoomTitle;
        public LoadUrlImageView mUserPic;
        public AvatarView mUserHead;
        public ImageView mIvLabel;
    }
}


