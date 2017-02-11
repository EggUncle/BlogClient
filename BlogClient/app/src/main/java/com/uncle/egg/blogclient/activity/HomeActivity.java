package com.uncle.egg.blogclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.Service.MessageService;
import com.uncle.egg.blogclient.adapter.RcvAdapterHomePage;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.NetWorkUtil;
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

    //广播相关
    private HomeActivity.BlogJsonReceiver blogJsonReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;

    //网络请求工具类
    private NetWorkUtil internetUtil;

    private List<Results> listBlog;
    private RcvAdapterHomePage rcvAdapterHomePage;

    //用于下拉刷新
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;

    private SPUtil spUtil;

    public final static String HOME_BROADCAST = "com.uncle.egg.HOME_BOROADCAST";
    private final static String TAG = "HomeActivity";

    private int maxId = 0;
    private int minId = 0;

    //设置浏览数据的类型，浏览公共博客/自己的博客
    private final static int PUBLIC_BLOG = 1;
    private final static int MY_BLOG = 2;

    //浏览数据的方式，默认为浏览公共博客
    private int nowMode = PUBLIC_BLOG;

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
        linearLayoutManager = new LinearLayoutManager(this);
        rcvHome.setLayoutManager(linearLayoutManager);
        rcvHome.setItemAnimator(new DefaultItemAnimator());

        //设置状态栏为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


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
                String url="";
                if (nowMode==PUBLIC_BLOG){
                    url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MAX, maxId);
                }
                if (nowMode==MY_BLOG){
                    int userId=Integer.parseInt(spUtil.getUserId());
                    url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MAX, maxId,userId);
                }


                internetUtil.getBlog(listBlog, url);
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

        //rcv滚动监听，在向下浏览信息的时候及时发送请求获取新的数据
        rcvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于5个item时，自动加载下一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 5 >= linearLayoutManager.getItemCount()) {
                    if (listBlog != null && listBlog.size() != 0) {

                        //根据当前模式来构造URL
                        String url="";
                        if (nowMode==PUBLIC_BLOG){
                            url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MIN, minId);
                        }
                        if (nowMode==MY_BLOG){
                            int userId=Integer.parseInt(spUtil.getUserId());
                            url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MIN, minId,userId);
                        }
                     //   String url = internetUtil.makeUrl(NetWorkUtil.GET_MORE_MIN, minId);
                        internetUtil.getBlog(listBlog, url);
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
        //广播相关
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        internetUtil = new NetWorkUtil(localBroadcastManager);
        intentFilter = new IntentFilter();
        intentFilter.addAction(HOME_BROADCAST);

        blogJsonReceiver = new HomeActivity.BlogJsonReceiver();
        localBroadcastManager.registerReceiver(blogJsonReceiver, intentFilter);

        //rcv相关
        listBlog = new ArrayList<>();
        rcvAdapterHomePage = new RcvAdapterHomePage(listBlog, this);
        rcvHome.setAdapter(rcvAdapterHomePage);

        //这个步骤暂时放到onresume中执行
//        //初始化侧划菜单部分
//        //用户昵称和描述
//        spUtil = SPUtil.getInstance(this);
//        String nickName = spUtil.getNickName();
//        String description = spUtil.getDescription();
//        tvName.setText(nickName);
//        tvDescription.setText(description);
//
//        //加载头像
//        //获取图片地址（网络）
//        String iconImgUrl = spUtil.getIconPath();
//        if (!"".equals(iconImgUrl)) {
//            Glide.with(HomeActivity.this)
//                    .load(iconImgUrl)
//                    .error(R.mipmap.ic_launcher)
//                    //  .override(100, 100)
//                    .fitCenter()
//                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
//                    .into(imgIcon);
//        }
//
//        String bgImgUrl = spUtil.getBgPath();
//        if (!"".equals(bgImgUrl)) {
//            Glide.with(HomeActivity.this)
//                    .load(bgImgUrl)
//                    //  .override(100, 100)
//                    .centerCrop()
//                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
//                    .into(imgBg);
//        }


    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_home;
