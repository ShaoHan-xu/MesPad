package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 纯文字显示弹框
 * Created by Lenovo on 2017/9/21.
 */

public class MyAlertDialog {

    public static void showAlert(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dlg_alert, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_alertMsg);
        tipTextView.setText(msg);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        dialog.show();
        dialog.getWindow().setLayout(SystemUtils.getScreenWidth(context), SystemUtils.getScreenHeight(context));

        v.findViewById(R.id.btn_dlg_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
