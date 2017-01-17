package com.uncle.egg.blogclient.bean;

import java.io.Serializable;

/**
 * Created by egguncle on 17-1-17.
 */


public class Results implements Serializable {
    private int blogId;

    private String blogDate;

    private String blogTitle;

    private String blogContent;

    private TableUserByUserId tableUserByUserId;

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
    public void setTableUserByUserId(TableUserByUserId tableUserByUserId){
        this.tableUserByUserId = tableUserByUserId;
    }
    public TableUserByUserId getTableUserByUserId(){
        return this.tableUserByUserId;
    }

}