package com.mango.datasave;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mango.datasave.tools.TimeTools;

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

        Log.e("MyApplication","onCreate");
    }
}
