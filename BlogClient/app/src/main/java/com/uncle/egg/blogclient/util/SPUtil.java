package com.uncle.egg.blogclient.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by egguncle on 17-1-21.
 *
 */

public class SPUtil {
    private static SPUtil instance;

    private final static String SP_NAME="user_info";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;


    private SPUtil(){}

    public  static SPUtil getInstance(Context context){
        if (instance == null) {
            synchronized (SPUtil.class) {
                instance = new SPUtil();
                mSharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
            }
        }
        return instance;
    }

    public static void saveUserInfo(String userName){
        mEditor.putString("userName",userName);
        mEditor.commit();
    }

}
