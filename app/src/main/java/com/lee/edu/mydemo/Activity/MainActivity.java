package com.lee.edu.mydemo.Activity;


import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import com.lee.edu.mydemo.JavaBean.GlobalBean;
import com.lee.edu.mydemo.R;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

   private GlobalBean globalBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            RequestPermission();
        }

        Bmob.initialize(this, "9dbc988651cd8b0403a4d8e2566459e9");
         globalBean=new GlobalBean(MainActivity.this);
        Init();


    }




    private void Init() {
        globalBean.btnPlayRecord = (Button) findViewById(R.id.btnplayrecord);
        globalBean.btnStopRecord = (Button) findViewById(R.id.btnstoprecord);
        globalBean.tvDist = (TextView) findViewById(R.id.textView1);
        globalBean.tvDist.setText(String.valueOf(0));
        globalBean.tvDist2 = (TextView) findViewById(R.id.textView2);
        globalBean.tvDist2.setText(String.valueOf(0));
        globalBean.btnStopRecord.setEnabled(false);
        globalBean.Init();
    }






    private void RequestPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
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