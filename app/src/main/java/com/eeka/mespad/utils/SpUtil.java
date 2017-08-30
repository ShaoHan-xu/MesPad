package com.eeka.mespad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.BTReasonBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.manager.Logger;

import java.util.List;

/**
 * 单例SharedPreference类
 * Created by Lenovo on 2017/6/28.
 */

public class SpUtil {

    //    private static SpUtil mInstance;
    private static SharedPreferences mSP;

    public static final String KEY_RESOURCE = "key_resource";

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

    public static void save(String key, String value) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String get(String key, String defaultValue) {
        return mSP.getString(key, defaultValue);
    }

    /**
     * 获取resource数据
     */
    public static PositionInfoBo.RESRINFORBean getResource() {
        String infoStr = mSP.getString(KEY_RESOURCE, null);
        return JSONObject.parseObject(infoStr, PositionInfoBo.RESRINFORBean.class);
    }

    /**
     * 保存site
     */
    public static void saveSite(String site) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("site", site);
        edit.apply();
    }

    /**
     * 获取site
     */
    public static String getSite() {
        return mSP.getString("site", null);
    }

    /**
     * 保存登录用户信息
     */
    public static void saveLoginUser(UserInfoBo userInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("loginUser", JSON.toJSONString(userInfo));
        edit.apply();
    }

    /**
     * 获取已登录用户信息
     */
    public static UserInfoBo getLoginUser() {
        String infoStr = mSP.getString("loginUser", null);
        return JSONObject.parseObject(infoStr, UserInfoBo.class);
    }

    /**
     * 保存站位登录用户信息
     */
    public static void savePositionUsers(List<UserInfoBo> userInfo) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("positionUsers", JSON.toJSONString(userInfo));
        edit.apply();
    }

    /**
     * 获取站位登录用户信息
     */
    public static List<UserInfoBo> getPositionUsers() {
        String infoStr = mSP.getString("positionUsers", null);
        return JSONObject.parseArray(infoStr, UserInfoBo.class);
    }

    /**
     * 保存cookie
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

    /**
     * 保存退补料原因
     */
    public static void saveBTReasons(int type, String reasons) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("BTReasons_" + type, reasons);
        edit.apply();
    }

    /**
     * 获取退补料原因
     */
    public static List<BTReasonBo> getBTReason(int type) {
        String BTReason = mSP.getString("BTReasons_" + type, null);
        if (!TextUtils.isEmpty(BTReason)) {
            return JSON.parseArray(BTReason, BTReasonBo.class);
        }
        return null;
    }

}
