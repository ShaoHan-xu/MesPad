package com.eeka.mespad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.view.dialog.ErrorDialog;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import cn.finalteam.okhttpfinal.LogUtil;

/**
 * Created by Administrator on 2017/2/22.
 */

public class MQTTService extends Service {

    public static final String TAG = MQTTService.class.getSimpleName();
    public static final int RECONNECT = 10;

    // This the application level keep-alive interval, that is used by the AlarmManager
    // to keep the connection active, even when the device goes to sleep.
    private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

    private static boolean isConnected = false;//是否已连接
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private static boolean doConnect = true;

    private String host = "tcp://10.7.121.40:1883";//默认端口号1883
    private String userName = "admin";
    private String passWord = "admin";
    private static String myTopic;
    private String clientId = HttpHelper.PAD_IP;
    // Connection log for the push service. Good for debugging.
    private static Context mContext;

    public static void actionStart(final Context ctx) {
        mContext = ctx;
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
            Logger.d("client publish failed" + e.toString());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        clientId = myTopic;
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
                Logger.d("Exception MqttConnectOptions Occured" + e.toString());
                doConnect = false;
            }
        }
        if (doConnect) {
            doClientConnection();
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
            isConnected = false;
            Logger.d("MQTT 断开连接");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("MQTT 断开连接异常" + e.getMessage());
        }
//        if (mLog != null) {
//            try {
//                mLog.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
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
                Logger.d("Exception connect occured" + e.toString());
            }
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken token) {
            Logger.d("MQTT 连接成功");
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic, 0);
                isConnected = true;
                Logger.d("MQTT 订阅" + myTopic + "成功");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("MQTT 订阅异常 " + e.getMessage());
                doClientConnection();//连接成功但是订阅失败时再次连接可以订阅成功，但是会收到两次推送
            }
        }

        @Override
        public void onFailure(IMqttToken token, Throwable throwable) {
            throwable.printStackTrace();
            // 连接失败，自动重连1次
            Logger.d("connect failure");
        }
    };

    private String mLastMsgType;
    private long mLastMsgMillis;

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Logger.d("MQTT收到推送->" + str2 + ",message:" + str1);
            LogUtil.writeToFile(LogUtil.LOGTYPE_MQTT, str1);

            PushJson pushJson = JSON.parseObject(str1, PushJson.class);
            if ("0".equals(pushJson.getCode())) {
                String type = pushJson.getType();
                if (!TextUtils.isEmpty(mLastMsgType)) {
                    if (mLastMsgType.equals(type)) {
                        long curMillis = System.currentTimeMillis();
                        if (curMillis - mLastMsgMillis < 500) {
                            //暂时解决接收两次推送的问题
                            return;
                        }
                    }
                }
                mLastMsgMillis = System.currentTimeMillis();
                mLastMsgType = type;
                EventBus.getDefault().post(pushJson);
            } else {
                ErrorDialog.showAlert(mContext, pushJson.getMessage());
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
