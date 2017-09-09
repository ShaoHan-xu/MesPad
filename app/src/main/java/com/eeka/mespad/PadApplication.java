package com.eeka.mespad;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.util.TypeUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.service.LogUtil;
import com.eeka.mespad.utils.SpUtil;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * Created by Lenovo on 2017/5/13.
 */

public class PadApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        LogUtil.init(this);
        initOkHttp();
        TypeUtils.compatibleWithJavaBean = true;//配置fastJson：JSON.toJsonString时首字母自动变小写的问题

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
        OkHttpFinal.getInstance().init(builder.build());
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


