package com.eeka.mespad.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.StartWorkParamsBo;
import com.eeka.mespad.bo.UpdateLabuBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.NetUtil;
import com.eeka.mespad.utils.SpUtil;

import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络交互类
 * Created by Lenovo on 2017/5/12.
 */

public class HttpHelper {
    private static final String STATE = "status";
    public static boolean IS_COOKIE_OUT;
    public static final String COOKIE_OUT = "SecurityException: Authorization failed.";//cookie过期
    public static String PAD_IP = "192.168.0.1";
//    public static final String PAD_IP = NetUtil.getHostIP();

    public static final String BASE_URL = "http://10.7.121.54:50000/eeka-mes/";

    public static final String login_url = BASE_URL + "login?";
    public static final String logout_url = BASE_URL + "logout?";
    public static final String loginByCard_url = BASE_URL + "loginByCard?";
    public static final String positionLogin_url = BASE_URL + "position/positionLogin?";
    public static final String positionLogout_url = BASE_URL + "position/positionLogout?";
    //    public static final String login_url = "http://10.7.121.54:50000/manufacturing/index.jsp?";//网页方式登录
    public static final String queryPositionByPadIp_url = BASE_URL + "position/queryPositionByPadIp?";
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
    public static final String signoffByShopOrder = BASE_URL + "product/signoffByShopOrder?";
    public static final String signoffByProcessLot = BASE_URL + "product/signoffByProcessLot?";
    public static final String getSewData = BASE_URL + "sweing/findPadKeyData?";
    public static final String getCardInfo = BASE_URL + "cutpad/cardRecognition?";
    public static final String cutMaterialReturnOrFeeding = BASE_URL + "cutpad/cutMaterialReturnOrFeeding?";
    public static final String hangerUnbind = BASE_URL + "hanger/unbind?";

    private static Context mContext;

    private static Pair<Request, HttpCallback> mFailRequest;

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
     * 登出
     */
    public static void logout(HttpCallback callback) {
        RequestParams params = getBaseParams();
        HttpRequest.post(logout_url, params, getResponseHandler(logout_url, callback));
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
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("CARD_ID", cardId);
        params.put("params", json.toJSONString());
        HttpRequest.post(positionLogout_url, params, getResponseHandler(positionLogout_url, callback));
    }

    /**
     * 查询当前平板绑定的工序
     *
     * @param padId 站位IP
     */
    public static void findProcessWithPadId(String padId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        if (!TextUtils.isEmpty(padId)) {
            PAD_IP = padId;
        }
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(findProcessWithPadId_url, params, getResponseHandler(findProcessWithPadId_url, callback));
    }

    /**
     * 根据RFID卡号获取信息、定制订单/批量订单/员工
     *
     * @param cardId 卡号
     */
    public static void getCardInfo(String cardId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("RFID", cardId);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(getCardInfo, params, getResponseHandler(getCardInfo, callback));
    }

    /**
     * 获取拉布、裁剪数据
     *
     * @param orderType 订单类型 P=批量、S=定制
     */
    public static void viewCutPadInfo(String orderType, String orderNum, String resourceBO, String processLotBo, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("RFID", orderNum);
        json.put("ORDER_TYPE", orderType);
        json.put("PAD_ID", PAD_IP);
        json.put("PROCESS_LOT_BO", processLotBo);
        json.put("RESOURCE_BO", resourceBO);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(viewCutPadInfo_url, params, getResponseHandler(viewCutPadInfo_url, callback));
    }

    /**
     * 退料/补料接口
     *
     * @param json TYPE:2=补料,3=退料
     *             SHOP_ORDER = 订单号
     *             ITEM_INFOS = 退/补料对象列表，对象：ITEM = 物料号，QTY = 数量，REASON_CODE = 原因代码
     */
    public static void cutMaterialReturnOrFeeding(JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(cutMaterialReturnOrFeeding, getResponseHandler(cutMaterialReturnOrFeeding, callback));
    }

