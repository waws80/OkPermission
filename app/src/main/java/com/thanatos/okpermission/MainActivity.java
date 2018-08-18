package com.thanatos.okpermission;

import androidx.appcompat.app.AppCompatActivity;
import top.waws.premission.IPermissionIntecepter;
import top.waws.premission.OkPermissionUtil;
import top.waws.premission.PermissionCallBack;
import top.waws.premission.PermissionRequest;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view){
        OkPermissionUtil.getInstance().build(this)
                .mustAgree()
                .request(Collections.singletonList(Manifest.permission.CALL_PHONE))
                .execute(new PermissionCallBack() {
                    @Override
                    public void next(int code) {
                    }
                });
    }
}
