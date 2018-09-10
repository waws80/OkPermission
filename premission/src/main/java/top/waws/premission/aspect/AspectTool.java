package top.waws.premission.aspect;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.waws.premission.Logger;
import top.waws.premission.OkPermissionUtil;
import top.waws.premission.PermissionCallBack;

/**
 *  功能描述:
 *  @className: AspectTool
 *  @author: thanatos
 *  @createTime: 2018/9/6
 *  @updateTime: 2018/9/6 10:59
 */
@Aspect
public class AspectTool {

    private static final String TAG = "AspectTool";

    private static final String POINT_CUT = "execution(@RequestPermission * *(..))";


    /**
     * 申请权限aop
     */
    @Pointcut(POINT_CUT)
    public void pointcut(){
    }

    @Around("pointcut()")
    public void methodAround(final ProceedingJoinPoint point) throws Throwable {
        Object parent = point.getTarget();
        String methodName = point.getSignature().getName();
        Method method = parent.getClass().getMethod(methodName,((MethodSignature)(point.getSignature())).getParameterTypes());
        //防止为私有的方法
        method.setAccessible(true);
        if (!method.getReturnType().equals(void.class)){
            throw new IllegalArgumentException("方法的返回值只能是 void");
        }
        RequestPermission requestPermission = method.getAnnotation(RequestPermission.class);
        Activity activity = null;
        if (parent instanceof Activity){
            activity = (Activity) parent;
        }
        if (activity == null && parent instanceof Fragment){
            activity = ((Fragment) parent).getActivity();
        }
        if (activity == null && parent instanceof android.app.Fragment){
            activity = ((android.app.Fragment) parent).getActivity();
        }
        if (activity == null){
            for (Object arg : point.getArgs()) {
                if (arg instanceof Activity || arg instanceof Fragment || arg instanceof android.app.Fragment){
                    if (arg instanceof Activity){
                        activity = (Activity) arg;
                    }else if (arg instanceof Fragment){
                        activity = ((Fragment) arg).getActivity();
                    }else {
                        activity = ((android.app.Fragment) arg).getActivity();
                    }
                }
            }
        }
        if (activity == null){
            Logger.d("当前注解只能用在activity 和 fragment 上 或者参数中有 activity 或 fragment");
            return;
        }

        OkPermissionUtil okPermissionUtil = OkPermissionUtil.getInstance().build(parent)
                .request(Arrays.asList(requestPermission.value()));
        if (requestPermission.mustAgree()){
            okPermissionUtil.mustAgree();
        }
        okPermissionUtil.execute(new PermissionCallBack() {
            @Override
            public void next(int code) {
                if (code == OkPermissionUtil.OK){
                    try {
                        point.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }else {
                    Logger.d("拒绝申请本次权限");
                }
            }
        });
    }




}
