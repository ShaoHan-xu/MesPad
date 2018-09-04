package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 成衣下线提示弹框
 */
public class OfflineDialog extends BaseDialog implements View.OnClickListener, HttpCallback {

    private String mSFC;
    private String mHangerId;
    private String mShopOrder;
    private String mOperation;
    private String mOperationDesc;

    private boolean isChange;//换片下架

    public OfflineDialog(@NonNull Context context, String sfc, String hangerId, String shopOrder, String operation, String operationDesc) {
        super(context);
        mSFC = sfc;
        mHangerId = hangerId;
        mShopOrder = shopOrder;
        mOperation = operation;
        mOperationDesc = operationDesc;
        init();
    }

    public OfflineDialog(@NonNull Context context, String sfc, String hangerId, String shopOrder, String operation, String operationDesc, boolean isChange) {
        super(context);
        mSFC = sfc;
        mHangerId = hangerId;
        mShopOrder = shopOrder;
        mOperation = operation;
        mOperationDesc = operationDesc;
        this.isChange = isChange;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_offline, null);
        setContentView(view);

        TextView tv_sfc = view.findViewById(R.id.tv_offline_sfc);
        TextView tv_hangerId = view.findViewById(R.id.tv_offline_hangerId);
        TextView tv_shopOrder = view.findViewById(R.id.tv_offline_orderNum);
        TextView tv_operation = view.findViewById(R.id.tv_offline_operation);
        TextView tv_operationDesc = view.findViewById(R.id.tv_offline_operationDesc);
        TextView tv_washLabel = view.findViewById(R.id.tv_offline_washLabel);
        tv_sfc.setText(mSFC);
        tv_hangerId.setText(mHangerId);
        tv_shopOrder.setText(mShopOrder);
        tv_operation.setText(mOperation);
        tv_operationDesc.setText(mOperationDesc);

        view.findViewById(R.id.btn_offline_offline).setOnClickListener(this);
        view.findViewById(R.id.btn_offline_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_offline_offline) {
            offline();
        } else if (v.getId() == R.id.btn_offline_cancel) {
            dismiss();
        }
    }

    /**
     * 成衣下架
     */
    private void offline() {
        LoadingDialog.show(mContext);
        if (isChange) {
            JSONObject json = new JSONObject();
            json.put("HANGER_ID", mHangerId);
            HttpHelper.hangerUnbind(json, this);
        } else {
            HttpHelper.productOff(mHangerId, mSFC, this);
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            ErrorDialog.showAlert(mContext, resultJSON.getString("result"), ErrorDialog.TYPE.ALERT, null, true);
            dismiss();
        } else {
            ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
        }
        LoadingDialog.dismiss();
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showAlert(mContext, message);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

}
