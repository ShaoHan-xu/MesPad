package com.eeka.mespad.http;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Lenovo on 2017/5/12.
 */

public interface HttpCallback {

    void onSuccess(String url, JSONObject resultJSON);

    void onFailure(String url, int code, String message);
}