    /**
     * 批量订单开始
     *
     * @param paramsBo 参数
     */
    public static void startBatchWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
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
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(completeBatchWork_url, params, getResponseHandler(completeBatchWork_url, callback));
    }

    /**
     * 获取作业订单列表
     */
    public static void getWorkOrderList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getWorkOrderList_url, params, getResponseHandler(getWorkOrderList_url, callback));
    }

    /**
     * 记录拉布数据
     */
    public static void saveLabuData(UpdateLabuBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveLabuData, params, getResponseHandler(saveLabuData, callback));
    }

    /**
     * 记录拉布数据并完成
     */
    public static void saveLabuDataAndComplete(UpdateLabuBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveLabuDataAndComplete, params, getResponseHandler(saveLabuDataAndComplete, callback));
    }

    /**
     * 获取不良数据列表
     */
    public static void getBadList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(getBadList, params, getResponseHandler(getBadList, callback));
    }

    /**
     * 保存不良数据
     */
    public static void saveBadRecord(@NonNull JSONObject json, HttpCallback callback) {
        json.put("PAD_ID", PAD_IP);
        json.put("RFID", "RFID00000013");
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(saveBadRecord, params, getResponseHandler(saveBadRecord, callback));
    }

    /**
     * 注销在制品-定制订单
     *
     * @param json json包含参数：SHOP_ORDER_BO、RESOURCE_BO、OPERATION_BO
     */
    public static void signoffByShopOrder(@NonNull JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(signoffByShopOrder, params, getResponseHandler(signoffByShopOrder, callback));
    }

    /**
     * 注销在制品-批量订单
     *
     * @param json json包含参数：{'PROCESS_LOTS':[""],'OPERATION_BO':'','RESOURCE_BO':'','SHOP_ORDER_BO':''}
     */
    public static void signoffByProcessLot(@NonNull JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(signoffByProcessLot, params, getResponseHandler(signoffByProcessLot, callback));
    }

    /**
     * 衣架解绑
     *
     * @param json {"HANGER_ID":"E34A3499","SFC":"19357930010001","PART_ID":"ZH"}
     *             HANGER_ID:衣架ID；
     *             SFC：工票号；
     *             PART_ID：生产部件编码
     *             参数输入(HANGER_ID) 或 (SFC与PART_ID) 必须输入其一
     */
    public static void hangerUnbind(JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(hangerUnbind, params, getResponseHandler(hangerUnbind, callback));
    }

    /**
     * 获取缝制数据
     *
     * @param rfid 卡号
     */
    public static void getSewData(String rfid, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfId", rfid);
        params.put("padIp", PAD_IP);
        HttpRequest.post(getSewData, params, getResponseHandler(getSewData, callback));
    }

    /**
     * 获取固定请求参数<br>
     */
    public static RequestParams getBaseParams() {
        RequestParams params = new RequestParams();
        String site = SpUtil.getSite();
        if (!TextUtils.isEmpty(site)) {
            params.put("site", site);
        }
        String cookie = SpUtil.getCookie();
        if (!TextUtils.isEmpty(cookie)) {
            params.addHeader("Cookie", cookie);
        }
        return params;
    }

    public static boolean isSuccess(JSONObject json) {
        return "Y".equals(json.getString(STATE));
    }

    public static String getResultStr(JSONObject json) {
        JSONObject result = json.getJSONObject("result");
        if (result != null)
            return result.toString();
        return null;
    }

    /**
     * 获取请求响应Handler
     */
    public static BaseHttpRequestCallback getResponseHandler(final String url, final HttpCallback callback) {
        BaseHttpRequestCallback response = new BaseHttpRequestCallback<JSONObject>() {

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                //无网络或者后台出错
                if (callback != null)
                    callback.onFailure(url, errorCode, msg);
            }

            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
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
                    if (IS_COOKIE_OUT) {
                        if (callback != null) {
                            callback.onFailure(url, 0, "系统登录成功，请继续操作");
                        }
                    }
                } else if (!isSuccess(jsonObject)) {
                    String message = jsonObject.getString("message");
                    if (message.contains(COOKIE_OUT)) {//cookie失效，重新登录获取新的cookie
                        IS_COOKIE_OUT = true;
                        UserInfoBo loginUser = SpUtil.getLoginUser();
                        login(loginUser.getUSER(), loginUser.getPassword(), callback);
                        Toast.makeText(mContext, "由于您长时间未操作，正在重新登录系统...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                IS_COOKIE_OUT = false;
                if (callback != null)
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
//                if (login_url.equals(url)) {
//                    if (!response.contains("Error")) {
//                        if (headers != null) {
//                            StringBuilder cookies = new StringBuilder();
//                            List<String> values = headers.values("set-cookie");
//                            for (String cookie : values) {
//                                cookies.append(cookie).append(";");
//                            }
//                            if (!TextUtils.isEmpty(cookies)) {
//                                SpUtil.saveCookie(cookies.substring(0, cookies.lastIndexOf(";")));
//                            }
//                        }
//                        JSONObject json = new JSONObject();
//                        json.put(STATE, "Y");
//                        callback.onSuccess(url, json);
//                    } else {
//                        callback.onFailure(url, 0, "登录失败");
//                    }
//
//                }

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
