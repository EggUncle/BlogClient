package com.uncle.egg.blogclient.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by egguncle on 17-1-13.
 */

public class BlogJson implements Serializable{
    private boolean error;

    private List<Results> results;

    public void setError(boolean error){
        this.error = error;
    }
    public boolean getError(){
        return this.error;
    }
    public void setResults(List<Results> results){
        this.results = results;
    }
    public List<Results> getResults(){
        return this.results;
    }

}
