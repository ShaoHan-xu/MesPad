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

    public static void showDialog(Context context, String error) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_error, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.text_error);
        tipTextView.setText(error);

        errorDialog = new AlertDialog.Builder(context);//Dialog(context, R.style.error_dialog);
        errorDialog.setCancelable(false);
        errorDialog.setTitle("出现错误:");
        errorDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
