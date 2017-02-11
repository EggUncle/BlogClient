package com.uncle.egg.blogclient.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.bean.UserEntity;
import com.uncle.egg.blogclient.util.NetWorkUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 显示博客详情页面
 */
public class BlogDetailActivity extends BaseAcitvity {

    private final static String TAG = "BlogDetailActivity";

    private LinearLayout activityDetails;
    private TextView tvDetailsTitle;
    private CircleImageView ivUserIcon;
    private TextView tvDetailsUser;
    private TextView tvDetailsDate;
    private TextView tvDetailsContent;

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
    public static void startActivity(Context context, Results blog) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
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

        Toolbar toolbar=getToolbar();
        toolbar.setTitle(mBlog.getBlogTitle());
     //   toolbar.setNavigationIcon(R.drawable.icon_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_blog_details;
    }


    private void initView() {
        activityDetails = (LinearLayout) findViewById(R.id.activity_details);
        tvDetailsTitle = (TextView) findViewById(R.id.tv_details_title);
        ivUserIcon = (CircleImageView) findViewById(R.id.iv_user_icon);
        tvDetailsUser = (TextView) findViewById(R.id.tv_details_user);
        tvDetailsDate = (TextView) findViewById(R.id.tv_details_date);
        tvDetailsContent = (TextView) findViewById(R.id.tv_details_content);
    }

    private void initData() {
        //获取上一个activity传入的blog对象
        mBlog = (Results) getIntent().getExtras().getSerializable("blog");

        //给界面设置数据
        tvDetailsTitle.setText(mBlog.getBlogTitle());
        tvDetailsUser.setText(mBlog.getUserEntity().getUsername());
        tvDetailsDate.setText(mBlog.getBlogDate());
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


        //网络请求工具类
        internetUtil = new NetWorkUtil();

        spUtil = SPUtil.getInstance(this);
    }

    private void initAction() {
        //点击用户头像，跳转到对应用户资料界面
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取博客作者用户名，传递到下一个界面
                UserEntity user = mBlog.getUserEntity();
//                Intent intent=new Intent(BlogDetailActivity.this,ChatActivity.class);
//                intent.putExtra("user",user);
//                startActivity(intent);
                UserDetailActivity.startActivity(BlogDetailActivity.this,user);
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
                String token = spUtil.getToken();

                internetUtil.deleteBlog(blogId, userName, token);

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
