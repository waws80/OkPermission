package top.waws.premission.aspect;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  功能描述: 请求权限注解
 *  @className: RequestPermission
 *  @author: thanatos
 *  @createTime: 2018/9/6
 *  @updateTime: 2018/9/6 11:06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestPermission {

    /**
     * 请求的权限组
     * @return
     */
    String[] value();

    /**
     * 是否必须同意
     * @return
     */
    boolean mustAgree() default false;

}
