package com.uncle.egg.blogclient.activity;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.util.InternetUtil;
import com.uncle.egg.blogclient.util.SPUtil;

public class EditBlogActivity extends BaseAcitvity {

    private RelativeLayout activityEditBlog;
 //   private ScrollView editForm;
    private TextInputLayout edTitle;
    private TextInputLayout edContent;
    private Button btnSubmit;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initAction();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_blog;
    }

    private void initView() {
        activityEditBlog = (RelativeLayout) findViewById(R.id.activity_edit_blog);
       // editForm = (ScrollView) findViewById(R.id.edit_form);
        edTitle = (TextInputLayout) findViewById(R.id.ed_title);
        edContent = (TextInputLayout) findViewById(R.id.ed_content);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }

    private void initData() {
        SPUtil.getInstance(this);
        userId = SPUtil.getUserId();
    }

    private void initAction() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InternetUtil internetUtil = new InternetUtil();

                String title=edTitle.getEditText().getText().toString();
                String content=edContent.getEditText().getText().toString();
                internetUtil.submitBlog(userId,title,content);
            }
        });
    }
}
