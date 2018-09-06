package top.waws.premission;

import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请封装类
 * Created on 2017/11/2.
 * @author liuxiongfei
 */
@SuppressWarnings("All")
public final class OkPermissionUtil {

    /**
     * 是否开启了调试模式
     */
    private static boolean DEBUG = false;

    public static final int OK = 100;

    public static final int ERROR = 101;

    private static final int REQUEST_CODE = 0x100;

    /**
     * 权限全局申请拦截器
     */
    private static IPermissionIntecepter mIntecepter;

    private List<String> mPermissions = new ArrayList<>();

    private WeakReference<AppCompatActivity> mActivityWRF;
    private boolean mustAgree = false; //是否必须统一



    private OkPermissionUtil(){
    }

    /**
     * 内部静态类专门获取Permission实类对象
     */
    private static final class Builder{
        private static final OkPermissionUtil PERMISSION = new OkPermissionUtil();
    }

    /**
     * 不开启调试模式的初始化
     * @return Permission
     */
    public static OkPermissionUtil getInstance(){
        return Builder.PERMISSION;
    }

    /**
     * 开启调试模式的初始化
     * @param debug 是否开启调试模式
     * @return Permission
     */
    public static void init(boolean debug) {
        DEBUG = debug;
        Logger.init(DEBUG);

    }

    /**
     * 设置请求的权限
     * @param premission 权限
     * @return Permission
     */
    public OkPermissionUtil request(List<String> premissions) {
        this.mPermissions.clear();
        this.mPermissions.addAll(premissions);
        return this;
    }

    /**
     * 构建上下文
     * @param activity
     * @return
     */
    public OkPermissionUtil build(Object activity){
        if (activity instanceof AppCompatActivity){
            this.mActivityWRF = new WeakReference<>((AppCompatActivity) activity);
        }
        return this;
    }


    public OkPermissionUtil mustAgree(){
        this.mustAgree = true;
        return this;
    }


    /**
     * 添加权限申请拦截器
     * @param intecepter
     * @return
     */
    public static void  setIntecepter(IPermissionIntecepter intecepter){
        mIntecepter = intecepter;
    }


    /**
     * 执行请求权限并获取回调
     * @param activity Activity
     */
    public void execute(PermissionCallBack callBack){
        if (this.mActivityWRF == null || this.mActivityWRF.get() == null){
            Logger.d("申请权限的目标栈为空");
            return;
        }
        if (callBack == null) {
            Logger.d("权限回调申请为空");
            return;
        }
        if (mPermissions.isEmpty()){
            Logger.d("权限列表为空");
            return;
        }
        //添加默认的拦截器
        if (mIntecepter == null){
            mIntecepter = new DefaultIntecepter();
        }
        new Call(mPermissions, REQUEST_CODE, this.mActivityWRF.get(), callBack, mIntecepter, mustAgree);
    }


}
