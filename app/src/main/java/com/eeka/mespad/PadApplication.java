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
    public static final String BASE_URL_P = "http://10.10.200.16:8000/eeka-mes/";//P系统
    public static final String WEB_URL_D = "http://10.7.121.54:50000/eeka-ws/";//D系统
    public static final String WEB_URL_Q = "http://10.7.121.60:50000/eeka-ws/";//Q系统
    public static final String WEB_URL_P = "http://10.10.200.16:8000/eeka-ws/";//P系统
    public static String BASE_URL;
    public static String WEB_URL;
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
        //根据不同的系统环境，配置不同的站点、服务器地址，MQ地址
        if (!TextUtils.isEmpty(systemCode)) {
            if ("D".equals(systemCode)) {
                SpUtil.saveSite("8081");
                BASE_URL = BASE_URL_D;
                WEB_URL = WEB_URL_D;
                MQTT_BROKER = "10.10.200.40";
            } else if ("Q".equals(systemCode)) {
                SpUtil.saveSite("8082");
                BASE_URL = BASE_URL_Q;
                WEB_URL = WEB_URL_Q;
                MQTT_BROKER = "10.7.121.40";
            } else if ("P".equals(systemCode)) {
                SpUtil.saveSite("8081");
                BASE_URL = BASE_URL_P;
                WEB_URL = WEB_URL_P;
                MQTT_BROKER = "10.10.200.11";
            }
        }

        //配置初始用户及站点
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
        builder.setDebug(true);
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


