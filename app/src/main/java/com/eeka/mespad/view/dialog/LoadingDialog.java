package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.eeka.mespad.R;

/**
 * Created by Lenovo on 2017/8/12.
 */

public class LoadingDialog {

    public static void show(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_loading, null);
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.show();
    }

    public static void show(Context context, String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_loading, null);
        TextView tv_loadingMsg = (TextView) view.findViewById(R.id.tv_loading_msg);
        tv_loadingMsg.setText(msg);
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.show();

    }
}
