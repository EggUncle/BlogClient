package com.uncle.egg.blogclient.bean;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-21.
 * 用于用户登录的json
 */
public class LoginJson implements Serializable{

    //是否发生错误
    private boolean error;

    //结果
    private TableUserByUserId userEntity;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public TableUserByUserId getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(TableUserByUserId userEntity) {
        this.userEntity = userEntity;
    }
}
