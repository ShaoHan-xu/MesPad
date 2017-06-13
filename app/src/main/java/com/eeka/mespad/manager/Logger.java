package com.eeka.mespad.manager;

import android.util.Log;

/**
 * 日志输出管理类
 * Created by xsh on 2016/8/12.
 */
public class Logger {

    private static final String TAG = "MesPad";
    private static final boolean isDebug = true;

    /**
     * Is debuggable.
     *
     * @return boolean debuggable
     */
    private static boolean isDebuggable() {
        return isDebug; //BuildConfig.DEBUG;
    }

    /**
     * Get the default tag.
     *
     * @return String default tag
     */
    private static String getTag() {
        return TAG;
    }

    public static void e(String errorMsg) {
        if (isDebuggable()) {
            e(getTag(), errorMsg);
        }
    }

    public static void e(String tag, String errorMsg) {
        if (isDebuggable()) {
            StackTraceElement stackTraceElement;
            if (getTag().equals(tag)) {
                stackTraceElement = Thread.currentThread().getStackTrace()[4];
            } else {
                stackTraceElement = Thread.currentThread().getStackTrace()[3];
            }
            Log.e(tag, stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + "  " + errorMsg);
        }
    }

    public static void e(Exception exception) {
        if (isDebuggable()) {
            exception.printStackTrace();
        }
    }

    public static void e(String tag, Exception exception) {
        if (isDebuggable()) {
            exception.printStackTrace();
        }
    }

    public static void i(String errorMsg) {
        if (isDebuggable()) {
            i(getTag(), errorMsg);
        }
    }

    public static void i(String tag, String errorMsg) {
        if (isDebuggable()) {
            StackTraceElement stackTraceElement;
            if (getTag().equals(tag)) {
                stackTraceElement = Thread.currentThread().getStackTrace()[4];
            } else {
                stackTraceElement = Thread.currentThread().getStackTrace()[3];
            }
            Log.i(tag, stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + "  " + errorMsg);
        }
    }

    public static void w(String errorMsg) {
        if (isDebuggable()) {
            w(getTag(), errorMsg);
        }
    }

    public static void w(String tag, String errorMsg) {
        if (isDebuggable()) {
            StackTraceElement stackTraceElement;
            if (getTag().equals(tag)) {
                stackTraceElement = Thread.currentThread().getStackTrace()[4];
            } else {
                stackTraceElement = Thread.currentThread().getStackTrace()[3];
            }
            Log.w(tag, stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + "  " + errorMsg);
        }
    }

    public static void d(String errorMsg) {
        if (isDebuggable()) {
            d(getTag(), errorMsg);
        }
    }

    public static void d(String tag, String errorMsg) {
        if (isDebuggable()) {
            StackTraceElement stackTraceElement;
            if (getTag().equals(tag)) {
                stackTraceElement = Thread.currentThread().getStackTrace()[4];
            } else {
                stackTraceElement = Thread.currentThread().getStackTrace()[3];
            }
            Log.d(tag, stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + " " + errorMsg);
        }
    }

    public static void v(String errorMsg) {
        if (isDebuggable()) {
            v(getTag(), errorMsg);
        }
    }

    public static void v(String tag, String errorMsg) {
        if (isDebuggable()) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.v(tag, stackTraceElement.getFileName()
                    + " "
                    + stackTraceElement.getLineNumber() + "  " + errorMsg);
        }
    }
}
