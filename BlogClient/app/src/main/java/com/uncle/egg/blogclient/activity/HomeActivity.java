package com.uncle.egg.blogclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.adapter.RcvAdapterHomePage;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.InternetUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private SwipeRefreshLayout rshHome;
    private RecyclerView rcvHome;
    private FloatingActionButton fabHome;

    //header部分
    private LinearLayout headerBg;
    private CircleImageView imgIcon;
    private TextView tvName;
    private TextView tvDescription;
    private ImageView imgBg;

    private View headerView;


    private HomeActivity.BlogJsonReceiver blogJsonReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private InternetUtil internetUtil;

    private List<Results> listBlog;
    private RcvAdapterHomePage rcvAdapterHomePage;

    private SPUtil spUtil;

    public final static String HOME_BROADCAST = "com.uncle.egg.HOME_BOROADCAST";
    private final static String TAG = "HomeActivity";

    private int maxId = 0;
    private int minId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initAction();
        initData();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //headerview 部分 ,需要这样获取一下才能得到headerview
        //或者注释掉xml的app:headerLayout部分，使用navigationView.inflateHeaderView(R.layout.navigation_header);

        headerView = navigationView.getHeaderView(0);
        headerBg = (LinearLayout) headerView.findViewById(R.id.header_bg);
        imgIcon = (CircleImageView) headerView.findViewById(R.id.img_icon);
        tvName = (TextView) headerView.findViewById(R.id.tv_name);
        tvDescription = (TextView) headerView.findViewById(R.id.tv_description);
        imgBg = (ImageView) headerView.findViewById(R.id.img_bg);

        rshHome = (SwipeRefreshLayout) findViewById(R.id.rsh_home);
        rcvHome = (RecyclerView) findViewById(R.id.rcv_home);
        rcvHome.setLayoutManager(new LinearLayoutManager(this));
        rcvHome.setItemAnimator(new DefaultItemAnimator());

    }

    private void initAction() {
        //点击浮动按钮跳转到发布界面
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, EditBlogActivity.class);
                startActivity(intent);
            }
        });

        //下拉刷新
        rshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                internetUtil.getBlog(listBlog, InternetUtil.GET_MORE_MAX, maxId);
            }
        });

        //点击用户头像跳转到登录界面
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initData() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        internetUtil = new InternetUtil(localBroadcastManager);
        intentFilter = new IntentFilter();
        intentFilter.addAction(HOME_BROADCAST);

        blogJsonReceiver = new HomeActivity.BlogJsonReceiver();
        localBroadcastManager.registerReceiver(blogJsonReceiver, intentFilter);

        listBlog = new ArrayList<>();
        rcvAdapterHomePage = new RcvAdapterHomePage(listBlog);
        rcvHome.setAdapter(rcvAdapterHomePage);

        spUtil = SPUtil.getInstance(this);
        //加载头像
        //获取图片地址（网络）
        String iconImgUrl = spUtil.getIconPath();
        if (!"".equals(iconImgUrl)) {
            // internetUtil.getImage(InternetUtil.ICON,imgUrl);
            Glide.with(HomeActivity.this)
                    .load(iconImgUrl)
                    //  .override(100, 100)
                    .fitCenter()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(imgIcon);
        }

        String bgImgUrl = spUtil.getBgPath();
        if (!"".equals(bgImgUrl)) {
            // internetUtil.getImage(InternetUtil.ICON,imgUrl);
            Glide.with(HomeActivity.this)
                    .load(bgImgUrl)
                    //  .override(100, 100)
                    .centerCrop()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(imgBg);
        }


    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_home;
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(blogJsonReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        int id=item.getItemId();
//        if (id == R.id.img_icon) {
//            // Handle the camera action
//            Log.i(TAG, "onContextItemSelected: fdsafdsafdsafdsafdsa");
//        }
//        return true;
//    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Log.i(TAG, "onNavigationItemSelected: fdasfdsafd");
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class BlogJsonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //先获取intent中的类型参数
            int type = intent.getIntExtra("type", 0);

            //若收到的是博客相关的广播
            if (type == InternetUtil.BLOG) {
                maxId = intent.getIntExtra("maxId", maxId);
                minId = intent.getIntExtra("minId", minId);

                Log.i(TAG, "onReceive: listSize" + listBlog.size());
                Log.i(TAG, "onReceive: maxId" + maxId);
                Log.i(TAG, "onReceive: minId" + minId);

                rshHome.setRefreshing(false);
                rcvAdapterHomePage.notifyDataSetChanged();

            }

        }
    }
}
