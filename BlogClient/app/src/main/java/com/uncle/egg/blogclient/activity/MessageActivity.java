package com.uncle.egg.blogclient.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.adapter.RcvAdapterMessageList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageActivity extends BaseAcitvity {

    private final static String TAG = "MessageActivity";

    private RecyclerView rcvMessages;
    private LinearLayoutManager linearLayoutManager;
    private RcvAdapterMessageList rcvAdapterMessageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    private void initView() {
        rcvMessages = (RecyclerView) findViewById(R.id.rcv_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        rcvMessages.setLayoutManager(linearLayoutManager);
        rcvMessages.setAdapter(rcvAdapterMessageList);
    }

    private void initData() {
        ArrayList<IMMessage> messagesList = (ArrayList<IMMessage>)
                getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT); // 可以获取消息的发送者，跳转到指定的单聊、群聊界面。

        for (IMMessage m : messagesList) {
            Log.i(TAG, "initData: " + m.getContent());
        }

//       name = messages.get(0).getFromAccount();
//        content = messages.get(0).getContent();
//        tvMessage.append(content + "\n");
        rcvAdapterMessageList = new RcvAdapterMessageList(messagesList);

        //消息处理
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        for (IMMessage m : messages) {
                            Log.i(TAG, "onEvent: " + m.getFromAccount() + "        " + m.getContent());
                        }
                        rcvAdapterMessageList.addData(messages);
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
    }
}
