package com.eeka.mespad.view.widget;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 解决 7.0 版本showAsDropDown时位置错误的问题
 */
public class MyPopWindow extends PopupWindow {

    public MyPopWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    @Override
    public void showAsDropDown(View anchor) {
        fixVersion24(anchor);
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        fixVersion24(anchor);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        fixVersion24(anchor);
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    /**
     * 修复版本号 24 时显示位置错误导致高度全屏的问题
     */
    private void fixVersion24(View anchor) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
    }

}
