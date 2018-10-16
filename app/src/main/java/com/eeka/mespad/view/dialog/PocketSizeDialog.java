package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.PocketSizeBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SystemUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 袋口尺寸信息弹框
 */
public class PocketSizeDialog extends BaseDialog {

    private List<PocketSizeBo> mItems;
    private LinearLayout mLayout_item;

    public PocketSizeDialog(@NonNull Context context, String shopOrder, String sfc, String operation) {
        super(context);
        init();
        LoadingDialog.show(mContext);
        HttpHelper.getPocketSize(shopOrder, sfc, operation, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    JSONArray result = resultJSON.getJSONArray("result");
                    if (result != null) {
                        mItems = JSON.parseArray(result.toString(), PocketSizeBo.class);
                        initView();
                    } else {
                        ErrorDialog.showAlert(mContext, "无数据返回");
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
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_pocketsize, null);
        setContentView(view);
        mLayout_item = view.findViewById(R.id.layout_pocketSize);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initView() {
        for (PocketSizeBo item : mItems) {
            View child = LayoutInflater.from(mContext).inflate(R.layout.item_pocketsize, null);
            TextView tv_size = child.findViewById(R.id.tv_pocketSize_size);
            TextView tv_code = child.findViewById(R.id.tv_pocketSize_code);
            tv_size.setText(item.getVALUE());
            tv_code.setText(item.getPART_NO());

            ImageView imageView = child.findViewById(R.id.iv_pocketSize_img);
            Picasso.with(mContext).load(item.getPICTURE_URL()).placeholder(R.drawable.loading).error(R.drawable.ic_error_img).into(imageView);

            mLayout_item.addView(child);
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.7), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }
}
