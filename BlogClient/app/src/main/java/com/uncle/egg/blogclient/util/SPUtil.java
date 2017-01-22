package com.uncle.egg.blogclient.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.uncle.egg.blogclient.bean.LoginJson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static void saveUserInfo(LoginJson userInfo) {

        mEditor.putString("userName", userInfo.getUserName());
        mEditor.putString("userId", userInfo.getUserId() + "");
        Log.i(TAG, "saveUserInfo: " + userInfo.getUserName() + "  " + userInfo.getUserId());

        mEditor.commit();
    }

    public static String getUserId() {
        return mSharedPreferences.getString("userId","0");
    }

}
