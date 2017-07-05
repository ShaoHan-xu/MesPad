package com.eeka.mespad.http;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.SpUtil;

import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * 网络交互类
 * Created by Lenovo on 2017/5/12.
 */

public class HttpHelper {
    private static final String STATE = "status";
    private static final String REMARK = "remark";

    public static final String BASE_URL = "http://10.7.121.54:50000/eeka-mes/";

    //    public static final String LOGIN_URL = BASE_URL + "com/eeka/web/LogicServlet?method=login&";
    public static final String LOGIN_URL = "http://10.7.121.54:50000/manufacturing/index.jsp?";//网页方式登录
    public static final String viewCutPadInfo_url = BASE_URL + "cutpad/viewCutPadInfor?";

    private static Context mContext;

    static {
        mContext = PadApplication.mContext;
    }

//    private static HttpHelper mInstance;

//    public static HttpHelper getInstance() {
//        if (mInstance == null) {
//            synchronized (HttpHelper.class) {
//                if (mInstance == null) {
//                    mInstance = new HttpHelper();
//                }
//            }
//        }
//
//        //设置默认连接超时时间5秒
////        OkHttpFinal.getInstance().getOkHttpClientBuilder().connectTimeout(5000, TimeUnit.MILLISECONDS);
//        return mInstance;
//    }

    /**
     * 登录
     *
     * @param user     账号
     * @param pwd      密码
     * @param callback 回调
     */
    public static void login(String user, String pwd, HttpCallback callback) {
        RequestParams params = new RequestParams();
        params.put("j_username", user);
        params.put("j_password", pwd);
        HttpRequest.post(LOGIN_URL, params, getResponseHandler(LOGIN_URL, callback));
    }

    /**
     * 获取拉布、裁剪数据
     *
     * @param processLot
     * @param callback
     */
    public static void viewCutPadInfo(String processLot, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("RFID", "R00000001");
        json.put("PAD_ID", "R00000001");
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(viewCutPadInfo_url, params, getResponseHandler(viewCutPadInfo_url, callback));
    }

    /**
     * 获取固定请求参数<br>
     *
     * @return
     */
    public static RequestParams getBaseParams() {
        RequestParams params = new RequestParams();
        String cookie = SpUtil.getCookie();
        if (!TextUtils.isEmpty(cookie)) {
            params.addHeader("Cookie", cookie);
        }
        return params;
    }

    /**
     * 获取请求响应Handler
     *
     * @param callback
     * @return
     */
    public static BaseHttpRequestCallback getResponseHandler(final String url, final HttpCallback callback) {
        BaseHttpRequestCallback response = new BaseHttpRequestCallback<JSONObject>() {

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                //无网络或者后台出错
                callback.onFailure(url, errorCode, msg);
            }

            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);

                //登录的时候保存cookie
                if (url.contains(LOGIN_URL)) {
                    if (headers != null) {
                        StringBuilder cookies = new StringBuilder();
                        List<String> values = headers.values("set-cookie");
                        for (String cookie : values) {
                            cookies.append(cookie).append(";");
                        }
                        if (!TextUtils.isEmpty(cookies)) {
                            SpUtil.saveCookie(cookies.substring(0, cookies.lastIndexOf(";")));
                        }
                    }
                }

                callback.onSuccess(url, jsonObject);
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                Logger.d(response);

                //网页方式登录测试用
                if (!response.contains("error")) {
                    if (url.contains(LOGIN_URL)) {
                        if (headers != null) {
                            StringBuilder cookies = new StringBuilder();
                            List<String> values = headers.values("set-cookie");
                            for (String cookie : values) {
                                cookies.append(cookie).append(";");
                            }
                            if (!TextUtils.isEmpty(cookies)) {
                                SpUtil.saveCookie(cookies.substring(0, cookies.lastIndexOf(";")));
                            }
                        }
                        JSONObject json = new JSONObject();
                        json.put(STATE, "Y");
                        callback.onSuccess(url, json);
                    }
                }
                //webService数据解析，因为xml数据无法格式化成json数据，所以不会调用onSuccess方法，所以需要在此处做回调处理
//                int firstIndex = response.indexOf("{");
//                int lastIndex = response.lastIndexOf("}");
//                if (firstIndex != -1 && lastIndex != -1) {
//                    String result = response.substring(firstIndex, lastIndex + 1);
//                    Logger.d(result);
//                    if (callback != null) {
//                        JSONObject json = JSON.parseObject(result);
//                        callback.onSuccess(url, json);
//                    }
//                } else {
//                    callback.onFailure(url, -999, "数据错误");
//                }
            }
        };
        return response;
    }

}
