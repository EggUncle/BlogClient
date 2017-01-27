package com.uncle.egg.blogclient.util;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.uncle.egg.blogclient.MyApplication;
import com.uncle.egg.blogclient.activity.HomeActivity;
import com.uncle.egg.blogclient.activity.LoginActivity;
import com.uncle.egg.blogclient.bean.BlogJson;
import com.uncle.egg.blogclient.bean.LoginJson;
import com.uncle.egg.blogclient.bean.Results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egguncle on 17-1-17.
 */

public class InternetUtil {

    private final static String TAG = "InternetUtil";

    public final static String URL_BASE = "http://192.168.1.106:8080";

    //获取单条博客的URL  GET   ex:http://localhost:8080/json/blog/one/20
    private final static String URL_ONE_BLOG = URL_BASE + "/api/blog/one/";
    //获取比该ID更大的博客的URL （20条）GET     ex:http://localhost:8080/json/blog/max/20
    private final static String URL_MAX_BLOG = URL_BASE + "/api/blog/max/";
    //获取比该ID更小的博客的URL  （20条）GET     ex:http://localhost:8080/json/blog/min/20
    private final static String URL_MIN_BLOG = URL_BASE + "/api/blog/min/";


    //登录用的URL  POST  参数 userName passwd
    private final static String URL_LOGIN = URL_BASE + "/api/client_login";
    //发布博客用的URL POST 参数 userId title content
    private final static String URL_SUMBIT_BLOG = URL_BASE + "/api/submit_blog";

    //图片请求时使用的参数
    //请求头像图片
    public final static int ICON = 1;
    //请求背景图片
    public final static int PATH = 2;

    //请求种类 用于在广播接收器中判断广播的内容
    //博客信息的广播
    public final static int BLOG = 1;
    //图片信息的广播
    public final static int IMAGE = 2;

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

                Intent intent = new Intent(HomeActivity.HOME_BROADCAST);
                intent.putExtra("type", BLOG);
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

                //发送广播给HomeActivity，更新adapter
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
                Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getUsername());
                Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getBgPath());

                Log.i(TAG, "onResponse: " + loginJson.isError());

                //拼接出图片的地址
                String bgpath = URL_BASE + loginJson.getUserEntity().getBgPath();
                String iconPath = URL_BASE + loginJson.getUserEntity().getIconPath();
                Log.i(TAG, "onResponse: " + bgpath);
                //将拼接出的图片地址设置给user
                loginJson.getUserEntity().setBgPath(bgpath);
                loginJson.getUserEntity().setIconPath(iconPath);

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
                CipherUtil cipherUtil = new CipherUtil();
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
                Log.i(TAG, "onErrorResponse: " + error);
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


    /**
     * 获取图片的类，暂时用来获取侧划菜单的icon和bg
     *
     * @param type 想要获取的图片类型 icon 或 bg
     *             <p>
     *             <p>
     *             注意：在构建ImageRequest实例时，需要传递七个参数（六个参数的重载方法已过时，少一个ScaleType参数）
     *             这七个参数类型分别为：
     *             <p>
     *             String ： 要获取的图片地址
     *             Response.Listener<Bitmap> ： 获取图片成功的回调
     *             int： maxWidth，获取图片的最大宽度，会自动进行压缩或拉伸，设置为0，即获取原图
     *             int ：maxHeight，同上
     *             ScaleType ：显示的类型，居中，平铺等
     *             Config：图片类型，如：Bitmap.Config.RGB_565
     *             Response.ErrorListener： 获取图片失败的回调
     */
//    public void getImage(final int type, String imgUrl) {
//        Log.i(TAG, "getImage: "+imgUrl);
//        ImageRequest imgRequest = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap response) {
//                Log.i(TAG, "onResponse: image");
//                Intent intent = new Intent(HomeActivity.HOME_BROADCAST);
//                intent.putExtra("type", IMAGE);
//                intent.putExtra("img_type",type);
//                intent.putExtra("image", response);
//                localBroadcastManager.sendBroadcast(intent);
//            }
//        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        MyApplication.getHttpQueues().add(imgRequest);
//    }

}
