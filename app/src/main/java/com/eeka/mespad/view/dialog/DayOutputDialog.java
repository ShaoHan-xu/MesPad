package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

public class DayOutputDialog extends BaseDialog {

    private LinearLayout mLayout_list;

    public DayOutputDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_dayoutput, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("日产量明细");

        mLayout_list = mView.findViewById(R.id.layout_dayOutput_list);

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void getData() {
        LoadingDialog.show(mContext);
        String userId = SpUtil.getLoginUserId();
        HttpHelper.padShowOpertion(userId, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                JSONObject resultRes = resultJSON.getJSONObject("result_response");
                String status = resultRes.getString("status");
                if ("Y".equals(status)) {
                    JSONArray result = new JSONArray();
                    Object row = resultRes.getJSONObject("result").getJSONObject("Rowset").get("Row");
                    if (row instanceof JSONObject) {
                        result.add(row);
                    } else {
                        result = (JSONArray) row;
                    }
                    for (int i = 0; i < result.size() + 1; i++) {
                        JSONObject item;
                        if (i == result.size()) {
                            //手动增加总计
                            item = new JSONObject();
                            item.put("DESCRIPTION", "总计");
                            item.put("QTY", result.getJSONObject(0).getIntValue("ALL_QTY"));
                        } else {
                            item = result.getJSONObject(i);
                        }
                        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dayoutput, null);
                        TextView tv_operationDesc = view.findViewById(R.id.tv_operationDesc);
                        TextView tv_qty = view.findViewById(R.id.tv_qty);
                        tv_operationDesc.setText(item.getString("DESCRIPTION"));
                        tv_qty.setText(item.getString("QTY"));

                        mLayout_list.addView(view);
                    }
                } else {
                    ErrorDialog.showAlert(mContext, resultRes.getString("result"));
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
        showOri();
        getData();
    }
}
