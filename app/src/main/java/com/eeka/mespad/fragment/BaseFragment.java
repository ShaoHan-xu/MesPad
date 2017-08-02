package com.eeka.mespad.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.LoginActivity;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class BaseFragment extends Fragment implements View.OnClickListener, HttpCallback {

    protected Context mContext;
    protected View mView;

    private Dialog mProDialog;
    private TextView mTv_loadingMsg;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
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

    /**
     * 退出登录，回到登录界面
     */
    public void logout() {
        SpUtil.saveLoginStatus(false);
        startActivity(new Intent(mContext, LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (!HttpHelper.isSuccess(resultJSON)) {
            String message = resultJSON.getString("message");
            if (!isEmpty(message) && message.contains(HttpHelper.COOKIE_OUT)) {
                PadApplication.IS_COOKIE_OUT = true;
                toast("由于您长时间未操作，指令已过期，请重新登录");
                logout();
            } else {
                PadApplication.IS_COOKIE_OUT = false;
            }
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        dismissLoading();
    }
}
