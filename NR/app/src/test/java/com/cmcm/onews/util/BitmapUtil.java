package com.cmcm.onews.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.cmcm.onews.NewsL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片缩放
 * Created by yuanshouhui on 2015/12/1
 */
public class BitmapUtil {

    private static String TAG = "BitmapUtil";

    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public  static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;

    }


    /**
     * 相片按相框的比例动态缩放
     * @param bmp 要缩放的图片
     * @param width 模板宽度
     * @param height 模板高度
     * @return
     */
    public static Bitmap upImageSize(Bitmap bmp, int width,int height) {
        if(bmp==null){
            return null;
        }
        // 计算比例
        float scaleX = (float)width / bmp.getWidth();// 宽的比例
        float scaleY = (float)height / bmp.getHeight();// 高的比例
        //新的宽高
        int newW = 0;
        int newH = 0;
        if(scaleX > scaleY){
            newW = (int) (bmp.getWidth() * scaleX);
            newH = (int) (bmp.getHeight() * scaleX);
        }else if(scaleX <= scaleY){
            newW = (int) (bmp.getWidth() * scaleY);
            newH = (int) (bmp.getHeight() * scaleY);
        }
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }


    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = scaleWidth;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

    /**
     * 将图片切成指定大小的方形
     * @param context 上下文
     * @param bitmap 图片
     * @param coverDip 正方形的边长，单位：dp，若不裁剪设为0
     * @param cornerDip 图片的圆角弧度，单位：dp，若不想要圆角设为0
     * @return
     */
    public static Bitmap trimBitmap(Context context, Bitmap bitmap, float coverDip, float cornerDip) {

        if(bitmap != null) {
            if(coverDip > 0.0f) {
                bitmap = getSquareBitmap(context, bitmap, coverDip);
            }

            if(cornerDip > 0.0f) {
                bitmap = getRoundedCornerBitmap(context, bitmap, cornerDip);
            }
        }
        return bitmap;
    }
    /**
     * 将输入的bitmap切成正方形的图片返回
     * @param context 上下文
     * @param bitmap 要切的bitmap
     * @param coverDip 正方形的边长，单位：dp
     * @return
     * @author caisenchuan
     */
    public static Bitmap getSquareBitmap(Context context, Bitmap bitmap, float coverDip) {
        Bitmap ret = bitmap;
        if(bitmap != null) {
            float px = dip2px(context, coverDip);
            int minLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
            float be = (float) px/minLength;
            if (be <= 0) {
               be = 1;
            }
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(be, be);
            // 得到新的图片
            ret = Bitmap.createBitmap(bitmap, 0, 0, minLength, minLength, matrix, true);
        }
        return ret;
    }

    /**
     * 生成圆角图片
     * @param context 上下文
     * @param bitmap 要添加圆角的图
     * @param coverDip 圆角弧度，单位：dp
     * @return
     * @author caisenchuan
     */
    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float coverDip) {
        Bitmap ret = bitmap;

        if(bitmap != null) {
            try {
                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
                float roundPx = dip2px(context, coverDip);     //圆角的弧度

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(Color.BLACK);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawBitmap(bitmap, src, rect, paint);

                ret = output;
            } catch (Exception e) {
                NewsL.push("Exception" + e);
            }
        }

        return ret;
    }

    /**
     * 生成圆角矩形纯色图
     * @param width 矩形宽
     * @param height 矩形高
     * @param coner 圆角弧度
     * @param color 矩形颜色
     * @return
     */
    public static Bitmap createRoundRectBitmap(int width, int height, int coner, int color) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, width, height);
            RectF rectF = new RectF(new Rect(0, 0, width, height));

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, coner, coner, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            Rect src = new Rect(0, 0, width, height);
            canvas.drawBitmap(bitmap, src, rect, paint);

        } catch (Exception e) {
            NewsL.push("Exception" + e);
        }
        return bitmap;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static Drawable createShapeDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        return drawable;
    }

    /**
     * 根据某一边长和原图比例居中裁剪图片
     * @param targetWidthRate  宽高比的宽
     * @param targetHeightRate 宽高比的高
     * @param source 原始图片,可能被回收
     * @param fixWidth 固定宽度，不固定宽度填0
     * @param fixHeight 固定高度，不固定高度填0，fixWidth和fixHeight不能同时为0
     * @return 裁剪后的图片或者原始图片
     */
    public static Bitmap getBitmapByRate(int targetWidthRate, int targetHeightRate, Bitmap source,
            int fixWidth, int fixHeight) {
        if (fixWidth == 0 && fixHeight == 0) {
            return source;
        }
        int oldWidth = source.getWidth();
        int oldHeight = source.getHeight();
        int newWidth, newHeight;
        if (fixWidth != 0) {
            if (oldWidth == fixWidth && oldWidth * targetHeightRate == oldHeight * targetWidthRate) {
                return source;
            }
            newWidth = fixWidth;
            newHeight = fixWidth * targetHeightRate / targetWidthRate;
        } else {
            if (oldHeight == fixHeight
                    && oldWidth * targetHeightRate == oldHeight * targetWidthRate) {
                return source;
            }
            newHeight = fixHeight;
            newWidth = fixHeight * targetWidthRate / targetHeightRate;
        }

        float widthRate = oldWidth * 1.0f / newWidth;
        float heightRate = oldHeight * 1.0f / newHeight;

        float rate = Math.min(widthRate, heightRate);

        int x=(int) ((oldWidth-newWidth*rate) / 2);
        int y=(int) ((oldHeight-newHeight*rate) / 2);
        int srcWidth=(int) (newWidth*rate);
        int srcHeight=(int) (newHeight*rate);

        srcWidth= Math.min(srcWidth, oldWidth - x);//防止四舍五入导致x+srcWidth>oldWidth
        srcHeight= Math.min(srcHeight, oldHeight - y);//防止四舍五入导致y+srcHeight>oldHeight
        
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f/rate, 1.0f/rate);
        try {
            Bitmap bitmap = Bitmap.createBitmap(source, x,
                    y, srcWidth, srcHeight, matrix, true);
            return bitmap;
        } catch (Exception e) {
            NewsL.push("Exception" + e.getLocalizedMessage());
            return source;
        } catch (OutOfMemoryError e) {
            NewsL.push("Exception" + e.getLocalizedMessage());
            return source;
        }
    }

    /**
     * 根据屏幕高度，等比例缩放
     * @param bitmap
     * @param high
     * @return
     */
    public static Bitmap getBitmap(Bitmap bitmap, int high) {
        high = high / 4;

        if (bitmap == null) {
            return null;
        }
        // 计算比例
        float scaleY = (float) high / bitmap.getHeight();// 高的比例
        // 新的宽高
        int newW = 0;
        int newH = 0;
        mNewBitmapWidth =  newW = (int) (bitmap.getWidth() * scaleY);
        mNewBitmapHigh = newH = (int) (bitmap.getHeight() * scaleY);

        return Bitmap.createScaledBitmap(bitmap, newW, newH, true);
    }

    public static Bitmap readBitMap(Context context, int resId){
        Bitmap bitmap = null;
        InputStream is = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        try {
            is = context.getResources().openRawResource(resId);
            bitmap =  BitmapFactory.decodeStream(is, null, opt);
            return bitmap;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, int resId,int high){
       return getBitmap(readBitMap(context, resId), high);
    }

    private static int mNewBitmapWidth;
    private static int mNewBitmapHigh;
    public static int getNewBitMapWidth(){
        return mNewBitmapWidth;
    }

    public static int getNewBitMapHight(){
        return mNewBitmapHigh;
    }


}
