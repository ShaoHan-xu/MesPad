package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.WebServiceUtils;
import com.eeka.mespad.utils.SpUtil;

/**
 * 模拟上裁刷卡弹框
 */
public class HangerSwipeDialog extends BaseDialog {

    public HangerSwipeDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_hangerswipe, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("模拟上裁刷卡");

        EditText et_site = mView.findViewById(R.id.et_site);
        et_site.setText(SpUtil.getSite());

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        EditText et_site = mView.findViewById(R.id.et_site);
        EditText et_hangerId = mView.findViewById(R.id.et_hangerId);
        EditText et_lineId = mView.findViewById(R.id.et_lineId);
        EditText et_stationId = mView.findViewById(R.id.et_stationId);
        EditText et_tag = mView.findViewById(R.id.et_tag);

        String site = et_site.getText().toString();
        String hangerId = et_hangerId.getText().toString();
        String lineId = et_lineId.getText().toString();
        String stationId = et_stationId.getText().toString();
        String tag = et_tag.getText().toString();

        if (isEmpty(site) || isEmpty(hangerId) || isEmpty(lineId) || isEmpty(stationId)) {
            Toast.makeText(mContext, "请输入所有参数", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("site", site);
        json.put("hangerId", hangerId);
        json.put("lineId", lineId);
        json.put("stationId", stationId);
        json.put("tag", tag);
        LoadingDialog.show(mContext);
        WebServiceUtils.hangerBind(json, new WebServiceUtils.HttpCallBack() {
            @Override
            public void onSuccess(String method, com.alibaba.fastjson.JSONObject result) {
                LoadingDialog.dismiss();
                if("0".equals(result.getString("code"))){
                    Toast.makeText(mContext,"操作成功",Toast.LENGTH_SHORT).show();
                }else{
                    ErrorDialog.showAlert(mContext,result.getString("message"));
                }
            }

            @Override
            public void onFail(String errMsg) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, errMsg);
            }
        });

    }

    @Override
    public void show() {
        showOri();
    }
}
