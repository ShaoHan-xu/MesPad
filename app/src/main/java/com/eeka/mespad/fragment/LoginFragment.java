package com.eeka.mespad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

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

        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null && loginUsers.size() != 0) {
            UserInfoBo userInfo = loginUsers.get(0);
            mEt_user.setText(userInfo.getUSER());
            mEt_pwd.setText(userInfo.getPassword());
        }

        mView.findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_login) {
            ContextInfoBo contextInfo = SpUtil.getContextInfo();
            if (contextInfo == null) {
                showLoading("初始化中...", false);
                HttpHelper.queryPositionByPadIp(this);
            } else {
                login();
            }
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
        if (HttpHelper.isSuccess(resultJSON)) {
            String result = resultJSON.getJSONObject("result").toString();
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(result, ContextInfoBo.class);
                List<UserInfoBo> loginUserList = contextInfoBo.getLOGIN_USER_LIST();
                SpUtil.saveContextInfo(contextInfoBo);
                SpUtil.savePositionUsers(loginUserList);
                if (PadApplication.IS_COOKIE_OUT) {
                    SpUtil.saveLoginStatus(true);
                    if (mCallback != null) {
                        mCallback.loginCallback(true);
                    }
                } else {
                    login();
                }
            } else if (url.contains(HttpHelper.login_url)) {
                UserInfoBo userInfo = JSON.parseObject(result, UserInfoBo.class);
                String pwd = mEt_pwd.getText().toString();
                userInfo.setPassword(pwd);
                SpUtil.saveLoginUser(userInfo);
                if (PadApplication.IS_COOKIE_OUT) {
                    HttpHelper.queryPositionByPadIp(this);
                } else {
                    String cardNumber = userInfo.getCARD_NUMBER();
                    if (isEmpty(cardNumber)) {
                        if ("shawn".equals(userInfo.getUSER())) {
                            cardNumber = "123";
                        } else if ("ethan".equals(userInfo.getUSER())) {
                            cardNumber = "789";
                        }
                    }
                    HttpHelper.positionLogin(cardNumber, this);
                }
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("登录成功");
                UserInfoBo userInfo = JSON.parseObject(result, UserInfoBo.class);
                List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
                if (positionUsers == null)
                    positionUsers = new ArrayList<>();
                positionUsers.add(userInfo);
                SpUtil.savePositionUsers(positionUsers);
                SpUtil.saveLoginStatus(true);
                if (mCallback != null) {
                    mCallback.loginCallback(true);
                }
            }
        } else {
            if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                toast("初始化失败，" + resultJSON.getString("message"));
            } else {
                toast("登录失败," + resultJSON.getString("message"));
                if (mCallback != null) {
                    mCallback.loginCallback(false);
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
            mCallback.loginCallback(false);
        }
    }

    public void setCallback(OnLoginCallback callback) {
        mCallback = callback;
    }

    public interface OnLoginCallback {
        /**
         * 登录回调
         *
         * @param success 是否登录成功
         */
        void loginCallback(boolean success);
    }
}
