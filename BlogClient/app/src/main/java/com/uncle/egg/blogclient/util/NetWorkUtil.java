package com.uncle.egg.blogclient.util;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.uncle.egg.blogclient.MyApplication;
import com.uncle.egg.blogclient.activity.HomeActivity;
import com.uncle.egg.blogclient.activity.LoginActivity;
import com.uncle.egg.blogclient.activity.RegisteredActivity;
import com.uncle.egg.blogclient.bean.BlogJson;
import com.uncle.egg.blogclient.bean.LoginJson;
import com.uncle.egg.blogclient.bean.Results;
import com.uncle.egg.blogclient.bean.UserEntity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by egguncle on 17-1-17.
 */

public class NetWorkUtil {

    private final static String TAG = "NetWorkUtil";

    public final static String URL_BASE = "http://192.168.1.106:8080/";

    //获取单条博客的URL  GET   ex:http://localhost:8080/json/blog/one/20
    private final static String URL_ONE_BLOG = URL_BASE + "api/blog/one/";
    //获取比该ID更大的博客的URL （20条）GET     ex:http://localhost:8080/json/blog/max/20
    private final static String URL_MAX_BLOG = URL_BASE + "api/blog/max/";
    //获取比该ID更小的博客的URL  （20条）GET     ex:http://localhost:8080/json/blog/min/20
    private final static String URL_MIN_BLOG = URL_BASE + "api/blog/min/";


    //登录用的URL  POST  参数 userName passwd
    private final static String URL_LOGIN = URL_BASE + "api/user/login";
    //发布博客用的URL POST 参数 userId title content imageFile
    private final static String URL_SUMBIT_BLOG = URL_BASE + "api/submit_blog";
    //删除博客用的URL POST 参数 blogId username userpasswd
    private final static String URL_DELETE_BLOG = URL_BASE + "api/blog/delete";

    //注册用户（云信 测试中
    private final static String URL_REGISTERED = "api/user/registered";

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

    private int maxId;
    private int minId;

    private LocalBroadcastManager mLocalBroadcastManager;
    //用来包装广播
    private Intent mIntent;


    public NetWorkUtil(LocalBroadcastManager localBroadcastManager, Intent intent) {
        mLocalBroadcastManager = localBroadcastManager;
        mIntent = intent;
    }

    public NetWorkUtil() {
    }


    /**
     * 构造请求需要的URL
     * /api/blog/{type}/{userId}/{blogId}
     *
     * @param type   类型：获取更大/更小/单条 博客
     * @param blogId 博客id
     * @param userId 用户id
     */
    public String makeUrl(int type, int blogId, int userId) {
        String blogUrl = "";

        switch (type) {
            case GET_ONE:
                blogUrl = URL_ONE_BLOG + blogId;
                Log.i(TAG, "getBlog: get a blog");
                break;
            case GET_MORE_MAX:
                if (userId == 0) {
                    blogUrl = URL_MAX_BLOG + blogId;
                } else {
                    blogUrl = URL_MAX_BLOG + userId + "/" + blogId;
                }

                Log.i(TAG, "getBlog: get more max blog");
                break;
            case GET_MORE_MIN:
                if (userId == 0) {
                    blogUrl = URL_MIN_BLOG + blogId;
                } else {
                    blogUrl = URL_MIN_BLOG + userId + "/" + blogId;
                }
                Log.i(TAG, "getBlog: get more min blog");
                break;
            default:
                blogUrl = URL_ONE_BLOG + blogId;
                break;
        }

        return blogUrl;
    }

    public String makeUrl(int type, int blogId) {
        return makeUrl(type, blogId, 0);
    }

