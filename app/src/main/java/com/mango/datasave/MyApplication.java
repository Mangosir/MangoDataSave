package com.mango.datasave;

import android.app.Application;
import android.util.Log;

/**
 * Created by lenovo on 2018/11/7.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("MyApplication","onCreate");
    }
}
