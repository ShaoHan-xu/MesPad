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

import java.util.List;

/**
 * 登录界面
 * Created by Lenovo on 2017/5/15.
 */

public class LoginActivity extends BaseActivity implements LoginFragment.OnClockCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        boolean loginStatus = SpUtil.getLoginStatus();
        if (loginStatus && loginUsers != null) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.aty_login);

        initView();

    }

    @Override
    protected void initView() {
        super.initView();

        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setOnClockCallback(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.loginFragment, loginFragment);
        transaction.commit();
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }
}
