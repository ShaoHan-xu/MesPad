package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BatchCutRecordBo;
import com.eeka.mespad.bo.BatchLabuDetailBo;
import com.eeka.mespad.bo.DictionaryDataBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.PostBatchRecordLabuBo;
import com.eeka.mespad.bo.ProcessSheetsBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.SubPackageInfoBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.ImageBrowserDialog;
import com.eeka.mespad.view.dialog.ManualPrintSubPackageDialog;
import com.eeka.mespad.view.dialog.PatternDialog;
import com.eeka.mespad.view.dialog.ProcessSheetsDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;
import okhttp3.Response;

public class BatchLabuDetailActivity extends NFCActivity {

    private static final int REQUEST_RECORD_LABU = 0;
    private static final int REQUEST_RECORD_CUT = 1;
    private static final int REQUEST_RECORD_NC = 2;

    private String mShopOrder;
    private String mShopOrderBo;
    private PositionInfoBo.OPERINFORBean mOperation;

    private LinearLayout mLayout_buttonList;
    private LinearLayout mLayout_layoutList;

    private LinearLayout.LayoutParams mLayoutParams_right10;
    private LinearLayout.LayoutParams mLayoutParams_weight1_horizontal;

    private RadioGroup mRG_tabMenu;
    private List<DictionaryDataBo> mList_tab;
    private RadioButton mRB_mianBu, mRB_liBu, mRB_poBu;

    private String mMatType;
    private Map<String, BatchLabuDetailBo> mMap_layoutInfo;
    private BatchLabuDetailBo mCurMatData;

    private ProcessSheetsBo mProcessSheetsBo;//工艺单信息

