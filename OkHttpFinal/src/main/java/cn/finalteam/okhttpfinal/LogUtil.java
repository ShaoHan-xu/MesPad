/*
 * $Id$
 */

package cn.finalteam.okhttpfinal;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录日志到本地文件
 */
public class LogUtil {
    public static final int LOGTYPE_MQTT = 0;
    public static final int LOGTYPE_HTTPREQUEST = 1;
    public static final int LOGTYPE_HTTPRESPONSE = 2;
    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("[HH:mm:ss] ");
    private static String mLogDir;

    public static void init(Context context) {
        File sdcard = context.getExternalFilesDir(null);
        if (sdcard == null || !sdcard.exists()) {
            sdcard.mkdirs();
        }
        File logDir = new File(sdcard, "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
            // do not allow media scan
            try {
                new File(logDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mLogDir = logDir.getAbsolutePath();
    }

    private static String getTodayString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static void writeToFile(int logType, String message) {
        File folder = null;
        if (logType == LOGTYPE_MQTT) {
            folder = new File(mLogDir, "MQTT");
        } else if (logType == LOGTYPE_HTTPREQUEST) {
            folder = new File(mLogDir, "HttpRequest");
        }else if (logType == LOGTYPE_HTTPRESPONSE){
            folder = new File(mLogDir, "HttpResponse");
        }
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File f = new File(folder.getAbsolutePath() + File.separator + getTodayString() + ".txt");
        String content = TIMESTAMP_FMT.format(new Date()) + message + "\n";
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "GBK"));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
