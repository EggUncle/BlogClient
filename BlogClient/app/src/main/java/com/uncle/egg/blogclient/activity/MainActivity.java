package com.uncle.egg.blogclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.adapter.RcvAdapterHomePage;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.InternetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * blog 客户端
 * <p>
 * 主页面
 */
public class MainActivity extends BaseAcitvity {

    //   private FrameLayout activityMain;
    private Toolbar toolbar;
    private SwipeRefreshLayout rshHome;
    private RecyclerView rcvHome;
    private FloatingActionButton fabHome;

    private BlogJsonReceiver blogJsonReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;

    private List<Results> listBlog;
    private RcvAdapterHomePage rcvAdapterHomePage;

    public final static String BLOG_BROADCAST = "com.uncle.egg.BLOG_BOROADCAST";
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
//        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
//        setSupportActionBar(toolbar);

        initView();
        initAction();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    private void initView() {
        //  activityMain = (FrameLayout) findViewById(R.id.activity_main);
        //     toolbarHome = (Toolbar) findViewById(R.id.toolbar_home);
        rshHome = (SwipeRefreshLayout) findViewById(R.id.rsh_home);
        rcvHome = (RecyclerView) findViewById(R.id.rcv_home);
        rcvHome.setLayoutManager(new LinearLayoutManager(this));
        rcvHome.setItemAnimator(new DefaultItemAnimator());

        fabHome = (FloatingActionButton) findViewById(R.id.fab_home);
    }

    private void initAction() {
        rshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                InternetUtil internetUtil = new InternetUtil(localBroadcastManager);
                internetUtil.getBlog(listBlog);
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, EditBlogActivity.class));
            }
        });


    }

    private void initData() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(BLOG_BROADCAST);
        blogJsonReceiver = new BlogJsonReceiver();
        localBroadcastManager.registerReceiver(blogJsonReceiver, intentFilter);

        listBlog = new ArrayList<>();
        rcvAdapterHomePage = new RcvAdapterHomePage(listBlog);
        rcvHome.setAdapter(rcvAdapterHomePage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                //  return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(blogJsonReceiver);
    }

    private class BlogJsonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            BlogJson blogJson = (BlogJson) getIntent().getSerializableExtra("blogJson");
//            if (blogJson!=null){
//            Log.i(TAG, "onReceive: " +blogJson.getResults().size());}
//            else {
//                Log.i(TAG, "onReceive: -------------------------");
//            }
            Log.i(TAG, "onReceive: " + listBlog.size());
            rshHome.setRefreshing(false);
            rcvAdapterHomePage.notifyDataSetChanged();


        }
    }
}
