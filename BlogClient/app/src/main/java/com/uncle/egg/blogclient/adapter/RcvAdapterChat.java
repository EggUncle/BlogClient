package com.uncle.egg.blogclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.uncle.egg.blogclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egguncle on 17-2-12.
 */

public class RcvAdapterChat extends RecyclerView.Adapter<RcvAdapterChat.ViewHolder> {

    private List<IMMessage> imMessageList;
    private String mAccount;

    /**
     * @param list
     * @param account 当前用户的用户名，用来判断添加的message是用户发的还是用户收到的
     */
    public RcvAdapterChat(List<IMMessage> list,String account){
        imMessageList=list;
        mAccount=account;
    }

    @Override
    public RcvAdapterChat.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(RcvAdapterChat.ViewHolder holder, int position) {
            //判断消息是发出去的还是收到的
            String account=imMessageList.get(position).getFromAccount();
            if (mAccount.equals(account)){
                holder.lineRight.setVisibility(View.VISIBLE);
                String content=imMessageList.get(position).getContent();
                holder.tvRight.setText(content);
            }else{
                holder.lineLeft.setVisibility(View.VISIBLE);
                String content=imMessageList.get(position).getContent();
                holder.tvLeft.setText(content);
            }
    }

    /**
     * 给最近聊天界面添加数据
     *
     * @param list
     */
    public void addData(List<IMMessage> list) {
        //若消息列表不为空，则将里面的用户名信息加入
        if (list != null) {
            for (IMMessage m : list) {
                imMessageList.add(m);
                notifyItemInserted(imMessageList.size());
            }
        }

        //notifyDataSetChanged();
    }

    /**
     * 给最近聊天界面添加数据
     *
     * @param msg
     */
    public void addData(IMMessage msg) {
        //若消息列表不为空，则将里面的用户名信息加入
        if (msg != null) {
                imMessageList.add(msg);
                notifyItemInserted(imMessageList.size());
        }

        //notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imMessageList==null?0:imMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lineLeft;
        private TextView tvLeft;
        private LinearLayout lineRight;
        private TextView tvRight;



        public ViewHolder(View itemView) {
            super(itemView);

            lineLeft = (LinearLayout) itemView.findViewById(R.id.line_left);
            tvLeft = (TextView) itemView.findViewById(R.id.tv_left);
            lineRight = (LinearLayout) itemView.findViewById(R.id.line_right);
            tvRight = (TextView) itemView.findViewById(R.id.tv_right);

        }
    }
}
