package com.mango.datasave.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Description TODO(Bitmap辅助类)
 * @author cxy
 * @Date 2018/11/14 15:22
 */
public class BitmapTools {

    private static String TAG= BitmapTools.class.getSimpleName();

    /**
     * 根据指定压缩比解码文件图片
     * @param filePath 图片路径
     * @param inSampleSize 压缩比
     * @return
     */
    public static Bitmap decodeFileBitmap(String filePath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据指定宽高计算压缩比解码文件图片
     * @param filePath 图片路径
     * @param targetWidth 指定宽
     * @param targetHeight 指定高
     * @return
     */
    public static Bitmap decodeFileBitmap(String filePath, int targetWidth, int targetHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int inSampleSize = calSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据指定压缩比解码资源图片
     * @param res
     * @param resId
     * @param inSampleSize
     * @return
     */
    public static Bitmap decodeResourceBitmap(Resources res, int resId, int inSampleSize) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 根据指定宽高计算压缩比解码资源图片
     * @param res
     * @param resId
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap decodeResourceBitmap(Resources res, int resId,int targetWidth, int targetHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calSampleSize(options, targetWidth, targetHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 根据指定压缩比解码流图片
     * @param is
     * @param inSampleSize
     * @return
     */
    public static Bitmap decodeStreamBitmap(InputStream is, int inSampleSize){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is,null,options);
    }

    /**
     * 根据指定宽高解码流图片
     * @param is
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap decodeStreamBitmap(InputStream is, int targetWidth, int targetHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        options.inSampleSize = calSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is,null,options);
    }

    /**
     * 根据指定宽高解码字节数组图片
     * @param data
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap decodeByteBitmap(byte[] data, int targetWidth, int targetHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data,0,data.length);
        options.inSampleSize = calSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }


    /**
     * 据指定宽高计算压缩比
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static int calSampleSize(BitmapFactory.Options options,
                                    int targetWidth, int targetHeight) {
        int rawWidth = options.outWidth;
        int rawHeight = options.outHeight;
        int inSampleSize = 1;
        //保证宽高中最小的要大于指定值，避免被拉伸
        if (rawWidth > targetWidth || rawHeight > targetHeight) {
            float ratioHeight = (float) rawHeight / targetHeight;
            float ratioWidth = (float) rawWidth / targetWidth;
            inSampleSize = (int) Math.min(ratioWidth, ratioHeight);
        }
        return inSampleSize;
    }

    /**
     *
     * @param bitmap
     * @return
     */
    public Bitmap qualityCompression(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,bos);
        byte[] buff = bos.toByteArray();
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(buff,0,buff.length);
        return bitmap1;
    }

    /**==================================================华丽丽的分割线======================================================**/

    /**
     * 修改像素颜色格式压缩图片
     * @param res
     * @param resId
     * @param inPreferredConfig
     * @return
     */
    public static Bitmap decodeResourceBitmap(Resources res, int resId,Bitmap.Config inPreferredConfig){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = inPreferredConfig;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }

    /**
     * 选择变换
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 偏移效果
     * @param origin 原图
     * @return 偏移后的bitmap
     */
    public static Bitmap skewBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


}
