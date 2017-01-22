package com.uncle.egg.blogclient.bean;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-21.
 * 用于用户登录的json
 */
public class LoginJson implements Serializable{

    //是否发生错误
    private boolean error;

    //用户名
    private String userName;

    //用户ID
    private int userId;


    //是否成功
    private boolean success;


    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
