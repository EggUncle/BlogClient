package com.uncle.egg.blogclient.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.Results;

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
//                NetWorkUtil internetUtil=new NetWorkUtil();
//                internetUtil.getBlog(testList,NetWorkUtil.GET_ONE,20);
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NetWorkUtil internetUtil=new NetWorkUtil();
//                internetUtil.getBlog(testList,NetWorkUtil.GET_MORE_MAX,20);
//            }
//        });
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NetWorkUtil internetUtil=new NetWorkUtil();
//                internetUtil.getBlog(testList,NetWorkUtil.GET_MORE_MIN,20);
//            }
//        });
    }
}
