package com.uncle.egg.blogclient.util;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.uncle.egg.blogclient.MyApplication;
import com.uncle.egg.blogclient.activity.MainActivity;
import com.uncle.egg.blogclient.bean.Blog;
import com.uncle.egg.blogclient.bean.BlogJson;
import com.uncle.egg.blogclient.bean.Results;

import java.io.Serializable;
import java.util.List;

/**
 * Created by egguncle on 17-1-17.
 */

public class InternetUtil {

    private final static String URL="http://192.168.1.106:8080/get_json";
    private final static String TAG="InternetUtil";

    private  LocalBroadcastManager localBroadcastManager;


    public InternetUtil(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
    }


    /**
     * 获取博客数据
     */
    public  void getBlog(final List<Results> listBlog){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " +response);
                Gson gson=new Gson();
                BlogJson blogJson = gson.fromJson(response, BlogJson.class);
            //    Log.i(TAG, "onResponse: "+blogJson.getResults().get(0).getBlogTitle());

                Intent intent=new Intent(MainActivity.BLOG_BROADCAST);
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("blogJson", blogJson);
//                intent.putExtras(bundle);
                listBlog.addAll(blogJson.getResults());
                intent.putExtra("test","hhaahhahaha");
                localBroadcastManager.sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getHttpQueues().add(stringRequest);
    }

}