    /**
     * 获取博客数据 默认使用URL_MAX_BLOG ID为0
     *
     * @param listBlog
     * @param blogUrl  请求的url
     */
    public void getBlog(final List<Results> listBlog, String blogUrl) {

        Log.i(TAG, "getBlog: " + blogUrl);
        StringRequest requestBlog = new StringRequest(Request.Method.GET, blogUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析数据
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                BlogJson blogJson = gson.fromJson(response, BlogJson.class);
                List<Results> listResults = blogJson.getResults();

                Intent intent = mIntent;
                intent.putExtra("type", BLOG);
                //是否请求到了新的数据
                //若结果为空，直接返回
                if (listResults == null || listResults.size() == 0) {
                    intent.putExtra("isNull", true);
                    mLocalBroadcastManager.sendBroadcast(intent);
                    return;
                } else {
                    intent.putExtra("isNull", false);
                }

                //反转获取到的list数据，使其更符合阅读习惯（上面是新的下面的旧的）
                //上述步骤转移至服务器中运行，但是仍然要注意
                //  Collections.reverse(blogJson.getResults());
                //    listBlog.addAll(blogJson.getResults());
                // List<Results> list = blogJson.getResults();

                //获取请求到的结果中第一条博客数据的ID，以此判断请求是去获取了更新的博客还是更旧的博客
                int id = listResults.get(0).getBlogId();

                if (listBlog.size() != 0) {
                    //获取bloglist当前最大和最小ID，已备后续的使用
                    maxId = listBlog.get(0).getBlogId();
                    minId = listBlog.get(listResults.size() - 1).getBlogId();

                } else {
                    maxId = 0;
                }

                if (id > maxId) {
                    listBlog.addAll(0, listResults);
                }
                if (id < minId) {
                    listBlog.addAll(listResults);
                }

                //再次获取bloglist当前最大和最小ID，已备后续的使用
                maxId = listBlog.get(0).getBlogId();
                minId = listBlog.get(listBlog.size() - 1).getBlogId();

                Log.i(TAG, "onResponse: maxid is " + maxId);
                Log.i(TAG, "onResponse: minid is " + minId);

                //当前list中的blog的最大和最小id
                intent.putExtra("maxId", maxId);
                intent.putExtra("minId", minId);


                //发送广播给HomeActivity，更新adapter
                mLocalBroadcastManager.sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getQueue().add(requestBlog);
    }


//    public void getBlog(final List<Results> listBlog, final int type, int blogId) {
//        getBlog(listBlog, type, blogId, 0);
//    }

    /**
     * 当getblog方法没有指定blogID时，将ID设置为0
     *
     * @param listBlog
     * @param type
     */
    //public void getBlog(List<Results> listBlog, int type) {
    //       getBlog(listBlog, type, 0);
    //   }

//    /**
//     * 登录使用的方法
//     *
//     * @param userName
//     * @param passwd
//     */
//    public void login(final String userName, final String passwd) {
//        StringRequest requestLogin = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //解析数据
//                Log.i(TAG, "onResponse: " + response);
//                Gson gson = new Gson();
//                LoginJson loginJson = gson.fromJson(response, LoginJson.class);
//                Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getUsername());
//                Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getBgPath());
//
//                Log.i(TAG, "onResponse: " + loginJson.isError());
//
//                //拼接出图片的地址
//                String bgpath = URL_BASE + loginJson.getUserEntity().getBgPath();
//                String iconPath = URL_BASE + loginJson.getUserEntity().getIconPath();
//                Log.i(TAG, "onResponse: " + bgpath);
//                //将拼接出的图片地址设置给user
//                loginJson.getUserEntity().setBgPath(bgpath);
//                loginJson.getUserEntity().setIconPath(iconPath);
//                //将加密过的密码设置给user
//                //对密码进行MD5加密
////                CipherUtil cipherUtil = new CipherUtil();
////                String passwdByMd5 = cipherUtil.generatePassword(passwd);
////                loginJson.getUserEntity().setUserPassWd(passwdByMd5);
//
//                Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);
//
//                //将loginjson对象序列化存入bundle
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("userInfo", loginJson);
//
//                intent.putExtras(bundle);
//                //发送广播给loginactivity的接收器
//                localBroadcastManager.sendBroadcast(intent);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(TAG, "onErrorResponse: " + error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> param = new HashMap<>();
//                param.put("userName", userName);
//                //对密码进行MD5加密
//                CipherUtil cipherUtil = new CipherUtil();
//                String passwdByMd5 = cipherUtil.generatePassword(passwd);
//                param.put("passwd", passwdByMd5);
//
//                return param;
//            }
//        };
//        MyApplication.getQueue().add(requestLogin);
//    }


    /**
     * 注册帐号
     *
     * @param userName
     * @param nickName
     * @param passwd
     */
    public void registered(final String userName, final String nickName, final String passwd) {
        final String url = URL_BASE + URL_REGISTERED;

        StringRequest registeredRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                LoginJson loginJson = gson.fromJson(response, LoginJson.class);
                if (!loginJson.isError()) {
                    Log.i(TAG, "onResponse: success");
                    //若没有错误
                    UserEntity userEntity = loginJson.getUserEntity();

                    //保存用户信息
                    SPUtil.getInstance(MyApplication.getMyContext()).saveUserInfo(userEntity);

//                    //进行一次登录过程，来通过登录获取一些用户相关信信息
//                    //对密码进行MD5加密
//                    CipherUtil cipherUtil = new CipherUtil();
//                    String passwdByMd5 = cipherUtil.generatePassword(passwd);
//                    login(userName,passwdByMd5);

                    String nickName = userEntity.getNickname();
                    String userName = userEntity.getUsername();
                    String token = userEntity.getToken();

                    //登录网易云信
                    //   doLoginWithIM(userName, token);

                    Log.i(TAG, "onResponse: " + nickName);
                    Log.i(TAG, "onResponse: " + userName);
                    Log.i(TAG, "onResponse: " + token);

                    //给RegisteredAcitvity发送广播，通知其关闭
                    Intent intent = new Intent(RegisteredActivity.REGISTERED_BROADCAST);
                    intent.putExtra("success", true);
                    mLocalBroadcastManager.sendBroadcast(intent);
                } else {
                    Log.i(TAG, "onResponse: failed");
                    //给RegisteredAcitvity发送广播，通知注册过程出现错误
                    Intent intent = new Intent(RegisteredActivity.REGISTERED_BROADCAST);
                    intent.putExtra("success", false);
                    mLocalBroadcastManager.sendBroadcast(intent);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userName", userName);
                param.put("nickName", nickName);
                //对密码进行MD5加密
                CipherUtil cipherUtil = new CipherUtil();
                String passwdByMd5 = cipherUtil.generatePassword(passwd);
                param.put("passwd", passwdByMd5);

                return param;
            }
        };
        MyApplication.getQueue().add(registeredRequest);
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
                if (!loginJson.isError()) {
                    UserEntity user = loginJson.getUserEntity();

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

                    //进行网易云信的登录
                    doLoginWithIM(user.getUsername(), user.getToken());

                    //在sharePreferences中保存登录状态信息
                    SPUtil.spLogin();

                    Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);
                    //将loginjson对象序列化存入bundle
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", loginJson);
                    intent.putExtras(bundle);
                    //发送广播给loginactivity的接收器
                    mLocalBroadcastManager.sendBroadcast(intent);
                } else {
                    // Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);
                    //  intent.putExtra("success", false);
                    //发送广播给loginactivity的接收器
                    //   localBroadcastManager.sendBroadcast(intent);
                }
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
        MyApplication.getQueue().add(requestLogin);
    }


    /**
     * 登录的到网易云信的方法
     *
     * @param userName
     * @param token
     */
    public static void doLoginWithIM(String userName, String token) {
        Log.i(TAG, "doLoginWithIM: ");
        LoginInfo info = new LoginInfo(userName, token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        Log.i(TAG, "onSuccess: is success");
                        String userName = loginInfo.getAccount();
                        String token = loginInfo.getToken();
                        //将登录信息保存入sp中
                        SPUtil spUtil = SPUtil.getInstance(MyApplication.getMyContext());
                        spUtil.setUserName(userName);
                        spUtil.setToken(token);


//                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i(TAG, "onFailed: failed ,error :" + i);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.i(TAG, "onException: failed ,error!!! ");
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }


    /**
     * 提交博客的方法
     *
     * @param userName
     * @param token
     * @param title
     * @param content
     * @param imagePath
     * @param imageType
     */
    public void submitBlog(final String userName, final String token, final String title, final String content, final String imagePath, final String imageType) {
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

                params.put("title", title);
                params.put("content", content);
                params.put("userName", userName);
                params.put("token", token);
                String base64StrOfImg = "";
//
                if (!"".equals(imagePath)) {
                    base64StrOfImg = getImageStr(imagePath);
                }

                //   Log.i(TAG, "getParams: "+base64StrOfImg);
//
                params.put("base64StrOfImg", base64StrOfImg);
                params.put("imgtype", imageType);

                return params;
            }
        };
        MyApplication.getQueue().add(submitRequest);
    }


    /**
     * 图片转化成base64字符串
     *
     * @param imagePath
     * @return
     */
    public String getImageStr(String imagePath) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        //裁剪图片，降低文件大小
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);

        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;//返回Base64编码过的字节数组字符串
    }

