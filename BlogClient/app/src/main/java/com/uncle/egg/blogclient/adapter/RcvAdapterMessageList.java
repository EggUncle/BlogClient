package com.uncle.egg.blogclient.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.uncle.egg.blogclient.MyApplication;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.activity.ChatActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by egguncle on 17-2-9.
 * 用于消息列表界面的适配器
 */

public class RcvAdapterMessageList extends RecyclerView.Adapter<RcvAdapterMessageList.ViewHolder> {

    //消息列表  IMMessage为网易云信自带的消息类
    private List<IMMessage> mImMessageList;

    private final static String TAG="RcvAdapterMessageList";

    public RcvAdapterMessageList(List<IMMessage> imMessageList){
        mImMessageList=imMessageList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvUserName.setText(mImMessageList.get(position).getFromAccount());
        holder.tvUserMessage.setText(mImMessageList.get(position).getContent());
        holder.ivIcon.setImageResource(R.mipmap.ic_launcher);

        //点击列表项，打开对应用户聊天界面
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( view.getContext(), ChatActivity.class);
                intent.putExtra("name",mImMessageList.get(position).getFromAccount());
                view.getContext().startActivity(intent);
            }
        });
    }

    public void addData(List<IMMessage> listAdd){
        mImMessageList.addAll(listAdd);
        Log.i(TAG, "addData: "+mImMessageList.size());
        notifyItemInserted(mImMessageList.size());
    }

    @Override
    public int getItemCount() {
        return mImMessageList == null ? 0 : mImMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivIcon;
        private TextView tvUserName;
        private TextView tvUserMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            ivIcon = (CircleImageView) itemView.findViewById(R.id.iv_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvUserMessage = (TextView) itemView.findViewById(R.id.tv_user_message);

        }
    }
}
