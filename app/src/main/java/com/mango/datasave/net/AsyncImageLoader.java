package com.mango.datasave.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.mango.datasave.cache.DiskLruCache;
import com.mango.datasave.cache.MemoryLruCache;
import com.mango.datasave.tools.LocalThreadPools;

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
public class AsyncImageLoader {

    private String TAG = AsyncImageLoader.class.getSimpleName();

    private final int LOAD_IMAGE_BITMAP = 1;

    private WeakReference<Context> mContext;

    private MemoryLruCache mMemoryCache;
    private DiskLruCache mDiskCache;

    private static AsyncImageLoader imageLoader;
    private AsyncImageLoader(Context context) {
        this.mContext = new WeakReference<>(context);
        mMemoryCache = new MemoryLruCache();
        mDiskCache = new DiskLruCache(context);
    }

    public static AsyncImageLoader getInstance(Context context){
        if (imageLoader == null) {
            imageLoader = new AsyncImageLoader(context);
        }
        return imageLoader;
    }

    public boolean loadMemoryBitmap(ImageView view, String imgUrl){
        Bitmap bitmap = mMemoryCache.getBitmap(imgUrl);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    public boolean loadDiskBitmap(ImageView view, String imgUrl, int targetWidth, int targetHeight){
        Bitmap bitmap = mDiskCache.getBitmap(imgUrl, targetWidth,targetHeight);
        if (bitmap != null) {
            sendMessage(view,bitmap,imgUrl);
            mMemoryCache.putBitmap(imgUrl,bitmap);
            return true;
        }
        return false;
    }

    /**
     * 加载图片
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

        LocalThreadPools.THREAD_POOL_EXECUTOR.execute(new Runnable() {
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
                    e.printStackTrace();
                } catch (IOException e) {
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
            }
        }
    };
}
