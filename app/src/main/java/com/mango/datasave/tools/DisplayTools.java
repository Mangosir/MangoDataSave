package com.mango.datasave.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @Description TODO(屏幕工具类)
 * @author cxy
 * @Date 2018/11/16 15:06
 */
public class DisplayTools {

    // 根据手机的分辨率将dp的单位转成px(像素)
    public static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    //获取屏幕密度density
    public static float getDensity(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    //获取每英寸对应多少个点（不是像素点）
    public static int getDensityDpi(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    // 屏幕宽度（像素）
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getWindowHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

}
