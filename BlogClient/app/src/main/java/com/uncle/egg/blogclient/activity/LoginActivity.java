package com.uncle.egg.blogclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;


import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.LoginJson;
import com.uncle.egg.blogclient.bean.TableUserByUserId;
import com.uncle.egg.blogclient.util.InternetUtil;
import com.uncle.egg.blogclient.util.SPUtil;


/**
 * 登录界面的activity
 */
public class LoginActivity extends BaseAcitvity {

    private ScrollView loginForm;
    private TextInputLayout edUsername;
    private TextInputLayout edPassword;
    private Button btnLogin;

    private LoginReceiver loginReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;

    public final static String LOGIN_BROADCAST = "com.uncle.egg.LOGIN_BROADCAST";
    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initAction();

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    private void initView() {

        loginForm = (ScrollView) findViewById(R.id.login_form);
        edUsername = (TextInputLayout) findViewById(R.id.ed_username);
        edPassword = (TextInputLayout) findViewById(R.id.ed_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void initData() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(LOGIN_BROADCAST);
        loginReceiver = new LoginReceiver();
        localBroadcastManager.registerReceiver(loginReceiver, intentFilter);

    }

    private void initAction() {
        final EditText edUserName = edUsername.getEditText();
        final EditText edPassWd = edPassword.getEditText();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternetUtil internetUtil = new InternetUtil(localBroadcastManager);
                internetUtil.login(edUserName.getText().toString(), edPassWd.getText().toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(loginReceiver);
    }

    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收广播中的loginjson对象
            LoginJson loginJson = (LoginJson) intent.getExtras().getSerializable("userInfo");
            boolean error = loginJson.isError();
            if (!error) {
             //   Log.i(TAG, "onReceive: "+userInfo.getUserName());
                Toast.makeText(context, "login success", Toast.LENGTH_SHORT).show();
                TableUserByUserId userInfo=loginJson.getUserEntity();

                SPUtil.getInstance(context);
                SPUtil.saveUserInfo(userInfo);
                finish();
            } else {
                Toast.makeText(context, "login failed", Toast.LENGTH_SHORT).show();
                edUsername.getEditText().setText("");
                edPassword.getEditText().setText("");
            }

        }
    }


}

