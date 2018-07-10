package com.eeka.mespad.http;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.INAInRequestBo;
import com.eeka.mespad.bo.INAOutRequestBo;
import com.eeka.mespad.utils.ReflectUtil;

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

    private static String WEB_SERVER_URL = PadApplication.WEB_URL + "ExtStepPassServiceWSService";
    private static String NAMESPACE = "http://integration.ina.ws.eeka.com/";
    //方法名
    private static final String INA_IN = "inaCommonWipIn";
    private static final String INA_OUT = "inaCommonWipOut";

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
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // soapEnvelope.setOutputSoapObject(soapObject);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.dotNet = false;
        soapEnvelope.bodyOut = soapObject;
        httpTransportSE.debug = true;

        final List<HeaderProperty> headers = new ArrayList<>();

        headers.add(new HeaderProperty("Authorization", "Basic UEFEX1VTRVI6bWVzMTIzNDU2"));

        // 开启线程去访问WebService
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                SoapObject resultSoapObject = null;
                try {
                    httpTransportSE.call(NAMESPACE + methodName, soapEnvelope, headers);
                    if (soapEnvelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    mHandler.sendMessage(mHandler.obtainMessage(0,
                            resultSoapObject));
                }
            }
        });
    }

    // 用于子线程与主线程通信的Handler
    @SuppressLint("HandlerLeak")
    static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 将返回值回调到callBack的参数中
            if (mCallback != null)
                mCallback.callBack((SoapObject) msg.obj);
        }

    };

    public interface HttpCallBack {
        void callBack(SoapObject result);
    }

    public static void inaIn(@NonNull INAInRequestBo data, HttpCallBack callBack) {
        SoapObject object = new SoapObject();
        HashMap<String, Object> reflect = null;
        try {
            reflect = ReflectUtil.Reflect(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Object> entry : reflect.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Result", object);
        callWebService(WEB_SERVER_URL, INA_IN, map, callBack);
    }

    public static void inaOut(@NonNull INAOutRequestBo data, HttpCallBack callBack) {
        SoapObject object = new SoapObject();
        HashMap<String, Object> reflect = null;
        try {
            reflect = ReflectUtil.Reflect(data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Object> entry : reflect.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Result", object);
        callWebService(WEB_SERVER_URL, INA_OUT, map, callBack);
    }

}
