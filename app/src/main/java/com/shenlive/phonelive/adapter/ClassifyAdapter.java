package com.shenlive.phonelive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shenlive.phonlive.R;

import java.util.List;

/**
 * Created by zqc on 2018/1/17.
 */

public class ClassifyAdapter extends BaseAdapter {
    Context context;
    List<String> list;

    public ClassifyAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.item_dialog_list, parent, false);

        }
        TextView textView = (TextView) convertView.findViewById(R.id.tv_classify);
        textView.setText(list.get(position));
        return convertView;
    }
}
