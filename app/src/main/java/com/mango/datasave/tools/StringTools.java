package com.mango.datasave.tools;

import android.text.TextUtils;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/1 9:16
 */
public class StringTools {

    /**
     * @Description TODO(判断字符串是否为empty)
     * @author cxy
     * @return true 有一个为empty就为true ，都不为empty就为false
     * @parame 接收一个字符串数组
     */
    public static boolean isEmpty(String... values){

        boolean isEmpty = false;

        for(int i=0; i<values.length; i++){
            if (TextUtils.isEmpty(values[i])) {
                isEmpty = true;
                break;
            }
        }

        return isEmpty;
    }

}
