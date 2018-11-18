package com.mango.datasave.sql;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.mango.datasave.tools.FileStorageTools;

import java.io.File;

/**
 * @Description TODO(加载SD卡上的db)
 * @author cxy
 * @Date 2018/11/5 17:04
 */
public class DataBaseContext extends ContextWrapper {

    public DataBaseContext(Context base) {
        super(base);
    }

    /**
     * 重写这个方法，加载SD卡的数据库
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @return
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        String dbPath = FileStorageTools.getInstance(this).makeFilePath("/Android/data/"+getPackageName()+"/database/"+SQLiteDBHelper.DATABASE_NAME+".db");
        File file = new File(dbPath);
        if (!file.exists()) throw new NullPointerException("DB File is not exists");
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
        return database;
    }
}
