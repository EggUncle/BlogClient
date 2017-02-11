package com.uncle.egg.blogclient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.UserEntity;
import com.uncle.egg.blogclient.util.NetWorkUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class UserDetailActivity extends SwipeBackActivity {


    private AppBarLayout appBar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView ivTitleBg;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private UserEntity userEntity;
    private NetWorkUtil netWorkUtil;
    private SPUtil spUtil;

    private final static String TAG="UserDetailActivity";

    /**
     * 用于启动该activity的方法
     *
     * @param context
     * @param user
     */
    public static void startActivity(Context context, UserEntity user) {
        Intent intent = new Intent(context, UserDetailActivity.class);
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
        setContentView(R.layout.activity_user_details2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //获取上一个activity传入的user对象
        userEntity = (UserEntity) getIntent().getExtras().getSerializable("user");
        //setSupportActionBar(toolbar);
        toolbar.setTitle(userEntity.getNickname());
//        toolbar.setNavigationIcon(R.drawable.icon_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        initView();
        initData();
        initAction();
    }

    private void initView() {
        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ivTitleBg = (ImageView) findViewById(R.id.iv_title_bg);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initAction() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ChatActivity.startActivity(UserDetailActivity.this,userEntity);
            }
        });
    }

    private void initData() {


        //给界面设置数据
//        tvDetailsTitle.setText(mBlog.getBlogTitle());
//        tvDetailsUser.setText(mBlog.getUserEntity().getUsername());
//        tvDetailsContent.setText(mBlog.getBlogContent());
        //加载头像
        //获取图片地址（网络）
        String iconImgUrl = NetWorkUtil.URL_BASE + userEntity.getIconPath();

        if (!"".equals(iconImgUrl)) {
            Glide.with(this)
                    .load(iconImgUrl)
                    //  .override(100, 100)
                    .error(R.mipmap.ic_launcher)
                    .fitCenter()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(ivTitleBg);
        }
       // String bgImgUrl = NetWorkUtil.URL_BASE + mBlog.getImgPath();
//        Log.i(TAG, "initData: "+bgImgUrl);
//        if (!"".equals(bgImgUrl)) {
//            Glide.with(this)
//                    .load(bgImgUrl)
//                    //  .override(100, 100)
//                    .fitCenter()
//                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
//                    .into(ivTitleBg);
//        }


        //网络请求工具类
        netWorkUtil = new NetWorkUtil();

        spUtil = SPUtil.getInstance(this);
    }
}
