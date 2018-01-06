package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.zxing.EncodingHandler;

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

    public static void showBarCode(Context context, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            msg = msg.split("\\.")[0];
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dlg_imageview, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(msg);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        try {
//            Bitmap barCode = EncodingHandler.create2Code(msg,400);
            Bitmap barCode = EncodingHandler.createBarCode(msg, 800, 400);
            imageView.setImageBitmap(barCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        dialog.show();
        dialog.getWindow().setLayout((int) (SystemUtils.getScreenWidth(context) * 0.5), (int) (SystemUtils.getScreenHeight(context) * 0.5));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
