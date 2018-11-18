package com.mango.datasave.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.mango.datasave.tools.BitmapTools;
import com.mango.datasave.tools.FileStorageTools;
import com.mango.datasave.tools.LocalThreadPools;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO(磁盘缓存 可缓存文件)
 * @author cxy
 * @Date 2018/11/14 11:18
 */
public class DiskLruCache {

    private String TAG = DiskLruCache.class.getSimpleName();

    private WeakReference<Context> mContext;

    //缓存目录
    private File cachePath;
    //默认缓存空间大小 100M
    private long FILE_CACHE_SIZE = 1024 * 1024 * 100;
    //文件保留时间 默认保存一个月内的缓存文件
    private int FARTHEST_TIME_FROM_NOW = 30 * 1;

    /**
     * 存放文件路径和时间信息
     * 有两种清除睡眠文件方法：
     *                      可根据时间删除文件
     *                      也可根据访问顺序删除文件
     */
    private LinkedHashMap<String, Long> map;


    /**
     * 尽量在应用启动的时候构造实例
     * 在子线程操作
     * @param context
     */
    public DiskLruCache(Context context) {
        this.mContext = new WeakReference<>(context);
        cachePath = FileStorageTools.getInstance(mContext.get()).getExternalStoragePrivateCache();
        map = new LinkedHashMap<>(0, 0.75f, true);
        getFileMsg();
        reviseCacheFile();
    }


    /**
     * 设置保留多少天内的缓存文件
     * @param days 不能低于7天
     */
    public void setFarthestTime(int days){
        if (days < 7 ) {
            return;
        }
        FARTHEST_TIME_FROM_NOW = days;
    }

    /**
     * 设置文件缓存大小
     * @param size 不能低于10M
     */
    public void setCacheSize(long size){
        if (size < 1024 * 1024 * 10) {
            return;
        }
        FILE_CACHE_SIZE = size;
    }

    /**
     * 设置私有缓存目录
     * 尽量在子线程执行
     * @param pathName 次级目录名称 比如
     *                 /imagecache
     *                 /httpcache
     *                 允许为null
     */
    public void setCachePath(String pathName){
        if (TextUtils.isEmpty(pathName)) return ;
        cachePath = null;
        cachePath = new File(cachePath.getAbsolutePath()+pathName);
        cachePath.mkdirs();
        getFileMsg();
    }

    public void putFileStream(String url, InputStream is){
        synchronized (this) {
            File f = FileStorageTools.getInstance(mContext.get()).putStreamToExternalStorage(cachePath,encryptUrl(url),is);
            map.put(f.getAbsolutePath(),f.lastModified());
            if (cachePath.length() > FILE_CACHE_SIZE) {
                reviseCacheFile();
            }
        }
    }

    public File getFile(String url){
        File f;
        synchronized (this) {
            Object[] objects = FileStorageTools.getInstance(mContext.get()).getDataFromExternalStorage(cachePath.getAbsolutePath()+ File.separator+encryptUrl(url),true);
            if (objects[1] == null) return null;
            f = (File) objects[1];
            map.put(f.getAbsolutePath(),f.lastModified());
        }
        return f;
    }

    public Bitmap getBitmap(String url, int targetWidth, int targetHeight){
        byte[] data;
        synchronized (this) {
            Object[] objects = FileStorageTools.getInstance(mContext.get()).getDataFromExternalStorage(cachePath.getAbsolutePath()+ File.separator+encryptUrl(url),true);
            if (objects[0] == null) return null;

            data = (byte[]) objects[0];
            File f = (File) objects[1];
            map.put(f.getAbsolutePath(),f.lastModified());
        }
        return BitmapTools.getByteBitmap(data,targetWidth,targetHeight);
    }


    /**
     * 清除指定目录缓存文件
     * @param path 缓存目录
     *             如果是null，默认为cachePath.getAbsolutePath()
     */
    public void cleanAllCache(final String path){
        LocalThreadPools.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(path)) {
                    FileStorageTools.getInstance(mContext.get()).delFile(cachePath.getAbsolutePath());
                } else {
                    FileStorageTools.getInstance(mContext.get()).delFile(path);
                }
            }
        });
    }

    /**
     * 修正缓存目录文件
     * 超出预设缓存大小 就删除那些早期文件
     * 早于文件保留时间跨度 删除
     */
    public void reviseCacheFile(){

        //获取时间跨度
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR,FARTHEST_TIME_FROM_NOW);
        long farthestDate = calendar.getTime().getTime();

        Iterator<Map.Entry<String, Long>> entrys = map.entrySet().iterator();
        while (entrys.hasNext()) {
            Map.Entry<String, Long> entry = entrys.next();
            String key = entry.getKey();
            long value = entry.getValue();

            /**
             * 先判断缓存空间是否超出预设值，这是最重要的
             * 因为map里面的顺序是按时间先后存放的，最先迭代出来的总是更新时间最久远的
             */
            if (cachePath.length() > FILE_CACHE_SIZE) {
                File file = new File(key);
                file.delete();
                entrys.remove();
                continue;
            }

            /**
             * 再判断文件更新时间是否早于预设时间
             * 如果早于预设时间 删除
             */
            if (value < farthestDate) {
                File file = new File(key);
                file.delete();
                entrys.remove();
                continue;
            }
        }

    }

    private void getFileMsg(){
        FileStorageTools.getInstance(mContext.get()).clearFlist();
        List<File> files = FileStorageTools.getInstance(mContext.get()).listFile(cachePath.getAbsolutePath());
        if (files == null) return;

        File[] fi = FileStorageTools.getInstance(mContext.get()).sortFile(files,true);
        int length = fi == null ? 0 : fi.length;
        for (int i=0; i<length; i++){
            File f = fi[i];
            map.put(f.getAbsolutePath(),f.lastModified());
        }
    }

    /**
     * 将url使用md5加密作为文件名
     * md5加密是不可逆加密，防止资源盗用
     * @param url
     * @return
     */
    private String encryptUrl(String url){

        if (TextUtils.isEmpty(url)) return null;

        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] msg = md.digest(url.getBytes());
            for (byte b:msg) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
