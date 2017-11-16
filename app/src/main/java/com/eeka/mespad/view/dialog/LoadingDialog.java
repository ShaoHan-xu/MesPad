package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.eeka.mespad.R;

/**
 * Created by Lenovo on 2017/8/12.
 */

public class LoadingDialog {

    private static Dialog mDialog;

    public static void show(Context context) {
        show(context, null);
    }

    public static void show(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_loading, null);
        if (!TextUtils.isEmpty(msg)) {
            TextView tv_loadingMsg = (TextView) view.findViewById(R.id.tv_loading_msg);
            tv_loadingMsg.setText(msg);
        }
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view);
        mDialog.show();
    }

    public static boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    public static void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
