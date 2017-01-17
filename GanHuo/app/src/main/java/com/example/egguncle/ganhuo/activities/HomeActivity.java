package com.example.egguncle.ganhuo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.egguncle.ganhuo.MyApplication;
import com.example.egguncle.ganhuo.R;
import com.example.egguncle.ganhuo.adapter.HomeRcvAdapter;
import com.example.egguncle.ganhuo.entries.GanHuoInfo;
import com.example.egguncle.ganhuo.services.MyService;
import com.example.egguncle.ganhuo.uitls.NetWorkUtils;
import com.example.egguncle.ganhuo.uitls.URLUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    private final static String MY_TAG = "MY_TAG";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private MyService.MyBinder mBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (MyService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent startIntent = new Intent(this, MyService.class);
        //   startService(startIntent);
        bindService(startIntent, serviceConnection, BIND_AUTO_CREATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private GanHuoInfo mGanHuoInfo;
        private HomeRcvAdapter mHomeRcvAdapter;
        private ContentLoadingProgressBar mProgressBar;
        private SwipeRefreshLayout mSrhHome;
        private RecyclerView mRcvHome;
        private List<GanHuoInfo.ResultsBean> mResultsBeanList;
        private int lastVisibleItem;
        private static int pageNum = 2;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            mProgressBar = (ContentLoadingProgressBar) rootView.findViewById(R.id.progress_bar);
            mSrhHome = (SwipeRefreshLayout) rootView.findViewById(R.id.srh_home);
            mRcvHome = (RecyclerView) rootView.findViewById(R.id.rcv_home);
            final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRcvHome.setLayoutManager(mLinearLayoutManager);
            mRcvHome.setItemAnimator(new DefaultItemAnimator());
            mRcvHome.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            if (mGanHuoInfo == null) {
                mGanHuoInfo = new GanHuoInfo();
            }
            mResultsBeanList = new ArrayList<>();

            mResultsBeanList = mGanHuoInfo.getResults();
            mHomeRcvAdapter = new HomeRcvAdapter(getActivity(), mResultsBeanList);
            mRcvHome.setAdapter(mHomeRcvAdapter);
            //recyclerview滚动监听
            mRcvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                    // 滑动状态停止并且剩余少于两个item时，自动加载下一页
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItem + 5 >= mLinearLayoutManager.getItemCount()) {
                        String type = "";
                        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                            case 1:
                                type = URLUtils.S_ANDROID;
                                break;
                            case 2:
                                type = URLUtils.S_IOS;
                                break;
                            case 3:
                                type = URLUtils.S_HTML;
                                break;
                            case 4:
                                type = URLUtils.S_GIRL;
                                break;
                        }

                        StringRequest request = new StringRequest(Request.Method.GET, URLUtils.getMyUrl(type, pageNum++), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                GanHuoInfo info = new Gson().fromJson(response, GanHuoInfo.class);
                                if (info.getResults()!= null&&mResultsBeanList!=null) {
                                    mResultsBeanList.addAll(info.getResults());
                                    mHomeRcvAdapter.notifyDataSetChanged();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });


                        request.setTag("newGet");
                        MyApplication.getHttpQueues().add(request);


                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //获取加载的最后一个可见视图在适配器的位置。
                    lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                }
            });


            if (mGanHuoInfo != null) {
                mProgressBar.setVisibility(View.GONE);
            }

            mSrhHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSrhHome.setRefreshing(false);
                }
            });

//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        public GanHuoInfo getGanHuoInfo() {
            return mGanHuoInfo;
        }

        public void setGanHuoInfo(GanHuoInfo ganHuoInfo) {
            this.mGanHuoInfo = ganHuoInfo;

            mHomeRcvAdapter.notifyDataSetChanged();
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.v(MY_TAG, (String) getPageTitle(position));
            Log.v(MY_TAG, URLUtils.getMyUrl((String) getPageTitle(position), 1));
            final PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position + 1);
            if (placeholderFragment.getGanHuoInfo() == null) {
                StringRequest request = new StringRequest(Request.Method.GET, URLUtils.getMyUrl((String) getPageTitle(position), 1), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GanHuoInfo info = new Gson().fromJson(response, GanHuoInfo.class);
                        placeholderFragment.setGanHuoInfo(info);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


                request.setTag("newGet");
                MyApplication.getHttpQueues().add(request);

            }


            return placeholderFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Android";
                case 1:
                    return "iOS";
                case 2:
                    return "前端";
                case 3:
                    return "福利";
            }
            return null;
        }
    }
}
