package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;

public class EfficiencyDialog extends BaseDialog {

    public EfficiencyDialog(@NonNull Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_efficiency_day, null);
        setContentView(mView);

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
        HttpHelper.padShowEffic(userId, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                JSONObject resultRes = resultJSON.getJSONObject("result_response");
                String status = resultRes.getString("status");
                if ("Y".equals(status)) {
                    JSONObject result = resultRes.getJSONObject("result").getJSONObject("Rowset").getJSONObject("Row");
                    TextView tv_userName = mView.findViewById(R.id.tv_userName);
                    TextView tv_prodEffic = mView.findViewById(R.id.tv_prodEffic);
                    TextView tv_monthProdEffic = mView.findViewById(R.id.tv_monthProdEffic);
                    TextView tv_efficRank = mView.findViewById(R.id.tv_efficiencyRank);
                    TextView tv_loginEffic = mView.findViewById(R.id.tv_loginEfficiency);
                    TextView tv_qty = mView.findViewById(R.id.tv_qty);
                    TextView tv_monthQty = mView.findViewById(R.id.tv_monthQty);

                    tv_userName.setText(result.getString("USER_NAME"));
                    tv_prodEffic.setText(result.getString("DAY_PROD_EFFIC"));
                    tv_monthProdEffic.setText(result.getString("MONTH_PROD_EFFIC"));
                    tv_efficRank.setText(result.getString("ROWNUM"));
                    tv_loginEffic.setText(result.getString("LOGIN_EFFIC"));
                    tv_qty.setText(result.getString("D_TOTAL_OPER_PRICE"));
                    tv_monthQty.setText(result.getString("AVG_OPER_PRICE"));
                } else {
                    ErrorDialog.showAlert(mContext, resultRes.getString("result"));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                ErrorDialog.showAlert(mContext, message);
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
