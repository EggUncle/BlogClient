package com.uncle.egg.blogclient.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.adapter.RcvAdapterChat;
import com.uncle.egg.blogclient.bean.UserEntity;
import com.uncle.egg.blogclient.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseAcitvity {
    private LinearLayout activityChat;
    private RecyclerView rcvChat;
    private EditText edMessage;
    private Button btnSend;

    private RcvAdapterChat rcvAdapterChat;
    private List<IMMessage> imMessageList;

    private final static String TAG = "ChatActivity";

    //聊天对象的用户名
    private String name;

    //对应聊天的用户
    private UserEntity userEntity;

    /**
     * 用于启动该activity的方法
     *
     * @param context
     * @param user
     */
    public static void startActivity(Context context, UserEntity user) {
        Intent intent = new Intent(context, ChatActivity.class);
        //用bundle包装博客对象
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        //启动activty
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initAction();
    }

    private void initAction() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入的信息
                String content = edMessage.getText().toString();
                // 创建文本消息
                final IMMessage message = MessageBuilder.createTextMessage(
                        name, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                        SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                        content // 文本内容
                );
                // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
                //false表示发送消息，true表示重发
                NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: ");
                        rcvAdapterChat.addData(message);
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i(TAG, "onFailed: error code is " + i);
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });

                //将信息加入textview中
                // tvMessage.append("我:  " + content + "\n");

                //清空输入框
                edMessage.setText("");

            }
        });
    }

    private void initData() {
        imMessageList=new ArrayList<>();
        SPUtil spUtil = SPUtil.getInstance(this);
        String userName = spUtil.getUserName();
        rcvAdapterChat = new RcvAdapterChat(imMessageList, userName);
        rcvChat.setAdapter(rcvAdapterChat);

        //如果是点击博客中的作者头像进入聊天页面
        userEntity = (UserEntity) getIntent().getSerializableExtra("user");
        String content = "";
        //如果是通过消息列表进入的界面
        if (userEntity == null) {
            Bundle bundle = getIntent().getBundleExtra("messageBundle");
            List<IMMessage> messages = (List<IMMessage>) bundle.getSerializable("list");
            rcvAdapterChat.addData(messages);
            name = messages.get(0).getFromAccount();
            //       content = messages.get(0).getContent();
        } else {
            name = userEntity.getUsername();
        }
        Log.i(TAG, "initData: " + name);

        //给标题栏设置信息
        Toolbar toolbar = getToolbar();
        toolbar.setTitle(name);

        //消息处理
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        rcvAdapterChat.addData(messages);
                        //  tvMessage.append(messages.get(0).getContent() + "\n");
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    private void initView() {
        activityChat = (LinearLayout) findViewById(R.id.activity_chat);
        rcvChat = (RecyclerView) findViewById(R.id.rcv_chat);
        rcvChat.setLayoutManager(new LinearLayoutManager(this));
        rcvChat.setItemAnimator(new DefaultItemAnimator());
        edMessage = (EditText) findViewById(R.id.ed_message);
        btnSend = (Button) findViewById(R.id.btn_send);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 进入聊天界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(name, SessionTypeEnum.P2P);
    }
}
