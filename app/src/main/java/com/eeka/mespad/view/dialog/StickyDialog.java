package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup mRadioGroup;
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

        mRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_sticky);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        setCanceledOnTouchOutside(false);

        mList = SpUtil.getDictionaryData(DictionaryDataBo.CODE_STICKY);
        if (mList == null || mList.size() == 0) {
            LoadingDialog.show(mContext);
            HttpHelper.getDictionaryData(DictionaryDataBo.CODE_STICKY, new HttpCallback() {
                @Override
                public void onSuccess(String url, JSONObject resultJSON) {
                    if (HttpHelper.isSuccess(resultJSON)) {
                        mList = JSON.parseArray(resultJSON.getJSONArray("result").toString(), DictionaryDataBo.class);
                        SpUtil.saveDictionaryData(DictionaryDataBo.CODE_STICKY,JSON.toJSONString(mList));
                        initView();
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
            });
        } else {
            initView();
        }
    }

    private void initView() {
        mRadioGroup.removeAllViews();
        for (int i = 0; i < mList.size(); i++) {
            DictionaryDataBo item = mList.get(i);
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setTextSize(18);
            radioButton.setTextColor(mContext.getResources().getColor(R.color.text_black_default));
            radioButton.setText(item.getLABEL());
            int id = R.id.radioButton1 + i;
            radioButton.setId(id);
            radioButton.setTag(item.getVALUE());
            mRadioGroup.addView(radioButton);
        }
    }

    private void done() {
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(checkedId);
        if (checkedId == -1) {
            ErrorDialog.showAlert(mContext, "请选择一种粘朴方式");
        } else {
            LoadingDialog.show(mContext);
            String stickyCode = (String) radioButton.getTag();

            HttpHelper.tellFusingStyleToGST(mProcessLot, stickyCode, new HttpCallback() {
                @Override
                public void onSuccess(String url, JSONObject resultJSON) {
                    LoadingDialog.dismiss();
                    if (HttpHelper.isSuccess(resultJSON)) {
                        Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
                    }
                }

                @Override
                public void onFailure(String url, int code, String message) {
                    LoadingDialog.dismiss();
                    ErrorDialog.showAlert(mContext, message);
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.8));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {

    }

    @Override
    public void onFailure(String url, int code, String message) {

    }
}
