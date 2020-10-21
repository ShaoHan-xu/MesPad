package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;

public class ReportWorkDialog extends BaseDialog {

    private String mShopOrder;
    private LinearLayout mLayout_list;

    public ReportWorkDialog(@NonNull Context context, String shopOrder) {
        super(context);
        mShopOrder = shopOrder;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_cutreportwork, null);
        setContentView(mView);

        mLayout_list = findViewById(R.id.layout_sizeCode_list);

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyQty()) {
                    submit();
                }
            }
        });

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean verifyQty() {
        int childCount = mLayout_list.getChildCount();
        boolean flag = true;
        StringBuilder sb = new StringBuilder("码数:");
        for (int i = 1; i < childCount; i++) {
            View childAt = mLayout_list.getChildAt(i);
            TextView tv_amount = childAt.findViewById(R.id.tv_AMOUNT);
            EditText et_sc = childAt.findViewById(R.id.et_SC_QTY);
            String amount = tv_amount.getText().toString();
            String sc = et_sc.getText().toString();
            if (!amount.equals(sc)) {
                flag = false;
                TextView tv_sizeCode = childAt.findViewById(R.id.tv_sizeCode);
                String sizeCode = tv_sizeCode.getText().toString();
                sb.append(sizeCode);
                if (i != childCount - 1) {
                    sb.append(",");
                }
            }
        }
        if (!flag) {
            sb.append("订单数与实裁数不一致，请与组长确认.");
            ErrorDialog.showConfirmAlert(mContext, sb.toString(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });
        }
        return flag;
    }

    private void submit() {
        int childCount = mLayout_list.getChildCount();
        int allQty = 0;
        for (int i = 1; i < childCount; i++) {
            View childAt = mLayout_list.getChildAt(i);
            EditText et_sc = childAt.findViewById(R.id.et_SC_QTY);
            String sc = et_sc.getText().toString();
            int qty = FormatUtil.strToInt(sc);
            allQty += qty;
        }

    }

    private void getData() {
        LoadingDialog.show(mContext);
        HttpHelper.getReportWorkSizeInfo(mShopOrder, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray array = resultJSON.getJSONArray("result");
                    if (array != null && array.size() > 0) {
                        for (int i = 0; i < array.size(); i++) {
                            View view = LayoutInflater.from(mContext).inflate(R.layout.item_reportwork, null);
                            TextView tv_sizeCode = view.findViewById(R.id.tv_sizeCode);
                            TextView tv_amount = view.findViewById(R.id.tv_AMOUNT);
                            TextView tv_FB_QTY = view.findViewById(R.id.tv_FB_QTY);
                            EditText et_SC_QTY = view.findViewById(R.id.et_SC_QTY);
                            TextView tv_different = view.findViewById(R.id.tv_different);
                            JSONObject object = array.getJSONObject(i);
                            tv_sizeCode.setText(object.getString("SIZE_CODE"));
                            tv_amount.setText(object.getString("SIZE_AMOUNT"));
                            tv_FB_QTY.setText(object.getString("FB_QTY"));
                            et_SC_QTY.setText(object.getString("SC_QTY"));
                            tv_different.setText(object.getIntValue("SIZE_AMOUNT") - object.getIntValue("SC_QTY"));
                            mLayout_list.addView(view);
                        }
                    }
                } else {
                    ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
            }
        });
    }

    @Override
    public void show() {
        showOri();
        getData();
    }
}
