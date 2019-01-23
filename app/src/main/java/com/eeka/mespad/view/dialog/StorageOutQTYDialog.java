package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.eeka.mespad.R;
import com.eeka.mespad.callback.IntegerCallback;
import com.eeka.mespad.utils.SystemUtils;

public class StorageOutQTYDialog extends BaseDialog {

    private int mLesQTY;
    private IntegerCallback mCallback;

    public StorageOutQTYDialog(@NonNull Context context, String lessQTY, IntegerCallback callback) {
        super(context);
        mLesQTY = Integer.valueOf(lessQTY);
        mCallback = callback;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_inputoutqty, null);
        setContentView(mView);
        final EditText et_value = mView.findViewById(R.id.et_qty);

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mView.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_value.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ErrorDialog.showAlert(mContext, "出库数量不能为空");
                    return;
                }
                int qty = Integer.valueOf(s);
                if (qty > mLesQTY) {
                    ErrorDialog.showAlert(mContext, "出库数量不能大于库存剩余数量");
                    return;
                }
                if (mCallback != null) {
                    mCallback.callback(qty);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.3), (int) (SystemUtils.getScreenHeight(mContext) * 0.4));

    }
}
