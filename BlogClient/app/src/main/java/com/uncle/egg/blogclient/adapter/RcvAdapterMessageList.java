package com.uncle.egg.blogclient.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.activity.ChatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by egguncle on 17-2-9.
 * 用于消息列表界面的适配器
 */

public class RcvAdapterMessageList extends RecyclerView.Adapter<RcvAdapterMessageList.ViewHolder> {

    //消息集合  IMMessage为网易云信自带的消息类
    private List<IMMessage> mImMessageList;
    //用户名集合
    private List<String> accountList;
    //用户对应的消息集合
    private Map<String, List<IMMessage>> messageMap;

    private final static String TAG = "RcvAdapterMessageList";

    public RcvAdapterMessageList(List<IMMessage> imMessageList) {
        mImMessageList = imMessageList;
        accountList = new ArrayList<>();
        messageMap = new HashMap<>();

        addData(imMessageList);
    }

    /**
     * 给最近消息界面添加数据
     *
     * @param list
     */
    public void addData(List<IMMessage> list) {
        //若消息列表不为空，则将里面的用户名信息加入
        if (list != null) {
            for (IMMessage m : list) {
                //获取消息列表item的用户名
                String account = m.getFromAccount();
                //根据用户名获取对应用户的消息列表
                List<IMMessage> messageList = messageMap.get(account);
                if (messageList == null) {
                    //如果消息列表为空，则新建列表
                    messageList = new ArrayList<>();
                    //将该条信息加入对应用户列表中
                    messageList.add(m);
                    //将用户名和信息列表的对应信息存入map中
                    accountList.add(account);
                    messageMap.put(account, messageList);
                    for (IMMessage i:messageList){
                        Log.i(TAG, "addData: 添加用户和数据"+i.getContent());
                    }
                } else {
                    messageList.add(m);
                }

            }
        }

        notifyItemRangeChanged(0,accountList.size());
        //notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.tvUserName.setText(mImMessageList.get(position).getFromAccount());
//        holder.tvUserMessage.setText(mImMessageList.get(position).getContent());

        Log.i(TAG, "onBindViewHolder: ");

        //获取对应位置的用户名
        final String account = accountList.get(position);
        //获取该用户名的消息集合
        List<IMMessage> list = messageMap.get(account);
        for (IMMessage m:list){
            Log.i(TAG, "onBindViewHolder: "+m.getContent());
        }

        holder.tvUserName.setText(account);
        holder.tvUserMessage.setText(list.get(list.size()-1).getContent());
        holder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        holder.tvNotify.setText(list.size()+"");
        holder.tvNotify.setText(list.get(position).getTime()+"");

        //点击列表项，打开对应用户聊天界面
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                Bundle bundle=new Bundle();
                List<IMMessage> messages= messageMap.get(account);

                for (IMMessage m:messages
                     ) {
                    Log.i(TAG, "onClick: "+m.getContent());
                }
                bundle.putSerializable("list", (Serializable) messages);
                intent.putExtra("messageBundle", bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

//    public void addData(List<IMMessage> listAdd) {
//        mImMessageList.addAll(listAdd);
//        Log.i(TAG, "addData: " + mImMessageList.size());
//        notifyItemInserted(mImMessageList.size());
//    }

    @Override
    public int getItemCount() {
        return accountList == null ? 0 : accountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivIcon;
        private TextView tvUserName;
        private TextView tvUserMessage;
        private TextView tvMessageDate;

        //未读计数
        private TextView tvNotify;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (CircleImageView) itemView.findViewById(R.id.iv_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvUserMessage = (TextView) itemView.findViewById(R.id.tv_user_message);
            tvMessageDate = (TextView) itemView.findViewById(R.id.tv_message_date);
            tvNotify = (TextView) itemView.findViewById(R.id.tv_notify);

        }
    }
}
