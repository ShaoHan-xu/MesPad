package com.eeka.mespad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.UserInfoBo;

import java.util.List;

/**
 * 单例SharedPreference类
 * Created by Lenovo on 2017/6/28.
 */

public class SpUtil {

    //    private static SpUtil mInstance;
    private static SharedPreferences mSP;

    public static final String KEY_RESOURCE = "key_resource";
    public static final String KEY_DEBUG = "key_debug";
    public static final String KEY_SYSTEMCODE = "key_systemCode";

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
     * 设置是否开启debug记录日志模式
     */
    public static void setDebugLog(boolean isDebug) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putBoolean(KEY_DEBUG, isDebug);
        edit.apply();
    }

    public static boolean isDebugLog() {
        return mSP.getBoolean(KEY_DEBUG, false);
    }

    /**
     * 获取resource数据
     */
    public static PositionInfoBo.RESRINFORBean getResource() {
        String infoStr = mSP.getString(KEY_RESOURCE, null);
        return JSONObject.parseObject(infoStr, PositionInfoBo.RESRINFORBean.class);
    }

    /**
     * 保存销售订单号
     */
    public static void saveSalesOrder(String salesOrder) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("salesOrder", salesOrder);
        edit.apply();
    }

    /**
     * 获取销售订单号
     */
    public static String getSalesOrder() {
        return mSP.getString("salesOrder", null);
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
     * 保存字典数据
     */
    public static void saveDictionaryData(String key, String data) {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("dictionaryData_" + key, data);
        edit.apply();
    }

    /**
     * 清空字典数据
     */
    public static void cleanDictionaryData() {
        SharedPreferences.Editor edit = mSP.edit();
        edit.putString("dictionaryData_" + DictionaryDataBo.CODE_STICKY, null);
        edit.putString("dictionaryData_" + DictionaryDataBo.CODE_BlReason, null);
        edit.putString("dictionaryData_" + DictionaryDataBo.CODE_TlReason, null);
        edit.apply();
    }

    /**
     * 获取字典数据
     */
    public static List<DictionaryDataBo> getDictionaryData(String key) {
        String BTReason = mSP.getString("dictionaryData_" + key, null);
        if (!TextUtils.isEmpty(BTReason)) {
            return JSON.parseArray(BTReason, DictionaryDataBo.class);
        }
        return null;
    }

}
