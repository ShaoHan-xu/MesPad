package com.eeka.mespad.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.view.dialog.ErrorDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * 系统工具类 Created by xsh on 2016/8/12.
 */
public class SystemUtils {
    /**
     * Get the density of device screen.
     *
     * @param context The Context used to get DisplayMetrics.
     * @return Density.
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    @SuppressLint("NewApi")
    public static int getScreenWidth(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return activity.getWindowManager().getDefaultDisplay().getWidth();
        } else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            return size.x;
        }
    }

    @SuppressLint("NewApi")
    public static int getScreenHeight(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return activity.getWindowManager().getDefaultDisplay().getHeight();
        } else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            return size.y;
        }
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * Get the total CPU number.
     *
     * @return total CPU number
     */
    public static int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            Logger.d("CPU Count: " + files.length);
            return files.length;
        } catch (Exception e) {
            Logger.e("CPU Count: Failed.");
            return 1;
        }
    }

    /**
     * Get the max CPU freq(KHZ).
     *
     * @return max CPU freq
     */
    public static int getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException e) {
            Logger.e(e);
        }
        Logger.d("Max CPU Freq: " + result.trim());
        return Integer.valueOf(result.trim());
    }

    /**
     * Get the min CPU freq(KHZ).
     *
     * @return min CPU freq
     */
    public static int getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException e) {
            Logger.e(e);
        }
        Logger.d("Min CPU Freq: " + result.trim());
        return Integer.valueOf(result.trim());
    }

    /**
     * Get the mobile total memory.(KB)
     *
     * @return total memory
     */
    public static int getTotalMemory() {
        String memFile = "/proc/meminfo";
        int totalMemory = 0;

        try {
            FileReader localFileReader = new FileReader(memFile);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

            String totalMemoryString = localBufferedReader.readLine();
            Logger.d(totalMemoryString);
            String[] memArray = totalMemoryString.split(" +");

            totalMemory = Integer.valueOf(memArray[1]).intValue();
            localBufferedReader.close();
        } catch (IOException e) {
            Logger.e(e);
        }
        return totalMemory;
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            Logger.e(e);
            return 0;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            Logger.e(e);
            return "";
        }
    }

    // @SuppressWarnings("deprecation")

    @SuppressLint("NewApi")
    public static void readSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = 0;
            long blockCount = 0;
            long availCount = 0;
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            // {
            // blockSize = sf.getBlockSizeLong();
            // blockCount = sf.getBlockCountLong();
            // availCount = sf.getAvailableBlocksLong();
            // } else {
            blockSize = sf.getBlockSize();
            blockCount = sf.getBlockCount();
            availCount = sf.getAvailableBlocks();
            // }

            Logger.d("", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
            Logger.d("", "可用的block数目：:" + availCount + ",剩余空间:" + availCount * blockSize / 1024 + "KB");
        }
    }

    // @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void readSystem() {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());

        long blockSize = 0;
        long blockCount = 0;
        long availCount = 0;
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        // blockSize = sf.getBlockSizeLong();
        // blockCount = sf.getBlockCountLong();
        // availCount = sf.getAvailableBlocksLong();
        // } else {
        blockSize = sf.getBlockSize();
        blockCount = sf.getBlockCount();
        availCount = sf.getAvailableBlocks();
        // }

        Logger.d("", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
        Logger.d("", "可用的block数目：:" + availCount + ",可用大小:" + availCount * blockSize / 1024 + "KB");
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.ANDROID_ID);
    }

    /**
     * 收起键盘
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 安装APK文件
     */
    public static void installApk(Context context, String path) {
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    /**
     * 开启视频播放界面
     *
     * @param context
     * @param videoUrl
     */
    public static void startVideoActivity(final Context context, String videoUrl) {
        String videoPath = null;
        try {
            if (!TextUtils.isEmpty(videoUrl)) {
                int indexOf = videoUrl.lastIndexOf("/");
                if (indexOf != -1) {
                    String host = videoUrl.substring(0, indexOf + 1);
                    String name = videoUrl.substring(indexOf + 1, videoUrl.length());
                    videoPath = host + URLEncoder.encode(name, "utf-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(videoPath)) {
            final String finalVideoPath = videoPath;
            //先判断视频文件是否存在
            HttpRequest.post(videoUrl, new BaseHttpRequestCallback() {
                @Override
                public void onResponse(Response httpResponse, String response, Headers headers) {
                    super.onResponse(httpResponse, response, headers);
                    if (httpResponse.code() == 404) {
                        ErrorDialog.showAlert(context, "视频文件不存在");
                    } else {
                        //自定义播放器，可缓存视频到本地
                        // context.startActivity(VideoPlayerActivity.getIntent(context, videoPath));
                        //系统自带视频播放，无缓存
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(finalVideoPath), "video/mp4");
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            ErrorDialog.showAlert(context, "当前工序无视频");
        }
    }
}
