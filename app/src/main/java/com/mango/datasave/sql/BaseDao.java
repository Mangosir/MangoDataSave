package com.mango.datasave.sql;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/5 18:12
 */
public class BaseDao {

    protected WeakReference<DataBaseContext> mContext;
    protected SQLiteDBHelper mHelper;

    public BaseDao(Context context) {
        mContext = new WeakReference<>(new DataBaseContext(context));
        mHelper = new SQLiteDBHelper(mContext.get());
    }
}
