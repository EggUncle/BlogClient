package com.uncle.egg.blogclient.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-17.
 */


public class Results implements Serializable {
    private int blogId;

    private String blogDate;

    private String blogTitle;

    private String blogContent;

    @SerializedName("user")
    private UserEntity userEntity;

    private String imgPath;

    public void setBlogId(int blogId){
        this.blogId = blogId;
    }
    public int getBlogId(){
        return this.blogId;
    }
    public void setBlogDate(String blogDate){
        this.blogDate = blogDate;
    }
    public String getBlogDate(){
        return this.blogDate;
    }
    public void setBlogTitle(String blogTitle){
        this.blogTitle = blogTitle;
    }
    public String getBlogTitle(){
        return this.blogTitle;
    }
    public void setBlogContent(String blogContent){
        this.blogContent = blogContent;
    }
    public String getBlogContent(){
        return this.blogContent;
    }
    public void setUserEntity(UserEntity userEntity){
        this.userEntity = userEntity;
    }
    public UserEntity getUserEntity(){
        return this.userEntity;
    }
    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }
    public String getImgPath(){
        return this.imgPath;
    }
}