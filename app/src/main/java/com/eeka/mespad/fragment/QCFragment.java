package com.eeka.mespad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.activity.RecordSewNCActivity;
import com.eeka.mespad.activity.WebActivity;
import com.eeka.mespad.bo.ClothSizeBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.INARequestBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.ReworkItemBo;
import com.eeka.mespad.bo.SaveClothSizeBo;
import com.eeka.mespad.bo.SewAttr;
import com.eeka.mespad.bo.SewQCDataBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.http.WebServiceUtils;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.TabViewUtil;
import com.eeka.mespad.view.dialog.CreateCardDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.MyAlertDialog;
import com.eeka.mespad.view.dialog.OfflineDialog;
import com.eeka.mespad.view.dialog.ProductOnOffDialog;
import com.eeka.mespad.view.dialog.ReworkInfoDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 质检
 * Created by Lenovo on 2017/8/8.
 */
public class QCFragment extends BaseFragment {

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
    //    private TextView mTv_monthOutput;
    private TextView mTv_orderNum;
    private TextView mTv_MTMOrderNum;
    private TextView mTv_matNum;
    private TextView mTv_matDesc;
    private TextView mTv_size;
    private TextView mTv_special;
    private TextView mTv_reworkInfo;
    private TextView mTv_lastPosition;
    private LinearLayout mLayout_lastPosition;

    private SewQCDataBo mSewQCData;
    private ClothSizeBo mClothSizeData;

    private String mRFID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fm_sewqc, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mLayout_sizeInfo = mView.findViewById(R.id.layout_sewQC_sizeInfo);
        mLayout_productComponent = mView.findViewById(R.id.layout_sewQC_productComponent);
        mLayout_designComponent = mView.findViewById(R.id.layout_sewQC_designComponent);
        mLayout_matInfo = mView.findViewById(R.id.layout_sewQC_matInfo);
        mLayout_lastPosition = mView.findViewById(R.id.layout_sewQC_lastPosition);

        mTv_componentDesc = mView.findViewById(R.id.tv_sewQC_componentDesc);
        mTv_SFC = mView.findViewById(R.id.tv_sewQC_SFC);
        mTv_curProcess = mView.findViewById(R.id.tv_sewQC_curProcess);
        mTv_dayOutput = mView.findViewById(R.id.tv_sewQC_dayOutput);
//        mTv_monthOutput = mView.findViewById(R.id.tv_sewQC_monthOutput);
        mTv_orderNum = mView.findViewById(R.id.tv_sewQC_orderNum);
        mTv_MTMOrderNum = mView.findViewById(R.id.tv_sewQC_MTMOrderNum);
        mTv_matNum = mView.findViewById(R.id.tv_sewQC_matNum);
        mTv_matDesc = mView.findViewById(R.id.tv_sewQC_matDesc);
        mTv_size = mView.findViewById(R.id.tv_sewQC_size);
        mTv_lastPosition = mView.findViewById(R.id.tv_sewQC_lastPosition);
        mTv_special = mView.findViewById(R.id.tv_sewQC_special);
        mTv_reworkInfo = mView.findViewById(R.id.tv_sewQC_reworkInfo);
        mTv_reworkInfo.setOnClickListener(this);
        mTv_special.setOnClickListener(this);
        mTv_MTMOrderNum.setOnClickListener(this);

