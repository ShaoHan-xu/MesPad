package com.eeka.mespad;

import android.app.Application;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import okhttp3.Headers;

/**
 * Created by Lenovo on 2017/5/13.
 */

public class PadApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initOkHttp();
    }

    private void initOkHttp() {
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
    }

}


