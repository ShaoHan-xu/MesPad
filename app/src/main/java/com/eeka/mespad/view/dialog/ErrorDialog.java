package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eeka.mespad.R;

public class ErrorDialog {

    private static AlertDialog mDialog;
    private static Handler mHandler = new Handler();

    /**
     * 错误提示弹框
     */
    public static void showAlert(Context context, String msg) {
        showAlert(context, msg, false);
    }

    public static void showAlert(Context context, String msg, boolean autoDismiss) {
        showAlert(context, msg, true, null, autoDismiss);
    }

    /**
     * 确认提示弹框
     */
    public static void showConfirmAlert(Context context, String msg, View.OnClickListener listener) {
        showAlert(context, msg, false, listener, false);
    }

    private static void showAlert(Context context, String msg, boolean error, final View.OnClickListener positiveListener, boolean autoDismiss) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_alert, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_alertMsg);
        tipTextView.setText(msg);

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mHandler.removeCallbacksAndMessages(null);
        if (autoDismiss) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();
                }
            }, 5000);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (error) {
            builder.setTitle("出现错误:");
        } else {
            builder.setTitle("温馨提示：");
        }
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (positiveListener != null) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveListener.onClick(null);
                }
            });
        }
        builder.setView(v);
        mDialog = builder.create();
        mDialog.show();
    }

    public static void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
