package com.mango.clib.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:Mangoer
 * Time:2018/11/27 20:50
 * Version:
 * Desc:TODO(时间辅助类)
 */
public class TimeTools {

    private static String TAG = TimeTools.class.getSimpleName();

    private static Map<String,Long> mStartTime = new HashMap<>();
    private static String TIME_BEGIN = "begin";
    private static String TIME_PART = "part";

    /**
     * 保存应用创建的时间点
     */
    public static void saveBeginTime(){
        long time = System.currentTimeMillis();
        mStartTime.put(TIME_BEGIN,time);
    }

    /**
     * 保存应用冷启动到第一个Activity完整显示所消耗的时间
     */
    public static void savePartTime(){
        //判断是不是冷启动
        if (!mStartTime.containsKey(TIME_BEGIN) || mStartTime.get(TIME_BEGIN) <= 0l) {
            return;
        }
        long timePart = System.currentTimeMillis() - mStartTime.get(TIME_BEGIN);
        mStartTime.put(TIME_PART,timePart);
        mStartTime.remove(TIME_BEGIN);
    }

    /**
     * 将启动时间提交到服务端
     */
    public static void commitTime2Server(){

        if (!mStartTime.containsKey(TIME_PART) || mStartTime.get(TIME_PART) <= 0) {
            return;
        }

        LocalThreadPools.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                long time = mStartTime.get(TIME_PART);
                mStartTime.remove(TIME_PART);
            }
        });
    }
}
