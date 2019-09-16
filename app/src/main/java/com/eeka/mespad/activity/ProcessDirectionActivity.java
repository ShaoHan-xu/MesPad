package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.ProcessDirectionBo;
import com.eeka.mespad.callback.IntegerCallback;
import com.eeka.mespad.callback.StringCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.view.dialog.AllCutDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工序流确认
 */
public class ProcessDirectionActivity extends BaseActivity {

    private String mShopOrderBo;

    private ProcessDirectionBo mData;

    private CheckedChangeListener mListener;

    private CheckBox mCkb_mianBu, mCkb_liBu, mCkb_poBu;
    private ImageView mIv_liBuTag, mIv_poBuTag;
    private Map<String, CheckBox> mMap_processCheckBox;
    private Map<String, ImageView> mMap_processLock;

    private String mMatType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_process_direction);

        mShopOrderBo = getIntent().getStringExtra("shopOrderBo");

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("工序流确认");
        findViewById(R.id.btn_back).setOnClickListener(this);

        TextView tv_shopOrder = findViewById(R.id.tv_processDirection_shopOrder);
        tv_shopOrder.setText(getIntent().getStringExtra("shopOrder"));
        TextView tv_item = findViewById(R.id.tv_processDirection_item);
        tv_item.setText(getIntent().getStringExtra("item"));

        findViewById(R.id.btn_processDirection_submit).setOnClickListener(this);

        mListener = new CheckedChangeListener();
        mCkb_mianBu = findViewById(R.id.ckb_mianBu);
        mCkb_liBu = findViewById(R.id.ckb_liBu);
        mCkb_poBu = findViewById(R.id.ckb_poBu);
        mCkb_mianBu.setOnCheckedChangeListener(mListener);

        mIv_liBuTag = findViewById(R.id.iv_processDirection_liBuTag);
        mIv_poBuTag = findViewById(R.id.iv_processDirection_poBuTag);

        CheckBox ckb_SP = findViewById(R.id.ckb_SP);
        CheckBox ckb_DS = findViewById(R.id.ckb_DS);
        CheckBox ckb_CA = findViewById(R.id.ckb_CA);
        CheckBox ckb_DB = findViewById(R.id.ckb_DB);
        CheckBox ckb_FI = findViewById(R.id.ckb_FI);
        CheckBox ckb_CP = findViewById(R.id.ckb_CP);
        CheckBox ckb_CK = findViewById(R.id.ckb_CK);
        CheckBox ckb_FB = findViewById(R.id.ckb_FB);
        CheckBox ckb_RK = findViewById(R.id.ckb_RK);
        mMap_processCheckBox = new HashMap<>();
        mMap_processCheckBox.put("SP", ckb_SP);
        mMap_processCheckBox.put("DS", ckb_DS);
        mMap_processCheckBox.put("CA", ckb_CA);
        mMap_processCheckBox.put("DB", ckb_DB);
        mMap_processCheckBox.put("FI", ckb_FI);
        mMap_processCheckBox.put("CP", ckb_CP);
        mMap_processCheckBox.put("CK", ckb_CK);
        mMap_processCheckBox.put("FB", ckb_FB);
        mMap_processCheckBox.put("RK", ckb_RK);

        ImageView iv_SP = findViewById(R.id.iv_lock_SP);
        ImageView iv_DS = findViewById(R.id.iv_lock_DS);
        ImageView iv_CA = findViewById(R.id.iv_lock_CA);
        ImageView iv_DB = findViewById(R.id.iv_lock_DB);
        ImageView iv_FI = findViewById(R.id.iv_lock_FI);
        ImageView iv_CP = findViewById(R.id.iv_lock_CP);
        ImageView iv_CK = findViewById(R.id.iv_lock_CK);
        ImageView iv_FB = findViewById(R.id.iv_lock_FB);
        ImageView iv_RK = findViewById(R.id.iv_lock_RK);
        mMap_processLock = new HashMap<>();
        mMap_processLock.put("SP", iv_SP);
        mMap_processLock.put("DS", iv_DS);
        mMap_processLock.put("CA", iv_CA);
        mMap_processLock.put("DB", iv_DB);
        mMap_processLock.put("FI", iv_FI);
        mMap_processLock.put("CP", iv_CP);
        mMap_processLock.put("CK", iv_CK);
        mMap_processLock.put("FB", iv_FB);
        mMap_processLock.put("RK", iv_RK);
    }

    @Override
    protected void initData() {
        super.initData();

        getData();
    }

    private void getData() {
        showLoading();
        HttpHelper.getProcessDirection(mShopOrderBo, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_processDirection_submit) {
            ImageView iv_readedTag;
            if (mCkb_liBu.isEnabled()) {
                iv_readedTag = findViewById(R.id.iv_processDirection_liBuTag);
                if (iv_readedTag.getVisibility() == View.GONE) {
                    showErrorDialog("请查看所有面料工序流后再提交");
                    return;
                }
            }
            if (mCkb_poBu.isEnabled()) {
                iv_readedTag = findViewById(R.id.iv_processDirection_poBuTag);
                if (iv_readedTag.getVisibility() == View.GONE) {
                    showErrorDialog("请查看所有面料工序流后再提交");
                    return;
                }
            }

            new AllCutDialog(mContext, new StringCallback() {
                @Override
                public void callback(String value) {
                    mData.setIsAllCut(value);
                    showLoading();
                    HttpHelper.submitProcessDirection(mData, ProcessDirectionActivity.this);
                }
            }).show();

        }
    }

    private void setupTabView() {
        if (mData != null) {
            mCkb_mianBu.setChecked(true);
            List<ProcessDirectionBo.CUTFLOWTEMPLETEBean> cutFlows = mData.getMaterialCutFlows();
            for (ProcessDirectionBo.CUTFLOWTEMPLETEBean item : cutFlows) {
                CheckBox checkBox = null;
                if ("L".equals(item.getMaterialType())) {
                    checkBox = mCkb_liBu;
                } else if ("N".equals(item.getMaterialType())) {
                    checkBox = mCkb_poBu;
                }
                if (checkBox != null) {
                    checkBox.setEnabled(true);
                    checkBox.setOnCheckedChangeListener(mListener);
                }
            }
        }
    }

    private class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.ckb_mianBu:
                        mCkb_liBu.setChecked(false);
                        mCkb_poBu.setChecked(false);
                        setupProcessView("M");
                        break;
                    case R.id.ckb_liBu:
                        if (mIv_liBuTag.getVisibility() == View.GONE)
                            mIv_liBuTag.setVisibility(View.VISIBLE);
                        mCkb_mianBu.setChecked(false);
                        mCkb_poBu.setChecked(false);
                        setupProcessView("L");
                        break;
                    case R.id.ckb_poBu:
                        if (mIv_poBuTag.getVisibility() == View.GONE)
                            mIv_poBuTag.setVisibility(View.VISIBLE);
                        mCkb_mianBu.setChecked(false);
                        mCkb_liBu.setChecked(false);
                        setupProcessView("N");
                        break;
                }
            }

            List<ProcessDirectionBo.CUTFLOWTEMPLETEBean> cutFlows = mData.getMaterialCutFlows();
            ProcessDirectionBo.CUTFLOWTEMPLETEBean bean = null;
            for (ProcessDirectionBo.CUTFLOWTEMPLETEBean item : cutFlows) {
                if (mMatType.equals(item.getMaterialType())) {
                    bean = item;
                    break;
                }
            }

            String operation = null;
            switch (buttonView.getId()) {
                case R.id.ckb_SP:
                    operation = "SP";
                    break;
                case R.id.ckb_DS:
                    operation = "DS";
                    break;
                case R.id.ckb_CA:
                    operation = "CA";
                    break;
                case R.id.ckb_DB:
                    operation = "DB";
                    break;
                case R.id.ckb_FI:
                    operation = "FI";
                    break;
                case R.id.ckb_CP:
                    operation = "CP";
                    break;
                case R.id.ckb_CK:
                    operation = "CK";
                    break;
                case R.id.ckb_FB:
                    operation = "FB";
                    break;
                case R.id.ckb_RK:
                    operation = "RK";
                    break;
            }

            if (!isEmpty(operation)) {
                for (ProcessDirectionBo.CUTFLOWTEMPLETEBean.CutFlowListBean item : bean.getCutFlowList()) {
                    if (operation.equals(item.getOperation())) {
                        if (isChecked) {
                            item.setIsUsed("true");
                        } else {
                            item.setIsUsed("false");
                        }
                    }
                }
            }
        }
    }

    private void setupProcessView(@NonNull String type) {
        mMatType = type;
        List<ProcessDirectionBo.CUTFLOWTEMPLETEBean> cutFlows = mData.getMaterialCutFlows();
        for (ProcessDirectionBo.CUTFLOWTEMPLETEBean item : cutFlows) {
            if (type.equals(item.getMaterialType())) {
                List<ProcessDirectionBo.CUTFLOWTEMPLETEBean.CutFlowListBean> cutFlowList = item.getCutFlowList();
                for (ProcessDirectionBo.CUTFLOWTEMPLETEBean.CutFlowListBean bean : cutFlowList) {
                    String operation = bean.getOperation();
                    CheckBox checkBox = mMap_processCheckBox.get(operation);
                    ImageView iv_lock = mMap_processLock.get(operation);
                    if (checkBox != null) {
                        checkBox.setOnCheckedChangeListener(mListener);
                        if ("true".equals(bean.getIsUsed())) {
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                        if ("true".equals(bean.getIsLock())) {
                            checkBox.setEnabled(false);
                            iv_lock.setVisibility(View.VISIBLE);
                        } else {
                            checkBox.setEnabled(true);
                            iv_lock.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getProcessDirection.equals(url)) {
                mData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ProcessDirectionBo.class);
                if (mData != null) {
                    mData.setShopOrderRef(mShopOrderBo);
                    setupTabView();
                } else {
                    showErrorDialog("后台返回数据为空");
                }
            }
        }
    }

    public static Intent getIntent(Context context, String shopOrder, String shopOrderBo, String item) {
        Intent intent = new Intent(context, ProcessDirectionActivity.class);
        intent.putExtra("shopOrder", shopOrder);
        intent.putExtra("shopOrderBo", shopOrderBo);
        intent.putExtra("item", item);
        return intent;
    }
}
