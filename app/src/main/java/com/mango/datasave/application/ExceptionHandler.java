package com.mango.datasave.application;

import android.content.Context;
import android.os.Build;
import android.os.Process;

import com.mango.clib.tools.DevicesTools;
import com.mango.clib.tools.FileStorageTools;
import com.mango.clib.tools.LocalThreadPools;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author:Mangoer
 * Time:2018/11/29 20:45
 * Version:
 * Desc:TODO()
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private String TAG = ExceptionHandler.class.getSimpleName();

    //默认异常处理对象
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    //用来存储设备信息和异常信息
    private StringBuffer sbInfo = new StringBuffer();
    //默认日志文件存储路径
    public static String CRASH_FILE_PATH ;

    public ExceptionHandler(Context context) {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.mContext = context;
        CRASH_FILE_PATH = "/Android/data/"+mContext.getPackageName()+"/crash";
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        //如果没有处理就交给系统处理
        if (mDefaultHandler != null && !handleException(t,e)) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
            //用作自杀操作
            Process.killProcess(Process.myPid());
            //终止当前正在运行的Java虚拟机，导致程序终止，0表示正常终止，其它表示异常结束
            //在部分机型中，当退出应用后弹出应用程序崩溃的对话框，有时退出后还会再次启动，少部分的用户体验不太好
//            System.exit(0);
            //重启应用
            /*Intent intent = new Intent(mContext, SplashActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, 0, restartIntent);
            Process.killProcess(Process.myPid());*/
        }
    }

    private boolean handleException(Thread t,Throwable ex){
        if (t == null || ex == null) return false;
        collectDeviceInfo();
        collectRunningMsg();
        saveMsg2File(t,ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {

        sbInfo.append("======设备参数信息======" + "\n");

        //版本信息
        Object[] pack = DevicesTools.getAppVersion(mContext);
        sbInfo.append("versionName" + " = " + pack[0] + "\n");
        sbInfo.append("versionCode" + " = " + pack[1] + "\n");

        //获取设备信息
        //sdk版本
        sbInfo.append("SDK_INT" + " = " + Build.VERSION.SDK_INT + "\n");
        //Android版本
        sbInfo.append("RELEASE" + " = " + Build.VERSION.RELEASE + "\n");

        sbInfo.append("PRODUCT" + " = " + Build.PRODUCT + "\n");
        sbInfo.append("MODEL" + " = " + Build.MODEL + "\n");
        sbInfo.append("DEVICE" + " = " + Build.DEVICE + "\n");
        sbInfo.append("DISPLAY" + " = " + Build.DISPLAY + "\n");
        sbInfo.append("BRAND" + " = " + Build.BRAND + "\n");
        sbInfo.append("BOARD" + " = " + Build.BOARD + "\n");
        sbInfo.append("FINGERPRINT" + " = " + Build.FINGERPRINT + "\n");
        sbInfo.append("ID" + " = " + Build.ID + "\n");
        sbInfo.append("MANUFACTURER" + " = " + Build.MANUFACTURER + "\n");
        sbInfo.append("USER" + " = " + Build.USER + "\n");

        String[] cpu = Build.SUPPORTED_ABIS;
        int size = cpu == null ? 0 : cpu.length;
        for (int i=0; i<size; i++) {
            sbInfo.append("SUPPORTED_ABIS " + i + " = " + cpu[i] + "\n");
        }
    }

    /**
     * 获取运行时信息
     */
    private void collectRunningMsg(){

        sbInfo.append("====设备运行时信息=====" + "\n");

        Object[] ram = DevicesTools.getRAMMemory(mContext);

        sbInfo.append("totalMem = " + ram[0] + " \n");

        sbInfo.append("availMem = " + ram[1] + " \n");

        sbInfo.append("lowMemory = " + ram[2] + "\n");

        sbInfo.append("maxMemory = " + DevicesTools.getAPPMaxMemory() + " \n");

        sbInfo.append("allocated = " + DevicesTools.getAPPAllocatedMemory() + " \n");

        sbInfo.append("freeMemory = " + DevicesTools.getAPPFreeMemory() + " \n");

    }

    private void saveMsg2File(Thread t,Throwable ex){

        sbInfo.append("====应用崩溃信息====" + "\n");

        sbInfo.append("ThreadGroup = " + t.getThreadGroup().getName() + "\n");
        sbInfo.append("Thread = " + t.getName() + "\n");
        sbInfo.append("Priority = " + t.getPriority() + "\n");
        sbInfo.append("activeCount = " + t.activeCount() + "\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        sbInfo.append(writer.toString());
        printWriter.close();

        /**
         * 如果内存卡不可用，那就没办法了
         * 就直接提交 可能会提交失败
         */
        if (!FileStorageTools.isSdCardMount()) {
            pushMsg2Server();
            return;
        }

        String parent = FileStorageTools.getInstance(mContext).makeFilePath(CRASH_FILE_PATH);
        File parentFile = new File(parent);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        //文件名还可以加上用户名等信息
        String fileName = "crash-" + time + ".txt";
        FileStorageTools.getInstance(mContext).putStringToExternalStorage(sbInfo.toString(),parentFile,fileName,false);
    }

    /**
     * 提交日志信息到服务器
     */
    private void pushMsg2Server(){

        LocalThreadPools.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String crash = sbInfo.toString();

            }
        });
    }

    /**
     * 在每次用户打开APP时调用该方法
     * 检测crash文件并上报
     * 切记不要在主线程操作
     */
    public static void pushFile2Server(){

        if (!FileStorageTools.isSdCardMount()) return;

        File parent = new File(CRASH_FILE_PATH);
        if (!parent.exists()) return;

        File[] files = parent.listFiles();
        int size = files == null ? 0 : files.length;
        for (int i=0; i<size; i++) {
            File file = files[i];
            //获取到文件后将文件上传到服务器



            //上传成功后记得将文件删除，避免频繁上传
            file.delete();
        }

    }

}

