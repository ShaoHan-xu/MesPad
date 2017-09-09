package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;

public class ErrorDialog {

    private static Dialog dialog;
    private static AlertDialog.Builder errorDialog;

    /**
     * 错误提示弹框
     */
    public static void showAlert(Context context, String msg) {
        showAlert(context, msg, true, null);
    }

    /**
     * 确认提示弹框
     */
    public static void showConfirmAlert(Context context, String msg, View.OnClickListener listener) {
        showAlert(context, msg, false, listener);
    }

    private static void showAlert(Context context, String msg, boolean error, final View.OnClickListener positiveListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_alert, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_alertMsg);
        tipTextView.setText(msg);

        errorDialog = new AlertDialog.Builder(context);
        if (error) {
            errorDialog.setTitle("出现错误:");
        } else {
            errorDialog.setTitle("温馨提示：");
        }
        errorDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (positiveListener != null) {
            errorDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveListener.onClick(null);
                }
            });
        }
        errorDialog.setView(v);
        errorDialog.show();
    }

    public static Dialog createDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.load_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText("正在处理...请稍后");

        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setCancelable(false);
        dialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return dialog;
    }

}
