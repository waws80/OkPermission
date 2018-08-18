package top.waws.premission;

import java.util.List;

/**
 *  功能描述: 权限全局拦截器
 *  @className: IPermissionIntecepter
 *  @author: thanatos
 *  @createTime: 2018/6/12
 *  @updateTime: 2018/6/12 12:07
 */
public interface IPermissionIntecepter {

    /**
     * 开始请求的时候
     * @param permissionRequest
     */
    boolean start(PermissionRequest permissionRequest);

    /**
     * 权限没有请求过会走此方法
     * @param permissionRequest
     */
    void request(PermissionRequest permissionRequest);

    /**
     * 权限列表中有请求过的权限会走此方法
     * @param permissionRequest
     */
    void hasRequested(PermissionRequest permissionRequest);


    /**
     * 点击了不再询问
     * @param permissionRequest
     * @param dangers
     */
    void noAskComplete(PermissionRequest permissionRequest,List<String> dangers);

    /**
     * 权限申请结束走此方法
     * @param code
     */
    void complete(int code, List<String> dangers);
}
