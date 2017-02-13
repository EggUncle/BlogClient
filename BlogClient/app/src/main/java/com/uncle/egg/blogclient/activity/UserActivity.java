package com.uncle.egg.blogclient.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uncle.egg.blogclient.R;
import com.uncle.egg.blogclient.bean.UserEntity;
import com.uncle.egg.blogclient.util.ImageUtil;
import com.uncle.egg.blogclient.util.NetWorkUtil;
import com.uncle.egg.blogclient.util.SPUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class UserActivity extends BaseAcitvity {

    private LinearLayout activityUser;
    private CircleImageView ivUserIconSetting;
    private TextView tvUserNameSetting;
    private EditText edUserNicknameSetting;
    private EditText edUserDescription;

    private UserEntity userEntity;
    private SPUtil spUtil;
    private Button btnUserSettingUpdate;

    private NetWorkUtil netWorkUtil;

    private static final int SELECT_PHOTO = 0;//调用相册照片
    private static final int TAKE_PHOTO = 1;//调用相机拍照
    private static final int CROP_PHOTO = 2;//裁剪照片

    private final static String TAG = "UserActivity";

    private String imgPath;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initAction();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user;
    }

    private void initView() {
        activityUser = (LinearLayout) findViewById(R.id.activity_user);
        ivUserIconSetting = (CircleImageView) findViewById(R.id.iv_user_icon_setting);
        tvUserNameSetting = (TextView) findViewById(R.id.tv_user_name_setting);
        edUserNicknameSetting = (EditText) findViewById(R.id.ed_user_nickname_setting);
        edUserDescription = (EditText) findViewById(R.id.ed_user_description);
        btnUserSettingUpdate = (Button) findViewById(R.id.btn_user_setting_update);
    }

    private void initData() {
        netWorkUtil=new NetWorkUtil();
        spUtil = SPUtil.getInstance(this);
        userEntity = spUtil.getUser();
        //显示传入的用户实体类数据
        String iconPath = userEntity.getIconPath();
        if (iconPath != null) {
            Glide.with(this).load(iconPath).centerCrop().error(R.mipmap.ic_launcher).into(ivUserIconSetting);
        }
        tvUserNameSetting.setText(userEntity.getUsername());
        edUserNicknameSetting.setText(userEntity.getNickname());
        edUserDescription.setText(userEntity.getDescription());
    }

    private void initAction() {
        ivUserIconSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动系统相册来获取图片
                //申请读取SD卡和调用相机的权限
                if (ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(UserActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ) {
                    ActivityCompat.requestPermissions(UserActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);

                   // getImage();
                } else {
                    getImage();
                }

            }
        });

        btnUserSettingUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEntity.setDescription(edUserDescription.getText().toString());
                userEntity.setNickname(edUserNicknameSetting.getText().toString());
                String token = spUtil.getToken();
                netWorkUtil.updateUserInfo(userEntity, imgPath,token);
            }
        });
    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);//调用相册照片
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
                        imgPath = ImageUtil.handleImageOnKitKat(data);
                        displayImage(imgPath);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        imgPath = ImageUtil.handleImageBeforeKitKat(data);
                        displayImage(imgPath);
                    }

                }

            case CROP_PHOTO:


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
            Log.i(TAG, "displayImage: " + imagePath);
            //  Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Glide.with(this).load(imagePath).into(ivUserIconSetting);
        }
    }


}
