package com.eeka.mespad.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.manager.Logger;

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
    private static final String STATE = "state";
    private static final String REMARK = "remark";

    private static final int STATE_402 = 402;//账号在其他设备登录

    public static final String BASE_URL = "http://10.8.42.149/Gst_OperationSOP";

    public static final String LOGIN_URL = BASE_URL + "/login";

    public static final String GETGXDM_URL = BASE_URL + "/gxck.asmx/OutputOperationSOP_Json";

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

    public static void login(String user, String pwd, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("user", user);
        params.put("password", pwd);
        HttpRequest.post(LOGIN_URL, params, getResponseHandler(LOGIN_URL, callback));
    }

    /**
     * 获取固定请求参数<br>
     * 已包含参数：user_id、token、family_id、area
     *
     * @return
     */
    public static RequestParams getBaseParams() {
        RequestParams params = new RequestParams();

        return params;
    }

    /**
     * 获取 JSON 请求参数
     *
     * @return
     */
    public static JSONObject getBaseJSONParams() {
        JSONObject jsonObj = new JSONObject();

        return jsonObj;
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
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                int firstIndex = response.indexOf("{");
                int lastIndex = response.lastIndexOf("}");
                if (firstIndex != -1 && lastIndex != -1) {
                    String result = response.substring(firstIndex, lastIndex + 1);
                    Logger.d(result);
                    if (callback != null) {
                        JSONObject json = JSON.parseObject(result);
                        callback.onSuccess(url, json);
                    }
                } else {
                    callback.onFailure(url, -999, "数据错误");
                }
            }
        };
        return response;
    }

}
