package com.uncle.egg.blogclient.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.NetWorkUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 显示博客详情页面
 */
public class DetailsActivity extends BaseAcitvity {

    private final static String TAG = "DetailsActivity";

    private LinearLayout activityDetails;
    private TextView tvDetailsTitle;
    private TextView tvDetailsContent;
    private TextView tvDetailsUser;
    private CircleImageView ivUserIcon;
    private ImageView ivBlogImg;


    //上一个activity传入的blog对象
    private Results mBlog;

    private SPUtil spUtil;

    private NetWorkUtil internetUtil;

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
        initAction();
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
        ivUserIcon = (CircleImageView) findViewById(R.id.iv_user_icon);
        ivBlogImg = (ImageView) findViewById(R.id.iv_blog_img);
    }

    private void initData() {
        //获取上一个activity传入的blog对象
        mBlog = (Results) getIntent().getExtras().getSerializable("blog");

        //给界面设置数据
        tvDetailsTitle.setText(mBlog.getBlogTitle());
        tvDetailsUser.setText(mBlog.getUserEntity().getUsername());
        tvDetailsContent.setText(mBlog.getBlogContent());
        //加载头像
        //获取图片地址（网络）
        String iconImgUrl = NetWorkUtil.URL_BASE + mBlog.getUserEntity().getIconPath();
        if (!"".equals(iconImgUrl)) {
            Glide.with(this)
                    .load(iconImgUrl)
                    //  .override(100, 100)
                    .error(R.mipmap.ic_launcher)
                    .fitCenter()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(ivUserIcon);
        }
        String bgImgUrl = NetWorkUtil.URL_BASE + mBlog.getImgPath();
        if (!"".equals(bgImgUrl)) {
            Glide.with(this)
                    .load(bgImgUrl)
                    //  .override(100, 100)
                    .fitCenter()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(ivBlogImg);
        }


        //网络请求工具类
        internetUtil = new NetWorkUtil();

        spUtil = SPUtil.getInstance(this);
    }

    private void initAction() {
        //点击用户头像，跳转到对应聊天页面
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取博客作者用户名，传递到下一个界面
                String name = mBlog.getUserEntity().getUsername();
                Intent intent=new Intent(DetailsActivity.this,ChatActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete:
                //准备请求需要的相关数据
                int blogId = mBlog.getBlogId();
                String userName = spUtil.getUserName();
                String userPassWd = spUtil.getUserPassWd();

                internetUtil.deleteBlog(blogId, userName, userPassWd);

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
