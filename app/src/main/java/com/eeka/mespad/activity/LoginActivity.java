package com.eeka.mespad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

/**
 * 登录界面
 * Created by Lenovo on 2017/5/15.
 */

public class LoginActivity extends BaseActivity implements LoginFragment.OnLoginCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoBo userInfo = SpUtil.getUserInfo();
        boolean loginStatus = SpUtil.getLoginStatus();
        if (loginStatus && userInfo != null) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }

        setContentView(R.layout.aty_login);

        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            showLoading("正在初始化...", false);
            HttpHelper.queryPositionByPadIp(this);
        }

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setCallback(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.loginFragment, loginFragment);
        transaction.commit();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(resultJSON.getJSONObject("result").toString(), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
            }
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        toast(message);
    }

    @Override
    public void loginCallback(boolean success, UserInfoBo userInfoBo) {
        if (success) {
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }

}
