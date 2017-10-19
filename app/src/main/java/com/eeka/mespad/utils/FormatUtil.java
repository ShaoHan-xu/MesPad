package com.eeka.mespad.utils;

import android.text.TextUtils;

import com.eeka.mespad.manager.Logger;

/**
 * 数据转换类，做转换异常处理
 * Created by Lenovo on 2017/8/31.
 */

public class FormatUtil {

    public static float strToFloat(String str) {
        if (TextUtils.isEmpty(str))
            return 0;
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            Logger.w(e.toString());
        }
        return 0;
    }

}
