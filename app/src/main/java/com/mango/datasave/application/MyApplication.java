package com.mango.datasave.application;

import android.app.Application;
import android.content.Context;

import com.mango.clib.tools.TimeTools;


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
    }
}
