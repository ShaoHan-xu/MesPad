package com.eeka.mespad;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.util.TypeUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.utils.SpUtil;
import com.tencent.bugly.Bugly;

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
    public static String BASE_URL = BASE_URL_Q;

    @Override
    public void onCreate() {
        super.onCreate();

        Bugly.init(getApplicationContext(), "6af52b66e6", true);

        mContext = this;
        initOkHttp();
        TypeUtils.compatibleWithJavaBean = true;//配置fastJson：JSON.toJsonString时首字母自动变小写的问题

//        CrashHandler.getInstance().init(getApplicationContext());

        String systemCode = SpUtil.get(SpUtil.KEY_SYSTEMCODE, null);
        if (!TextUtils.isEmpty(systemCode)) {
            if ("D".equals(systemCode)) {
                BASE_URL = BASE_URL_D;
            } else if ("Q".equals(systemCode)) {
                BASE_URL = BASE_URL_Q;
            } else if ("P".equals(systemCode)) {
                BASE_URL = BASE_URL_P;
            }
        }

        //配置初始用户及站点
        UserInfoBo loginUser = SpUtil.getLoginUser();
        if (loginUser == null)
            SpUtil.saveLoginUser(new UserInfoBo("PAD_USER", "mes123456"));
//            SpUtil.saveLoginUser(new UserInfoBo("SHAWN", "sap12345"));
        String site = SpUtil.getSite();
        if (TextUtils.isEmpty(site))
            SpUtil.saveSite("8081");
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


