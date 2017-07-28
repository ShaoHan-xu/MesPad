package com.eeka.mespad.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.StartWorkParamsBo;
import com.eeka.mespad.bo.UpdateLabuBo;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.NetUtil;
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
    public static final String PAD_ID = "P00001";
    //    public static final String PAD_IP = "192.168.0.7";
    public static final String PAD_IP = NetUtil.getHostIP();

    public static final String BASE_URL = "http://10.7.121.54:50000/eeka-mes/";

    public static final String login_url = BASE_URL + "login?";
    public static final String logout_url = BASE_URL + "logout?";
    public static final String loginByCard_url = BASE_URL + "loginByCard?";
    public static final String positionLogin_url = BASE_URL + "position/positionLogin?";
    public static final String positionLogout_url = BASE_URL + "position/positionLogout?";
    //    public static final String login_url = "http://10.7.121.54:50000/manufacturing/index.jsp?";//网页方式登录
    public static final String queryPositionByPadIp_url = BASE_URL + "queryPositionByPadIp?";
    public static final String findProcessWithPadId_url = BASE_URL + "cutpad/findPadBindOperations?";
    public static final String viewCutPadInfo_url = BASE_URL + "cutpad/viewCutPadInfor?";
    public static final String startBatchWork_url = BASE_URL + "product/startByProcessLot?";
    public static final String startCustomWork_url = BASE_URL + "product/startByShopOrder?";
    public static final String completeBatchWork_url = BASE_URL + "product/completeByProcessLot?";
    public static final String completeCustomWork_url = BASE_URL + "product/completeByShopOrder?";
    public static final String getWorkOrderList_url = BASE_URL + "cutpad/viewJobOrderList?";
    public static final String saveLabuData = BASE_URL + "cutpad/saveRabData?";
    public static final String saveLabuDataAndComplete = BASE_URL + "cutpad/saveRabDataAndComplete?";
    public static final String getBadList = BASE_URL + "logNcPad/listNcCodesOnOperation?";
    public static final String saveBadRecord = BASE_URL + "logNcPad/logNc?";

    private static Context mContext;

    static {
        mContext = PadApplication.mContext;
    }

    /**
     * 根据PAD的IP地址查询站点的相关信息
     *
     * @param callback 回调
     */
    public static void queryPositionByPadIp(HttpCallback callback) {
        RequestParams params = new RequestParams();
        params.put("padIp", PAD_IP);
        HttpRequest.post(queryPositionByPadIp_url, params, getResponseHandler(queryPositionByPadIp_url, callback));
    }

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
        HttpRequest.post(login_url, params, getResponseHandler(login_url, callback));
    }

    /**
     * 卡号密码登录
     *
     * @param cardId   卡号
     * @param pwd      密码
     * @param callback 回调
     */
    public static void loginByCard(String cardId, String pwd, HttpCallback callback) {
        RequestParams params = new RequestParams();
        params.put("cardId", cardId);
        params.put("passwd", pwd);
        HttpRequest.post(loginByCard_url, params, getResponseHandler(loginByCard_url, callback));
    }

    /**
     * 站位登录
     *
     * @param cardId   卡号
     * @param callback 回调
     */
    public static void positionLogin(String cardId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("CARD_ID", cardId);
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(positionLogin_url, params, getResponseHandler(positionLogin_url, callback));
    }

    /**
     * 站点登出
     *
     * @param cardId   卡号
     * @param callback 回调
     */
    public static void positionLogout(String cardId, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("CARD_ID", cardId);
        params.put("params", json.toJSONString());
        HttpRequest.post(positionLogout_url, params, getResponseHandler(positionLogout_url, callback));
    }

    /**
     * 查询当前平板绑定的工序
     *
     * @param padId
     * @param callback
     */
    public static void findProcessWithPadId(String padId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_ID);
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(findProcessWithPadId_url, params, getResponseHandler(findProcessWithPadId_url, callback));
    }

    /**
     * 获取拉布、裁剪数据
     *
     * @param callback
     */
    public static void viewCutPadInfo(String resourceBO, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("RFID", "RFID00000013");//批量订单
//        json.put("SHOP_ORDER", "GC-SO-DZ-003");//定制订单
        json.put("PAD_ID", PAD_ID);
        json.put("RESOURCE_BO", resourceBO);
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(viewCutPadInfo_url, params, getResponseHandler(viewCutPadInfo_url, callback));
    }

    /**
     * 批量订单开始
     *
     * @param paramsBo 参数
     */
    public static void startBatchWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(startBatchWork_url, params, getResponseHandler(startBatchWork_url, callback));
    }

    /**
     * 定制订单开始
     *
     * @param paramsBo 参数
     */
    public static void startCustomWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(startCustomWork_url, params, getResponseHandler(startCustomWork_url, callback));
    }

    /**
     * 定制订单完成
     *
     * @param paramsBo 参数
     */
    public static void completeCustomWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(completeCustomWork_url, params, getResponseHandler(completeCustomWork_url, callback));
    }

    /**
     * 批量订单完成
     *
     * @param paramsBo 参数
     */
    public static void completeBatchWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(completeBatchWork_url, params, getResponseHandler(completeBatchWork_url, callback));
    }

    /**
     * 获取作业订单列表
     */
    public static void getWorkOrderList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", "192.168.1.2");
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getWorkOrderList_url, params, getResponseHandler(getWorkOrderList_url, callback));
    }

    /**
     * 记录拉布数据
     */
    public static void saveLabuData(UpdateLabuBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveLabuData, params, getResponseHandler(saveLabuData, callback));
    }

    /**
     * 记录拉布数据并完成
     */
    public static void saveLabuDataAndComplete(UpdateLabuBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveLabuDataAndComplete, params, getResponseHandler(saveLabuDataAndComplete, callback));
    }

    /**
     * 获取不良数据列表
     */
    public static void getBadList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_ID);
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(getBadList, params, getResponseHandler(getBadList, callback));
    }

    /**
     * 保存不良数据
     */
    public static void saveBadRecord(@NonNull JSONObject json, HttpCallback callback) {
        json.put("PAD_ID", PAD_ID);
        json.put("RFID", "RFID00000013");
        RequestParams params = getBaseParams();
        params.put("site", "TEST");
        params.put("params", json.toJSONString());
        HttpRequest.post(saveBadRecord, params, getResponseHandler(saveBadRecord, callback));
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

    public static boolean isSuccess(JSONObject json) {
        return "Y".equals(json.getString(STATE));
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
                if (!NetUtil.isNetworkAvalible(mContext)) {
                    return;
                }
                //登录的时候保存cookie
                if (login_url.contains(url)) {
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
                if (!NetUtil.isNetworkAvalible(mContext)) {
                    Toast.makeText(mContext, "网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response == null) {
                    Toast.makeText(mContext, "连接错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                Logger.d(response);

                //网页方式登录测试用
                if (login_url.equals(url)) {
                    if (!response.contains("Error")) {
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
                    } else {
                        callback.onFailure(url, 0, "登录失败");
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
