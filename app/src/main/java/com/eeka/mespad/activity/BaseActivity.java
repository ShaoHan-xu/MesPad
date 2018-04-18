package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.fragment.LoginFragment;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.ToastUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity基类
 * Created by Lenovo on 2017/5/13.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, HttpCallback, LoginFragment.OnLoginCallback, LoginFragment.OnClockCallback {

    protected Context mContext;

    private Dialog mProDialog;
    private TextView mTv_loadingMsg;

    protected FragmentManager mFragmentManager;
    protected Dialog mLoginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        mFragmentManager = getSupportFragmentManager();

    }

    protected void initView() {

    }

    protected void bindListener() {

    }

    protected void initData() {
    }

    /**
     * 刷卡获取卡信息
     */
    public void getCardInfo(String orderNum) {
        showLoading();
        HttpHelper.getCardInfo(orderNum, this);
    }

    /**
     * 刷卡上岗
     */
    public void clockIn(String cardNum) {
        HttpHelper.positionLogin(cardNum, this);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading), true);
    }

    protected void showLoading(String msg, boolean cancelAble) {
        if (mProDialog == null) {
            initProgressDialog();
        }
        mTv_loadingMsg.setText(msg);
        mProDialog.setCancelable(cancelAble);
        mProDialog.show();
    }

    protected void dismissLoading() {
        if (mProDialog != null) {
            mProDialog.dismiss();
        }
    }

    private void initProgressDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_loading, null);
        mTv_loadingMsg = (TextView) view.findViewById(R.id.tv_loading_msg);

        mProDialog = new Dialog(mContext);
        mProDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProDialog.setContentView(view);
    }

    protected void showErrorDialog(String msg) {
        ErrorDialog.showAlert(mContext, msg);
    }

    protected void toast(String msg) {
        toast(msg, Toast.LENGTH_LONG);
    }

    protected void toast(String msg, int duration) {
        ToastUtil.showToast(this, msg, duration);
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 显示登录弹框
     */
    public void showLoginDialog() {
        mLoginDialog = new Dialog(mContext);
        mLoginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoginDialog.setContentView(R.layout.dlg_login);

        final LoginFragment loginFragment = (LoginFragment) mFragmentManager.findFragmentById(R.id.loginFragment);
        loginFragment.setOnClockCallback(this);

        mLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mFragmentManager.beginTransaction().remove(loginFragment).commit();
            }
        });
        mLoginDialog.show();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (!HttpHelper.isSuccess(resultJSON)) {
            showErrorDialog(resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        dismissLoading();
        ErrorDialog.showAlert(mContext, message);
    }

    @Override
    public void onLogin(boolean success) {

    }

    @Override
    public void onClockIn(boolean success) {
    }

}
