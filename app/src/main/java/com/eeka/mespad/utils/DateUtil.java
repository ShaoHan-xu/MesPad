package com.eeka.mespad.utils;

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

}
