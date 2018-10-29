package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ReworkListBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;

import java.util.List;

public class ReworkListDialog extends BaseDialog {

    private String mSFC;
    private LinearLayout mLayout_reworkList;

    public ReworkListDialog(@NonNull Context context, String sfc) {
        super(context);
        mSFC = sfc;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_reworklist, null);
        mLayout_reworkList = view.findViewById(R.id.layout_reworkList);
        setContentView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void getData() {
        LoadingDialog.show(mContext);
        HttpHelper.listOffLineReWorkInfo(mSFC, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray array = resultJSON.getJSONArray("result");
                    if (array != null) {
                        List<ReworkListBo> list_data = JSON.parseArray(array.toString(), ReworkListBo.class);
                        for (ReworkListBo item : list_data) {
                            View view1 = LayoutInflater.from(mContext).inflate(R.layout.item_reworklist, null);
                            TextView tv_operation = view1.findViewById(R.id.tv_reworkList_operation);
                            TextView tv_operationDesc = view1.findViewById(R.id.tv_reworkList_operationDesc);
                            TextView tv_userID = view1.findViewById(R.id.tv_reworkList_userID);
                            TextView tv_userName = view1.findViewById(R.id.tv_reworkList_userName);
                            tv_operation.setText(item.getOPERATION());
                            tv_operationDesc.setText(item.getDESCRIPTION());
                            tv_userID.setText(item.getUSER_ID());
                            tv_userName.setText(item.getUSER_NAME());
                            mLayout_reworkList.addView(view1);
                        }
                    }
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
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
        getData();//防止方法内的加载弹框被此弹框覆盖
    }
}
