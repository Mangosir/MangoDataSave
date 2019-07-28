package com.mango.clib.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO(SharedPreferences使用封装)
 * @author cxy
 * @Date 2018/10/30 10:07
 */
public class SharedPTools {

    private String TAG = SharedPTools.class.getSimpleName();

    private static SharedPTools instance;
    //避免内存泄漏
    private WeakReference<Context> mContext ;
    /**
     * 防止直接使用时无限new Editor 消耗内存 避免内存泄漏
     * 退出应用时调用 {@link #clearCache()}
     */
    private Map<SharedPreferences,WeakReference<SharedPreferences.Editor>> mEditorCache = new HashMap<>();

    public SharedPTools(Context context) {
        mContext = new WeakReference<>(context);
    }
    public static SharedPTools getInstance(Context context){
        if (instance == null) {
            instance = new SharedPTools(context);
        }
        return instance;
    }

    private SharedPreferences.Editor editor(SharedPreferences sp){
        WeakReference<SharedPreferences.Editor> weak = mEditorCache.get(sp);
        SharedPreferences.Editor edit ;
        if (weak == null) {
            edit = sp.edit();
            weak = new WeakReference<>(edit);
            mEditorCache.put(sp,weak);
        }
        return weak.get();
    }

    private SharedPreferences getSharedP(String key){
        return mContext.get().getSharedPreferences(key, Activity.MODE_PRIVATE);
    }


    /**==========================================================华丽丽分割线======================================================**/

    /**
     * 从SharedPreferences文件中获取值
     * @param key xml文件名
     * @param valuekey 与值对应的key
     * @param defaultObject 默认值
     * @return
     */
    public Object getValue(String key, String valuekey, Object defaultObject){
        if (defaultObject instanceof String)
            return getSharedP(key).getString(valuekey, (String) defaultObject);
        else if (defaultObject instanceof Boolean)
            return getSharedP(key).getBoolean(valuekey, (Boolean) defaultObject);
        else if (defaultObject instanceof Integer)
            return getSharedP(key).getInt(valuekey, (Integer) defaultObject);
        else if (defaultObject instanceof Float)
            return getSharedP(key).getFloat(valuekey, (Float) defaultObject);
        else if (defaultObject instanceof Long)
            return getSharedP(key).getLong(valuekey, (Long) defaultObject);
        else
            return null;
    }

    /**
     * 往SharedPreferences文件中添加值
     * @param key xml文件名
     * @param valuekey 与值对应的key
     * @param value 要添加的值
     * @return
     */
    public SharedPTools putValue(String key, String valuekey, Object value){
        if (value instanceof String)
            editor(getSharedP(key)).putString(valuekey, (String) value);
        else if (value instanceof Boolean)
            editor(getSharedP(key)).putBoolean(valuekey, (Boolean) value);
        else if (value instanceof Integer)
            editor(getSharedP(key)).putInt(valuekey, (Integer) value);
        else if (value instanceof Float)
            editor(getSharedP(key)).putFloat(valuekey, (Float) value);
        else if (value instanceof Long)
            editor(getSharedP(key)).putLong(valuekey, (Long) value);
        return this;
    }

    public SharedPTools remove(String key, String valuekey){
        editor(getSharedP(key)).remove(valuekey);
        return this;
    }

    public SharedPTools clear(String key){
        editor(getSharedP(key)).clear();
        return this;
    }

    public boolean contains(String key, String valuekey){
        return getSharedP(key).contains(valuekey);
    }

    public boolean commit(String key){
        return editor(getSharedP(key)).commit();
    }

    public void apply(String key){
        editor(getSharedP(key)).apply();
    }

    /**
     * 清空内存
     */
    public void clearCache(){
        for(WeakReference<SharedPreferences.Editor> weak : mEditorCache.values()){
            weak.clear();
        }
        mEditorCache.clear();
    }

}
