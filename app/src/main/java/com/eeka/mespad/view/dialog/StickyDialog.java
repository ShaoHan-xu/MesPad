package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 选择粘朴方式弹框
 * Created by Lenovo on 2017/9/19.
 */

public class StickyDialog extends Dialog implements HttpCallback {

    private Context mContext;
    private String mProcessLot;
    private LinearLayout mLayout_sticky;
    private List<DictionaryDataBo> mList;

    public StickyDialog(@NonNull Context context, String processLot) {
        super(context);
        mContext = context;
        mProcessLot = processLot;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_sticky, null);
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mLayout_sticky = (LinearLayout) view.findViewById(R.id.layout_sticky);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);

        mList = SpUtil.getDictionaryData(DictionaryDataBo.CODE_STICKY);
        if (mList == null || mList.size() == 0) {
            LoadingDialog.show(mContext);
            HttpHelper.getDictionaryData(DictionaryDataBo.CODE_STICKY, this);
        } else {
            initView();
        }
    }

    private void initView() {
        mLayout_sticky.removeAllViews();
        for (int i = 0; i < mList.size(); i++) {
            DictionaryDataBo item = mList.get(i);
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setTextSize(18);
            checkBox.setPadding(10, 10, 10, 10);
            checkBox.setTextColor(mContext.getResources().getColor(R.color.text_black_default));
            checkBox.setText(item.getLABEL());
            checkBox.setTag(item.getVALUE());
            mLayout_sticky.addView(checkBox);
        }
    }

    private int checkedCount;//选择粘朴方式个数

    private void done() {
        checkedCount = 0;
        int count = mLayout_sticky.getChildCount();
        for (int i = 0; i < count; i++) {
            CheckBox checkBox = (CheckBox) mLayout_sticky.getChildAt(i);
            if (checkBox.isChecked()) {
                checkedCount++;
                if (!LoadingDialog.isShowing()) {
                    LoadingDialog.show(mContext);
                }
                String stickyCode = (String) checkBox.getTag();
                HttpHelper.tellFusingStyleToGST(mProcessLot, stickyCode, this);
            }
        }
        if (checkedCount == 0) {
            ErrorDialog.showAlert(mContext, "请选择一种粘朴方式");
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.8));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getDictionaryData.equals(url)) {
                mList = JSON.parseArray(resultJSON.getJSONArray("result").toString(), DictionaryDataBo.class);
                SpUtil.saveDictionaryData(DictionaryDataBo.CODE_STICKY, JSON.toJSONString(mList));
                initView();
            } else if (HttpHelper.tellFusingStyleToGST.equals(url)) {
                checkedCount--;
                if (checkedCount == 0) {
                    Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        } else {
            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
        }
        LoadingDialog.dismiss();
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showAlert(mContext, message);
    }
}
