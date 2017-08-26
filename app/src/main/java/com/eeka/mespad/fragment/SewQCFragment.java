package com.eeka.mespad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.http.HttpHelper;

import java.util.List;

/**
 * 缝制质检
 * Created by Lenovo on 2017/8/8.
 */

public class SewQCFragment extends BaseFragment {

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

        mLayout_sizeInfo.removeAllViews();
        List<SewQCDataBo.ClothingSizeBean> clothingSize = mSewQCData.getClothingSize();
        for (SewQCDataBo.ClothingSizeBean clothing : clothingSize) {
            mLayout_sizeInfo.addView(getSizeInfoView(clothing));
        }

        mLayout_productComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean> designComponent = mSewQCData.getDesignComponent();
        for (int i = 0; i < designComponent.size(); i++) {
            SewQCDataBo.DesignComponentBean component = designComponent.get(i);
            mLayout_productComponent.addView(getTabView(component, i));
            if (i == 0) {
                refreshDesignComponentView(component);
            }
        }
    }

    /**
     * 记录不良
     */
    public void recordNc() {
        if (mSewQCData != null) {
            startActivityForResult(RecordSewNCActivity.getIntent(mContext, mSewQCData.getSfc(), mSewQCData.getSfc(), mSewQCData.getDesignComponent()), REQUEST_NC);
        } else {
            showErrorDialog("请先获取工单数据");
        }
    }

    public void searchOrder(String orderNum) {
        if (isAdded())
            showLoading();
        HttpHelper.findPadKeyDataForNcUI(orderNum, this);
    }

    /**
     * 获取导航标签布局
     */
    private <T> View getTabView(T data, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tab, null);
        TextView tv_tabName = (TextView) view.findViewById(R.id.textView);
        tv_tabName.setPadding(10, 10, 10, 10);
        if (position == 0)
            tv_tabName.setEnabled(false);
        if (data instanceof SewQCDataBo.DesignComponentBean) {
            SewQCDataBo.DesignComponentBean item = (SewQCDataBo.DesignComponentBean) data;
            tv_tabName.setText(item.getDescription());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshTab(mLayout_productComponent, position);

                    List<SewQCDataBo.DesignComponentBean> designComponent = mSewQCData.getDesignComponent();
                    SewQCDataBo.DesignComponentBean component = designComponent.get(position);
                    refreshDesignComponentView(component);
                }
            });
        } else if (data instanceof SewQCDataBo.DesignComponentBean.DesgComponentsBean) {
            SewQCDataBo.DesignComponentBean.DesgComponentsBean item = (SewQCDataBo.DesignComponentBean.DesgComponentsBean) data;
            tv_tabName.setText(item.getDescription());
            tv_tabName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshTab(mLayout_designComponent, position);
                }
            });
        }
        return view;
    }

    private void refreshDesignComponentView(SewQCDataBo.DesignComponentBean component) {
        mLayout_designComponent.removeAllViews();
        List<SewQCDataBo.DesignComponentBean.DesgComponentsBean> desgComponents = component.getDesgComponents();
        for (int i = 0; i < desgComponents.size(); i++) {
            SewQCDataBo.DesignComponentBean.DesgComponentsBean bean = desgComponents.get(i);
            mLayout_designComponent.addView(getTabView(bean, i));
            if (i == 0) {
                mTv_componentDesc.setText(bean.getQualityStandard());
            }
        }
    }

    /**
     * 刷新标签视图
     */
    private void refreshTab(ViewGroup parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            childAt.findViewById(R.id.textView).setEnabled(true);
        }
        parent.getChildAt(position).findViewById(R.id.textView).setEnabled(false);
    }

    /**
     * 获取尺寸信息布局
     */
    private View getSizeInfoView(SewQCDataBo.ClothingSizeBean sizeInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sewqc_size, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        TextView tv_sizeAttr = (TextView) view.findViewById(R.id.tv_sewQc_sizeAttr);
        TextView tv_refSize = (TextView) view.findViewById(R.id.tv_sewQc_refSize);
        TextView tv_refTolerance = (TextView) view.findViewById(R.id.tv_sewQc_refTolerance);
        TextView tv_realTolerance = (TextView) view.findViewById(R.id.tv_sewQc_realTolerance);
        EditText et_clothingSize = (EditText) view.findViewById(R.id.et_sewQc_clothingSize);

        tv_sizeAttr.setText(sizeInfo.getName());
        tv_refSize.setText(sizeInfo.getAttributes().getValue());

        return view;
    }

    /**
     * 获取物料属性布局
     *
     * @return
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
            }
        }
    }
}
