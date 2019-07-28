package com.mango.clib.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

import static com.mango.clib.tools.FileStorageTools.isSdCardMount;


/**
 * Author:Mangoer
 * Time:2018/11/29 20:47
 * Version:
 * Desc:TODO()
 */
public class DevicesTools {

    /**
     * 获取手机ram内存，即应用运行所用内存
     * @param context
     * @return [0] 手机ram总内存， [1] 当前ram可用内存 , [2] 系统是否处于低内存运行模式
     */
    public static Object[] getRAMMemory(Context context) {
        Object[] mem = new Object[3];
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        mem[0] = reviseFileSize(mi.totalMem);
        mem[1] = reviseFileSize(mi.availMem);
        mem[2] = mi.lowMemory;
        return mem;
    }

    /**
     * 获取软件版本信息
     * @param context
     * @return
     */
    public static Object[] getAppVersion(Context context){
        Object[] msg = new Object[2];
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
            msg[0] = info.versionName == null ? "unknow" : info.versionName;
            msg[1] = info.versionCode ;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }


    /**
     * 获取应用虚拟机能获取使用的最大内存
     * @return
     */
    public static String getAPPMaxMemory(){
        return reviseFileSize(Runtime.getRuntime().maxMemory());
    }

    /**
     * 获取应用虚拟机已开辟内存
     * @return
     */
    public static String getAPPAllocatedMemory(){
        return reviseFileSize(Runtime.getRuntime().totalMemory());
    }

    /**
     * 获取应用虚拟机已释放的内存
     * 调用gc()会使该值增大
     * @return
     */
    public static String getAPPFreeMemory(){
        return reviseFileSize(Runtime.getRuntime().freeMemory());
    }


    //获取sd卡总大小
    public static String getSDTotalSize(){
        if(isSdCardMount()){
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            return reviseFileSize(totalBlocks*blockSize);
        }else {
            return null;
        }
    }

    //获取sd卡可用大小
    public static String getSDAvailableSize(){
        if(isSdCardMount()){
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSizeLong();
            long availableBlocks = statFs.getFreeBlocksLong();
            return reviseFileSize(availableBlocks*blockSize);
        }else {
            return null;
        }
    }


    public static String reviseFileSize(long size){
        String str="KB";
        float reviseSize = 0f;
        if(size>1024){
            reviseSize = size/1024f;
            if(reviseSize>1024){
                str="M";
                reviseSize = reviseSize/1024f;
                if (reviseSize>1024) {
                    str="G";
                    reviseSize = reviseSize/1024f;
                }
            }
        }

        DecimalFormat formatter=new DecimalFormat();
        formatter.setGroupingSize(3);
        String result = formatter.format(reviseSize) + str;
        return result;
    }


}
