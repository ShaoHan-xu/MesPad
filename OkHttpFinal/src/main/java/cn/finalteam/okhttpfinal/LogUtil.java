/*
 * $Id$
 */

package cn.finalteam.okhttpfinal;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 记录日志到本地文件
 */
public class LogUtil {
    public static final int LOGTYPE_MQTT = 0;//mqtt推送相关的记录目录
    public static final int LOGTYPE_HTTPREQUEST = 1;//http网络请求相关的记录目录
    public static final int LOGTYPE_HTTPRESPONSE = 2;//http网络请求返回数据记录目录，开启debug模式才会记录
    public static final int LOGTYPE_HTTPFAIL = 3;//http请求失败时返回的数据
    public static final int LOGTYPE_MQTT_STATUS = 4;//mqtt状态记录，掉线/重连
    public static final int LOGTYPE_EXCEPTION = 5;//各种异常记录

    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("[HH:mm:ss] ", Locale.CHINA);
    private static String mLogDir;

    public static void init(Context context) {
        File sdcard = context.getExternalFilesDir(null);
        if (sdcard == null || !sdcard.exists()) {
            sdcard.mkdirs();
        }
        File logDir = new File(sdcard, "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        mLogDir = logDir.getAbsolutePath();
    }

    private static String getTodayString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return df.format(new Date());
    }

    public static void writeToFile(int logType, String message) {
        File folder = null;
        if (logType == LOGTYPE_MQTT) {
            folder = new File(mLogDir, "MQTT");
        } else if (logType == LOGTYPE_HTTPREQUEST) {
            folder = new File(mLogDir, "HttpRequest");
        } else if (logType == LOGTYPE_HTTPRESPONSE) {
            folder = new File(mLogDir, "HttpResponse");
        } else if (logType == LOGTYPE_HTTPFAIL) {
            folder = new File(mLogDir, "HttpFail");
        } else if (logType == LOGTYPE_MQTT_STATUS) {
            folder = new File(mLogDir, "mqttStatus");
        } else if (logType == LOGTYPE_EXCEPTION) {
            folder = new File(mLogDir, "exception");
        }
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        File f = new File(folder.getAbsolutePath() + File.separator + getTodayString() + ".txt");
        String content = TIMESTAMP_FMT.format(new Date()) + message + "\n\n";
        try {
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除过期log文件
     */
    public static void deletePastLogFile() {
        File folder = new File(mLogDir);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] fs = file.listFiles();
                for (int i = fs.length - 1; i >= 0; i--) {
                    File f = fs[i];
                    if (f.isFile()) {
                        long l = f.lastModified() / 1000;
                        long curSeconds = System.currentTimeMillis() / 1000;
                        if (curSeconds - l > getWeekSeconds()) {
                            f.delete();
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取一个月的秒数
     */
    private static long getWeekSeconds() {
        return 30 * 24 * 60 * 60;
    }

}
