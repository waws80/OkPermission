package top.waws.premission;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Created on 2017/11/2.
 * @author liuxiongfei.
 * Desc 权限请求核心类
 */
 final class Call {

    private List<String> mPermissions;
    private int mRequestCode;
    private AppCompatActivity mActivity;
    private PermissionCallBack callBack;
    private boolean mustAgree;

    Call(List<String> permissions, int requestCode, AppCompatActivity activity,
         PermissionCallBack callBack, IPermissionIntecepter intecepter, boolean mustAgree){
        this.mPermissions = permissions;
        this.mRequestCode = requestCode;
        this.mActivity = activity;
        this.callBack = callBack;
        this.mustAgree = mustAgree;
        build(intecepter);
    }

    private void build(IPermissionIntecepter intecepter){

        if (this.mPermissions == null || this.mPermissions.isEmpty()){
            throw new IllegalArgumentException("premissions isEmpty");
        }
        if (this.callBack == null){
            throw new IllegalArgumentException("callBack is null");
        }
        PermissionFragment fragment = new PermissionFragment();
        fragment.setRequestCode(this.mRequestCode);
        fragment.setPermissions(this.mPermissions);
        fragment.setCallBack(this.callBack);
        fragment.setIntecepter(intecepter);
        fragment.setMustAgree(this.mustAgree);
        FragmentManager manager = this.mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(fragment,"PermissionFragment").commit();
    }

}
