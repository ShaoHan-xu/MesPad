package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

/**
 * Dialog基类，用于封装一些常用的方法和字段
 * Created by Lenovo on 2017/11/9.
 */

public class BaseDialog extends Dialog {

    protected Context mContext;
    protected View mView;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    protected void init(Context context) {
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
    }
}
