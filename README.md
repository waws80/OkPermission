# OkPermission
权限申请

[![](https://jitpack.io/v/waws80/OkPermission.svg)](https://jitpack.io/#waws80/OkPermission)


aspectj使用 添加 第一步 和 第二步 然后添加

	api 'org.aspectj:aspectjrt:1.9.1'

2018-09-06：
#添加了注解申请权限
##使用步骤：
### 1：build.gradle中添加

	dependencies {
        	classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0'
    	}
	
	repositories {
        	maven { url 'https://jitpack.io' }
    	}
    
### 2：每个module的gradle中添加

	apply plugin: 'android-aspectjx'
	
### 3：base module的gradle中添加

	dependencies {
	        implementation 'com.github.waws80:OkPermission:0.2.1'
	}
	
### 4：使用例子：
	//初始化
	OkPermissionUtil.init(true);
	
	//普通使用方式
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
	
	
	//注解使用方式 当前注解只能用在 activity 和fragment 或者 参数中 包含 activity 或 fragment
	@RequestPermission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE}, mustAgree = true)
   	public void click(View view){
        	Log.d(TAG, "click: 我是点击事件内部的方法");
    	}
	
### 5: 拦截器	
	//拦截器
	OkPermissionUtil.setIntecepter(new IPermissionIntecepter() {
            @Override
            public boolean start(PermissionRequest permissionRequest) {
                //返回 true 开始执行， false 不执行
                return true;
            }

            @Override
            public void request(PermissionRequest permissionRequest) {
                //执行请求
                permissionRequest.request();
            }

            @Override
            public void hasRequested(PermissionRequest permissionRequest) {
                //请求过
                //permissionRequest.request();
            }

            @Override
            public void noAskComplete(PermissionRequest permissionRequest, List<String> dangers) {
                //用户选择了不再询问的请求
                //permissionRequest.request();
            }

            @Override
            public void complete(int code, List<String> dangers) {
                //请求结果回调
            }
        });
	
	
