package com.eeka.mespad.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class BitmapUtil {

    /**
     * 清空图片的内存
     */
    public static void clearImgMemory(ImageView V) {
        Drawable d = V.getDrawable();
        if (d != null && d instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) d).getBitmap();
            bmp.recycle();
        }
        V.setImageBitmap(null);
        if (d != null) {
            d.setCallback(null);
        }
    }
}
