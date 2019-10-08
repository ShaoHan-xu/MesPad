package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;

public class LabuCheckDialog extends BaseDialog {

    private View.OnClickListener mListener;

    public LabuCheckDialog(@NonNull Context context, View.OnClickListener listener) {
        super(context);
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_labu_check, null);
        setContentView(mView);

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check() {
        boolean flag = true;
        LinearLayout layout = mView.findViewById(R.id.layout_checkBtn);
        for (int i = 0; i < layout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) layout.getChildAt(i);
            if (!checkBox.isChecked()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            dismiss();
            mListener.onClick(null);
        } else {
            ErrorDialog.showAlert(mContext, "请勾选所有检查项");
        }
    }
}