    private String mItem;
    private String mSampleImgUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_labudetail);

        mShopOrder = getIntent().getStringExtra("shopOrder");
        mShopOrderBo = getIntent().getStringExtra("shopOrderBo");
        mItem = getIntent().getStringExtra("item");
        mOperation = (PositionInfoBo.OPERINFORBean) getIntent().getSerializableExtra("operation");

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mOperation.getDESCRIPTION() + "作业");

        mLayoutParams_right10 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams_right10.rightMargin = UnitUtil.dip2px(mContext, 10);
        mLayoutParams_weight1_horizontal = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutParams_weight1_horizontal.weight = 1;

        mLayout_buttonList = findViewById(R.id.layout_button);
        initButton();

        mRG_tabMenu = findViewById(R.id.tag_menu);
        mRG_tabMenu.setOnCheckedChangeListener(new TabMenuCheckedListener());

        mRB_mianBu = findViewById(R.id.radioBtn_mianBu);
        mRB_liBu = findViewById(R.id.radioBtn_liBu);
        mRB_poBu = findViewById(R.id.radioBtn_poBu);

        mLayout_layoutList = findViewById(R.id.layout_labuDetail_layoutList);

        findViewById(R.id.btn_back).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        mMap_layoutInfo = new HashMap<>();

        getMatInfo();
    }

    private void getMatInfo() {
        showLoading();
        HttpHelper.getBatchMatInfo(mOperation.getOPERATION(), mShopOrderBo, this);
    }

    private void getProcessSheetsInfo() {
        showLoading();
        HttpHelper.getProcessSheets(mShopOrder, this);
    }

    private void initTabView() {
        mRG_tabMenu.setVisibility(View.VISIBLE);
        for (DictionaryDataBo item : mList_tab) {
            if ("L".equals(item.getVALUE())) {
                mRB_liBu.setEnabled(true);
                mRB_liBu.setVisibility(View.VISIBLE);
            } else if ("N".equals(item.getVALUE())) {
                mRB_poBu.setEnabled(true);
                mRB_poBu.setVisibility(View.VISIBLE);
            }
        }
        mRB_mianBu.setChecked(true);
    }

    private void setupView() {
        TextView tv_workRequires = findViewById(R.id.tv_labuDetail_workRequires);
        String instruction = mOperation.getOPERATION_INSTRUCTION();
        if (!isEmpty(instruction)) {
            tv_workRequires.setText(instruction.replace("\\n", "\n"));
        }
        TextView tv_qualityRequires = findViewById(R.id.tv_labuDetail_qualityRequires);
        String requirement = mOperation.getQUALITY_REQUIREMENT();
        if (!isEmpty(requirement)) {
            tv_qualityRequires.setText(requirement.replace("\\n", "\n"));
        }

        ImageView iv_sampleImg = findViewById(R.id.iv_labuDetail_sampleImg);
        iv_sampleImg.setOnClickListener(this);
        mSampleImgUrl = getString(R.string.sampleImgUrl_jpg, mItem);
        setupSampleImg(iv_sampleImg);
    }

    /**
     * 因为图片 url 是自己拼接的，有可能是 jpg 和 png 两种情况，所以要先判断
     */
    private void setupSampleImg(final ImageView iv_sampleImg) {
        HttpRequest.post(mSampleImgUrl, new BaseHttpRequestCallback() {
            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                if (httpResponse == null || httpResponse.code() == 404) {
                    if (getString(R.string.sampleImgUrl_jpg, mItem).equals(mSampleImgUrl)) {
                        mSampleImgUrl = getString(R.string.sampleImgUrl_png, mItem);
                        setupSampleImg(iv_sampleImg);
                    }
                } else {
                    Picasso.with(mContext).load(mSampleImgUrl).into(iv_sampleImg);
                }
            }
        });
    }

    private class TabMenuCheckedListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radioBtn_mianBu:
                    mMatType = "M";
                    refreshMatView();
                    break;
                case R.id.radioBtn_liBu:
                    mMatType = "L";
                    refreshMatView();
                    break;
                case R.id.radioBtn_poBu:
                    mMatType = "N";
                    refreshMatView();
                    break;
            }
        }
    }

    private void refreshMatView() {
        mLayoutItem = null;
        if (!mMap_layoutInfo.containsKey(mMatType)) {
            showLoading();
            HttpHelper.getBatchLayoutInfo(mOperation.getOPERATION(), mShopOrderBo, mMatType, BatchLabuDetailActivity.this);
        } else {
            mCurMatData = mMap_layoutInfo.get(mMatType);
            boolean isFirst = mCurMatData.isDISPLAY();
            ImageView iv_mainMat = findViewById(R.id.iv_labuDetail_mainMat);
            iv_mainMat.setOnClickListener(this);
            if (mCurMatData.getMAT_URL() != null) {
                Picasso.with(mContext).load(mCurMatData.getMAT_URL()).into(iv_mainMat);
            }

            getTableView(mCurMatData, BatchLabuDetailBo.class, false, 0);

            mLayout_layoutList.removeAllViews();
            List<BatchLabuDetailBo.LAYOUTINFOBean> layoutInfo = mCurMatData.getLAYOUT_INFO();
            for (int i = 0; i < layoutInfo.size(); i++) {
                BatchLabuDetailBo.LAYOUTINFOBean layoutBean = layoutInfo.get(i);
                mLayout_layoutList.addView(getTableView(layoutBean, BatchLabuDetailBo.LAYOUTINFOBean.class, isFirst, i));
            }

            //默认显示按钮
//            isInitButton = true;
//            for (int i = 0; i < mLayout_layoutList.getChildCount(); i++) {
//                View view = mLayout_layoutList.getChildAt(i);
//                CheckBox checkBox = view.findViewById(R.id.ckb_labuTable_toggle);
//                checkBox.setChecked(true);
//            }
//            isInitButton = false;
            refreshProcessDirection(mCurMatData.getORDER_STATUS());
        }
    }

    private void refreshProcessDirection(List<BatchLabuDetailBo.ORDERSTATUSBean> list) {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(-1);

        LinearLayout layout_processNameContainer = findViewById(R.id.layout_labuDetail_processNameContainer);
        layout_processNameContainer.removeAllViews();
        LinearLayout layout_processIconContainer = findViewById(R.id.layout_labuDetail_processIconContainer);
        for (int i = 0; i < layout_processIconContainer.getChildCount(); i++) {
            View view = layout_processIconContainer.getChildAt(i);
            Object tag = view.getTag();
            if (tag != null && tag.equals("animation")) {
                Animation animation = view.getAnimation();
                animation.cancel();
                animation.reset();
                view.setAnimation(null);
            }
        }
        layout_processIconContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, (int) getResources().getDimension(R.dimen.dp_15));
        params.weight = 1;
        for (BatchLabuDetailBo.ORDERSTATUSBean item : list) {
            TextView textView = new TextView(mContext);
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(params);
            switch (item.getSTATUS()) {
                case "IN_WORK":
                    imageView.setImageResource(R.drawable.ic_labu_working);
                    imageView.setTag("animation");
                    imageView.startAnimation(rotateAnimation);
                    textView.setTextColor(getResources().getColor(R.color.orange));
                    break;
                case "UN_START":
                    imageView.setImageResource(R.drawable.ic_labu_wait);
                    break;
                case "IN_QUEUE":
                    imageView.setImageResource(R.drawable.ic_labu_working);
                    break;
                case "DONE":
                    imageView.setImageResource(R.drawable.ic_labu_completed);
                    break;
            }
            layout_processIconContainer.addView(imageView);

            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            textView.setText(item.getOPERATION_DESC());
            layout_processNameContainer.addView(textView);
        }
    }

    private void initButton() {
        mLayout_buttonList.removeAllViews();
        String buttons = SpUtil.get(SpUtil.KEY_BUTTON, null);
        if (!isEmpty(buttons)) {
            List<PositionInfoBo.BUTTONINFORBean> buttonList = JSON.parseArray(buttons, PositionInfoBo.BUTTONINFORBean.class);
            for (PositionInfoBo.BUTTONINFORBean item : buttonList) {
                Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button_orange, null);
                switch (item.getBUTTON_ID()) {
                    case "PROCESS_FORM":
                        button.setText("工艺单显示");
                        button.setId(R.id.btn_processSheets);
                        break;
                    case "CUT_MAT_INFO":
                        button.setText("面料裁剪确认单");
                        button.setId(R.id.btn_cutMatInfo);
                        break;
                    case "MATERIALRETURN":
                        button.setText("退料");
                        button.setId(R.id.btn_materialReturn);
                        break;
                    case "MATERIALFEEDING":
                        button.setText("补料");
                        button.setId(R.id.btn_materialFeeding);
                        break;
                    case "VIDEO":
                        button.setText("视频查看");
                        button.setId(R.id.btn_playVideo);
                        break;
                    case "NCRECORD":
                        button.setText("不良录入");
                        button.setId(R.id.btn_NcRecord);
                        break;
                    case "CUT_PICTURE":
                        button.setText("裁剪图片");
                        button.setId(R.id.btn_cutBmp);
                        break;
                    case "XH_MESSAGE":
                        button.setText("绣花信息");
                        button.setId(R.id.btn_embroiderInfo);
                        break;
                    case "PATTERN_MESSAGE":
                        button.setText("显示纸样");
                        button.setId(R.id.btn_pattern);
                        break;
                    case "QR_CODE":
                        button.setText("二维码");
                        button.setId(R.id.btn_qrCode);
                        break;
                }
                if (isEmpty(button.getText().toString())) {
                    continue;
                }
                button.setOnClickListener(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.leftMargin = UnitUtil.dip2px(mContext, 5);
                button.setLayoutParams(params);
                mLayout_buttonList.addView(button);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_firstLabu:
            case R.id.btn_batchLabu:
            case R.id.btn_addLabu:
                startLabuRecordActivity(v, true);
                break;
            case R.id.btn_materialReturn:
                returnAndFeedingMat(true);
                break;
            case R.id.btn_materialFeeding:
                returnAndFeedingMat(false);
                break;
            case R.id.btn_playVideo:
                playVideo();
                break;
            case R.id.btn_NcRecord:
                recordNC();
                break;
            case R.id.btn_embroiderInfo:
//                String processLotBo = "";
//                startActivity(EmbroiderActivity.getIntent(mContext, null, processLotBo, TopicUtil.TOPIC_CUT));
                break;
            case R.id.btn_pattern:
                showPattern(null);
                break;
            case R.id.iv_labuDetail_sampleImg:
                new ImageBrowserDialog(mContext, mSampleImgUrl).setParams(0.5f, 0.9f).show();
                break;
            case R.id.btn_processSheets:
                if (mProcessSheetsBo != null) {
                    new ProcessSheetsDialog(mContext, mProcessSheetsBo).show();
                } else {
                    String mtmOrder = SpUtil.getSalesOrder();
                    String shopOrder = SpUtil.get(SpUtil.KEY_SHOPORDER, null);
                    if (isEmpty(mtmOrder) && isEmpty(shopOrder)) {
                        ErrorDialog.showAlert(mContext, "请先获取订单数据");
                    } else if (!isEmpty(mtmOrder)) {
                        String url = PadApplication.MTM_URL + mtmOrder;
                        startActivity(WebActivity.getIntent(mContext, url));
                    } else {
                        if (isEmpty(shopOrder)) {
                            ErrorDialog.showAlert(mContext, "未找到当前订单号");
                            return;
                        }
                        showLoading();
                        HttpHelper.getProcessSheets(shopOrder, this);
                    }
                }
                break;
            case R.id.iv_labuDetail_mainMat:
                BatchLabuDetailBo data1 = mMap_layoutInfo.get(mMatType);
                new ImageBrowserDialog(mContext, data1.getMAT_URL()).setParams(0.5f, 0.8f).show();
                break;
            case R.id.layout_button1:
            case R.id.layout_button2:
                String rabBo = (String) v.getTag(R.id.tag_rabBo);
                String rabNo = (String) v.getTag(R.id.tag_rabNo);
                String status = (String) v.getTag(R.id.tag_status);
                if ("SP".equals(mOperation.getOPERATION())) {
                    startLabuRecordActivity(v, false);
                } else {
                    BatchCutRecordBo data = new BatchCutRecordBo();
                    data.setSampleImg(mSampleImgUrl);
                    data.setRabRef(rabBo);
                    data.setRabNo(rabNo);
                    data.setMaterialType(mMatType);
                    data.setIsFinish("DONE".equals(status) ? "true" : "false");
                    BatchLabuDetailBo data2 = mMap_layoutInfo.get(mMatType);
                    if (data2 != null)
                        data.setMatImg(data2.getMAT_URL());

                    startActivityForResult(BatchCutWorkingActivity.getIntent(mContext, data, mOperation), REQUEST_RECORD_CUT);
                }
                break;
            case R.id.btn_rabHistory:
                mActionIndex = (int) v.getTag(R.id.tag_position);
                View labuOrderBtnWrap = mLayout_layoutList.getChildAt(mActionIndex).findViewById(R.id.layout_labuTable_labuOrderBtnWrap);
                if (labuOrderBtnWrap.getVisibility() == View.VISIBLE) {
                    mLayout_layoutList.getChildAt(mActionIndex).findViewById(R.id.layout_labuTable_labuBtn).setVisibility(View.VISIBLE);
                    labuOrderBtnWrap.setVisibility(View.GONE);
                } else {
                    String layoutRef = mCurMatData.getLAYOUT_INFO().get(mActionIndex).getZ_LAYOUT_BO();
                    showLoading();
                    HttpHelper.getRabHistoryList(mShopOrderBo, layoutRef, mMatType, this);
                }
                break;
            case R.id.btn_qrCode:
                new ManualPrintSubPackageDialog(mContext).setParams(0.4f, 0.4f).show();
                break;
            case R.id.btn_itemDetail:
                String shopOrderBO = mCurMatData.getSHOP_ORDER_BO();
                showLoading();
                HttpHelper.getSubpackageInfoByShopOrderRef(shopOrderBO, mOperation.getOPERATION(), mMatType, this);
                break;
        }
    }

    private void startLabuRecordActivity(View view, boolean editAble) {
        PostBatchRecordLabuBo data = new PostBatchRecordLabuBo();
        data.setOperation(mOperation.getOPERATION());
        data.setShopOrderRef(mShopOrderBo);
        data.setMaterialType(mMatType);

        BatchLabuDetailBo.LAYOUTINFOBean layoutInfoBean = mCurMatData.getLAYOUT_INFO().get(mActionIndex);
        String layoutRef = layoutInfoBean.getZ_LAYOUT_BO();
        String layoutName = layoutInfoBean.getLAYOUT();
        String layoutImg = layoutInfoBean.getPICTURE_URL();
        String layoutNo = layoutInfoBean.getLAY_NO();
        String rabNo = (String) view.getTag(R.id.tag_rabNo);
        data.setLayOutRef(layoutRef);
        data.setLayOutName(layoutName);
        data.setItem(mItem);
        data.setLayoutImgUrl(layoutImg);
        data.setLayoutNo(layoutNo);
        data.setMaterialType(mMatType);
        data.setRabOrderNo(rabNo);

        startActivityForResult(BatchLabuRecordActivity.getIntent(mContext, data, editAble), REQUEST_RECORD_LABU);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_RECORD_NC) {
                mList_recordNC = (List<RecordNCBo>) data.getSerializableExtra("badList");
            } else if (requestCode == REQUEST_RECORD_LABU || requestCode == REQUEST_RECORD_CUT) {
                mMap_layoutInfo.remove(mMatType);
                refreshMatView();
            }
        }
    }

    /**
     * 显示纸样图案
     */
    public void showPattern(String shopOrder) {
        if (isEmpty(shopOrder)) {
            showErrorDialog("工单号不能为空");
            return;
        }
        new PatternDialog(mContext, shopOrder).show();
    }

    private List<RecordNCBo> mList_recordNC;//记录不良

    public void recordNC() {
        BatchLabuDetailBo labuDetailBo = mMap_layoutInfo.get(mMatType);
        if (labuDetailBo == null) {
            showErrorDialog("无工单对应数据");
            return;
        }
        TailorInfoBo infoBo = new TailorInfoBo();
        infoBo.setOrderType("P");
        infoBo.setRFID("");
        infoBo.setSFC_BO("");

        TailorInfoBo.SHOPORDERINFORBean shopOrderInfo = new TailorInfoBo.SHOPORDERINFORBean();
        shopOrderInfo.setSHOP_ORDER(labuDetailBo.getSHOP_ORDER());
        shopOrderInfo.setSHOP_ORDER_BO(labuDetailBo.getSHOP_ORDER_BO());
        infoBo.setSHOP_ORDER_INFOR(shopOrderInfo);

        List<TailorInfoBo.OPERINFORBean> operaList = new ArrayList<>();
        TailorInfoBo.OPERINFORBean operaInfo = new TailorInfoBo.OPERINFORBean();
        operaInfo.setOPERATION(mOperation.getOPERATION());
        operaInfo.setOPERATION_BO(mOperation.getOPERATION_BO());
        operaList.add(operaInfo);
        infoBo.setOPER_INFOR(operaList);

        startActivityForResult(RecordCutNCActivity.getIntent(mContext, infoBo, mList_recordNC), REQUEST_RECORD_NC);
    }

    public void playVideo() {
        String videoUrl = mOperation.getVIDEO_URL();
        SystemUtils.playVideo(mContext, videoUrl);
    }

    /**
     * 退补料
     */
    public void returnAndFeedingMat(boolean isReturn) {
//        if (mTailorInfo == null) {
//            toast("请先获取订单数据");
//            return;
//        }
//        List<TailorInfoBo.MatInfoBean> itemArray = mTailorInfo.getMAT_INFOR();
//        List<ReturnMaterialInfoBo.MaterialInfoBo> materialList = new ArrayList<>();
//        for (TailorInfoBo.MatInfoBean item : itemArray) {
//            ReturnMaterialInfoBo.MaterialInfoBo material = new ReturnMaterialInfoBo.MaterialInfoBo();
//            material.setPicUrl(item.getMAT_URL());
//            material.setITEM(item.getMAT_NO());
//            material.setUNIT_LABEL(item.getUNIT_LABEL());
//            materialList.add(material);
//        }
//        ReturnMaterialInfoBo materialInfoBo = new ReturnMaterialInfoBo();
//        materialInfoBo.setOrderNum(mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
//        materialInfoBo.setMaterialInfoList(materialList);
//        if (isReturn) {
//            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_RETURN, materialInfoBo).show();
//        } else {
//            new CutReturnMatDialog(mContext, CutReturnMatDialog.TYPE_ADD, materialInfoBo).show();
//        }
    }

    private String mLayoutItem;

    private <T> View getTableView(T data, Class<T> clas, boolean isFirst, int position) {
        List<BatchLabuDetailBo.LAYOUTINFOBean.SINGLELAYOUTBean> list;
        boolean rabDisplay = false;
        View view;
        if (clas == BatchLabuDetailBo.LAYOUTINFOBean.class) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_table, null);

            BatchLabuDetailBo.LAYOUTINFOBean bean = (BatchLabuDetailBo.LAYOUTINFOBean) data;
            TextView tv_number = view.findViewById(R.id.tv_labuTable_number);
            tv_number.setText(bean.getLAY_NO());
            list = bean.getSINGLE_LAYOUT();
            rabDisplay = bean.isLAB_DISPLAY();

            CheckBox checkBox = view.findViewById(R.id.ckb_labuTable_toggle);
            if (isEmpty(mLayoutItem)) {
                mLayoutItem = bean.getITEM();
            } else if (!mLayoutItem.equals(bean.getITEM())) {
                checkBox.setBackgroundResource(R.drawable.bg_layoutimg2);
            }
            checkBox.setText(bean.getITEM());
            checkBox.setTag(position);
            if (mOnCheckedChangeListener == null) {
                mOnCheckedChangeListener = new CheckedChangeListener();
            }
            checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);

            List<BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean> rabOrderInfo = bean.getRAB_ORDER_INFO();
            refreshButton(view, rabOrderInfo, position);
        } else {
            view = findViewById(R.id.layout_labuDetail_table1);
            view.setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_labuTable_toggle).setVisibility(View.GONE);
            view.findViewById(R.id.layout_labuTable_shopOrder).setVisibility(View.VISIBLE);
            TextView tv_shopOrder = view.findViewById(R.id.tv_labuTable_shopOrder);
            TextView tv_item = view.findViewById(R.id.tv_labuTable_item);

            BatchLabuDetailBo detailBo = mMap_layoutInfo.get(mMatType);
            tv_shopOrder.setText(detailBo.getSHOP_ORDER());
            tv_item.setText(mItem);

            BatchLabuDetailBo bean = (BatchLabuDetailBo) data;
            list = bean.getORDER_INFO();

            if ("FB".equals(mOperation.getOPERATION())) {
                TextView btn_itemDetail = view.findViewById(R.id.btn_itemDetail);
                btn_itemDetail.setVisibility(View.VISIBLE);
                btn_itemDetail.setOnClickListener(this);
            }

        }

        LinearLayout layout_table = view.findViewById(R.id.layout_labuTable_detail);
        layout_table.removeAllViews();
        int size = list.size() + 2;//+2是为了显示字段行和"合计"行
        int placeOrderAll = 0;
        int yiLaAll = 0;
        int unLaAll = 0;
        for (int i = 0; i < size; i++) {
            View item1 = LayoutInflater.from(mContext).inflate(R.layout.layout_labu_table_item, null);
            TextView tv_size = item1.findViewById(R.id.tv_labuTableItem_size);
            TextView tv_placeOrder = item1.findViewById(R.id.tv_labuTableItem_placeOrder);
            TextView tv_yiLabu = item1.findViewById(R.id.tv_labuTableItem_yiLabu);
            TextView tv_unLabu = item1.findViewById(R.id.tv_labuTableItem_unLabu);

            if (i == 0) {
                tv_size.setText("码数");
                tv_placeOrder.setText("下单数");
                tv_yiLabu.setText("已" + mOperation.getDESCRIPTION());
                tv_unLabu.setText("待" + mOperation.getDESCRIPTION());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UnitUtil.dip2px(mContext, 80), ViewGroup.LayoutParams.MATCH_PARENT);
                layout_table.addView(item1, params);
                continue;
            } else if (i == size - 1) {
                tv_size.setText("合计");
                tv_placeOrder.setText(placeOrderAll + "");
                tv_yiLabu.setText(yiLaAll + "");
                tv_unLabu.setText(unLaAll + "");
            } else {
                BatchLabuDetailBo.LAYOUTINFOBean.SINGLELAYOUTBean bean = list.get(i - 1);
                tv_size.setText(bean.getSIZE_CODE());
                tv_placeOrder.setText(bean.getSIZE_TOTAL() + "");
                tv_yiLabu.setText(bean.getSIZE_FEN() + "");
                tv_unLabu.setText(bean.getSIZE_LEFT() + "");

                placeOrderAll += bean.getSIZE_TOTAL();
                yiLaAll += bean.getSIZE_FEN();
                unLaAll += bean.getSIZE_LEFT();
            }

            layout_table.addView(item1, mLayoutParams_weight1_horizontal);
        }

        Button btn_rabHistory = view.findViewById(R.id.btn_rabHistory);
        btn_rabHistory.setTag(R.id.tag_position, position);
        btn_rabHistory.setOnClickListener(this);

        LinearLayout layout_actionList = view.findViewById(R.id.layout_labuTable_labuBtn);
        for (int i = isFirst ? 0 : 1; i < 3; i++) {
            Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button_green, null);
            switch (i) {
                case 0:
                    button.setId(R.id.btn_firstLabu);
                    button.setText("首期拉布单");
                    break;
                case 1:
                    if (!rabDisplay) {
                        button.setBackgroundResource(R.drawable.btn_disable_round);
                        button.setEnabled(false);
                    }
                    button.setId(R.id.btn_batchLabu);
                    button.setText("大货拉布单");
                    break;
                case 2:
                    if (!rabDisplay) {
                        button.setBackgroundResource(R.drawable.btn_disable_round);
                        button.setEnabled(false);
                    }
                    button.setId(R.id.btn_addLabu);
                    button.setText("补料拉布单");
                    break;
            }
            button.setOnClickListener(this);
            layout_actionList.addView(button, mLayoutParams_right10);
        }
        return view;
    }

    private int mActionIndex = -1;

    /**
     * 刷新拉布单按钮
     */
    private void refreshButton(View parent, List<BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean> rabOrderInfo, int index) {
        View view = mLayout_layoutList.getChildAt(index);
        if (view == null) {
            view = parent;
        }
        LinearLayout layout_orderBtnList = view.findViewById(R.id.layout_labuTable_labuOrderBtn);
        layout_orderBtnList.removeAllViews();
        for (int i = 0; i < rabOrderInfo.size(); i += 2) {
            View view1 = LayoutInflater.from(mContext).inflate(R.layout.layout_2button, null);
            BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean bean1 = rabOrderInfo.get(i);
            TextView tv_orderNo1 = view1.findViewById(R.id.tv_orderNo1);
            tv_orderNo1.setText(bean1.getRAB_NO());

            View button1 = view1.findViewById(R.id.layout_button1);
            setButtonStatus(button1, bean1.getSTATUS(), bean1.getZ_RAB_BO(), bean1.getRAB_NO());

            if (i + 1 < rabOrderInfo.size()) {
                BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean bean2 = rabOrderInfo.get(i + 1);
                TextView tv_orderNo2 = view1.findViewById(R.id.tv_orderNo2);
                tv_orderNo2.setText(bean2.getRAB_NO());

                View button2 = view1.findViewById(R.id.layout_button2);
                setButtonStatus(button2, bean2.getSTATUS(), bean2.getZ_RAB_BO(), bean2.getRAB_NO());
            } else {
                view1.findViewById(R.id.layout_button2).setVisibility(View.INVISIBLE);
            }
            layout_orderBtnList.addView(view1, mLayoutParams_right10);
        }
    }

    private void setButtonStatus(View view, String status, String rabBo, String rabNo) {
        if ("DONE".equals(status)) {
            view.setBackgroundResource(R.drawable.btn_gray_round);
        }
        view.setTag(R.id.tag_status, status);
        view.setTag(R.id.tag_rabBo, rabBo);
        view.setTag(R.id.tag_rabNo, rabNo);
        view.setOnClickListener(this);
    }

    private CheckedChangeListener mOnCheckedChangeListener;

    private class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mActionIndex = (int) buttonView.getTag();
            View childAt = mLayout_layoutList.getChildAt(mActionIndex);
            if (isChecked) {
                childAt.findViewById(R.id.layout_labuTable_detail).setVisibility(View.GONE);
                childAt.findViewById(R.id.layout_labuTable_btnWrap).setVisibility(View.VISIBLE);

                if ("SP".equals(mOperation.getOPERATION())) {
                    childAt.findViewById(R.id.btn_rabHistory).setVisibility(View.VISIBLE);
                    childAt.findViewById(R.id.layout_labuTable_labuBtn).setVisibility(View.VISIBLE);
                    childAt.findViewById(R.id.layout_labuTable_labuOrderBtnWrap).setVisibility(View.GONE);
                } else {
                    childAt.findViewById(R.id.layout_labuTable_labuOrderBtnWrap).setVisibility(View.VISIBLE);

                    String layoutRef = mCurMatData.getLAYOUT_INFO().get(mActionIndex).getZ_LAYOUT_BO();
                    HttpHelper.getRabInfoList(mOperation.getOPERATION(), mShopOrderBo, mMatType, layoutRef, BatchLabuDetailActivity.this);
                }
            } else {
                childAt.findViewById(R.id.layout_labuTable_detail).setVisibility(View.VISIBLE);
                childAt.findViewById(R.id.layout_labuTable_btnWrap).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (HttpHelper.XMII_URL.equals(url)) {
            if (HttpHelper.isSuccess(resultJSON)) {
                mProcessSheetsBo = JSON.parseObject(resultJSON.getString("result"), ProcessSheetsBo.class);
                if (mProcessSheetsBo == null) {
                    ErrorDialog.showAlert(mContext, "根据工单没有查到对应的款号");
                } else {
                    new ProcessSheetsDialog(mContext, mProcessSheetsBo).show();
                }
            } else {
                showErrorDialog(resultJSON.getString("result"));
            }
        } else {
            if (HttpHelper.isSuccess(resultJSON)) {
                if (HttpHelper.getBatchMatInfo.equals(url)) {
                    JSONArray array = resultJSON.getJSONArray("result");
                    if (array != null && array.size() != 0) {
                        mList_tab = JSON.parseArray(array.toString(), DictionaryDataBo.class);
                        initTabView();
                        setupView();
                    }
                } else if (HttpHelper.getBatchLayoutInfo.equals(url)) {
                    BatchLabuDetailBo data = JSON.parseObject(HttpHelper.getResultStr(resultJSON), BatchLabuDetailBo.class);
                    if (data == null) {
                        showErrorDialog("返回数据为空");
                    } else {
                        SpUtil.save(SpUtil.KEY_SHOPORDER, data.getSHOP_ORDER());
                        mMap_layoutInfo.put(mMatType, data);
                        refreshMatView();
                    }
                } else if (HttpHelper.getRabInfoList.equals(url)) {
                    List<BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean> rabOrderInfo = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean.class);
                    if (rabOrderInfo != null)
                        refreshButton(null, rabOrderInfo, mActionIndex);
                } else if (HttpHelper.getRabHistoryList.equals(url)) {
                    List<BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean> rabListInfo = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchLabuDetailBo.LAYOUTINFOBean.RABORDERINFOBean.class);
                    if (rabListInfo == null || rabListInfo.size() == 0) {
                        showErrorDialog("该排料图暂无拉布记录");
                        return;
                    }
                    mLayout_layoutList.getChildAt(mActionIndex).findViewById(R.id.layout_labuTable_labuBtn).setVisibility(View.GONE);
                    mLayout_layoutList.getChildAt(mActionIndex).findViewById(R.id.layout_labuTable_labuOrderBtnWrap).setVisibility(View.VISIBLE);
                    refreshButton(null, rabListInfo, mActionIndex);
//                    new RabNoListDialog(mContext, rabListInfo).setParams(0.6f, 0.6f).show();
                }else if (HttpHelper.getSubpackageInfoByShopOrderRef.equals(url)){
                    SubPackageInfoBo data = JSON.parseObject(HttpHelper.getResultStr(resultJSON),SubPackageInfoBo.class);
                    startActivity(SubPackageDetailActivity.getIntent(mContext,data,mCurMatData.getSHOP_ORDER(),mItem));
                }
            }
        }
    }

    public static Intent getIntent(Context context, PositionInfoBo.OPERINFORBean operation, String shopOrder, String shopOderBo, String item) {
        Intent intent = new Intent(context, BatchLabuDetailActivity.class);
        intent.putExtra("operation", operation);
        intent.putExtra("shopOrder", shopOrder);
        intent.putExtra("shopOrderBo", shopOderBo);
        intent.putExtra("item", item);
        return intent;
    }
}
