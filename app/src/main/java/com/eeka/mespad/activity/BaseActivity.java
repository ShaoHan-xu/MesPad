package com.eeka.mespad.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.eeka.mespad.http.HttpCallback;

/**
 * Created by Lenovo on 2017/5/13.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, HttpCallback {

    protected Context mContext;

    private Dialog mProDialog;
    private TextView mTv_loadingMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
    }

    protected void initView() {

    }

    protected void bindListener() {

    }

    protected void initData() {

    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading), false);
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

    protected void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    protected void toast(String msg, int duration) {
        Toast.makeText(mContext, msg, duration).show();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
    }

    @Override
    public void onFailure(String url, int code, String message) {
        dismissLoading();
    }
}
