package com.mango.datasave.sql;

import android.database.Cursor;

import com.mango.clib.sqlite.MangoDao;

/**
 * Author: mango
 * Time: 2019/8/18 10:38
 * Version:
 * Desc: TODO()
 */
public class MyUserDao extends MangoDao {

    public long count(){
        String sql = "select count(1) from " + mTableName;
        Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        //获得某一列的长度
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }
}
