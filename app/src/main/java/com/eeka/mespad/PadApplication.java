package com.eeka.mespad;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.util.TypeUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * Created by Lenovo on 2017/5/13.
 */
public class PadApplication extends Application {

    public static Context mContext;
    public static final String BASE_URL_D = "http://10.7.121.54:50000/eeka-mes/";//D系统
    public static final String BASE_URL_Q = "http://10.7.121.60:50000/eeka-mes/";//Q系统
    public static final String BASE_URL_P_LH = "http://10.7.121.64:50000/eeka-mes/";//龙华P系统
    public static final String BASE_URL_P = "http://10.10.200.16:8000/eeka-mes/";//P系统
    public static final String XMII_URL_D = "http://10.7.121.54:50000/XMII/";//D系统
    public static final String XMII_URL_Q = "http://10.7.121.60:50000/XMII/";//Q系统
    public static final String XMII_URL_P_LH = "http://10.7.121.64:50000/XMII/";//龙华P系统
    public static final String XMII_URL_P = "http://10.10.200.16:8000/XMII/";//P系统
    public static final String WEB_URL_D = "http://10.7.121.54:50000/eeka-ws/";//D系统
    public static final String WEB_URL_Q = "http://10.7.121.60:50000/eeka-ws/";//Q系统
    public static final String WEB_URL_P_LH = "http://10.7.121.60:50000/eeka-ws/";//龙华P系统
    public static final String WEB_URL_P = "http://10.10.200.16:8000/eeka-ws/";//P系统
    public static final String URL_MTM_D = "http://att.eeka.info:4080/eeka-mtm-centric/externalcall/qrySaleOrderLineDetail?orderNoAndLine=";//Q系统
    public static final String URL_MTM_Q = "http://att.eeka.info:4080/eeka-mtm-centric/externalcall/qrySaleOrderLineDetail?orderNoAndLine=";//Q系统
    public static final String URL_MTM_P = "https://mtm.eeka.info:4080/eeka-mtm-centric/externalcall/qrySaleOrderLineDetail?orderNoAndLine=";//P系统
    public static String MQTT_D = "10.7.121.40"; //MQ地址
    public static String MQTT_Q = "10.7.121.40"; //MQ地址
    public static String MQTT_P = "10.10.200.40"; //MQ地址
    public static String BASE_URL;
    public static String XMII_URL;
    public static String WEB_URL;
    public static String MTM_URL;
    public static String MQTT_BROKER; //MQ地址

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        initBugly();
        initOkHttp();
        TypeUtils.compatibleWithJavaBean = true;//配置fastJson：JSON.toJsonString时首字母自动变小写的问题

        String systemCode = getString(R.string.system_code);
        if (SystemUtils.isApkInDebug(this)) {//debug版本提供更换系统环境功能
            String systemCodeTemp = SpUtil.get(SpUtil.KEY_SYSTEMCODE, null);
            if (!TextUtils.isEmpty(systemCodeTemp)) {
                systemCode = systemCodeTemp;
            }
        }
        //根据不同的系统环境，配置不同的服务器地址
        if (!TextUtils.isEmpty(systemCode)) {
            if ("D".equals(systemCode)) {
                BASE_URL = BASE_URL_D;
                WEB_URL = WEB_URL_D;
                MQTT_BROKER = MQTT_D;
                MTM_URL = URL_MTM_D;
                XMII_URL = XMII_URL_D;
            } else if ("Q".equals(systemCode)) {
                BASE_URL = BASE_URL_Q;
                WEB_URL = WEB_URL_Q;
                MQTT_BROKER = MQTT_Q;
                MTM_URL = URL_MTM_Q;
                XMII_URL = XMII_URL_Q;
            } else if ("P".equals(systemCode)) {
                BASE_URL = BASE_URL_P;
                WEB_URL = WEB_URL_P;
                MQTT_BROKER = MQTT_P;
                MTM_URL = URL_MTM_P;
                XMII_URL = XMII_URL_P;
            } else if ("LH_P".equals(systemCode)) {
                BASE_URL = BASE_URL_P_LH;
                WEB_URL = WEB_URL_P_LH;
                MQTT_BROKER = MQTT_Q;
                MTM_URL = URL_MTM_P;
                XMII_URL = XMII_URL_P_LH;
            }
        }

        //根据渠道设置工厂站点
        String channel = getString(R.string.app_channel);
        if ("LH".equals(channel) || "LH_P".equals(channel)) {
            SpUtil.saveSite("8082");
        } else if ("YD".equals(channel)) {
            SpUtil.saveSite("8081");
        }

        //配置初始用户
        UserInfoBo loginUser = SpUtil.getLoginUser();
        if (loginUser == null) {
            SpUtil.saveLoginUser(new UserInfoBo(getString(R.string.user), getString(R.string.password)));
//            SpUtil.saveLoginUser(new UserInfoBo("SHAWN", "sap12345"));
        }
    }

    private void initBugly() {
        Beta.enableHotfix = false;//关闭热更新功能
//        Beta.autoDownloadOnWifi = true;//WiFi网络下自动下载安装包
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Bugly.setAppChannel(this, getString(R.string.app_channel));//设置渠道
        Bugly.init(getApplicationContext(), getString(R.string.buglyAPPID), false);
    }

    private void initOkHttp() {
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        builder.setDebug(SystemUtils.isApkInDebug(this));
        OkHttpFinal.getInstance().init(this, builder.build());
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        PadApplication app = (PadApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)//1G缓存
                .build();
    }

}


