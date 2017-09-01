package com.eeka.mespad.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;

import java.util.List;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class BaseFragment extends Fragment implements View.OnClickListener, HttpCallback {

    protected Context mContext;
    protected View mView;

    private Dialog mProDialog;
    private TextView mTv_loadingMsg;

    public LinearLayout mLayout_loginUser;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    protected void initView() {
        View rootView = mView.findViewById(R.id.layout_rootView);
        if (rootView != null) {
            rootView.setOnClickListener(this);
        }
        mLayout_loginUser = (LinearLayout) mView.findViewById(R.id.layout_loginUsers);
    }

    protected void bindListener() {

    }

    protected void initData() {

    }

    /**
     * 刷新登录用户、有用户登录或者登出时调用
     */
    public void refreshLoginUsers() {
        if (mLayout_loginUser == null) {
            return;
        }
        mLayout_loginUser.removeAllViews();
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null) {
            ScrollView scrollView = (ScrollView) mView.findViewById(R.id.scrollView_loginUsers);
            if (loginUsers.size() >= 3) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UnitUtil.dip2px(mContext, 100));
                scrollView.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                scrollView.setLayoutParams(params);
            }
            for (UserInfoBo userInfo : loginUsers) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loginuser, null);
                TextView tv_userName = (TextView) view.findViewById(R.id.tv_userName);
                TextView tv_userId = (TextView) view.findViewById(R.id.tv_userId);
                tv_userName.setText(userInfo.getNAME());
                tv_userId.setText(userInfo.getEMPLOYEE_NUMBER() + "");
                mLayout_loginUser.addView(view);
            }
        }
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
        ErrorDialog.showDialog(mContext, msg);
    }

    protected void toast(String msg) {
        toast(msg, Toast.LENGTH_LONG);
    }

    protected void toast(String msg, int duration) {
        Toast.makeText(mContext, msg, duration).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_rootView) {
            SystemUtils.hideKeyboard(mContext, v);
        }
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
        ErrorDialog.showDialog(mContext, message);
    }

}
