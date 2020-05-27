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

    public BaseDialog(@NonNull Context context,int theme) {
        super(context,theme);
        mContext = context;
    }

    protected void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    private float mWidth = 0.8f;
    private float mHeight = 0.9f;

    public BaseDialog setWidth(float width) {
        mWidth = width;
        return this;
    }

    public BaseDialog setHeight(float height) {
        mHeight = height;
        return this;
    }

    public BaseDialog setParams(float width, float height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    /**
     * 不自定义宽高
     */
    void showOri(){
        super.show();
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * mWidth), (int) (SystemUtils.getScreenHeight(mContext) * mHeight));
    }

}
