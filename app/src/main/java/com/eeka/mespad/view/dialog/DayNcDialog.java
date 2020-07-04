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

public class DayNcDialog extends BaseDialog {

    private LinearLayout mLayout_list;

    public DayNcDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_daync, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("返修信息");

        mLayout_list = mView.findViewById(R.id.layout_dayNc_list);

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
        HttpHelper.padShowOpeartionNc(userId, new HttpCallback() {
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
                            item.put("OP_DESCRIPTION", result.size() + "");
                        } else {
                            item = result.getJSONObject(i);
                        }
                        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daync, null);
                        TextView tv_sfc = view.findViewById(R.id.tv_sfc);
                        TextView tv_operationDesc = view.findViewById(R.id.tv_operationDesc);
                        TextView tv_ncDesc = view.findViewById(R.id.tv_ncDesc);
                        TextView tv_user = view.findViewById(R.id.tv_user);
                        tv_sfc.setText(item.getString("SFC"));
                        tv_operationDesc.setText(item.getString("OP_DESCRIPTION"));
                        tv_ncDesc.setText(item.getString("NC_DESCRIPTION"));
                        tv_user.setText(item.getString("USER_NAME"));

                        mLayout_list.addView(view);
                    }
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
