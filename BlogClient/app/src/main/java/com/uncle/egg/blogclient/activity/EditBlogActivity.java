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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.util.InternetUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Target;

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
                InternetUtil internetUtil = new InternetUtil();
                String title = edTitle.getEditText().getText().toString();
                String content = edContent.getEditText().getText().toString();
                String imageType = null;
                if ("".equals(imagePath)) {
                    //图片类型
                    imageType = imagePath.substring(imagePath.indexOf(".") + 1);
                    Log.i(TAG, "onClick: " + imageType);

                }
                internetUtil.submitBlog(userId, title, content, imagePath, imageType);
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
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }

                }

                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {

        Log.i(TAG, "handleImageOnKitKat: ");

        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的ID
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通的方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取图片的真实路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

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
