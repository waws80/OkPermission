package top.waws.premission;

import android.util.Log;

/**
 * Created on 2017/11/2.
 *
 * @author liuxiongfei.
 *         Desc
 */

public final class Logger {

    private static boolean DEBUG = false;

    private Logger(){
    }

    static void init(boolean debug){
        DEBUG = debug;
    }

    public static void d(String msg){
        if (DEBUG){
            Log.d("Permission",msg);
        }

    }


}
