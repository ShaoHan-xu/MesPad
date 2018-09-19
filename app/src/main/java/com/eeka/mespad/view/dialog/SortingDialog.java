package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.http.WebServiceUtils;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 分拣扫码弹框
 */
public class SortingDialog extends BaseDialog implements View.OnClickListener {

    private String mTopic;

    public SortingDialog(@NonNull Context context, String topic) {
        super(context);
        mTopic = topic;
        init();
    }

    private EditText mEditText;
    private String mLastNum;

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_sorting, null);
        setContentView(view);

        mEditText = view.findViewById(R.id.et_sorting_code);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    mLastNum = mEditText.getText().toString();
                    sorting();
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        Button btn_jump = view.findViewById(R.id.btn_jump);
        if (TopicUtil.TOPIC_MANUAL.equals(mTopic)) {
            btn_jump.setVisibility(View.VISIBLE);
            btn_jump.setOnClickListener(this);
        }
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                mLastNum = mEditText.getText().toString();
                if (TextUtils.isEmpty(mLastNum)) {
                    Toast.makeText(mContext, "请输入条码后继续操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                sorting();
                break;
            case R.id.btn_jump:
                dismiss();
                EventBus.getDefault().post(true);
                break;
        }
    }

    private void sorting() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            ErrorDialog.showAlert(mContext, "站位数据未获取，请重启应用获取");
        } else {
            LoadingDialog.show(mContext);
            WebServiceUtils.sendProductMessage(contextInfo.getLINE_CATEGORY(), contextInfo.getPOSITION(), mLastNum, new WebServiceUtils.HttpCallBack() {
                @Override
                public void onSuccess(String method, JSONObject result) {
                    LoadingDialog.dismiss();
                    dismiss();
                    Toast.makeText(mContext, result.getString("message"), Toast.LENGTH_LONG).show();
                    if (TopicUtil.TOPIC_MANUAL.equals(mTopic)) {
                        EventBus.getDefault().post(true);
                    }
                }

                @Override
                public void onFail(String errMsg) {
                    LoadingDialog.dismiss();
                    ErrorDialog.showAlert(mContext, errMsg);
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.4));
    }

}
