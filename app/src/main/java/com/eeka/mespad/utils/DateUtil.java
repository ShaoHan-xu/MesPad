package com.eeka.mespad.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo on 2017/7/25.
 */

public class DateUtil {

    /**
     * 毫秒转换为时间
     *
     * @param millis     传递过来的时间毫秒
     * @param dateFormat 返回的时间格式
     * @return
     */
    public static String msecToTime(long millis, String dateFormat) {
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
}
