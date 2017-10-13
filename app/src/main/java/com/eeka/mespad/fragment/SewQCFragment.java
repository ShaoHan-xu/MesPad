package com.eeka.mespad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.RecordSewNCActivity;
import com.eeka.mespad.bo.ClothSizeBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.SaveClothSizeBo;
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.MyAlertDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 缝制质检
 * Created by Lenovo on 2017/8/8.
 */
public class SewQCFragment extends BaseFragment {

    public static int TYPE_QC = 0;
    public static int TYPE_QA = 1;

    private static final int REQUEST_NC = 0;

    private LinearLayout mLayout_sizeInfo;
    private LinearLayout mLayout_productComponent;
    private LinearLayout mLayout_designComponent;
    private LinearLayout mLayout_matInfo;

    private TextView mTv_componentDesc;
    private TextView mTv_SFC;
    private TextView mTv_curProcess;
    private TextView mTv_dayOutput;
    private TextView mTv_monthOutput;
    private TextView mTv_orderNum;
    private TextView mTv_matNum;
    private TextView mTv_size;
    private TextView mTv_special;

    private SewQCDataBo mSewQCData;
    private ClothSizeBo mClothSizeData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_sewqc, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mLayout_sizeInfo = (LinearLayout) mView.findViewById(R.id.layout_sewQC_sizeInfo);
        mLayout_productComponent = (LinearLayout) mView.findViewById(R.id.layout_sewQC_productComponent);
        mLayout_designComponent = (LinearLayout) mView.findViewById(R.id.layout_sewQC_designComponent);
        mLayout_matInfo = (LinearLayout) mView.findViewById(R.id.layout_sewQC_matInfo);

        mTv_componentDesc = (TextView) mView.findViewById(R.id.tv_sewQC_componentDesc);
        mTv_SFC = (TextView) mView.findViewById(R.id.tv_sewQC_SFC);
        mTv_curProcess = (TextView) mView.findViewById(R.id.tv_sewQC_curProcess);
        mTv_dayOutput = (TextView) mView.findViewById(R.id.tv_sewQC_dayOutput);
        mTv_monthOutput = (TextView) mView.findViewById(R.id.tv_sewQC_monthOutput);
        mTv_orderNum = (TextView) mView.findViewById(R.id.tv_sewQC_orderNum);
        mTv_matNum = (TextView) mView.findViewById(R.id.tv_sewQC_matNum);
        mTv_size = (TextView) mView.findViewById(R.id.tv_sewQC_size);
        mTv_special = (TextView) mView.findViewById(R.id.tv_sewQC_special);
        mTv_special.setOnClickListener(this);

