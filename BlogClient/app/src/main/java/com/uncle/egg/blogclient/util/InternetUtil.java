package com.uncle.egg.blogclient.util;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.uncle.egg.blogclient.MyApplication;
import com.uncle.egg.blogclient.activity.LoginActivity;
import com.uncle.egg.blogclient.activity.MainActivity;
import com.uncle.egg.blogclient.bean.Blog;
import com.uncle.egg.blogclient.bean.BlogJson;
import com.uncle.egg.blogclient.bean.LoginJson;
import com.uncle.egg.blogclient.bean.Results;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egguncle on 17-1-17.
 */

public class InternetUtil {

    private final static String URL_BLOG = "http://192.168.1.106:8080/json/blog";
    private final static String URL_LOGIN = "http://192.168.1.106:8080/json/client_login";
    private final static String TAG = "InternetUtil";

    private LocalBroadcastManager localBroadcastManager;


    public InternetUtil(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
    }

    public InternetUtil() {
    };



    /**
     * 获取博客数据
     */
    public void getBlog(final List<Results> listBlog) {
        StringRequest requestBlog = new StringRequest(Request.Method.GET, URL_BLOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析数据
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                BlogJson blogJson = gson.fromJson(response, BlogJson.class);
                Intent intent = new Intent(MainActivity.BLOG_BROADCAST);
                listBlog.addAll(blogJson.getResults());

                //发送广播给MainActivity，更新adapter
                localBroadcastManager.sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getHttpQueues().add(requestBlog);
    }


    /**
     * 登录使用的方法
     *
     * @param userName
     * @param passwd
     */
    public void login(final String userName, final String passwd) {
        StringRequest requestLogin = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析数据
                Log.i(TAG, "onResponse: "+ response);
                Gson gson=new Gson();
                LoginJson loginJson=gson.fromJson(response,LoginJson.class);
                Log.i(TAG, "onResponse: "+ loginJson.getUserName());
                Log.i(TAG, "onResponse: "+ loginJson.getSuccess());
                Log.i(TAG, "onResponse: "+ loginJson.isError());

                Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);

                //将loginjson对象序列化存入bundle
                Bundle bundle=new Bundle();
                bundle.putSerializable("userInfo",loginJson);

                intent.putExtras(bundle);
                //发送广播给loginactivity的接收器
                localBroadcastManager.sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userName", userName);
                param.put("passwd", passwd);

                return param;
            }
        };
        MyApplication.getHttpQueues().add(requestLogin);
    }

}
