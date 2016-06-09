package com.cmcm.feedback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jason.Su on 2016/3/30.
 * com.cmcm.feedback
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class ImageUtil {
    public static Bitmap getResizedBitmap(File file, int limitWith) {
        try {
            if (file != null && file.exists()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                int sample = 1;
                opt.inJustDecodeBounds = true;
                opt.inSampleSize = sample;
                BitmapFactory.decodeFile(file.getAbsolutePath(),opt);
                while ((opt.outWidth < opt.outHeight ? opt.outWidth : opt.outHeight) > limitWith) {
                    opt.inSampleSize = ++sample;
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                }
                opt.inSampleSize = sample;
                opt.inJustDecodeBounds = false;
                Bitmap map = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Bitmap getResizedBitmap(InputStream openInputStream, int limitWith) {
        byte[] bytes = getByteArrays(openInputStream);
        return getResizedBitmap(bytes,limitWith);
    }
    private  static byte[] getByteArrays(InputStream openInputStream) {
        ByteArrayOutputStream out;
        try {
            out = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int read;
            while ((read = openInputStream.read(bytes, 0, 1024)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return out.toByteArray();
    }

    public static Bitmap getResizedBitmap(byte[] bytes, int limitWith) {
        try {
            if (bytes != null) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                int sample = 1;
                opt.inJustDecodeBounds = true;
                opt.inSampleSize = sample;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
                while ((opt.outWidth < opt.outHeight ? opt.outWidth : opt.outHeight) > limitWith) {
                    opt.inSampleSize = ++sample;
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
                }
                opt.inSampleSize = sample;
                opt.inJustDecodeBounds = false;
                Bitmap map = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };
}