        mView.findViewById(R.id.btn_sewQc_save).setOnClickListener(this);
        mView.findViewById(R.id.btn_sewQc_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_sewQC_special) {
            String content = mTv_special.getText().toString();
            if (!isEmpty(content)) {
                MyAlertDialog.showAlert(mContext, content);
            }
        } else if (v.getId() == R.id.btn_sewQc_refresh) {
            getClothSizeData();
        } else if (v.getId() == R.id.btn_sewQc_save) {
            saveClothSizeData();
        }
    }

    private void setupView() {
        if (mSewQCData == null) {
            toast("数据错误");
            return;
        }
        mTv_SFC.setText(mSewQCData.getSfc());
        mTv_curProcess.setText(mSewQCData.getCurrentOperation());
        mTv_dayOutput.setText(mSewQCData.getDailyOutput() + "");
        mTv_monthOutput.setText(mSewQCData.getMonthlyOutput() + "");
        mTv_orderNum.setText(mSewQCData.getShopOrder());
        mTv_matNum.setText(mSewQCData.getItem());
        mTv_size.setText(mSewQCData.getSfcSize());
        mTv_special.setText(mSewQCData.getSoMark());

        mLayout_matInfo.removeAllViews();
        List<SewQCDataBo.BomComponentBean> bomComponent = mSewQCData.getBomComponent();
        for (SewQCDataBo.BomComponentBean bom : bomComponent) {
            mLayout_matInfo.addView(getMatInfo(bom));
        }

        mLayout_productComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean> designComponent = mSewQCData.getDesignComponent();
        for (int i = 0; i < designComponent.size(); i++) {
            SewQCDataBo.DesignComponentBean component = designComponent.get(i);
            final int finalI = i;
            mLayout_productComponent.addView(TabViewUtil.getTabView(mContext, component, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabViewUtil.refreshTabView(mLayout_productComponent, finalI);
                    List<SewQCDataBo.DesignComponentBean> designComponent = mSewQCData.getDesignComponent();
                    SewQCDataBo.DesignComponentBean component = designComponent.get(finalI);
                    refreshDesignComponentView(component);
                }
            }));
            if (i == 0) {
                TabViewUtil.refreshTabView(mLayout_productComponent, 0);
                refreshDesignComponentView(component);
            }
        }
    }

    /**
     * 设置尺寸表格数据
     */
    private void setupSizeInfo() {
        if (mClothSizeData == null) {
            toast("获取尺寸数据失败");
            return;
        }
        mLayout_sizeInfo.removeAllViews();
        List<ClothSizeBo.DCPARRMSBean> parrms = mClothSizeData.getDC_PARRMS();
        if (parrms != null) {
            for (int i = 0; i < parrms.size(); i++) {
                ClothSizeBo.DCPARRMSBean item = parrms.get(i);
                mLayout_sizeInfo.addView(getSizeInfoView(item, i));
            }
        }
    }

    /**
     * 记录不良
     */
    public void recordNC(int type) {
        if (mSewQCData != null) {
            startActivityForResult(RecordSewNCActivity.getIntent(mContext, type, mSewQCData.getSfc(), mSewQCData.getDesignComponent()), REQUEST_NC);
        } else {
            showErrorDialog("请先获取工单数据");
        }
    }

    public void searchOrder(String orderNum) {
        if (isAdded())
            showLoading();
        boolean flag = HttpHelper.findPadKeyDataForNcUI(orderNum, this);
        if (!flag) {
            dismissLoading();
            showErrorDialog("需要员工上岗后才能搜索工单");
        }
    }

    /**
     * 获取成衣尺寸数据
     */
    private void getClothSizeData() {
        if (mSewQCData != null) {
            if (isAdded())
                showLoading();
            String currentOperation = mSewQCData.getCurrentOperation();
            String operationBo = "OperationBO:" + SpUtil.getSite() + "," + currentOperation + ",A";
            HttpHelper.getClothSize(mSewQCData.getSfc(), operationBo, this);
        }
    }

    /**
     * 保存成衣数据
     */
    private void saveClothSizeData() {
        if (mSewQCData != null && mClothSizeData != null) {
            SaveClothSizeBo data = new SaveClothSizeBo();
            data.setDC_GROUP(mClothSizeData.getDC_GROUP());
            data.setSFC(mSewQCData.getSfc());
            PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
            data.setRESOURCE_BO(resource.getRESOURCE_BO());
            String currentOperation = mSewQCData.getCurrentOperation();
            String operationBo = "OperationBO:" + SpUtil.getSite() + "," + currentOperation + ",A";
            data.setOPERATION_BO(operationBo);

            List<SaveClothSizeBo.Item> items = new ArrayList<>();
            for (ClothSizeBo.DCPARRMSBean bean : mClothSizeData.getDC_PARRMS()) {
                if ("true".equals(bean.getALLOW_COLLECTION())) {
                    String value = bean.getCOLLECTED_VALUE();
                    if (!isEmpty(value)) {
                        SaveClothSizeBo.Item item = new SaveClothSizeBo.Item();
                        item.setDATA_TYPE(bean.getDATA_TYPE());
                        item.setMEASURED_ATTRIBUTE(bean.getMEASURED_ATTRIBUTE());
                        item.setVALUE(value);
                        items.add(item);
                    }
                }
            }
            if (items.size() != 0) {
                data.setDCS(items);
                HttpHelper.saveClothSizeData(data, this);
            } else {
                toast("请录入实际尺寸后保存");
            }

        }
    }

    private void refreshDesignComponentView(SewQCDataBo.DesignComponentBean component) {
        mLayout_designComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean.DesgComponentsBean> desgComponents = component.getDesgComponents();
        for (int i = 0; i < desgComponents.size(); i++) {
            final SewQCDataBo.DesignComponentBean.DesgComponentsBean bean = desgComponents.get(i);
            final int finalI = i;
            mLayout_designComponent.addView(TabViewUtil.getTabView(mContext, bean, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabViewUtil.refreshTabView(mLayout_designComponent, finalI);
                    mTv_componentDesc.setText(bean.getDescription());
                }
            }));
            if (i == 0) {
                TabViewUtil.refreshTabView(mLayout_designComponent, 0);
                mTv_componentDesc.setText(bean.getDescription());
            }
        }
    }

    /**
     * 获取尺寸信息布局
     */
    private View getSizeInfoView(ClothSizeBo.DCPARRMSBean sizeInfo, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sewqc_size, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        TextView tv_sizeAttr = (TextView) view.findViewById(R.id.tv_sewQc_sizeAttr);
        TextView tv_refSize = (TextView) view.findViewById(R.id.tv_sewQc_refSize);
        TextView tv_refTolerance = (TextView) view.findViewById(R.id.tv_sewQc_refTolerance);
        TextView tv_realTolerance = (TextView) view.findViewById(R.id.tv_sewQc_realTolerance);
        EditText et_finishedSize = (EditText) view.findViewById(R.id.et_sewQc_finishedSize);
//        et_finishedSize.setTag(position);
//        et_finishedSize.setOnFocusChangeListener(new FocusChangedListener());

        String description = sizeInfo.getDESCRIPTION();
        if (isEmpty(description)) {
            tv_sizeAttr.setText("Measure Attribute");
        } else {
            tv_sizeAttr.setText(description + sizeInfo.getUNIT());
        }

        String refSize = sizeInfo.getVALUE();
        String refTolerance = sizeInfo.getSTANDARD();
        tv_refSize.setText(refSize);
        tv_refTolerance.setText(refTolerance);

        String realSize = sizeInfo.getCOLLECTED_VALUE();
        if (isEmpty(realSize)) {
            tv_realTolerance.setText(null);
            et_finishedSize.setText(null);
        } else {
            et_finishedSize.setText(realSize);

            float fRealSize = FormatUtil.strToFloat(realSize);
            float fRefSize = FormatUtil.strToFloat(refSize);
            float realTolerance = fRealSize - fRefSize;
            float fRefTolerance = FormatUtil.strToFloat(refTolerance);
            tv_realTolerance.setText(realTolerance + "");
            if (Math.abs(realTolerance) > fRefTolerance) {
                tv_realTolerance.setTextColor(getResources().getColor(R.color.white));
                tv_realTolerance.setBackgroundResource(R.color.text_red_default);
            } else {
                tv_realTolerance.setTextColor(getResources().getColor(R.color.text_black_default));
                tv_realTolerance.setBackgroundResource(R.color.white);
            }
        }

        String flag = sizeInfo.getALLOW_COLLECTION();
        if ("false".equals(flag)) {
            et_finishedSize.setEnabled(false);
            et_finishedSize.setHint(null);
        } else {
            et_finishedSize.setEnabled(true);
            et_finishedSize.setHint("点击录入");
        }

        et_finishedSize.addTextChangedListener(new TextChangedListener(position));
        return view;
    }

    private class TextChangedListener implements TextWatcher {

        int position;

        public TextChangedListener(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            int posDot = text.indexOf(".");
            if (posDot > 0) {
                if (text.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }

            ClothSizeBo.DCPARRMSBean sizeInfo = mClothSizeData.getDC_PARRMS().get(position);
            float finishedSize = FormatUtil.strToFloat(text);
            float refSize = FormatUtil.strToFloat(sizeInfo.getVALUE());
            sizeInfo.setCOLLECTED_VALUE(finishedSize + "");

            mClothSizeData.getDC_PARRMS().set(position, sizeInfo);
            View view = mLayout_sizeInfo.getChildAt(position);
            TextView tv_realTolerance = (TextView) view.findViewById(R.id.tv_sewQc_realTolerance);
            float realTolerance = new BigDecimal(finishedSize - refSize).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            float fRefTolerance = FormatUtil.strToFloat(sizeInfo.getSTANDARD());
            tv_realTolerance.setText(String.format("%.1f", realTolerance));

            if (Math.abs(realTolerance) > fRefTolerance) {
                tv_realTolerance.setTextColor(getResources().getColor(R.color.white));
                tv_realTolerance.setBackgroundResource(R.color.text_red_default);
            } else {
                tv_realTolerance.setTextColor(getResources().getColor(R.color.text_black_default));
                tv_realTolerance.setBackgroundResource(R.color.white);
            }
//                Logger.d(JSON.toJSONString(sizeInfo));
        }
    }

    /**
     * 获取物料属性布局
     */
    private View getMatInfo(SewQCDataBo.BomComponentBean bom) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_key_value, null);
        TextView tv_key = (TextView) view.findViewById(R.id.tv_key);
        TextView tv_value = (TextView) view.findViewById(R.id.tv_value);
        tv_key.setText(bom.getDescription());
        tv_value.setText(bom.getName() + "-" + bom.getAttributes().getPART_ID());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.findPadKeyDataForNcUI.equals(url)) {
                mSewQCData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), SewQCDataBo.class);
                setupView();

                getClothSizeData();
            } else if (HttpHelper.getClothSize.equals(url)) {
                mClothSizeData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ClothSizeBo.class);
                setupSizeInfo();
            } else if (HttpHelper.saveQCClothSizeData.equals(url)) {
                toast("保存成功");
            }
        }
    }
}
