package com.eeka.mespad.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.BatchSplitPackagePrintBo;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ManualPrintSubPackageDialog extends BaseDialog implements HttpCallback {

    private EditText mEt_rfid;
    private EditText mEt_packageNo;

    public ManualPrintSubPackageDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_print_manual, null);
        setContentView(mView);

        mEt_rfid = mView.findViewById(R.id.et_rfid);
        mEt_packageNo = mView.findViewById(R.id.et_packageNo);

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EventBus.getDefault().unregister(ManualPrintSubPackageDialog.this);
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rfid = mEt_rfid.getText().toString();
                if (TextUtils.isEmpty(rfid)) {
                    ErrorDialog.showAlert(mContext, "请输入 RFID 卡号");
                    return;
                }

                String packageNo = mEt_packageNo.getText().toString();
                int anInt = FormatUtil.strToInt(packageNo);
                if (anInt < 100) {
                    ErrorDialog.showAlert(mContext, "请输入分包号，且分包号不能少于 3 位数");
                    return;
                }

                LoadingDialog.show(mContext);
                HttpHelper.getSubPackageInfoByRfid(rfid, ManualPrintSubPackageDialog.this);
            }
        });
    }

    /**
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(PushJson push) {
        String type = push.getType();
        if (PushJson.TYPE_RFID.equals(type)) {
            String rfid = push.getContent();
            mEt_rfid.setText(rfid);
            mEt_packageNo.requestFocus();
            SystemUtils.showSoftInputFromWindow(mContext);
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            JSONArray result = resultJSON.getJSONArray("result");
            BatchSplitPackagePrintBo printBo = JSONObject.parseObject(result.get(0).toString(), BatchSplitPackagePrintBo.class);
            printBo.setMatType("M");
            BluetoothHelper.printSubPackageInfo((Activity) mContext, printBo);
            LoadingDialog.dismiss();
            dismiss();
        } else {
            LoadingDialog.dismiss();
            ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showAlert(mContext, message);
    }

    @Override
    public void show() {
        super.show();
        EventBus.getDefault().register(ManualPrintSubPackageDialog.this);
    }
}
