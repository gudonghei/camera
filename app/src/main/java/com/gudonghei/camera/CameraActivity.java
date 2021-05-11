package com.gudonghei.camera;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gudonghei.camera.permissions.PermissionsActivity;
import com.gudonghei.camera.permissions.PermissionsChecker;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoFitTextureView mTextureview;

    private Button mTakePictureBtn;//拍照
    private Button mVideoRecodeBtn;//开始录像
    private Button preview;//预览
    private Button go_back;//返回


    private CameraController mCameraController;
    private boolean mIsRecordingVideo; //开始停止录像
    public static String BASE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    //获取系统的公共存储路径

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private static final int PERMISSIONS_REQUEST_CODE = 0; // 请求码
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo 状态栏背景透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.alpha(0));
        }
        setContentView(R.layout.activity_camera);
        mPermissionsChecker = new PermissionsChecker(this);

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, PERMISSIONS_REQUEST_CODE, PERMISSIONS);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取相机管理类的实例
        mCameraController = CameraController.getmInstance(this);
        mCameraController.setFolderPath(BASE_PATH);

        initView();

    }

    private void initView() {
        mTextureview = (AutoFitTextureView) findViewById(R.id.textureview);
        mTakePictureBtn = (Button) findViewById(R.id.take_picture_btn);
        mTakePictureBtn.setOnClickListener(this);
        mVideoRecodeBtn = (Button) findViewById(R.id.video_recode_btn);
        mVideoRecodeBtn.setOnClickListener(this);
        preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(this);
        go_back= (Button) findViewById(R.id.go_back);
        go_back.setOnClickListener(this);
        mCameraController.InitCamera(mTextureview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.take_picture_btn:
                mCameraController.takePicture();
                break;
            case R.id.go_back:
                finish();
                break;
            case R.id.preview:
                mCameraController.unlockFocus();
                break;
            case R.id.video_recode_btn:
                if (mIsRecordingVideo) {
                    mIsRecordingVideo = !mIsRecordingVideo;
                    mCameraController.stopRecordingVideo();
                    mVideoRecodeBtn.setText("开始录像");
                    Toast.makeText(this, "录像结束", Toast.LENGTH_SHORT).show();
                } else {
                    mVideoRecodeBtn.setText("停止录像");
                    mIsRecordingVideo = !mIsRecordingVideo;
                    mCameraController.startRecordingVideo();
                    Toast.makeText(this, "录像开始", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }
}