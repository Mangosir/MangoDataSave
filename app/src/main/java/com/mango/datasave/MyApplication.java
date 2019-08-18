package com.mango.datasave;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.mango.datasave.dao.DaoMaster;
import com.mango.datasave.dao.DaoSession;

/**
 * Author: mango
 * Time: 2019/8/15 17:04
 * Version:
 * Desc: TODO()
 */
public class MyApplication extends Application {

    public static final String DB_NAME = "mango.db";

    private static DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
    }



    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
}
