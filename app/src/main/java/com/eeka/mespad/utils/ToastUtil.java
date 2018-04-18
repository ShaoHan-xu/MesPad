package com.eeka.mespad.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 可在子线程使用的toast
 */
public class ToastUtil {
    /**
     * 显示toast
     */
    public static void showToast(final Activity ctx, final String msg, final int duration) {
        // 判断是在子线程，还是主线程
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(ctx, msg, duration).show();
        } else {
            // 子线程
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, duration).show();
                }
            });
        }
    }
}
