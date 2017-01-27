package com.uncle.egg.blogclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.activity.DetailsActivity;
import com.uncle.egg.blogclient.activity.HomeActivity;
import com.uncle.egg.blogclient.activity.MainActivity;
import com.uncle.egg.blogclient.bean.Blog;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.InternetUtil;

import java.util.List;

/**
 * Created by egguncle on 17-1-16.
 * <p>
 * 主页中的recyclerview
 */

public class RcvAdapterHomePage extends RecyclerView.Adapter<RcvAdapterHomePage.ViewHolder> {
    private List<Results> listBlog;
    private Context mContext;

    private final static String BASE_URL="";

    private final static String TAG="RcvAdapterHomePage";

    public RcvAdapterHomePage(List<Results> listData, Context context) {
        this.listBlog = listData;
        this.mContext = context;
    }

    @Override
    public RcvAdapterHomePage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate中最后一个boolean值的意思是是否绑定第二个参数的layout
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(final RcvAdapterHomePage.ViewHolder holder, final int position) {
        holder.tvAuthor.setText(listBlog.get(position).getTableUserByUserId().getUsername());
        holder.tvTitle.setText(listBlog.get(position).getBlogTitle());
        holder.tvDate.setText(listBlog.get(position).getBlogDate());


        String urlPath = listBlog.get(position).getImgPath();


        if (urlPath!=null) {
            urlPath=InternetUtil.URL_BASE+urlPath;
            Log.i(TAG, "onBindViewHolder: "+urlPath);
            Glide.with(mContext)
                    .load(urlPath)
                    //  .override(100, 100)
                    .centerCrop()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(holder.imgBlogBg);

        }
        //rcv对应的点击事件，点击后进入详情页面，并且传入对应的博客对象
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用DetailsActivity专用的方法来启动activity
                DetailsActivity.startAction(view.getContext(), listBlog.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listBlog == null ? 0 : listBlog.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgBlogBg;
        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvDate;


        public ViewHolder(View itemView) {
            super(itemView);

            imgBlogBg = (ImageView) itemView.findViewById(R.id.img_blog_bg);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
