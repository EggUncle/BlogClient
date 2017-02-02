package com.uncle.egg.blogclient.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.InternetUtil;
import com.uncle.egg.blogclient.util.SPUtil;

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

    private SPUtil spUtil;

    private InternetUtil internetUtil;

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

        //网络请求工具类
        internetUtil=new InternetUtil();

        spUtil=SPUtil.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.menu_delete:
                //准备请求需要的相关数据
                int blogId=mBlog.getBlogId();
                String userName=spUtil.getUserName();
                String userPassWd=spUtil.getUserPassWd();

                internetUtil.deleteBlog(blogId,userName,userPassWd);

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
