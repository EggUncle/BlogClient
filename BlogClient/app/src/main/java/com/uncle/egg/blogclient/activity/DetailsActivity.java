package com.uncle.egg.blogclient.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Results;

/**
 * 显示博客详情页面
 */
public class DetailsActivity extends BaseAcitvity {


    private LinearLayout activityDetails;
    private TextView tvDetailsTitle;
    private TextView tvDetailsContent;
    private TextView tvDetailsUser;

    //上一个activity传入的blog对象
    private Results mBlog;

    /**
     * 用于启动该activity的方法
     *
     * @param context
     * @param blog    博客对象
     */
    public static void startAction(Context context, Results blog) {
        Intent intent = new Intent(context, DetailsActivity.class);
        //用bundle包装博客对象
        Bundle bundle = new Bundle();
        bundle.putSerializable("blog", blog);
        intent.putExtras(bundle);
        //启动activty
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_details;
    }


    private void initView() {
        activityDetails = (LinearLayout) findViewById(R.id.activity_details);
        tvDetailsTitle = (TextView) findViewById(R.id.tv_details_title);
        tvDetailsContent = (TextView) findViewById(R.id.tv_details_content);
        tvDetailsUser = (TextView) findViewById(R.id.tv_details_user);
    }

    private void initData() {
        //获取上一个activity传入的blog对象
        mBlog = (Results) getIntent().getExtras().getSerializable("blog");

        //给界面设置数据
        tvDetailsTitle.setText(mBlog.getBlogTitle());
        tvDetailsUser.setText(mBlog.getTableUserByUserId().getUsername());
        tvDetailsContent.setText(mBlog.getBlogContent());
    }
}
