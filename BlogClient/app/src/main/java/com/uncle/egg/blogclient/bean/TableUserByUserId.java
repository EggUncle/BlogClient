package com.uncle.egg.blogclient.bean;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-17.
 */

public class TableUserByUserId implements Serializable{
    private int userId;

    private String username;

    private String userpasswd;

    public void setUserId(int userId){
        this.userId = userId;
    }
    public int getUserId(){
        return this.userId;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setUserpasswd(String userpasswd){
        this.userpasswd = userpasswd;
    }
    public String getUserpasswd(){
        return this.userpasswd;
    }

}