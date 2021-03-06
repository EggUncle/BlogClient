package com.uncle.egg.blogclient.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.util.ImageUtil;
import com.uncle.egg.blogclient.util.NetWorkUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import java.io.ByteArrayOutputStream;

public class EditBlogActivity extends BaseAcitvity {

    private static final int SELECT_PHOTO = 0;//调用相册照片

    private final static String TAG = "EditBLogActivity";

    private RelativeLayout activityEditBlog;
    //   private ScrollView editForm;
    private TextInputLayout edTitle;
    private TextInputLayout edContent;
    private Button btnSubmit;
    private Button btnAddImage;
    private ImageView ivSubmit;


    private String userId;

    private String imagePath = "";


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
        btnAddImage = (Button) findViewById(R.id.btn_add_image);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);
        ivSubmit.setDrawingCacheEnabled(true);
    }

    private void initData() {
        SPUtil.getInstance(this);
        userId = SPUtil.getUserId();
    }

    private void initAction() {

        //提交请求
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从sp中取出用户名，若为默认值，则说明没有登录
                SPUtil sp = SPUtil.getInstance(EditBlogActivity.this);
                String userName = sp.getUserName();
                String token = sp.getToken();
                if ("".equals(userName)) {
                    Toast.makeText(EditBlogActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                NetWorkUtil internetUtil = new NetWorkUtil();
                String title = edTitle.getEditText().getText().toString();
                String content = edContent.getEditText().getText().toString();
                String imageType = "";
                if (!"".equals(imagePath)) {
                    //图片类型
                    imageType = imagePath.substring(imagePath.indexOf(".") + 1);
                    Log.i(TAG, "onClick: " + imageType);

                }
                internetUtil.submitBlog(userName, token, title, content, imagePath, imageType);
            }
        });

        //启动系统相册来获取图片
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //申请读取SD卡和调用相机的权限
                if (ContextCompat.checkSelfPermission(EditBlogActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(EditBlogActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ) {
                    ActivityCompat.requestPermissions(EditBlogActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);

                } else {
                    getImage();
                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                } else {
                    //权限申请未通过

                }
                break;
            default:

        }
    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);//调用相册照片
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: SELECT_PHOTO");
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统用这个方法处理图片
                        String imagePath = ImageUtil.handleImageOnKitKat(data);
                        displayImage(imagePath);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        String imagePath = ImageUtil.handleImageBeforeKitKat(data);
                        displayImage(imagePath);
                    }

                }

                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 将相册选中的图片展示在界面上
     *
     * @param imagePath
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ivSubmit.setImageBitmap(bitmap);
        }
    }


    //将图片转换为数组  发送请求时使用
    public byte[] getImageBytes(Bitmap bmp) {
        if (bmp == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

}
