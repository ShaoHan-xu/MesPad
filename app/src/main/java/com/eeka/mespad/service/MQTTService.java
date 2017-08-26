package com.eeka.mespad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.TopicUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/22.
 */

public class MQTTService extends Service {

    public static final String TAG = MQTTService.class.getSimpleName();
    public static final int RECONNECT = 10;

    // This the application level keep-alive interval, that is used by the AlarmManager
    // to keep the connection active, even when the device goes to sleep.
    private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

    private int isReconnect = 0;
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private static boolean doConnect = true;

    private String host = "tcp://10.7.121.40:1883";//默认端口号1883
    private String userName = "admin";
    private String passWord = "admin";
    private static String myTopic;
    private String clientId = HttpHelper.PAD_IP;
    // Connection log for the push service. Good for debugging.
    private static ConnectionLog mLog;

    public static void actionStart(final Context ctx) {
        Intent intent = new Intent(ctx, MQTTService.class);
        ctx.startService(intent);
    }

    public static void actionStop(Context ctx) {
        stopConnect();
        Intent intent = new Intent(ctx, MQTTService.class);
        ctx.stopService(intent);
    }

    public void publish(String msg) {
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        try {
            client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
            log("client publish failed", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        clientId = myTopic;
        try {
            mLog = new ConnectionLog();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to open log", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        myTopic = HttpHelper.PAD_IP;
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(this, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(30);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(300);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());

        // last will message
//        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                log("Exception MqttConnectOptions Occured", e);
                doConnect = false;
            }
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    // log helper function
    private static void log(String message) {
        Logger.d(message, null);
    }

    private static void log(String message, Throwable e) {
        if (e != null) {
            Logger.e(message, e.getMessage());
        } else {
            Logger.d(message);
        }

        if (mLog != null) {
            try {
                if (e != null)
                    mLog.println("message : " + message + " .\\nMqttException : " + e.getMessage());
                else
                    mLog.println("message : " + message);
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopConnect();
    }

    private static void stopConnect() {
        try {
            client.unsubscribe(myTopic);
            client.unregisterResources();
            client.disconnect();
            client = null;
            doConnect = true;
        } catch (MqttException e) {
            e.printStackTrace();
            log("Exception stop connect ", e);
        }
        if (mLog != null) {
            try {
                mLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNomarl()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
                doConnect = false;
            } catch (MqttException e) {
                e.printStackTrace();
                log("Exception connect occured", e);
            }
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken token) {
            Logger.d("连接成功 ");
            isReconnect = 0;
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic, 0);
            } catch (MqttException e) {
                e.printStackTrace();
                log("Exception subscribe ", e);
            }
        }

        @Override
        public void onFailure(IMqttToken token, Throwable throwable) {
            throwable.printStackTrace();
            // 连接失败，自动重连1次
            Logger.d("connect failure");
        }
    };//  108030002  刘小勇

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Logger.d("MQTT收到推送->" + str2 + ",message:" + str1);
            PushJson pushJson = JSON.parseObject(str1, PushJson.class);
            if ("0".equals(pushJson.getCode())) {
                String type = pushJson.getType();
                if (TopicUtil.TOPIC_SEW.equals(type)) {//缝制
                    SewDataBo sewDataBo = JSON.parseObject(pushJson.getContent(), SewDataBo.class);
                    EventBus.getDefault().post(sewDataBo);
                } else if (TopicUtil.TOPIC_SUSPEND.equals(type)) {//上裁
                    EventBus.getDefault().post(pushJson.getContent());
                } else if ("LOGIN".equals(type)) {//缝制用户上岗
                    JSONObject json = JSON.parseObject(pushJson.getContent());
                    EventBus.getDefault().post(json);
                }
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e("test", "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable throwable) {
            Log.e("test", "connectionLost");
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}