        mTv_componentDesc.setOnClickListener(this);
        mView.findViewById(R.id.btn_sewQc_save).setOnClickListener(this);
        mView.findViewById(R.id.btn_sewQc_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sewQC_special: {
                String content = mTv_special.getText().toString();
                if (!isEmpty(content)) {
                    MyAlertDialog.showAlert(mContext, content);
                }
                break;
            }
            case R.id.tv_sewQC_componentDesc: {
                String content = mTv_componentDesc.getText().toString();
                if (!isEmpty(content)) {
                    MyAlertDialog.showAlert(mContext, content);
                }
                break;
            }
            case R.id.btn_sewQc_refresh:
                getClothSizeData();
                break;
            case R.id.btn_sewQc_save:
                saveClothSizeData();
                break;
            case R.id.tv_sewQC_reworkInfo:
                showLoading();
                HttpHelper.getReworkInfo(mSewQCData.getSfcRef(), this);
                break;
            case R.id.tv_sewQC_MTMOrderNum:
                String url = PadApplication.MTM_URL + mSewQCData.getSalesOrder();
                startActivity(WebActivity.getIntent(mContext, url));
                break;
        }
    }

    private OfflineDialog mOfflineDialog;

    private void setupView() {
        if (mSewQCData == null) {
            toast("数据错误");
            return;
        }
        mTv_SFC.setText(mSewQCData.getSfc());
        List<SewAttr> currentOperations = mSewQCData.getCurrentOperation();
        if (currentOperations != null && currentOperations.size() != 0) {
            String currentOperation = currentOperations.get(0).getDescription();
            mTv_curProcess.setText(currentOperation);
            for (int i = 0; i < currentOperations.size(); i++) {
                SewAttr opera = currentOperations.get(i);
                if ("TQTXJ002".equals(opera.getName())) {
                    if (mOfflineDialog != null) {
                        mOfflineDialog.dismiss();
                        mOfflineDialog = null;
                    }
                    mOfflineDialog = new OfflineDialog(mContext, mSewQCData.getSfc(), mRFID, mSewQCData.getShopOrder(), opera.getName(), opera.getDescription());
                    mOfflineDialog.show();
                }
            }
        } else {
            mTv_curProcess.setText(null);
        }
        mTv_dayOutput.setText(mSewQCData.getDailyOutput() + "");
//        mTv_monthOutput.setText(mSewQCData.getMonthlyOutput() + "");
        mTv_orderNum.setText(mSewQCData.getShopOrder());
        mTv_MTMOrderNum.setText(mSewQCData.getSalesOrder());
        mTv_matNum.setText(mSewQCData.getItem());
        mTv_matDesc.setText(mSewQCData.getItemDesc());
        mTv_size.setText(mSewQCData.getSfcSize());
        mTv_special.setText(mSewQCData.getSoMark());

        String lastPosition = mSewQCData.getPrePosition();
        if (isEmpty(lastPosition)) {
            mLayout_lastPosition.setVisibility(View.GONE);
        } else {
            mLayout_lastPosition.setVisibility(View.VISIBLE);
            mTv_lastPosition.setText(String.format("%s-%s(%s)", mSewQCData.getPreLineId(), mSewQCData.getPrePosition(), mSewQCData.getPrePositionTypeDesc()));
        }

        if ("1".equals(mSewQCData.getReworkFlag())) {
            mTv_reworkInfo.setVisibility(View.VISIBLE);
        } else {
            mTv_reworkInfo.setVisibility(View.GONE);
        }

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
        //此处删除成衣数据是防止主界面获取到数据而成衣数据获取失败的情况导致成衣数据还显示的上一件的数据问题
        mLayout_sizeInfo.removeAllViews();
        List<ClothSizeBo.DCPARRMSBean> parrms = mClothSizeData.getDC_PARRMS();
        if (parrms != null) {
            for (int i = 0; i < parrms.size(); i++) {
                ClothSizeBo.DCPARRMSBean item = parrms.get(i);
                mLayout_sizeInfo.addView(getSizeInfoView(item, i));
            }
        }
    }

    private ProductOnOffDialog mProductOnOffDialog;

    /**
     * 成衣上架
     */
    public void productOn() {
        mProductOnOffDialog = new ProductOnOffDialog(mContext, mRFID, null, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inaIN(mRFID);
            }
        });
        mProductOnOffDialog.show();
    }

    /**
     * 衣架进站
     */
    public void inaIN(String rfid) {
        mRFID = rfid;
        if (isEmpty(mRFID)) {
            showErrorDialog("请输入条码获取数据");
            return;
        }
        INARequestBo bo = getINARequestParams();
        if (bo == null) {
            return;
        }
        showLoading();
        WebServiceUtils.inaIn(bo, new WebServiceCallback());
    }

    private void inaDoing() {
        INARequestBo bo = getINARequestParams();
        if (bo == null) {
            return;
        }
        showLoading();
        WebServiceUtils.inaDoing(bo, null);
    }

    private INARequestBo getINARequestParams() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null) {
            ErrorDialog.showAlert(mContext, "站位数据为空，请重启应用获取");
            return null;
        }
        INARequestBo bo = new INARequestBo();
        bo.setSite(SpUtil.getSite());
        bo.setLineId(contextInfo.getLINE_CATEGORY());
        bo.setStationId(contextInfo.getPOSITION());
        bo.setInTime(System.currentTimeMillis() + "");
        bo.setOutTime(System.currentTimeMillis() + "");
        bo.setDoTime(System.currentTimeMillis() + "");
        bo.setHangerId(mRFID);
        return bo;
    }

    private class WebServiceCallback implements WebServiceUtils.HttpCallBack {

        @Override
        public void onSuccess(String method, JSONObject result) {
            dismissLoading();
            if (WebServiceUtils.INA_IN.equals(method)) {
                inaDoing();
            } else if (WebServiceUtils.INA_DOING.equals(method)) {

            }
        }

        @Override
        public void onFail(String errMsg) {
            dismissLoading();
            //webservice的接口报错时都会有推送，所以此处不需要显示
//            showErrorDialog(errMsg);
        }
    }

    /**
     * 输入RFID号做成衣上架用
     */
    public boolean inputRFID(String rfid) {
        if (mProductOnOffDialog != null && mProductOnOffDialog.isShowing()) {
            mProductOnOffDialog.setWashLabel(rfid);
            return true;
        }
        return false;
    }

    /**
     * 成衣下架
     */
    public void productOff() {
        if (mSewQCData == null) {
            showErrorDialog("请先获取衣架数据");
            return;
        }
        new ProductOnOffDialog(mContext, mRFID, mSewQCData.getSfc(), true, null).show();
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

    public void gotoQA() {
        if (mSewQCData == null) {
            toast("请先获取缝制数据");
            return;
        }
        ErrorDialog.showConfirmAlert(mContext, "发现当前实物有不良，需要去质检站？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
                HttpHelper.initNcForQA(mSewQCData.getSfc(), resource.getRESOURCE_BO(), "NC2QA", QCFragment.this);
            }
        });
    }

    public void qaToQc() {
        if (mSewQCData == null) {
            toast("请先获取衣架数据");
            return;
        }
        ErrorDialog.showConfirmAlert(mContext, "确定执行QA去QC的操作吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                HttpHelper.qaToQc(mSewQCData.getSfc(), QCFragment.this);
            }
        });
    }

    /**
     * 解绑
     */
    public void unBind() {
        if (mSewQCData == null) {
            showErrorDialog("请先获取工单数据");
        } else {
            CreateCardDialog dialog = new CreateCardDialog(mContext, mSewQCData.getSfc());
            dialog.show();
        }
    }

    /**
     * 换片下架
     */
    public void change() {
        if (mSewQCData == null) {
            showErrorDialog("请先获取工单数据");
        } else {
            String operation = null;
            String operationDesc = null;
            List<SewAttr> currentOperation = mSewQCData.getCurrentOperation();
            if (currentOperation != null && currentOperation.size() != 0) {
                operation = currentOperation.get(0).getName();
                operationDesc = currentOperation.get(0).getDescription();
            }
            new OfflineDialog(mContext, mSewQCData.getSfc(), mRFID, mSewQCData.getShopOrder(), operation, operationDesc, true).show();
        }
    }

    public void searchOrder(String orderNum) {
        mRFID = orderNum;
        if (isAdded())
            showLoading();
        boolean flag = HttpHelper.findPadKeyDataForNcUI(orderNum, this);
        if (!flag) {
            HttpHelper.getPositionLoginUsers(this);
        }

    }

    /**
     * 获取成衣尺寸数据
     */
    private void getClothSizeData() {
        if (mSewQCData != null) {
            List<SewAttr> currentOperations = mSewQCData.getCurrentOperation();
            if (currentOperations != null && currentOperations.size() != 0) {
                String currentOperation = currentOperations.get(0).getName();
                String operationBo = "OperationBO:" + SpUtil.getSite() + "," + currentOperation + ",A";
                if (isAdded())
                    showLoading();

                HttpHelper.getClothSize(mSewQCData.getSfc(), operationBo, this);
            }
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
            List<SewAttr> currentOperations = mSewQCData.getCurrentOperation();
            if (currentOperations != null && currentOperations.size() != 0) {
                String currentOperation = currentOperations.get(0).getName();
                String operationBo = "OperationBO:" + SpUtil.getSite() + "," + currentOperation + ",A";
                data.setOPERATION_BO(operationBo);
            }

            List<SaveClothSizeBo.Item> items = new ArrayList<>();
            for (ClothSizeBo.DCPARRMSBean bean : mClothSizeData.getDC_PARRMS()) {
                if ("true".equals(bean.getALLOW_COLLECTION())) {
                    String value = bean.getCOLLECTED_VALUE();
                    if (!isEmpty(value)) {
                        SaveClothSizeBo.Item item = new SaveClothSizeBo.Item();
                        item.setDATA_TYPE(bean.getDATA_TYPE());
                        item.setMEASURED_ATTRIBUTE(bean.getMEASURED_ATTRIBUTE());
                        item.setVALUE(value);
                        item.setPARAM_DESC(bean.getPARAM_DESC());
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
                    mTv_componentDesc.setText(bean.getQualityStandard());
                }
            }));
            if (i == 0) {
                TabViewUtil.refreshTabView(mLayout_designComponent, 0);
                mTv_componentDesc.setText(bean.getQualityStandard());
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
        TextView tv_sizeAttr = view.findViewById(R.id.tv_sewQc_sizeAttr);
        TextView tv_refSize = view.findViewById(R.id.tv_sewQc_refSize);
        TextView tv_refTolerance = view.findViewById(R.id.tv_sewQc_refTolerance);
        TextView tv_realTolerance = view.findViewById(R.id.tv_sewQc_realTolerance);
        EditText et_finishedSize = view.findViewById(R.id.et_sewQc_finishedSize);
//        et_finishedSize.setTag(position);
//        et_finishedSize.setOnFocusChangeListener(new FocusChangedListener());

        String description = sizeInfo.getDESCRIPTION();
        if (isEmpty(description)) {
            tv_sizeAttr.setText("Measure Attribute");
        } else {
            tv_sizeAttr.setText(String.format("%s%s", description, sizeInfo.getUNIT()));
        }
        String content = tv_sizeAttr.getText().toString();
        if (content.length() > 6) {
            String st = content.substring(0, content.length() / 2);
            String en = content.substring(content.length() / 2, content.length());
            tv_sizeAttr.setText(String.format("%s\n%s", st, en));
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
            String format = String.format("%.1f", realTolerance);
            if (realTolerance > 0) {
                tv_realTolerance.setText(String.format("+%s", format));
            } else {
                tv_realTolerance.setText(format);
            }
            if ((int) (Math.abs(realTolerance) * 100) > (int) (fRefTolerance * 100)) {
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

        TextChangedListener(int position) {
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
            ClothSizeBo.DCPARRMSBean sizeInfo = mClothSizeData.getDC_PARRMS().get(position);
            View view = mLayout_sizeInfo.getChildAt(position);
            TextView tv_realTolerance = view.findViewById(R.id.tv_sewQc_realTolerance);
            String text = s.toString();
            if (isEmpty(text)) {
                sizeInfo.setCOLLECTED_VALUE(null);
                mClothSizeData.getDC_PARRMS().set(position, sizeInfo);
                tv_realTolerance.setText(null);
                return;
            }
            int posDot = text.indexOf(".");
            if (posDot > 0) {
                if (text.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }

            float finishedSize = FormatUtil.strToFloat(text);
            float refSize = FormatUtil.strToFloat(sizeInfo.getVALUE());
            sizeInfo.setCOLLECTED_VALUE(finishedSize + "");
            mClothSizeData.getDC_PARRMS().set(position, sizeInfo);

            float realTolerance = finishedSize - refSize;
            float fRefTolerance = FormatUtil.strToFloat(sizeInfo.getSTANDARD());
            String format = String.format("%.1f", realTolerance);
            if (realTolerance > 0) {
                tv_realTolerance.setText(String.format("+%s", format));
            } else {
                tv_realTolerance.setText(format);
            }

            if ((int) (Math.abs(realTolerance) * 100) > (int) (fRefTolerance * 100)) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loginuser, null);
        TextView tv_key = view.findViewById(R.id.tv_userName);
        TextView tv_value = view.findViewById(R.id.tv_userId);
        tv_key.setText(bom.getDescription());
        tv_value.setText(bom.getName());
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
                SpUtil.saveSalesOrder(mSewQCData.getSalesOrder());
                SpUtil.save(SpUtil.KEY_SHOPORDER, mSewQCData.getShopOrder());

                //此处删除成衣数据是防止主界面获取到数据而成衣数据获取失败的情况导致成衣数据还显示的上一件的数据问题
                mLayout_sizeInfo.removeAllViews();
                getClothSizeData();
            } else if (HttpHelper.getClothSize.equals(url)) {
                mClothSizeData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ClothSizeBo.class);
                setupSizeInfo();
            } else if (HttpHelper.initNcForQA.equals(url)) {
                toast("操作成功");
            } else if (HttpHelper.qaToQc.equals(url)) {
                toast("操作成功");
            } else if (HttpHelper.saveQCClothSizeData.equals(url)) {
                toast("保存成功");
            } else if (HttpHelper.getPositionLoginUser_url.equals(url)) {
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                refreshLoginUsers();
                if (positionUsers == null || positionUsers.size() == 0) {
                    showErrorDialog("请您先登录");
                } else {
                    searchOrder(mRFID);
                }
            } else if (HttpHelper.getReworkInfo.equals(url)) {
                JSONArray result = resultJSON.getJSONArray("result");
                if (result != null) {
                    List<ReworkItemBo> list = JSON.parseArray(result.toString(), ReworkItemBo.class);
                    new ReworkInfoDialog(mContext, list).show();
                }
            }
        }
    }
}
