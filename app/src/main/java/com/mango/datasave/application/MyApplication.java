package com.mango.datasave.application;

import android.app.Application;
import android.content.Context;

import com.mango.clib.sqlite.MangoDaoFactory;
import com.mango.clib.tools.FileStorageTools;
import com.mango.clib.tools.TimeTools;

import java.io.File;


/**
 * Created by lenovo on 2018/11/7.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        TimeTools.saveBeginTime();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler handler = new ExceptionHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
        initMangoDao();
    }

    private void initMangoDao() {
        /*内部存储*/
//        File databasePath = getDatabasePath("mango.db");
        /*外部存储私有目录*/
        File databasePath = FileStorageTools.getInstance(this).getExternalStoragePrivateCache();
        File dbFile = new File(databasePath,"mango.db");
        MangoDaoFactory.getDefault().initDataBase(dbFile);
    }
}
