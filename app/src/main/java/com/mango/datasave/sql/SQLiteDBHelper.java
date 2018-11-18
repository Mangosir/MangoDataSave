package com.mango.datasave.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @Description TODO(数据库及表创建)
 * @author cxy
 * @Date 2018/11/5 11:07
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    private String TAG = SQLiteDBHelper.class.getSimpleName();

    //类还没有实例化，只能是static修饰才能用来做参数
    public static final String DATABASE_NAME = "mango";
    public static final String TABLE_USER = "user";
    private static final int DATABASE_VERSION = 1;

    /**
     * 重载构造方法
     * @param context
     */
    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param name 数据库名
     * @param factory 游标工厂，默认为null,即使用默认工厂
     * @param version 数据库版本号
     */
    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 数据库第一次创建时被调用
     * 用来创建表
     * 或者一些数据初始化操作
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG,"onCreate");
        String dataBaseSql = "create table if not exists " + TABLE_USER +
                "(uid integer primary key autoincrement,name varchar(20),sex varchar,role varchar)";
        db.execSQL(dataBaseSql);
    }

    /**
     * 升级软件时更新数据库表结构
     * 通过比对版本号
     * 一般做删除数据表，并建立新的数据表操作
     * @param db
     * @param oldVersion 上一次版本号
     * @param newVersion 最新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 打开一个只读数据库
     * 如果数据库不存在，Android系统会自动生成一个数据库，接着调用onCreate()方法
     * @return 获取一个用于操作数据库的SQLiteDatabase实例
     */
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    /**
     * 创建或打开一个读写数据库
     * 如果数据库不存在，Android系统会自动生成一个数据库，接着调用onCreate()方法
     * @return 获取一个用于操作数据库的SQLiteDatabase实例
     */
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}
