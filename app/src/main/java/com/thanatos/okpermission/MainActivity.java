package com.thanatos.okpermission;


import top.waws.premission.OkPermissionUtil;
import top.waws.premission.aspect.RequestPermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkPermissionUtil.init(true);
    }

    //@RequestPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void click(View view){
//        OkPermissionUtil.getInstance().build(this)
//                .mustAgree()
//                .request(Collections.singletonList(Manifest.permission.CALL_PHONE))
//                .execute(new PermissionCallBack() {
//                    @Override
//                    public void next(int code) {
//                    }
//                });
        requestPermission();
        Log.d(TAG, "click: 我是点击事件内部的方法");
    }

    @RequestPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void requestPermission() {

    }

}
