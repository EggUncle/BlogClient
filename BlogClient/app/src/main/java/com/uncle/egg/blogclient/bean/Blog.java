package com.uncle.egg.blogclient.bean;

/**
 * Created by egguncle on 17-1-13.
 */

public class Blog
{
    private int blogId;

    private String blogAuthor;

    private String blogDate;

    private String blogTitle;

    private String blogContent;

    public void setBlogId(int blogId){
        this.blogId = blogId;
    }
    public int getBlogId(){
        return this.blogId;
    }
    public void setBlogAuthor(String blogAuthor){
        this.blogAuthor = blogAuthor;
    }
    public String getBlogAuthor(){
        return this.blogAuthor;
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
}