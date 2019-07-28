package com.mango.clib.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.mango.clib.cache.DiskLruCache;
import com.mango.clib.cache.MemoryLruCache;
import com.mango.clib.tools.BitmapTools;
import com.mango.clib.tools.LocalThreadPools;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Description TODO(结合缓存机制异步加载图片)
 * @author cxy
 * @Date 2018/11/14 11:18
 */
public class EasyImageLoader {

    private String TAG = EasyImageLoader.class.getSimpleName();

    private final int LOAD_IMAGE_BITMAP = 1000;
    private final int LOAD_IMAGE_ERROR = 2000;

    private WeakReference<Context> mContext;

    private MemoryLruCache mMemoryCache;
    private DiskLruCache mDiskCache;

    private int errorLoadId = -1;
    private int loadingId = -1;

    private static EasyImageLoader imageLoader;
    private EasyImageLoader(Context context) {
        this.mContext = new WeakReference<>(context);
        mMemoryCache = new MemoryLruCache();
        mDiskCache = new DiskLruCache(context);
    }

    public static EasyImageLoader getInstance(Context context){
        if (imageLoader == null) {
            imageLoader = new EasyImageLoader(context);
        }
        return imageLoader;
    }

    /**
     * 设置加载失败的显示图
     * @param resourceID
     * @return
     */
    public EasyImageLoader setErrorLoadView(int resourceID){
        errorLoadId = resourceID;
        return this;
    }

    /**
     * 设置正在加载中显示图
     * @param loadingId
     * @return
     */
    public EasyImageLoader setLoadingView(int loadingId){
        this.loadingId = loadingId;
        return this;
    }

    /**
     * 设置内存缓存大小
     * @param cacheSize
     * @return
     */
    public EasyImageLoader setMemoryCache(int cacheSize){
        mMemoryCache.setMemoryCache(cacheSize);
        return this;
    }

    /**
     * 设置文件缓存保存最长时间
     * @param days
     * @return
     */
    public EasyImageLoader setFarthestTime(int days){
        mDiskCache.setFarthestTime(days);
        return this;
    }

    /**
     * 设置文件缓存大小
     * @param size
     * @return
     */
    public EasyImageLoader setCacheSize(long size){
        mDiskCache.setCacheSize(size);
        return this;
    }

    /**
     * 设置文件缓存路径
     * @param pathName
     * @return
     */
    public EasyImageLoader setCachePath(String pathName){
        mDiskCache.setCachePath(pathName);
        return this;
    }

    private boolean loadMemoryBitmap(ImageView view, String imgUrl){
        Bitmap bitmap = mMemoryCache.getBitmap(imgUrl);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    private boolean loadDiskBitmap(ImageView view, String imgUrl, int targetWidth, int targetHeight){
        Bitmap bitmap = mDiskCache.getBitmap(imgUrl, targetWidth,targetHeight);
        if (bitmap != null) {
            sendMessage(view,bitmap,imgUrl);
            mMemoryCache.putBitmap(imgUrl,bitmap);
            return true;
        }
        return false;
    }

    public void loadResourceImage(final ImageView view, final int resourceId, final int targetWidth, final int targetHeight){

        String imgUrl = resourceId + "";

        //从内存缓存获取
        if (loadMemoryBitmap(view,imgUrl)) {
            return;
        }

        Bitmap bitmap = BitmapTools.decodeResourceBitmap(mContext.get().getResources(),resourceId,targetWidth,targetHeight);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            mMemoryCache.putBitmap(imgUrl, bitmap);
        } else {
            if (errorLoadId == -1) {
//                view.setImageResource(errorLoadId);
            } else {
                view.setImageResource(errorLoadId);
            }

        }
    }

    /**
     * 加载网络图片
     * @param view
     * @param imageUrl
     * @param targetWidth
     * @param targetHeight
     */
    public void loadImage(final ImageView view, final String imageUrl, final int targetWidth, final int targetHeight) {

        //从内存缓存获取
        if (loadMemoryBitmap(view,imageUrl)) {
            return;
        }

        LocalThreadPools.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //从磁盘读取
                if (loadDiskBitmap(view,imageUrl,targetWidth,targetHeight)) {
                    return ;
                }
                //从网络下载
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    mDiskCache.putFileStream(imageUrl,connection.getInputStream());

                    loadDiskBitmap(view,imageUrl,targetWidth,targetHeight);
                } catch (MalformedURLException e) {
                    if (errorLoadId != -1) {
                        Message message = mHandler.obtainMessage();
                        message.what = LOAD_IMAGE_ERROR;
                        message.obj = view;
                        mHandler.sendMessage(message);
                    }
                    e.printStackTrace();
                } catch (IOException e) {
                    if (errorLoadId != -1) {
                        Message message = mHandler.obtainMessage();
                        message.what = LOAD_IMAGE_ERROR;
                        message.obj = view;
                        mHandler.sendMessage(message);
                    }
                    e.printStackTrace();
                }

            }
        });
    }

    private void sendMessage(ImageView view, Bitmap bitmap, String imageUrl){
        Message message = mHandler.obtainMessage();
        message.what = LOAD_IMAGE_BITMAP;
        message.obj = view;
        Bundle data = new Bundle();
        data.putParcelable("bitmap",bitmap);
        data.putString("url",imageUrl);
        message.setData(data);
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case LOAD_IMAGE_BITMAP:
                    ImageView view = (ImageView) msg.obj;
                    Bundle bundle = msg.getData();
                    Bitmap bitmap = bundle.getParcelable("bitmap");
                    String imageUrl = bundle.getString("url");
                    if (bitmap == null || view.getTag() == null) return;
                    if (TextUtils.equals((String)view.getTag(),imageUrl)) {
                        view.setImageBitmap(bitmap);
                    }
                    break;
                case LOAD_IMAGE_ERROR:
                    ImageView v = (ImageView) msg.obj;
                    v.setBackgroundResource(errorLoadId);
                    break;
                default:
            }
        }
    };

    public void cleanCache(String[] urls){
        mMemoryCache.cleanCache(urls);
    }
}
