package com.eeka.mespad.http;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.INARequestBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.ReflectUtil;
import com.eeka.mespad.utils.SpUtil;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 访问WebService的工具类,
 *
 * @author xiaanming
 */
public class WebServiceUtils {

    private static String WEB_IN_OUT_URL = PadApplication.WEB_URL + "ExtStepPassServiceWSService";
    private static String WEB_DOING_URL = PadApplication.WEB_URL + "HangerDoServiceWSService";
    private static String NAMESPACE = "http://integration.ina.ws.eeka.com/";
    //方法名
    public static final String INA_IN = "inaCommonWipIn";
    public static final String INA_OUT = "inaCommonWipOut";
    public static final String INA_DOING = "sendTecFileListByRfid";

    private static final int WHAT_SUCCESS = 0;
    private static final int WHAT_FAIL = 1;

    private static HttpCallBack mCallback;

    // 含有3个线程的线程池
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * @param url                WebService服务器地址
     * @param methodName         WebService的调用方法名
     * @param properties         WebService的参数
     * @param webServiceCallBack 回调接口
     */
    private static void callWebService(String url, final String methodName,
                                       Map<String, Object> properties,
                                       HttpCallBack webServiceCallBack) {
        mCallback = webServiceCallBack;
        // 创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE httpTransportSE = new HttpTransportSE(url);
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(NAMESPACE, methodName);

        // SoapObject添加参数
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                soapObject.addProperty(entry.getKey(), entry.getValue());
            }
        }

        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // soapEnvelope.setOutputSoapObject(soapObject);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.dotNet = false;
        soapEnvelope.bodyOut = soapObject;
        httpTransportSE.debug = true;

        final List<HeaderProperty> headers = new ArrayList<>();
        UserInfoBo user = SpUtil.getLoginUser();
        headers.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode((user.getUSER() + ":" + user.getPassword()).getBytes())));

        // 开启线程去访问WebService
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    httpTransportSE.call(NAMESPACE + methodName, soapEnvelope, headers);
                    if (soapEnvelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        SoapObject resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
                        if (resultSoapObject != null) {
                            Logger.d(resultSoapObject.toString());
                            int count = resultSoapObject.getPropertyCount();
                            JSONObject jsonObject = new JSONObject();
                            for (int i = 0; i < count; i++) {
                                SoapObject soap = (SoapObject) resultSoapObject.getProperty(i);
                                String code = soap.getProperty("code").toString();
                                String message = soap.getProperty("message").toString();
                                jsonObject.put("code", code);
                                jsonObject.put("message", message);
                                if (soap.hasProperty("nextLineId"))
                                    jsonObject.put("nextLineId", soap.getProperty("nextLineId").toString());
                                if (soap.hasProperty("nextStationId"))
                                    jsonObject.put("nextStationId", soap.getProperty("nextStationId").toString());
                            }
                            String code = jsonObject.getString("code");
                            if ("0".equals(code)) {
                                String url = null;
                                if (soapEnvelope.bodyOut.toString().startsWith(INA_IN)) {
                                    url = INA_IN;
                                } else if (soapEnvelope.bodyOut.toString().startsWith(INA_OUT)) {
                                    url = INA_OUT;
                                } else if (soapEnvelope.bodyOut.toString().startsWith(INA_DOING)) {
                                    url = INA_DOING;
                                }
                                jsonObject.put("url", url);
                                Message message = mHandler.obtainMessage(WHAT_SUCCESS, jsonObject);
                                mHandler.sendMessage(message);
//                            } else {
//                                mCallback.onFail(jsonObject.getString("message"));
                            }
                        } else {
                            Message message = mHandler.obtainMessage(WHAT_FAIL, "调用接口数据返回为空");
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCallback != null) {
                switch (msg.what) {
                    case WHAT_SUCCESS:
                        JSONObject json = (JSONObject) msg.obj;
                        mCallback.onSuccess(json.getString("url"), json);
                        break;
                    case WHAT_FAIL:
                        mCallback.onFail((String) msg.obj);
                        break;
                }
            }
        }
    };

    public interface HttpCallBack {
        void onSuccess(String method, JSONObject result);

        void onFail(String errMsg);

    }

    public static void inaDoing(@NonNull INARequestBo data, HttpCallBack callBack) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Request", getINAINRequestParams(data));
        callWebService(WEB_DOING_URL, INA_DOING, map, callBack);
    }

    public static void inaIn(@NonNull INARequestBo data, HttpCallBack callBack) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Result", getINAINRequestParams(data));
        callWebService(WEB_IN_OUT_URL, INA_IN, map, callBack);
    }

    public static void inaOut(@NonNull INARequestBo data, HttpCallBack callBack) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Result", getINAINRequestParams(data));
        callWebService(WEB_IN_OUT_URL, INA_OUT, map, callBack);
    }

    private static SoapObject getINAINRequestParams(INARequestBo data) {
        SoapObject object = new SoapObject();
        HashMap<String, Object> reflect = null;
        try {
            reflect = ReflectUtil.Reflect(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (reflect != null) {
            for (Map.Entry<String, Object> entry : reflect.entrySet()) {
                object.addProperty(entry.getKey(), entry.getValue());
            }
        }
        return object;
    }

}
