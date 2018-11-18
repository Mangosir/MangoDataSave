package com.mango.datasave.net.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description TODO(内存缓存)
 * @author cxy
 * @Date 2018/11/13 9:48
 */
public class MemoryLruCache {

    private String TAG = MemoryLruCache.class.getSimpleName();

    private LruCache<String,Bitmap> mMemoryCache;

    private int memoryCache;

    public MemoryLruCache(){
        //虚拟机能获得的最大内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        //内存缓存所使用的内存
        memoryCache = (int) (maxMemory / 8);
        mMemoryCache = new LruCache<String,Bitmap>(memoryCache){

            //需要重写两个方法

            //计算一张图片所在内存
            @Override
            protected int sizeOf(String key, Bitmap value) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
                    int size = value.getAllocationByteCount();
                    Log.e(TAG,"sizeOf size="+size);
                    return size;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
                    return value.getByteCount();
                }
                // 在低版本中使用 Bitmap所占用的内存空间数等于Bitmap的每一行所占用的空间数乘以Bitmap的行数
                return value.getRowBytes() * value.getHeight();
            }

            //回收内存
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                Log.e(TAG,"entryRemoved");
                if (oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };

    }

    public void setMemoryCache(int cacheSize){
        memoryCache = cacheSize;
    }

    /**
     * 从内存中取出图片
     * @param key 通常是图片下载地址
     * @return
     */
    public Bitmap getBitmap(String key){
        if (TextUtils.isEmpty(key)) return null;
        return mMemoryCache.get(key);
    }

    /**
     * 将bitmap保存到内存
     * @param key
     * @param bitmap
     */
    public void putBitmap(String key, Bitmap bitmap){
        mMemoryCache.put(key,bitmap);
    }

    /**
     * 通过反射剔除缓存中的bitmap
     * 回收bitmap内存
     * @param urls 需要清除的value对应的key 可以为null
     */
    public void cleanCache(String[] urls){

        try {
            Class classType = Class.forName("android.util.LruCache");

            Field field = classType.getDeclaredField("map");
            field.setAccessible(true);
            LinkedHashMap<String,Bitmap> map = (LinkedHashMap<String, Bitmap>) field.get(mMemoryCache);
            if (map == null) return;

            Iterator<Map.Entry<String,Bitmap>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {

                Map.Entry<String,Bitmap> entry = iterator.next();
                Bitmap bit = entry.getValue();

                if (urls != null && urls.length > 0) {
                    for (int i=0; i<urls.length; i++) {
                        if (TextUtils.equals(entry.getKey(),urls[i])) {
                            if (bit != null && !bit.isRecycled()) {
                                bit.recycle();
                            }
                            iterator.remove();
                            break;
                        }
                    }
                } else {
                    if (bit != null && !bit.isRecycled()) {
                        bit.recycle();
                    }
                    iterator.remove();
                }
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
