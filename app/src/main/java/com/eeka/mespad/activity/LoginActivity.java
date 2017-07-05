package com.eeka.mespad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

/**
 * 登录界面
 * Created by Lenovo on 2017/5/15.
 */

public class LoginActivity extends BaseActivity {

    private EditText mEt_user, mEt_pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mEt_user = (EditText) findViewById(R.id.et_login_user);
        mEt_pwd = (EditText) findViewById(R.id.et_login_pwd);

        UserInfoBo userInfo = SpUtil.getUserInfo();
        boolean loginStatus = SpUtil.getLoginStatus();
        if (loginStatus && userInfo != null) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
        if (userInfo != null) {
            mEt_user.setText(userInfo.getUserName());
            mEt_pwd.setText(userInfo.getPassword());
        }

        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_login) {
            login();
        }
    }

    private void login() {
        String user = mEt_user.getText().toString();
        if (isEmpty(user)) {
            toast("请输入账户名");
            return;
        }
        String pwd = mEt_pwd.getText().toString();
        if (isEmpty(pwd)) {
            toast("请输入密码");
            return;
        }

        showLoading();
        HttpHelper.login(user, pwd, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        dismissLoading();
        if (url.contains(HttpHelper.LOGIN_URL)) {
            String status = resultJSON.getString("status");
            if ("Y".equals(status)) {
                String user = mEt_user.getText().toString();
                String pwd = mEt_pwd.getText().toString();
                UserInfoBo userInfoBo = new UserInfoBo(user, pwd);
                SpUtil.saveUserInfo(userInfoBo);
                SpUtil.saveLoginStatus(true);

                toast("登录成功");
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            } else {
                toast("登录失败");
            }
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        dismissLoading();
        toast("登录失败");
    }
}
