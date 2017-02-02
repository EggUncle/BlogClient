package com.uncle.egg.blogclient.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Blog;
import com.uncle.egg.blogclient.bean.BlogJson;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.util.InternetUtil;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseAcitvity {

    private LinearLayout activityTest;
    private Button btn1;
    private Button btn2;
    private Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAction();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    private void initView(){
        activityTest = (LinearLayout) findViewById(R.id.activity_test);
        btn1 = (Button) findViewById(R.id.btn_1);
        btn2 = (Button) findViewById(R.id.btn_2);
        btn3 = (Button) findViewById(R.id.btn_3);
    }

    private void initAction(){

        final List<Results> testList=new ArrayList<>();

//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InternetUtil internetUtil=new InternetUtil();
//                internetUtil.getBlog(testList,InternetUtil.GET_ONE,20);
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InternetUtil internetUtil=new InternetUtil();
//                internetUtil.getBlog(testList,InternetUtil.GET_MORE_MAX,20);
//            }
//        });
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InternetUtil internetUtil=new InternetUtil();
//                internetUtil.getBlog(testList,InternetUtil.GET_MORE_MIN,20);
//            }
//        });
    }
}
