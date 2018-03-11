package com.lee.edu.mydemo;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.lee.edu.mydemo.Global.Global_data;


public class MainActivity extends AppCompatActivity {

    private Global_data global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            RequestPermission();
        }

        global = new Global_data();
        InitView();
        global.InitListener();

    }


    private void InitView() {
        global.port = (EditText) findViewById(R.id.port);
        global.btnPlayRecord = (Button) findViewById(R.id.btnplayrecord);
        global.btnStopRecord = (Button) findViewById(R.id.btnstoprecord);
        global.btnStopRecord.setEnabled(false);
        global.textView1 = (TextView) findViewById(R.id.textView1);
        global.textView2 = (TextView) findViewById(R.id.textView2);
    }


    private void RequestPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionsUtil.hasPermission(MainActivity.this, permissions)) {
            //已经获取相关权限
        } else {
            PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    //用户授予了权限
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    //用户拒绝了权限
                    Toast.makeText(MainActivity.this, "相关权限被拒绝，本应用将无法正常运行", Toast.LENGTH_SHORT).show();
                }
            }, permissions);
        }
    }
}