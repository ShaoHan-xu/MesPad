package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.eeka.mespad.R;
import com.eeka.mespad.callback.StringCallback;
import com.eeka.mespad.utils.SystemUtils;

public class AllCutDialog extends BaseDialog {

    private StringCallback mListener;
    private CheckBox mCkb_allCut;

    public AllCutDialog(@NonNull Context context, StringCallback listener) {
        super(context);
        mContext = context;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_allcut, null);
        setContentView(mView);

        mCkb_allCut = mView.findViewById(R.id.ckb_allCut);

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String value = "N";
                if (mCkb_allCut.isChecked()) {
                    value = "Y";
                }
                mListener.callback(value);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.4), (int) (SystemUtils.getScreenHeight(mContext) * 0.4));
    }
}