//    /**
//     * 获取图片的类，暂时用来获取侧划菜单的icon和bg
//     *
//     * @param type 想要获取的图片类型 icon 或 bg
//     *             <p>
//     *             <p>
//     *             注意：在构建ImageRequest实例时，需要传递七个参数（六个参数的重载方法已过时，少一个ScaleType参数）
//     *             这七个参数类型分别为：
//     *             <p>
//     *             String ： 要获取的图片地址
//     *             Response.Listener<Bitmap> ： 获取图片成功的回调
//     *             int： maxWidth，获取图片的最大宽度，会自动进行压缩或拉伸，设置为0，即获取原图
//     *             int ：maxHeight，同上
//     *             ScaleType ：显示的类型，居中，平铺等
//     *             Config：图片类型，如：Bitmap.Config.RGB_565
//     *             Response.ErrorListener： 获取图片失败的回调
//     */
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
//        MyApplication.getQueue().add(imgRequest);
//    }

    /**
     * 删除博客的方法，需要在登录状态下使用，需要验证用户名和密码
     *
     * @param blogId   需要删除的博客ID
     * @param userName 当前用户的用户名
     * @param token
     */
    public void deleteBlog(final int blogId, final String userName, final String token) {
        StringRequest deleteRequest = new StringRequest(Request.Method.POST, URL_DELETE_BLOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: deleteBlog " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("blogId", blogId + "");
                params.put("userName", userName);
                params.put("token", token);
                return params;
            }
        };
        MyApplication.getQueue().add(deleteRequest);
    }

}
