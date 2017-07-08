package com.eeka.mespad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.LoginFragment;
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
    public void loginCallback(boolean success, UserInfoBo userInfoBo) {
        if (success) {
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }

}
