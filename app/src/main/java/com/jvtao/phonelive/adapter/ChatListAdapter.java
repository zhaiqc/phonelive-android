package com.jvtao.phonelive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jvtao.phonelive.AppContext;


import com.jvtao.phonelive.bean.ChatBean;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonlive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间聊天
 */
public class ChatListAdapter extends BaseAdapter {
    private List<ChatBean> mChats = new ArrayList<>();


    public ChatListAdapter(Context mContext) {

    }

    public void setChats(List<ChatBean> chats) {
        this.mChats = chats;
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_live_chat, null);
            viewHolder = new ViewHolder();
            viewHolder.mChat1 = (BlackTextView) convertView.findViewById(R.id.tv_chat_1);
            viewHolder.mChat2 = (BlackTextView) convertView.findViewById(R.id.tv_chat_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatBean c = mChats.get(position);

        viewHolder.mChat1.setText(c.getUserNick());
        viewHolder.mChat2.setText(c.getSendChatMsg());
        return convertView;
    }

    protected class ViewHolder {
        BlackTextView mChat1, mChat2;
    }

}
