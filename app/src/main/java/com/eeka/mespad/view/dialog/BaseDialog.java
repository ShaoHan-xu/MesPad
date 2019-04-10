package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.eeka.mespad.utils.SystemUtils;

/**
 * Dialog基类，用于封装一些常用的方法和字段
 * Created by Lenovo on 2017/11/9.
 */

public class BaseDialog extends Dialog {

    protected Context mContext;
    protected View mView;

    public BaseDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    protected void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

}
