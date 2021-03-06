package com.ylive.phonelive.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.R;
import com.ylive.phonelive.adapter.MessageAdapter;
import com.ylive.phonelive.bean.PrivateChatUserBean;
import com.ylive.phonelive.bean.PrivateMessage;
import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.ui.other.PhoneLivePrivateChat;
import com.ylive.phonelive.widget.BlackEditText;
import com.ylive.phonelive.widget.BlackTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 *直播间私信发送页面
 */
public class MessageDetailDialogFragment extends DialogFragment{
    @InjectView(R.id.tv_private_chat_title)
    BlackTextView mTitle;

    @InjectView(R.id.et_private_chat_message)
    BlackEditText mMessageInput;

    private com.ylive.phonelive.interf.DialogInterface mDialogInterface;

    @InjectView(R.id.lv_message)
    ListView mChatMessageListView;


    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;

    private BroadcastReceiver broadCastReceiver;

    private long lastTime = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),R.style.BottomViewTheme_Transparent);
        dialog.setContentView(R.layout.dialog_fragment_private_chat_message);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        ButterKnife.inject(this,dialog);
        initData();
        initView(dialog);

        return dialog;
    }

    @OnClick({R.id.iv_private_chat_send,R.id.et_private_chat_message,R.id.iv_close})

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

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
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = (PrivateChatUserBean) getArguments().getParcelable("user");

        try{
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mToUser.id);
            //指定会话消息未读数清零
            conversation.markAllMessagesAsRead();
        }catch (Exception e){
            e.printStackTrace();
        }
        mTitle.setText(mToUser.user_nicename);

        //获取历史消息
        mChats = PhoneLivePrivateChat.getUnreadRecord(mUser,mToUser);

        //初始化adapter
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setChatList(mChats);
        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

        initBroadCast();

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


    public void initView(Dialog view) {

    }
    private void updateChatList(PrivateMessage message){
        //更新聊天列表
        mMessageAdapter.addMessage(message);
        mChatMessageListView.setAdapter(mMessageAdapter);
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


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mDialogInterface != null){
            mDialogInterface.cancelDialog(null,null);
        }

    }

    public void setDialogInterface(com.ylive.phonelive.interf.DialogInterface dialogInterface){
        mDialogInterface = dialogInterface;
    }
}
