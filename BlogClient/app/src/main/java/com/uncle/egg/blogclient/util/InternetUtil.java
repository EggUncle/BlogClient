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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egguncle on 17-1-17.
 */

public class InternetUtil {

    private final static String TAG = "InternetUtil";

    private final static String URL_BASE = "http://192.168.1.106:8080/";

    //获取单条博客的URL  GET   ex:http://localhost:8080/json/blog/one/20
    private final static String URL_ONE_BLOG = URL_BASE + "json/blog/one/";
    //获取比该ID更大的博客的URL （20条）GET     ex:http://localhost:8080/json/blog/max/20
    private final static String URL_MAX_BLOG = URL_BASE + "json/blog/max/";
    //获取比该ID更小的博客的URL  （20条）GET     ex:http://localhost:8080/json/blog/min/20
    private final static String URL_MIN_BLOG = URL_BASE + "json/blog/min/";


    //登录用的URL  POST  参数 userName passwd
    private final static String URL_LOGIN = URL_BASE + "json/client_login";
    //发布博客用的URL POST 参数 userId title content
    private final static String URL_SUMBIT_BLOG = URL_BASE + "json/submit_blog";

    public final static int GET_MORE_MAX = 1;
    public final static int GET_MORE_MIN = 2;
    public final static int GET_ONE = 0;

    private static int maxId;
    private static int minId;

    private LocalBroadcastManager localBroadcastManager;


    public InternetUtil(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
    }

    public InternetUtil() {
    }


    /**
     * 获取博客数据 默认使用URL_MAX_BLOG ID为0
     *
     * @param listBlog
     * @param type
     * @param blogId
     */
    public void getBlog(final List<Results> listBlog, int type, int blogId) {
        String URL_BLOG = null;

        switch (type) {
            case GET_ONE:
                URL_BLOG = URL_ONE_BLOG + blogId;
                break;
            case GET_MORE_MAX:
                URL_BLOG = URL_MAX_BLOG + blogId;
                break;
            case GET_MORE_MIN:
                URL_BLOG = URL_MIN_BLOG + blogId;
                break;
            default:
                URL_BLOG = URL_ONE_BLOG + blogId;
                break;
        }


        Log.i(TAG, "getBlog: " + URL_BLOG);
        StringRequest requestBlog = new StringRequest(Request.Method.GET, URL_BLOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析数据
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                BlogJson blogJson = gson.fromJson(response, BlogJson.class);
                List<Results> listResults = blogJson.getResults();

                Intent intent = new Intent(MainActivity.BLOG_BROADCAST);
                //若结果为空，直接返回
                if (listResults == null || listResults.size() == 0) {
                    localBroadcastManager.sendBroadcast(intent);
                    return;
                }

                //反转获取到的list数据，使其更符合阅读习惯（上面是新的下面的旧的）
                //上述步骤转移至服务器中运行，但是仍然要注意
                //  Collections.reverse(blogJson.getResults());
                //    listBlog.addAll(blogJson.getResults());
                listBlog.addAll(0, blogJson.getResults());

                //获取bloglist最大和最小ID，已备后续的使用
                maxId = listResults.get(0).getBlogId();
                minId = listResults.get(listResults.size() - 1).getBlogId();


                intent.putExtra("maxId", maxId);
                intent.putExtra("minId", minId);

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
     * 当getblog方法没有指定blogID时，将ID设置为0
     *
     * @param listBlog
     * @param type
     */
    public void getBlog(List<Results> listBlog, int type) {
        getBlog(listBlog, type, 0);
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
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                LoginJson loginJson = gson.fromJson(response, LoginJson.class);
                Log.i(TAG, "onResponse: " + loginJson.getUserName());
                Log.i(TAG, "onResponse: " + loginJson.getSuccess());
                Log.i(TAG, "onResponse: " + loginJson.isError());

                Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);

                //将loginjson对象序列化存入bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", loginJson);

                intent.putExtras(bundle);
                //发送广播给loginactivity的接收器
                localBroadcastManager.sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userName", userName);
                //对密码进行MD5加密
                CipherUtil cipherUtil=new CipherUtil();
                String passwdByMd5 = cipherUtil.generatePassword(passwd);
                param.put("passwd", passwdByMd5);

                return param;
            }
        };
        MyApplication.getHttpQueues().add(requestLogin);
    }


    /**
     * 发送博客的方法
     *
     * @param userId
     * @param title
     * @param content
     */
    public void submitBlog(final String userId, final String title, final String content) {
        StringRequest submitRequest = new StringRequest(Request.Method.POST, URL_SUMBIT_BLOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId + "");
                params.put("title", title);
                params.put("content", content);

                return params;
            }
        };
        MyApplication.getHttpQueues().add(submitRequest);
    }

}
