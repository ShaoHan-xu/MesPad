package com.eeka.mespad.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.manager.Logger;

import org.greenrobot.eventbus.EventBus;

import cn.finalteam.okhttpfinal.LogUtil;

public class ErrorDialog {

    public enum TYPE {
        ERROR, ALERT, WARING
    }

    private static AlertDialog mDialog;

    private static String mLastMsg;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mDialog.dismiss();
            }
        }
    };

    /**
     * 错误提示弹框
     */
    public static void showAlert(Context context, String msg) {
        showAlert(context, msg, false);
    }

    /**
     * 错误提示弹框
     */
    public static void showAlert(Context context, String msg, DialogInterface.OnDismissListener listener) {
        showAlert(context, msg, TYPE.ERROR, null, false, listener);
    }

    public static void showAlert(Context context, String msg, boolean autoDismiss) {
        showAlert(context, msg, TYPE.ERROR, null, autoDismiss, null);
    }

    /**
     * 确认提示弹框
     */
    public static void showConfirmAlert(Context context, String msg, View.OnClickListener listener) {
        showAlert(context, msg, TYPE.ALERT, listener, false, null);
    }

    public static void showAlert(Context context, String msg, TYPE type, final View.OnClickListener positiveListener, boolean autoDismiss) {
        showAlert(context, msg, type, positiveListener, autoDismiss, null);
    }

    public static void showAlert(Context context, String msg, TYPE type, final View.OnClickListener positiveListener, boolean autoDismiss, final DialogInterface.OnDismissListener listener) {
        if (context == null) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        //衣架卡出站口时INA每7秒会推送一次错误报告，所以此处处理相同消息时重新计时10秒后弹框消失
        if (mLastMsg != null && mLastMsg.equals(msg) && mDialog != null && mDialog.isShowing()) {
            mHandler.sendEmptyMessageDelayed(0, 10000);
            return;
        }
        mLastMsg = msg;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_alert, null);
        TextView tipTextView = v.findViewById(R.id.tv_txtAlertMsg);
        tipTextView.setText(msg);

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (autoDismiss) {
            mHandler.sendEmptyMessageDelayed(0, 10000);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (type == TYPE.ERROR) {
            builder.setTitle("出现错误:");
        } else if (type == TYPE.WARING) {
            builder.setTitle("警告：");
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
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeCallbacksAndMessages(null);
                if (listener != null) {
                    listener.onDismiss(dialog);
                } else {
                    PushJson push = new PushJson();
                    push.setType(PushJson.TYPE_ErrDialogDismiss);
                    EventBus.getDefault().post(push);
                }
            }
        });
        builder.setView(v);
        mDialog = builder.create();
        if (!((Activity) context).isFinishing()) {
            mDialog.show();
        }
    }

    public static void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
