package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 选择粘朴方式弹框
 * Created by Lenovo on 2017/9/19.
 */

public class StickyDialog extends Dialog {

    private Context mContext;
    private String mProcessLot;

    public StickyDialog(@NonNull Context context, String processLot) {
        super(context);
        mContext = context;
        mProcessLot = processLot;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_sticky, null);
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }

    private void done() {
        final RadioGroup radioGroup = (RadioGroup) getWindow().findViewById(R.id.radioGroup_sticky);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            ErrorDialog.showAlert(mContext, "请选择一种粘朴方式");
        } else {
            LoadingDialog.show(mContext);
            int stickyCode = 0;
            switch (checkedId) {
                case R.id.rb_sticky_1:
                    stickyCode = 1;
                    break;
                case R.id.rb_sticky_2:
                    stickyCode = 2;
                    break;
                case R.id.rb_sticky_4:
                    stickyCode = 4;
                    break;
            }

            HttpHelper.tellFusingStyleToGST(mProcessLot, stickyCode, new HttpCallback() {
                @Override
                public void onSuccess(String url, JSONObject resultJSON) {
                    LoadingDialog.dismiss();
                    if (HttpHelper.isSuccess(resultJSON)) {
                        Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                    }
                }

                @Override
                public void onFailure(String url, int code, String message) {
                    LoadingDialog.dismiss();
                    ErrorDialog.showAlert(mContext, message);
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.8));
    }
}
