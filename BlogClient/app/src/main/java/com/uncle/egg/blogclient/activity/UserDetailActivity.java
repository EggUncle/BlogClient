package com.uncle.egg.blogclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.adapter.RcvAdapterHomePage;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.bean.UserEntity;
import com.uncle.egg.blogclient.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class UserDetailActivity extends SwipeBackActivity {


    private AppBarLayout appBar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView ivTitleBg;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView rcvUser;
    private RcvAdapterHomePage rcvAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;

    //广播相关
    private UserBlogJsonReceiver blogJsonReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    public final static String USER_BROADCAST = "com.uncle.egg.USER_DETAIL_BOROADCAST";

    private List<Results> blogList;

    private UserEntity userEntity;
    private NetWorkUtil netUtil;


    private int minId = 0;

    private final static String TAG = "UserDetailActivity";

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
        rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
        rcvUser.setItemAnimator(new DefaultItemAnimator());
        rcvUser.setHasFixedSize(true);
        rcvUser.setNestedScrollingEnabled(false);
    }

    private void initAction() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.startActivity(UserDetailActivity.this, userEntity);
            }
        });

        //rcv滚动监听，在向下浏览信息的时候及时发送请求获取新的数据
        rcvUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于5个item时，自动加载下一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 5 >= linearLayoutManager.getItemCount()) {
                    if (blogList != null && blogList.size() != 0) {
                        String url="";
                        url = netUtil.makeUrl(NetWorkUtil.GET_MORE_MIN, minId,userEntity.getUserId());

                        //   String url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MIN, minId);
                        netUtil.getBlog(blogList, url);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initData() {
        blogList=new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rcvUser.setLayoutManager(linearLayoutManager);
        rcvAdapter=new RcvAdapterHomePage(blogList,this);
        rcvUser.setAdapter(rcvAdapter);


        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(USER_BROADCAST);
        blogJsonReceiver = new UserBlogJsonReceiver();
        localBroadcastManager.registerReceiver(blogJsonReceiver, intentFilter);

        //给界面设置数据
        //网络请求工具类
        netUtil = new NetWorkUtil(localBroadcastManager,new Intent(USER_BROADCAST));
        String url= netUtil.makeUrl(NetWorkUtil.GET_MORE_MAX,0,userEntity.getUserId());
        netUtil.getBlog(blogList,url);
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

     //   spUtil = SPUtil.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(blogJsonReceiver);
    }

    private class UserBlogJsonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //先获取intent中的类型参数
            int type = intent.getIntExtra("type", 0);
            Log.i(TAG, "onReceive: ----------------------------------------------------------");

            //若收到的是博客相关的广播
            if (type == NetWorkUtil.BLOG) {
                //若新请求到的list为空，说明没有数据了
                boolean isNull = intent.getBooleanExtra("isNull", true);
                if (isNull) {
                    Toast.makeText(context, "没有数据了", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mMinId = intent.getIntExtra("minId", minId);


                Log.i(TAG, "onReceive: MINID IS " + mMinId);

                if (mMinId < minId && minId != 0 || minId == 0) {
                    Log.i(TAG, "onReceive: get smaller id");
                    minId = mMinId;
                }
                rcvAdapter.notifyDataSetChanged();


            }

        }
    }
}
