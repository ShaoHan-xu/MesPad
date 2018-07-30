package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 成衣上下架提示弹框
 */
public class ProductOnOffDialog extends BaseDialog implements HttpCallback {

    private String mHangerId;
    private String mSFC;
    private EditText mEt_hangerId;
    private EditText mEt_washLabel;
    private boolean OFF;//是否是下架
    private View.OnClickListener mListener;

    public ProductOnOffDialog(@NonNull Context context, String hangerId, String SFC, boolean off, View.OnClickListener listener) {
        super(context);
        mHangerId = hangerId;
        mSFC = SFC;
        OFF = off;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_product_on_off, null);
        setContentView(view);

        mEt_hangerId = view.findViewById(R.id.et_productOnOff_hangerId);
        mEt_hangerId.setText(mHangerId);

        TextView tv_title = view.findViewById(R.id.tv_productOnOff_title);
        if (OFF) {
            tv_title.setText("成衣下架");
            mEt_hangerId.setEnabled(false);
            view.findViewById(R.id.layout_productOnOff_washLabel).setVisibility(View.GONE);
            TextView tv_sfc = view.findViewById(R.id.tv_productOnOff_sfc);
            tv_sfc.setText(mSFC);
        } else {
            tv_title.setText("成衣上架");
            view.findViewById(R.id.layout_productOnOff_sfc).setVisibility(View.GONE);
            mEt_washLabel = view.findViewById(R.id.et_productOnOff_washLabel);
        }

        view.findViewById(R.id.btn_productOnOff_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.hideKeyboard(mContext, v);
                if (OFF) {
                    off();
                } else {
                    on();
                }
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.hideKeyboard(mContext, v);
                dismiss();
            }
        });
    }

    private void on() {
        String washLabel = mEt_washLabel.getText().toString();
        if (TextUtils.isEmpty(washLabel)) {
            Toast.makeText(mContext, "请输入洗水唛", Toast.LENGTH_SHORT).show();
        } else {
            LoadingDialog.show(mContext);
            mHangerId = mEt_hangerId.getText().toString();
            HttpHelper.productOn(mHangerId, washLabel, this);
        }
    }

    private void off() {
        LoadingDialog.show(mContext);
        HttpHelper.productOff(mHangerId, mSFC, this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            ErrorDialog.showAlert(mContext, resultJSON.getString("result"), ErrorDialog.TYPE.ALERT, null, true);
            if (mListener != null)
                mListener.onClick(null);
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
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.4), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

}
