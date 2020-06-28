package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.eeka.mespad.utils.ToastUtil;

public class FeedbackDialog extends BaseDialog {

    private EditText mEt_content;
    private JSONObject mData;

    public FeedbackDialog(@NonNull Context context, JSONObject data) {
        super(context);
        mData = data;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_feedback, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("试产反馈");

        mEt_content = mView.findViewById(R.id.et_content);

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        String msg = mEt_content.getText().toString();
        if (isEmpty(msg)) {
            ErrorDialog.showAlert(mContext, "请输入反馈内容");
            return;
        }
        JSONObject json = new JSONObject();
        json.put("item", mData.getString("item"));
        json.put("router", mData.getString("router"));
        json.put("orderNo", mData.getString("orderNo"));
        json.put("orderType", mData.getString("orderType"));
        json.put("feedbackMsg", msg);
        LoadingDialog.show(mContext);
        HttpHelper.trialFeedBack(json, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "反馈提交成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    @Override
    public void show() {
        showOri();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.7));
    }
}
