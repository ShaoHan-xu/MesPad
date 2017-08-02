package com.eeka.mespad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.UserInfoBo;

import java.util.List;

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
    public static void saveLoginUser(UserInfoBo userInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("loginUser", JSON.toJSONString(userInfo));
        edit.apply();
    }

    /**
     * 获取已登录用户信息
     *
     * @return
     */
    public static UserInfoBo getLoginUser() {
        String infoStr = mSP.getString("loginUser", null);
        return JSONObject.parseObject(infoStr, UserInfoBo.class);
    }

    /**
     * 保存站位登录用户信息
     *
     * @param userInfo
     */
    public static void savePositionUsers(List<UserInfoBo> userInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("positionUsers", JSON.toJSONString(userInfo));
        edit.apply();
    }

    /**
     * 获取站位登录用户信息
     *
     * @return
     */
    public static List<UserInfoBo> getPositionUsers() {
        String infoStr = mSP.getString("positionUsers", null);
        return JSONObject.parseArray(infoStr, UserInfoBo.class);
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

    /**
     * 保存上下文信息
     */
    public static void saveContextInfo(ContextInfoBo contextInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("contextInfo", JSON.toJSONString(contextInfo));
        edit.apply();
    }

    /**
     * 获取上下文信息
     */
    public static ContextInfoBo getContextInfo() {
        String contextInfo = mSP.getString("contextInfo", null);
        if (!TextUtils.isEmpty(contextInfo)) {
            return JSON.parseObject(contextInfo, ContextInfoBo.class);
        }
        return null;
    }

}
