package com.uncle.egg.blogclient.bean;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-17.
 */

public class UserEntity implements Serializable{
    private int userId;

    private String username;

    private String iconPath;

    private String description;

    private String nickname;

    private String status;

    private String bgPath;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

//    private String userPassWd;
//
//    public String getUserPassWd() {
//        return userPassWd;
//    }
//
//    public void setUserPassWd(String userPassWd) {
//        this.userPassWd = userPassWd;
//    }

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
    public void setIconPath(String iconPath){
        this.iconPath = iconPath;
    }
    public String getIconPath(){
        return this.iconPath;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setBgPath(String bgPath){
        this.bgPath = bgPath;
    }
    public String getBgPath(){
        return this.bgPath;
    }

}
