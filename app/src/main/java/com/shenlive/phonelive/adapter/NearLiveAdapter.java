package com.shenlive.phonelive.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.bean.LiveJson;
import com.shenlive.phonelive.utils.SimpleUtils;
import com.shenlive.phonelive.widget.AvatarView;
import com.shenlive.phonelive.widget.BlackTextView;
import com.shenlive.phonelive.widget.LoadUrlImageView;
import com.shenlive.phonlive.R;

import java.util.List;

//热门主播
public class NearLiveAdapter extends BaseAdapter {
    private List<LiveJson> mUserList;
    private LayoutInflater inflater;

    public NearLiveAdapter(LayoutInflater inflater, List<LiveJson> mUserList) {
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
            convertView = inflater.inflate(R.layout.item_near_live,null);
            viewHolder = new ViewHolder();
//            viewHolder.mUserNick = (BlackTextView) convertView.findViewById(R.id.tv_live_nick);
//            viewHolder.mUserLocal = (BlackTextView) convertView.findViewById(R.id.tv_live_local);
//            viewHolder.mUserNums = (BlackTextView) convertView.findViewById(R.id.tv_live_usernum);
//            viewHolder.mUserHead = (AvatarView) convertView.findViewById(R.id.iv_live_user_head);
            viewHolder.mUserPic = (LoadUrlImageView) convertView.findViewById(R.id.iv_live_user_pic);
            viewHolder.mRoomTitle = (BlackTextView) convertView.findViewById(R.id.tv_hot_room_title);
            viewHolder.mIvLabel = (ImageView) convertView.findViewById(R.id.iv_label);
            viewHolder.mdistance = (TextView) convertView.findViewById(R.id.distance);
            convertView.setTag(viewHolder);
        }
        LiveJson live = mUserList.get(position);
        viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.mUserNick.setText(live.user_nicename);
//        viewHolder.mUserNick.setTextSize(13);
//        viewHolder.mUserLocal.setText(live.city);
//        viewHolder.mUserHead.setAvatarUrl(live.avatar_thumb);
//        viewHolder.mUserNums.setText(live.nums);
        viewHolder.mdistance.setText(live.distance);

        if (live.admission != null)
            if (live.admission.equals("") ) {
                viewHolder.mIvLabel.setVisibility(View.GONE);
            } else if (live.admission.equals("收费")) {
                viewHolder.mIvLabel.setVisibility(View.VISIBLE);
            }
        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mUserPic,live.avatar_thumb,0);


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
        public TextView  mdistance;
    }
}


