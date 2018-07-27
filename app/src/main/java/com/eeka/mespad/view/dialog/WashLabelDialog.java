package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 上裁绑定洗水唛弹框
 */
public class WashLabelDialog extends BaseDialog {

    private String mSFC;
    private String mPart;

    private EditText mEt_washLabel;

    public WashLabelDialog(@NonNull Context context, String sfc, String part) {
        super(context);
        mSFC = sfc;
        mPart = part;
        init();
    }

    private String mLastNum;

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_washlabel, null);
        setContentView(view);

        TextView tv_sfc = view.findViewById(R.id.tv_washLabel_SFC);
        tv_sfc.setText(mSFC);
        TextView tv_part = view.findViewById(R.id.tv_washLabel_part);
        tv_part.setText(mPart);
        mEt_washLabel = view.findViewById(R.id.et_washLabel_washLabel);
        mEt_washLabel.requestFocus();
        SystemUtils.showSoftInputFromWindow(mContext);
        mEt_washLabel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String orderNum = mEt_washLabel.getText().toString();
                    if (!TextUtils.isEmpty(mLastNum)) {
                        mLastNum = orderNum.replaceFirst(mLastNum, "");
                    } else {
                        mLastNum = orderNum;
                    }
                    mEt_washLabel.setText(mLastNum);
                    return true;
                }
                return false;
            }
        });
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String washLabel = mEt_washLabel.getText().toString();
                if (TextUtils.isEmpty(washLabel)) {
                    Toast.makeText(mContext, "请输入洗水唛编号", Toast.LENGTH_SHORT).show();
                } else {
                    EventBus.getDefault().post(washLabel);
                    dismiss();
                }
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.7), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }
}
