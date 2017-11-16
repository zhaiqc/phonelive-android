package com.jvtao.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jvtao.phonelive.AppConfig;
import com.jvtao.phonelive.AppContext;
import com.jvtao.phonelive.bean.OrderBean;
import com.jvtao.phonelive.utils.LiveUtils;
import com.jvtao.phonelive.utils.SimpleUtils;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonelive.widget.CircleImageView;
import com.jvtao.phonlive.R;


import java.util.ArrayList;

/**
 * 贡献榜
 */
public class OrderAdapter extends BaseAdapter {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    private Context mContext;

    public OrderAdapter(ArrayList<OrderBean> mOrderList, LayoutInflater mInflater, Context mContext) {
        this.mOrderList = mOrderList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {

        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(position == 0){
            convertView = View.inflate(mContext, R.layout.view_order_top1,null);
        }else if(position == 1){
            convertView = View.inflate(mContext,R.layout.view_order_top2,null);
        }else if(position == 2){
            convertView = View.inflate(mContext,R.layout.view_order_top3,null);
        }else{
            convertView = View.inflate(mContext,R.layout.item_order_user,null);
        }

        viewHolder = new ViewHolder();
        viewHolder.mOrderUhead = (CircleImageView) convertView.findViewById(R.id.ci_order_item_u_head);
        viewHolder.mOrderULevel = (ImageView) convertView.findViewById(R.id.tv_order_item_u_level);
        viewHolder.mOrderUSex = (ImageView) convertView.findViewById(R.id.iv_order_item_u_sex);
        viewHolder.mOrderUname = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_name);
        viewHolder.mOrderUGx = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_gx);
        viewHolder.mOrderNo = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_no);
        convertView.setTag(viewHolder);

        OrderBean o = mOrderList.get(position);

        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mOrderUhead,o.getAvatar(),R.drawable.null_blacklist);

        viewHolder.mOrderULevel.setImageResource(LiveUtils.getLevelRes(o.getLevel()));
        viewHolder.mOrderUSex.setImageResource(LiveUtils.getSexRes(o.getSex()));
        viewHolder.mOrderUname.setText(o.getUser_nicename());
        viewHolder.mOrderUGx.setText("贡献:" + o.getTotal() + AppConfig.TICK_NAME);
        if(position > 2){
            viewHolder.mOrderNo.setText("  No." + (position+1));
        }
        return convertView;
    }
    class ViewHolder{
        public CircleImageView mOrderUhead;
        public ImageView mOrderUSex,mOrderULevel;
        public BlackTextView mOrderUname,mOrderUGx,mOrderNo;
    }
}