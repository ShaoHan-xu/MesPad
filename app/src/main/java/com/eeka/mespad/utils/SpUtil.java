package com.eeka.mespad.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.UserInfoBo;

/**
 * 单例SharedPreference类
 * Created by Lenovo on 2017/6/28.
 */

public class SpUtil {

    //    private static SpUtil mInstance;
    private static SharedPreferences mSP;

    static {
        new SpUtil();
    }

    private SpUtil() {
        Context mContext = PadApplication.mContext;
        mSP = mContext.getSharedPreferences("eeka_mesPad", Context.MODE_PRIVATE);
    }

//    public static SpUtil getInstance() {
//        if (mInstance == null) {
//            synchronized (SpUtil.class) {
//                if (mInstance == null) {
//                    mInstance = new SpUtil();
//                }
//            }
//        }
//        return mInstance;
//    }

    /**
     * 保存登录用户信息
     *
     * @param userInfo
     */
    public static void saveUserInfo(UserInfoBo userInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("userInfo", JSON.toJSONString(userInfo));
        edit.apply();
    }

    /**
     * 获取登录用户信息
     *
     * @return
     */
    public static UserInfoBo getUserInfo() {
        String infoStr = mSP.getString("userInfo", null);
        if (infoStr == null)
            return null;
        return JSONObject.parseObject(infoStr, UserInfoBo.class);
    }

    /**
     * 保存cookie
     *
     * @param cookie
     */
    public static void saveCookie(String cookie) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("cookie", cookie);
        edit.apply();
    }

    public static String getCookie() {
        return mSP.getString("cookie", null);
    }

    /**
     * 保存登录状态
     *
     * @param status
     */
    public static void saveLoginStatus(boolean status) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putBoolean("loginStatus", status);
        edit.apply();
    }

    public static boolean getLoginStatus() {
        return mSP.getBoolean("loginStatus", false);
    }

}
