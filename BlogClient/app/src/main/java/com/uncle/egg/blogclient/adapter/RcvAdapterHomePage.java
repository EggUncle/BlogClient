package com.uncle.egg.blogclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Blog;
import com.uncle.egg.blogclient.bean.Results;

import java.util.List;

/**
 * Created by egguncle on 17-1-16.
 * <p>
 * 主页中的recyclerview
 */

public class RcvAdapterHomePage extends RecyclerView.Adapter<RcvAdapterHomePage.ViewHolder> {
    private List<Results> listBlog;

    public RcvAdapterHomePage(List<Results> listData) {
        this.listBlog = listData;
    }

    @Override
    public RcvAdapterHomePage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate中最后一个boolean值的意思是是否绑定第二个参数的layout
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(RcvAdapterHomePage.ViewHolder holder, int position) {
        holder.tvAuthor.setText(listBlog.get(position).getTableUserByUserId().getUsername());
        holder.tvTitle.setText(listBlog.get(position).getBlogTitle());
        holder.tvDate.setText(listBlog.get(position).getBlogDate());
    }

    @Override
    public int getItemCount() {
        return listBlog == null ? 0 : listBlog.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthor;
        private TextView tvTitle;
        private TextView tvDate;


        public ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
