package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.callback.StringCallback;
import com.eeka.mespad.utils.SystemUtils;

public class BindRfidCardDialog extends BaseDialog {

    private StringCallback mListener;

    private EditText mEditText;

    private String mOldRfid;

    private String mLastNum;

    BindRfidCardDialog(@NonNull Context context, String oldRfid, StringCallback listener) {
        super(context);
        mOldRfid = oldRfid;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_bindrfidcard, null);
        setContentView(mView);

        TextView tv_oldRfid = mView.findViewById(R.id.tv_oldRfid);
        tv_oldRfid.setText(mOldRfid);

        mEditText = mView.findViewById(R.id.et_newRfid);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String orderNum = mEditText.getText().toString();
                    if (!TextUtils.isEmpty(mLastNum)) {
                        mLastNum = orderNum.replaceFirst(mLastNum, "");
                    } else {
                        mLastNum = orderNum;
                    }
                    mEditText.setText(mLastNum);
                    if (TextUtils.isEmpty(mLastNum) || mLastNum.length() < 9 || mLastNum.length() > 10) {
                        ErrorDialog.showAlert(mContext, "卡号有误，请查验");
                        return true;
                    }
                    dismiss();
                    mListener.callback(mLastNum);
//                    editText.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            editText.requestFocus();
//                        }
//                    }, 50);
                    return true;
                }
                return false;
            }
        });

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String value = mEditText.getText().toString();
                mListener.callback(value);
            }
        });
    }

    private void showKeyboard() {
        if (BluetoothHelper.isConnectedScannerDevice()) {
            //如果平板连接着蓝牙输入设备，则在应用启动1秒后拉起使输入框获取焦点，拉起键盘
            //否则扫码输入时第一位会获取不到
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEditText.requestFocus();
                    SystemUtils.showSoftInputFromWindow(mContext);
                }
            }, 500);
        }
    }

    @Override
    public void show() {
        super.show();
        showKeyboard();
    }
}
