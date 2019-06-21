package com.eeka.mespad.utils;

import com.eeka.mespad.manager.Logger;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lenovo on 2017/7/25.
 */

public class DateUtil {

    public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    public static long getNetTimeMillis() {
        URL url;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            return uc.getDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取网络时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getNetTime() {
        URL url;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
            Logger.d("当前网络时间为: " + format);
            return format;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 毫秒转换为时间
     *
     * @param millis     传递过来的时间毫秒
     * @param dateFormat 返回的时间格式
     * @return
     */
    public static String millisToDate(long millis, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millis);
        return sdf.format(date);
    }

    /**
     * 日期转换为毫秒数
     *
     * @param date       要转换的日期
     * @param dateFormat 日期的格式
     * @return 转换后的毫秒数
     */
    public static long dateToMillis(String date, String dateFormat) {
        long millis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            millis = sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * 获取星期几
     */
    public static String getWeek() {
        return getWeek(0);
    }

    /**
     * 获取星期几
     */
    public static String getWeek(long millis) {
        Calendar cal = Calendar.getInstance();
        if (millis != 0) {
            cal.setTimeInMillis(millis);
        }
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    public static String getCurDate() {
        return millisToDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }

}
