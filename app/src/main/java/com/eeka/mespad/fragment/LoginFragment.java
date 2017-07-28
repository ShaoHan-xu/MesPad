package com.eeka.mespad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

/**
 * 登录界面
 * Created by Lenovo on 2017/7/6.
 */

public class LoginFragment extends BaseFragment {

    private EditText mEt_user, mEt_pwd;
    private OnLoginCallback mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_login, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mEt_user = (EditText) mView.findViewById(R.id.et_login_user);
        mEt_pwd = (EditText) mView.findViewById(R.id.et_login_pwd);

        UserInfoBo userInfo = SpUtil.getUserInfo();
        if (userInfo != null) {
            mEt_user.setText(userInfo.getUserName());
            mEt_pwd.setText(userInfo.getPassword());
        }

        mView.findViewById(R.id.btn_login).setOnClickListener(this);
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
        if (url.contains(HttpHelper.login_url)) {
            String status = resultJSON.getString("status");
            if ("Y".equals(status)) {
                String user = mEt_user.getText().toString();
                String pwd = mEt_pwd.getText().toString();
                UserInfoBo userInfoBo = new UserInfoBo(user, pwd);
                SpUtil.saveUserInfo(userInfoBo);
                SpUtil.saveLoginStatus(true);

                toast("登录成功");
                if (mCallback != null) {
                    mCallback.loginCallback(true, userInfoBo);
                }
            } else {
                toast("登录失败");
                if (mCallback != null) {
                    mCallback.loginCallback(false, null);
                }
            }
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        super.onFailure(url, code, message);
        dismissLoading();
        toast("登录失败");
        if (mCallback != null) {
            mCallback.loginCallback(false, null);
        }
    }

    public void setCallback(OnLoginCallback callback) {
        mCallback = callback;
    }

    public interface OnLoginCallback {
        /**
         * 登录回调
         *
         * @param success  是否登录成功
         * @param userInfo 登录成功时返回用户信息，失败则为null
         */
        void loginCallback(boolean success, UserInfoBo userInfo);
    }
}
