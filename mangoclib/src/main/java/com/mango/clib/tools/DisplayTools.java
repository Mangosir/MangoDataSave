package com.mango.clib.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @Description TODO(屏幕参数工具类)
 * @author cxy
 * @Date 2018/11/16 15:06
 */
public class DisplayTools {

    /** dip转px */
    public static int dip2px(Context context,int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5);
    }

    /** px转换dip */
    public static int px2dip(Context context,int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    /** px转换sp */
    public static int px2sp(Context context,int pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    /** sp转换px */
    public static int sp2px(Context context,int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
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
