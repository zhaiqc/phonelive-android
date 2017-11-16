package com.jvtao.phonelive.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.jvtao.phonelive.bean.UserBean;
import com.jvtao.phonelive.utils.UIHelper;
import com.hyphenate.chat.EMMessage;
import com.jvtao.phonelive.AppContext;

import com.jvtao.phonelive.adapter.MessageAdapter;
import com.jvtao.phonelive.base.BaseFragment;
import com.jvtao.phonelive.bean.PrivateChatUserBean;
import com.jvtao.phonelive.bean.PrivateMessage;
import com.jvtao.phonelive.ui.other.PhoneLivePrivateChat;
import com.jvtao.phonelive.widget.BlackEditText;
import com.jvtao.phonelive.widget.BlackTextView;
import com.jvtao.phonlive.R;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


//个人中心私信发送页面
public class MessageDetailFragment extends BaseFragment {
    @InjectView(R.id.tv_private_chat_title)
    BlackTextView mTitle;
    @InjectView(R.id.et_private_chat_message)
    BlackEditText mMessageInput;
    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;
    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;
    private BroadcastReceiver broadCastReceiver;
    private long lastTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_chat_message,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = getActivity().getIntent().getParcelableExtra("user");
        mTitle.setText(mToUser.user_nicename);

        try{
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mToUser.id);
            //指定会话消息未读数清零
            conversation.markAllMessagesAsRead();
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取历史消息
        mChats = PhoneLivePrivateChat.getUnreadRecord(mUser,mToUser);

        //初始化adapter
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setChatList(mChats);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

        initBroadCast();
    }

    @OnClick({R.id.iv_private_chat_send,R.id.et_private_chat_message,R.id.iv_private_chat_back,R.id.iv_private_chat_user})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发送私信
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

                break;
            case R.id.iv_private_chat_back:
                getActivity().finish();
                break;
            case R.id.iv_private_chat_user:
                UIHelper.showHomePageActivity(getActivity(),mToUser.id);
                break;
        }

    }
    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0){
            Toast.makeText(getActivity(),"操作频繁",Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = System.currentTimeMillis();
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
            return;
        }
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToastAppMsg(getActivity(),"内容为空");
        }
        EMMessage emMessage = PhoneLivePrivateChat.sendChatMessage(mMessageInput.getText().toString(),mToUser);

        //更新列表
        updateChatList(PrivateMessage.crateMessage(emMessage,mUser.avatar));
        mMessageInput.setText("");
    }



    //注册监听私信消息广播
    private void initBroadCast() {
        IntentFilter cmdFilter = new IntentFilter("com.yunbao.phonelive");
        if(broadCastReceiver == null){

            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    final EMMessage emMessage = intent.getParcelableExtra("cmd_value");
                    //判断是否是当前回话的消息
                    if(emMessage.getFrom().trim().equals(String.valueOf(mToUser.id))) {

                        updateChatList(PrivateMessage.crateMessage(emMessage,mToUser.avatar));

                    }
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }


    private void updateChatList(PrivateMessage message){
        //更新聊天列表
        mMessageAdapter.addMessage(message);
        //mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mMessageAdapter.getCount()-1);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadCastReceiver);
        }catch (Exception e){

        }

    }
}
