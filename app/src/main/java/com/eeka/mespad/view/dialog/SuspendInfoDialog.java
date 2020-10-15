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

public class SuspendInfoDialog extends BaseDialog {

    public SuspendInfoDialog(@NonNull Context context, String shopOrder, String item, JSONArray jsonArray) {
        super(context);
        init(shopOrder, item, jsonArray);
    }

    protected void init(String shopOrder, String itemNum, JSONArray jsonArray) {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_suspend_orderinfo, null);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("订单明细");

        TextView tv_shopOrder = mView.findViewById(R.id.tv_shopOrder);
        tv_shopOrder.setText(shopOrder);
        TextView tv_itemNum = mView.findViewById(R.id.tv_itemNum);
        tv_itemNum.setText(itemNum);

        LinearLayout mLayout_list = mView.findViewById(R.id.layout_sizeCode_list);
        int qtyAmount = 0, scAmount = 0, surplusAmount = 0;
        for (int i = 0; i < jsonArray.size() + 1; i++) {
            JSONObject item;
            if (i == jsonArray.size()) {
                //手动增加总计
                item = new JSONObject();
                item.put("SIZE_CODE", "合计");
                item.put("SIZE_AMOUNT", qtyAmount);
                item.put("QTY_SC", scAmount);
                item.put("QTY_SURPLUS", surplusAmount);
            } else {
                item = jsonArray.getJSONObject(i);
                qtyAmount += Integer.parseInt(item.getString("SIZE_AMOUNT"));
                scAmount += Integer.parseInt(item.getString("QTY_SC"));
                surplusAmount += Integer.parseInt(item.getString("QTY_SURPLUS"));
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_suspend_orderinfo, null);
            TextView tv_sizeCode = view.findViewById(R.id.tv_sizeCode);
            TextView tv_amount = view.findViewById(R.id.tv_amount);
            TextView tv_sc = view.findViewById(R.id.tv_sc);
            TextView tv_surplus = view.findViewById(R.id.tv_surplus);
            tv_sizeCode.setText(item.getString("SIZE_CODE"));
            tv_amount.setText(item.getString("SIZE_AMOUNT"));
            tv_sc.setText(item.getString("QTY_SC"));
            tv_surplus.setText(item.getString("QTY_SURPLUS"));

            mLayout_list.addView(view);
        }

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void show() {
        showOri();
    }
}
