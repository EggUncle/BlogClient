package com.uncle.egg.blogclient.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.uncle.egg.blogclient.bean.UserEntity;


/**
 * Created by egguncle on 17-1-21.
 */

public class SPUtil {
    private static SPUtil instance;

    private final static String SP_NAME = "user_info";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private final static String TAG = "SPUtil";

    private SPUtil() {
    }

    //获取实例
    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                instance = new SPUtil();
                mSharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
            }
        }
        return instance;
    }

    //保存用户信息
    public static void saveUserInfo(UserEntity userInfo) {

        Log.i(TAG, "saveUserInfo: " + userInfo.getUsername());
        Log.i(TAG, "saveUserInfo: " + userInfo.getToken());
        Log.i(TAG, "saveUserInfo: " + userInfo.getNickname());
        Log.i(TAG, "saveUserInfo: " + userInfo.getBgPath());
        Log.i(TAG, "saveUserInfo: " + userInfo.getIconPath());

        mEditor.putString("userName", userInfo.getUsername());
        mEditor.putString("userId", userInfo.getUserId() + "");
        mEditor.putString("nickName", userInfo.getNickname());
        mEditor.putString("description", userInfo.getDescription());
        mEditor.putString("iconPath", userInfo.getIconPath());
        mEditor.putString("bgPath", userInfo.getBgPath());
        mEditor.putString("token", userInfo.getToken());

        Log.i(TAG, "saveUserInfo: " + userInfo.getUsername() + "  " + userInfo.getUserId());

        mEditor.commit();
    }


    //获取用户ID
    public static String getUserId() {
        return mSharedPreferences.getString("userId", "0");
    }

    //获取用户密码
    public static String getUserPassWd() {
        return mSharedPreferences.getString("userPassWd", "");
    }

    //获取用户头像路径
    public static String getIconPath() {
        return mSharedPreferences.getString("iconPath", "");
    }


    //获取用户背景图片路径
    public static String getBgPath() {
        return mSharedPreferences.getString("bgPath", "");
    }

    //获取用户昵称
    public static String getNickName() {
        return mSharedPreferences.getString("nickName", "");
    }

    //获取用户描述
    public static String getDescription() {
        return mSharedPreferences.getString("description", "");
    }

    //获取用户名
    public String getUserName() {
        return mSharedPreferences.getString("userName", "");
    }

    //保存用户名
    public static void setUserName(String userName) {
        mEditor.putString("userName", userName);
        mEditor.commit();
    }

    //获取用户token
    public String getToken() {
        return mSharedPreferences.getString("token", "");
    }
    //保存用户token
    public void setToken(String token){
        mEditor.putString("token", token);
        mEditor.commit();
    }
}
