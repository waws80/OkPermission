package top.waws.premission;

import android.util.Log;

/**
 * Created on 2017/11/2.
 *
 * @author liuxiongfei.
 *         Desc
 */

final class Logger {

    private static boolean DEBUG = false;

    private Logger(){
    }

    static void init(boolean debug){
        DEBUG = debug;
    }

    static void d(String msg){
        if (DEBUG){
            Log.d("Permission",msg);
        }

    }


}
