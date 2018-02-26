package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.eeka.mespad.R;

/**
 * 裁剪记件弹框
 * Created by xushaohan on 2018/2/24.
 */

public class CutRecordQtyDialog extends BaseDialog {

    public CutRecordQtyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_cutrecordqty, null);
        setContentView(view);


    }
}
