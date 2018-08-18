package top.waws.premission;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;


/**
 *  功能描述: 权限申请代理类
 *  @className: PermissionFragment
 *  @author: thanatos
 *  @createTime: 2018/6/12
 *  @updateTime: 2018/6/12 14:49
 */
public final class PermissionFragment extends Fragment implements PermissionRequest{

    private PermissionCallBack callBack;

    private List<String> permissions = new ArrayList<>();

    private int requestCode;

    private IPermissionIntecepter mIntecepter;
    private List<String> mRequestPermission = new ArrayList<>();
    private boolean mustAgree = false;
    private List<String> mDangers = new ArrayList<>();
    private boolean mCheckResume = false;

    public void setCallBack(PermissionCallBack callBack) {
        this.callBack = callBack;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions.clear();
        this.permissions.addAll(permissions);
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setIntecepter(IPermissionIntecepter intecepter) {
        this.mIntecepter = intecepter;
    }

    public void setMustAgree(boolean mustAgree) {
        this.mustAgree = mustAgree;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();

    }

    /**
     * 判断权限是否需要申请
     */
    private void start(){
        if (mIntecepter != null && !mIntecepter.start(this)){
            return;
        }
        //判断申请的权限是否是空
        if (this.permissions.isEmpty()){
            this.callBack.next(OkPermissionUtil.OK);
            if (mIntecepter != null){
                mIntecepter.complete(OkPermissionUtil.OK, new ArrayList<String>());
            }
            destroy();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            this.callBack.next(OkPermissionUtil.OK);
            if (mIntecepter != null){
                mIntecepter.complete(OkPermissionUtil.OK, new ArrayList<String>());
            }
            destroy();
        }else {
            //检查获取没有申请的权限
            List<String> requestPermissions = new ArrayList<>();
            for (String permission : this.permissions) {
                if (ContextCompat.checkSelfPermission(this.getActivity(),permission)
                        != PackageManager.PERMISSION_GRANTED){
                    requestPermissions.add(permission);
                }
            }
            //判断是否有没有申请的权限
            if (requestPermissions.isEmpty()){
                this.callBack.next(OkPermissionUtil.OK);
                if (mIntecepter != null){
                    mIntecepter.complete(OkPermissionUtil.OK, new ArrayList<String>());
                }
                destroy();
                return;
            }
            //申请权限
            request(requestPermissions);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void request(List<String> requestPermission) {
        this.mRequestPermission.clear();
        this.mRequestPermission.addAll(requestPermission);
        boolean hasRequested = false;
        for (String s : requestPermission) {
            if (shouldShowRequestPermissionRationale(s)){
                hasRequested = true;
            }
        }
        if (hasRequested){
            if (mIntecepter == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示")
                        .setMessage("请求程序运行所需权限")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //开始授权
                                for (String s : mRequestPermission) {
                                    Logger.d("开始申请拒绝过的权限 Permission："+s);
                                }
                                requestPermissions(list2Array(mRequestPermission),requestCode);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                for (String s : mRequestPermission) {
                                    Logger.d("再次拒绝申请权限 Permission："+s);
                                }
                                Logger.d("please give me a chance! please!\n permission is denied");
                            }
                        })
                        .setCancelable(false)
                        .show();

            }else {
                //拒绝过权限，重新申请权限
                mIntecepter.hasRequested(this);
            }
        } else {
            //开始授权
            for (String s : mRequestPermission) {
                Logger.d("开始申请权限 Permission："+s);
            }
            if (mIntecepter == null){
                requestPermissions(list2Array(mRequestPermission),requestCode);
            }else {
                //第一次授权
                mIntecepter.request(this);
            }
        }
    }

    /**
     * 请求权限回调
     * @param requestCode requestCode
     * @param permissions permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.requestCode == requestCode){
            List<String> dangers = new ArrayList<>();//存放拒绝的权限
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this.getActivity(),permission)
                        != PackageManager.PERMISSION_GRANTED){
                    dangers.add(permission);
                    Logger.d("拒绝了权限 Permission："+permission);
                }else {
                    Logger.d("获取权限成功 Permission："+permission);
                }
            }
            if (mIntecepter != null){
                mIntecepter.complete(dangers.isEmpty()? OkPermissionUtil.OK : OkPermissionUtil.ERROR, dangers);
            }
            if (dangers.isEmpty()){
                callBack.next(OkPermissionUtil.OK);
                destroy();
            }else {
                if (this.mustAgree){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean haveRejected = false;
                        for (String danger : dangers) {
                            if (shouldShowRequestPermissionRationale(danger)){
                                haveRejected = true;
                                break;
                            }
                        }
                        if (haveRejected){//用户未勾选不再询问
                            requestPermissions(list2Array(dangers),requestCode);
                        }else { //用户勾选了不再询问
                            startSetting(dangers);
                        }
                    }
                }else {
                    callBack.next(OkPermissionUtil.ERROR);
                    destroy();
                }

            }
        }
    }

    /**
     * 销毁当前fragment
     */
    private void destroy() {
        if (getActivity() == null){
            return;
        }
        if (getActivity().getFragmentManager() == null){
            return;
        }
        FragmentTransaction transaction = getActivity().getFragmentManager()
                .beginTransaction();
        transaction.remove(this).commit();
    }

    /**
     * 开始请求权限
     */
    @Override
    public void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(list2Array(mRequestPermission),requestCode);
        }
    }

    /**
     * 获取申请的权限
     * @return
     */
    @Override
    public List<String> getPermissions() {
        return permissions;
    }


    /**
     * 集合转数组
     * @param list
     * @return
     */
    private String[] list2Array(List<String> list){
        if (list == null || list.isEmpty()){
            return new String[]{};
        }
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * 用户点击了不再询问，弹出是否去设置去设置
     * @param dangers
     */
    private void startSetting(final List<String> dangers) {
        this.mDangers = dangers;
        if (mIntecepter != null){
            mIntecepter.noAskComplete(this,dangers);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("申请权限");
        builder.setMessage("若您想修改权限设置请点击“确定”去系统设置中进行设置。");
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startSetting();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (mIntecepter != null){
                    mIntecepter.complete(OkPermissionUtil.ERROR,dangers);
                }
                callBack.next(OkPermissionUtil.ERROR);
                destroy();
            }
        });
        dialog.show();
    }

    @Override
    public void startSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getActivity().getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,0x111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 0x111){
            destroy();
            return;
        }
        if (mDangers != null && !mDangers.isEmpty()){
            List<String> failures = new ArrayList<>();
            for (String danger : mDangers) {
                if (ContextCompat.checkSelfPermission(this.getActivity(),danger)
                        != PackageManager.PERMISSION_GRANTED){
                    failures.add(danger);
                }
            }
            if (mIntecepter != null){
                mIntecepter.complete(failures.isEmpty()? OkPermissionUtil.OK : OkPermissionUtil.ERROR, failures);
            }
            if (failures.isEmpty()){
                callBack.next(OkPermissionUtil.OK);
            }else {
                callBack.next(OkPermissionUtil.ERROR);
            }
        }
        destroy();
    }

}