//    }


    @Override
    protected void onResume() {
        super.onResume();

        //初始化侧划菜单部分 处理注册后回到该页面的情况，后期回优化这个步骤
        //用户昵称和描述
        spUtil = SPUtil.getInstance(this);
        String nickName = spUtil.getNickName();
        String description = spUtil.getDescription();
        tvName.setText(nickName);
        tvDescription.setText(description);

        //加载头像
        //获取图片地址（网络）
        String iconImgUrl = spUtil.getIconPath();
        if (!"".equals(iconImgUrl)) {
            Glide.with(HomeActivity.this)
                    .load(iconImgUrl)
                    .error(R.mipmap.ic_launcher)
                    //  .override(100, 100)
                    .fitCenter()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(imgIcon);
        }

        String bgImgUrl = spUtil.getBgPath();
        if (!"".equals(bgImgUrl)) {
            Glide.with(HomeActivity.this)
                    .load(bgImgUrl)
                    .error(R.drawable.bg)
                    //  .override(100, 100)
                    .centerCrop()
                    // .thumbnail(0.1f) //加载缩略图  为原图的十分之一
                    .into(imgBg);
        }
    }

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

        if (id == R.id.nav_public) {
            //点击公共选项时，若当前模式不是公共，则清除整个列表，重新请求一次获取公共博客
            if (nowMode != PUBLIC_BLOG) {
                //清空博客数据
                clearBlogList();
                //设置当前模式为public blog
                nowMode = PUBLIC_BLOG;
            }
        } else if (id == R.id.nav_my_blog) {
            //先判断用户是否登录
            // SPUtil spUtil = SPUtil.getInstance(HomeActivity.this);
            int userId = Integer.parseInt(spUtil.getUserId());
            if (userId == 0) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();

            } else {
                //点击个人选项时，若当前模式不是个人，则清除整个列表，重新请求一次获取个人博客
                if (nowMode != MY_BLOG) {
                    //清空博客数据
                    clearBlogList();
                    //设置当前模式为myblog
                    nowMode = MY_BLOG;
                }
            }
        } else if (id == R.id.nav_recent) {
            Intent intent=new Intent(this,MessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 清空当前博客数据，用于模式切换时使用
     */
    public void clearBlogList(){
        //清空list的数据，刷新adapter
        listBlog.clear();
        rcvAdapterHomePage.notifyDataSetChanged();
        //清空当前博客maxid minid数据
        maxId=0;
        minId=0;
    }

    private class BlogJsonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //先获取intent中的类型参数
            int type = intent.getIntExtra("type", 0);

            //若收到的是博客相关的广播
            if (type == NetWorkUtil.BLOG) {
                //停止刷新动画
                rshHome.setRefreshing(false);
                //若新请求到的list为空，说明没有数据了
                boolean isNull = intent.getBooleanExtra("isNull", true);
                if (isNull) {
                    Toast.makeText(context, "没有数据了", Toast.LENGTH_SHORT).show();
                    return;
                }

                int mMaxId = intent.getIntExtra("maxId", maxId);
                int mMinId = intent.getIntExtra("minId", minId);

                Log.i(TAG, "onReceive: MAXID IS " + mMaxId);
                Log.i(TAG, "onReceive: MINID IS " + mMinId);

                if (mMaxId > maxId && maxId != 0 || maxId == 0) {
                    Log.i(TAG, "onReceive: get bigger id");
                    maxId = mMaxId;
                }
                if (mMinId < minId && minId != 0 || minId == 0) {
                    Log.i(TAG, "onReceive: get smaller id");
                    minId = mMinId;
                }

                Log.i(TAG, "onReceive: listSize" + listBlog.size());
                Log.i(TAG, "onReceive: maxId" + maxId);
                Log.i(TAG, "onReceive: minId" + minId);

                rcvAdapterHomePage.notifyDataSetChanged();


            }

        }
    }
}
