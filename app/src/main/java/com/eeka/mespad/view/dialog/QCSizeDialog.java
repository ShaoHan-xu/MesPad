package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.QCSizeItemBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

public class QCSizeDialog extends BaseDialog {

    private String mShopOrder;
    private String mSize;

    public QCSizeDialog(@NonNull Context context, String shopOrder, String size) {
        super(context);
        mShopOrder = shopOrder;
        mSize = size;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_qcsize, null);
        setContentView(mView);
        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        LoadingDialog.show(mContext);
        HttpHelper.getQCSize(mShopOrder, mSize, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    List<QCSizeItemBo> list = JSON.parseArray(resultJSON.getJSONArray("result").toString(), QCSizeItemBo.class);
                    if (list != null && list.size() != 0) {
                        LinearLayout layout_item = mView.findViewById(R.id.layout_qcSize_items);
                        for (QCSizeItemBo item : list) {
                            View view = LayoutInflater.from(mContext).inflate(R.layout.item_qcsize, null);
                            TextView tv_code = view.findViewById(R.id.tv_qcSize_code);
                            TextView tv_name = view.findViewById(R.id.tv_qcSize_name);
                            TextView tv_size = view.findViewById(R.id.tv_qcSize_size);
                            TextView tv_unit = view.findViewById(R.id.tv_qcSize_unit);
                            tv_code.setText(item.getMEASURED_ATTRIBUTE());
                            tv_name.setText(item.getMEASURED_DESC());
                            tv_size.setText(item.getSIZE());
                            tv_unit.setText(item.getUNIT());

                            layout_item.addView(view);
                        }
                    } else {
                        ErrorDialog.showAlert(mContext, "该件无质检尺寸数据");
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
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.6), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
        initData();
    }
}
