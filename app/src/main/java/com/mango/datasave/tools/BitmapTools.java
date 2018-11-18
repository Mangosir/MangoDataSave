package com.mango.datasave.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/14 15:22
 */
public class BitmapTools {

    public static int calculateInSampleSize(int srcWidth,int srcHeight, int targetWidth, int targetHeight){

        int inSampleSize = 1;

        if (srcWidth > targetHeight && srcWidth > targetWidth) {
            inSampleSize  = (srcWidth /targetWidth);
        } else if(srcWidth <targetHeight  && srcHeight >targetHeight  ){
            inSampleSize  = (srcHeight /targetHeight);
        }
        return inSampleSize;
    }


    public static Bitmap getBitmapFromFile(String filePath, int targetWidth, int targetHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int inSampleSize = calculateInSampleSize(srcWidth,srcHeight,targetWidth,targetHeight);
        if(inSampleSize  <=0){
            inSampleSize  =1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getByteBitmap(byte[] imgByte, int targetWidth, int targetHeight) {

        Bitmap bitmap ;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imgByte,0,imgByte.length,options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        options.inSampleSize = calculateInSampleSize(srcWidth,srcHeight,targetWidth,targetHeight);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length,options);

        return bitmap;
    }

    public static Bitmap getTargetSizeBitmap(byte[] imgByte, int inSamplesize) {

        InputStream input ;
        Bitmap bitmap ;
        if(inSamplesize  <=0){
            inSamplesize  =1;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSamplesize;
        input = new ByteArrayInputStream(imgByte);
        bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length,options);
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
