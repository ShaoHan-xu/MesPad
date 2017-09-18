package com.eeka.mespad.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

/**
 * 制卡弹框
 * Created by Lenovo on 2017/9/8.
 */

public class CreateCardDialog extends Dialog implements View.OnClickListener, HttpCallback {

    private Context mContext;
    private View mView;

    private TextView mTv_site;
    private EditText mEt_sfc, mEt_hangerId;
    private TextView mTv_sfc;
    private TextView mTv_hangerId;
    private TextView mTv_orderNum;
    private TextView mTv_operation;
    private TextView mTv_operationDesc;
    private EditText mEt_rfidNum;

    private String mSFC, mRfId, mHangerId;

    public CreateCardDialog(@NonNull Context context, String sfc) {
        super(context);
        mSFC = sfc;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dlg_create_card, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mView);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        mTv_site = (TextView) mView.findViewById(R.id.tv_createCard_site);
        mEt_sfc = (EditText) mView.findViewById(R.id.et_createCard_sfc);
        mEt_hangerId = (EditText) mView.findViewById(R.id.et_createCard_hangerId);
        mTv_sfc = (TextView) mView.findViewById(R.id.tv_createCard_sfc);
        mTv_hangerId = (TextView) mView.findViewById(R.id.tv_createCard_hangerId);
        mTv_orderNum = (TextView) mView.findViewById(R.id.tv_createCard_orderNum);
        mTv_operation = (TextView) mView.findViewById(R.id.tv_createCard_operation);
        mTv_operationDesc = (TextView) mView.findViewById(R.id.tv_createCard_operationDesc);
        mEt_rfidNum = (EditText) mView.findViewById(R.id.et_createCard_rfidNum);

        mView.findViewById(R.id.btn_createCard_search).setOnClickListener(this);
        mView.findViewById(R.id.btn_createCard_createCard).setOnClickListener(this);
        mView.findViewById(R.id.btn_createCard_unbind).setOnClickListener(this);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(this);

        mTv_site.setText("站点：" + SpUtil.getSite());
        mTv_sfc.setText(mSFC);

        if (!TextUtils.isEmpty(mSFC)) {
            search();
        }
    }

    /**
     * 搜索
     */
    private void search() {
        mSFC = mEt_sfc.getText().toString();
        mHangerId = mEt_hangerId.getText().toString();
        if (!TextUtils.isEmpty(mSFC)) {
            LoadingDialog.show(mContext);
            HttpHelper.findCardInfoBySfcOrHangerId(mSFC, null, this);
        } else if (!TextUtils.isEmpty(mHangerId)) {
            LoadingDialog.show(mContext);
            HttpHelper.findCardInfoBySfcOrHangerId(null, mHangerId, this);
        } else {
            ErrorDialog.showAlert(mContext, "请输入搜索条件");
        }

    }

    private void initData(JSONObject json) {
        mSFC = json.getString("sfc");
        mHangerId = json.getString("hangerId");
        mRfId = json.getString("rfid");

        mTv_sfc.setText(mSFC);
        mTv_hangerId.setText(mHangerId);
        mTv_orderNum.setText(json.getString("shopOrder"));
        mTv_operation.setText(json.getString("operation"));
        mTv_operationDesc.setText(json.getString("operationDesc"));
        mEt_rfidNum.setText(mRfId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_createCard_search:
                search();
                break;
            case R.id.btn_createCard_createCard:
                mRfId = mEt_rfidNum.getText().toString();
                LoadingDialog.show(mContext);
                HttpHelper.createCard(mSFC, mRfId, this);
                break;
            case R.id.btn_createCard_unbind:
                if (TextUtils.isEmpty(mHangerId)) {
                    ErrorDialog.showAlert(mContext, "衣架号为空，请确认已制卡后再进行解绑操作;\n如果已制卡，请查询出衣架号，不为空后继续解绑。");
                    return;
                }
                LoadingDialog.show(mContext);
                JSONObject json = new JSONObject();
                json.put("HANGER_ID", mHangerId);
                HttpHelper.hangerUnbind(json, this);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        LoadingDialog.dismiss();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.createCard.equals(url)) {
                Toast.makeText(mContext, "制卡成功", Toast.LENGTH_SHORT).show();
            } else if (HttpHelper.hangerUnbind.equals(url)) {
                Toast.makeText(mContext, "解绑成功", Toast.LENGTH_SHORT).show();
            } else if (HttpHelper.findCardInfoBySfcOrHangerId.equals(url)) {
                initData(resultJSON.getJSONObject("result"));
            }
        } else {
            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));
        }
    }

    @Override
    public void onFailure(String url, int code, String message) {
        ErrorDialog.showAlert(mContext, message);
    }
}
