package com.eeka.mespad.utils;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public class PattermUtil {

    //判断字符串是否纯数字
    public static boolean isNumeric(@NonNull String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //判断是否为纯字母
    public static boolean isLetter(@NonNull String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        for (int i = 0; i < str.length(); i++) {
            String substring = str.substring(i, i + 1);
            if (!pattern.matcher(substring).matches()) {
                return false;
            }
        }
        return true;
    }

    //判断是否为纯汉字
    public static boolean isChinese(@NonNull String str) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        for (int i = 0; i < str.length(); i++) {
            String substring = str.substring(i, i + 1);
            if (!pattern.matcher(substring).matches()) {
                return false;
            }
        }
        return true;
    }

}
