package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 根据吊牌走分拣系统
 */
public class SortForClothTagDialog extends BaseDialog {

    public SortForClothTagDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_sortfortag, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("通过吊牌号走分拣系统");

        EditText et_hangerId = mView.findViewById(R.id.et_sortForTag_hangerId);
        et_hangerId.requestFocus();
        SystemUtils.showSoftInputFromWindow(mContext);

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    private void done() {
        EditText et_hangerId = mView.findViewById(R.id.et_sortForTag_hangerId);
        String hangerID = et_hangerId.getText().toString();
        if (TextUtils.isEmpty(hangerID) || hangerID.length() != 10) {
            ErrorDialog.showAlert(mContext, "衣架号有误，请核对");
            return;
        }
        EditText et_tag = mView.findViewById(R.id.et_sortForTag_tag);
        String tag = et_tag.getText().toString();

        LoadingDialog.show(mContext);
        HttpHelper.sortForClothTag(tag, hangerID, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    ErrorDialog.showAlert(mContext, resultJSON.getString("result"));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.6), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }
}
