package com.eeka.mespad.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BatchCutRecordBo;
import com.eeka.mespad.bo.BatchCutWorkingBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.BatchSplitPackageDialog;
import com.eeka.mespad.view.dialog.CutRecordCheckDialog;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.ImageBrowserDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 大货裁剪作业单
 */
public class BatchCutWorkingActivity extends BaseActivity {

    protected PositionInfoBo.OPERINFORBean mOperation;

    private BatchCutWorkingBo mData;

    private BatchCutRecordBo mPostData;

    private LinearLayout mLayout_items;

    private Button mBtn_start;

    private boolean isStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_batchcut_working);

        mPostData = (BatchCutRecordBo) getIntent().getSerializableExtra("data");
        mOperation = (PositionInfoBo.OPERINFORBean) getIntent().getSerializableExtra("operation");
        mPostData.setOperationBo(mOperation.getOPERATION_BO());
        mOperationFlag = "BEGIN";

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        mLayout_items = findViewById(R.id.layout_batchCutWork_table);

        mBtn_start = findViewById(R.id.btn_start);
        mBtn_start.setOnClickListener(this);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_completed).setOnClickListener(this);
        findViewById(R.id.btn_layoutImg).setOnClickListener(this);
        findViewById(R.id.btn_mainMatImg).setOnClickListener(this);
        findViewById(R.id.btn_sampleImg).setOnClickListener(this);
        findViewById(R.id.btn_splitPackage_qty).setOnClickListener(this);
        findViewById(R.id.btn_splitPackage_orderNum).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isStarted) {
            ErrorDialog.showAlert(mContext, "该工单已开始操作，是否确定退出？", ErrorDialog.TYPE.ALERT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exit();
                }
            }, false);
        } else {
            exit();
        }
    }

    private void exit() {
        List<BatchCutRecordBo.CutSizesBean> selectedSize = getSelectedSize();
        if (selectedSize != null && selectedSize.size() != 0) {
            HttpHelper.removeSizesMarked(mOperation.getOPERATION(), mPostData.getRabRef(), mPostData.getShopOrderRef(), selectedSize, this);
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_layoutImg:
                String layoutImg = mPostData.getLayoutImg();
                new ImageBrowserDialog(mContext, layoutImg).setParams(0.8f, 0.8f).show();
                break;
            case R.id.btn_mainMatImg:
                new ImageBrowserDialog(mContext, mPostData.getMatImg()).setParams(0.5f, 0.8f).show();
                break;
            case R.id.btn_sampleImg:
                String sampleImg = mPostData.getSampleImg();
                if (!isEmpty(sampleImg))
                    new ImageBrowserDialog(mContext, sampleImg).setParams(0.5f, 0.8f).show();
                break;
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_completed:
                if ("BEGIN".equals(mOperationFlag)) {
                    ErrorDialog.showAlert(mContext, "请先执行开始操作");
                    return;
                }
                List<BatchCutRecordBo.CutSizesBean> selectedSize = getSelectedSize();
                if (selectedSize.size() == 0) {
                    showErrorDialog("请选择尺码");
                    return;
                }
                new CutRecordCheckDialog(mContext, mOperation.getDESCRIPTION(), mData.getORDER_NO(), selectedSize, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completed();
                    }
                }).setParams(0.5f, 0.5f).show();
                break;
            case R.id.btn_splitPackage_qty:
                splitPackage(0);
                break;
            case R.id.btn_splitPackage_orderNum:
                splitPackage(1);
                break;
            case R.id.layout_rootView:
                int index = (int) v.getTag();
                View view = mLayout_items.getChildAt(index);
                TextView tv_size = view.findViewById(R.id.tv_size);
                String size = tv_size.getText().toString();
                mSelectCheckBox = view.findViewById(R.id.ckb_sizeSelect);
                if (mSelectCheckBox.isEnabled()) {
                    mSelectCheckBox.setChecked(!mSelectCheckBox.isChecked());

                    //码数锁定
//                    showLoading();
//                    String flag = mSelectCheckBox.isChecked() ? "UNSELECT" : "SELECT";
//                    BatchCutWorkingBo.RABINFOBean bean = mData.getRAB_INFO().get(index - 1);
//                    HttpHelper.markSelectedSize(mOperation.getOPERATION(), mPostData.getRabRef(), mPostData.getShopOrderRef(), bean.getCUT_NUM(), size, flag, this);
                } else {
                    if ("FB".equals(mOperation.getOPERATION())) {
                        List<BatchCutWorkingBo.RABINFOBean> rabInfo = mData.getRAB_INFO();
                        BatchCutWorkingBo.RABINFOBean bean = rabInfo.get(index - 1);

                        showSplitPrintDialog(false, bean.getSIZE_CODE(), bean.getSIZE_TOTAL(), bean.getCUT_NUM(), true);
                    }
                }
                break;
        }
    }

    private CheckBox mSelectCheckBox;

    /**
     * @param type 0=件数分包，1=工单号分包
     */
    private void splitPackage(int type) {
        List<BatchCutRecordBo.CutSizesBean> selectedSize = getSelectedSize();
        if (selectedSize.size() == 0) {
            showErrorDialog("请选择尺码");
            return;
        }
        if (selectedSize.size() > 1) {
            showErrorDialog("分包操作一次只能选择一个尺码");
            return;
        }
        mPostData.setCutSizes(selectedSize);
        BatchCutRecordBo.CutSizesBean sizesBean = selectedSize.get(0);
        showSplitPrintDialog(type == 0, sizesBean.getSizeCode(), sizesBean.getSizeTotal(), sizesBean.getCutNum(), false);
    }

    private void showSplitPrintDialog(boolean editable, String sizeCode, int sizeTotal, int cutNum, boolean onlyPrint) {
        mPostData.setWorkSeq(mData.getORDER_SEQ());
        mPostData.setCutNum(cutNum);
        BatchSplitPackageDialog dialog = new BatchSplitPackageDialog(mContext, editable, mPostData, sizeCode, sizeTotal, onlyPrint);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (isFinish) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    initData();
                }
            }
        });
        dialog.setOnSaveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < mLayout_items.getChildCount() - 1; i++) {
                    View view = mLayout_items.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.ckb_sizeSelect);
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        checkBox.setEnabled(false);
                        //分包是单选，所以只要找到一个被选中的尺码就退出循环
                        break;
                    }
                }
            }
        });
        dialog.setParams(0.6f, 0.8f).show();
    }

    /**
     * 获取已选择的码数数据
     */
    private List<BatchCutRecordBo.CutSizesBean> getSelectedSize() {
        isFinish = true;
        List<BatchCutRecordBo.CutSizesBean> list = new ArrayList<>();
        if (mData == null) {
            return list;
        }
        List<BatchCutWorkingBo.RABINFOBean> rabInfo = mData.getRAB_INFO();
        for (int i = 0; i < rabInfo.size(); i++) {
            View view = mLayout_items.getChildAt(i + 1);
            CheckBox checkBox = view.findViewById(R.id.ckb_sizeSelect);
            if (checkBox.isEnabled()) {
                if (checkBox.isChecked()) {
                    BatchCutWorkingBo.RABINFOBean item = rabInfo.get(i);
                    BatchCutRecordBo.CutSizesBean bean = new BatchCutRecordBo.CutSizesBean();
                    bean.setSizeCode(item.getSIZE_CODE());
                    bean.setSizeFen(item.getSIZE_FEN());
                    bean.setSizeLeft(item.getSIZE_LEFT());
                    bean.setSizeTotal(item.getSIZE_TOTAL());
                    bean.setCutNum(item.getCUT_NUM());
                    list.add(bean);
                } else {
                    isFinish = false;
                }
            }
        }
        return list;
    }

    private String mOperationFlag;

    private void start() {
        String userId = SpUtil.getLoginUserId();
        if (isEmpty(userId)) {
            showErrorDialog("需要员工登录上岗才能操作");
            return;
        }
        showLoading();
        HttpHelper.operationProduce(userId, mOperationFlag, mPostData.getShopOrderRef(), mPostData.getRabRef(), mData.getORDER_NO(), mPostData.getOperation(), this);
    }

    private boolean isFinish;

    private void completed() {
        String userId = SpUtil.getLoginUserId();
        if (isEmpty(userId)) {
            showErrorDialog("需要员工登录上岗才能操作");
            return;
        }
        mPostData.setIsFinish(isFinish ? "true" : "false");
        mPostData.setCutSizes(getSelectedSize());
        mPostData.setWorkSeq(mData.getORDER_SEQ());
        mPostData.setWorkNo(mData.getORDER_NO());

        showLoading();
        HttpHelper.saveBatchCutData(userId, mPostData, this);
    }

    private void setupView() {
        TextView tv_title = findViewById(R.id.tv_title);
        if ("true".equals(mPostData.getIsFinish())) {
            tv_title.setText(mOperation.getDESCRIPTION() + "作业单");
        } else {
            tv_title.setText(mOperation.getDESCRIPTION() + "作业单：" + mData.getORDER_NO());
        }

        if ("false".equals(mPostData.getIsFinish())) {
            if ("FB".equals(mOperation.getOPERATION())) {
                findViewById(R.id.layout_buttonList_splitPackage).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.layout_buttonList_cut).setVisibility(View.VISIBLE);
            }
        }

        mLayout_items.removeAllViews();
        List<BatchCutWorkingBo.RABINFOBean> rabInfo = mData.getRAB_INFO();
        if (rabInfo == null) {
            rabInfo = new ArrayList<>();
        }
        if (rabInfo.size() != 0 && "false".equals(mData.getIS_FINISH())) {
            mBtn_start.setEnabled(true);
            findViewById(R.id.btn_completed).setEnabled(true);
        }
        int size = rabInfo.size() + 2;//添加标题和合计
        int completedTotal = 0;
        int waitingTotal = 0;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        for (int i = 0; i < size; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_batchcut_tableitem, null);
            TextView tv_cutNum = view.findViewById(R.id.tv_cutNum);
            TextView tv_size = view.findViewById(R.id.tv_size);
            TextView tv_completed = view.findViewById(R.id.tv_completed);
            TextView tv_wait = view.findViewById(R.id.tv_waiting);
            TextView tv_action = view.findViewById(R.id.tv_action);
            CheckBox checkBox = view.findViewById(R.id.ckb_sizeSelect);
            if (i == 0) {
                tv_completed.setText("已" + mOperation.getDESCRIPTION() + "(件)");
                tv_wait.setText("待" + mOperation.getDESCRIPTION() + "(件)");

                checkBox.setVisibility(View.GONE);
            } else if (i == size - 1) {
                tv_cutNum.setText("合计");
                tv_size.setText(null);
                checkBox.setVisibility(View.INVISIBLE);
                tv_completed.setText(completedTotal + "");
                tv_wait.setText(waitingTotal + "");

                tv_action.setText(null);
                checkBox.setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.layout_rootView).setOnClickListener(this);
                view.setTag(i);
                BatchCutWorkingBo.RABINFOBean bean = rabInfo.get(i - 1);
                tv_cutNum.setText(bean.getCUT_NUM() + "");
                tv_size.setText(bean.getSIZE_CODE());
                tv_completed.setText(bean.getSIZE_FEN() + "");
                tv_wait.setText(bean.getSIZE_LEFT() + "");
                completedTotal += bean.getSIZE_FEN();
                waitingTotal += bean.getSIZE_LEFT();

                if (bean.getSIZE_FEN() != 0) {
                    checkBox.setEnabled(false);
                }

                tv_action.setVisibility(View.GONE);
            }
            mLayout_items.addView(view, params);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        String loginUserId = SpUtil.getLoginUserId();
        if (isEmpty(loginUserId)) {
            showErrorDialog("请员工先登录");
            return;
        }
        showLoading();
        HttpHelper.getBatchCutWorkingInfo(loginUserId, mPostData.getMaterialType(), mOperation.getOPERATION(), mPostData.getRabNo(), mPostData.getIsFinish(), this);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBatchCutWorkingInfo.equals(url)) {
                mData = JSON.parseObject(HttpHelper.getResultStr(resultJSON), BatchCutWorkingBo.class);
                mPostData.setIsFinish(mData.getIS_FINISH());
                mPostData.setWorkNo(mData.getORDER_NO());
                mPostData.setOperation(mOperation.getOPERATION());
                mPostData.setOperationBo(mOperation.getOPERATION_BO());
                mPostData.setLayoutNo(mData.getLAYOUT_NO());
                mPostData.setItem(mData.getITEM());
                mPostData.setShopOrder(mData.getSHOP_ORDER());
                mPostData.setShopOrderRef(mData.getSHOP_ORDER_BO());
                mPostData.setLayOutRef(mData.getZ_LAYOUT_BO());
                mPostData.setLayoutImg(mData.getLAYOUT_IMAGE());
                mPostData.setRabRef(mData.getZ_RAB_BO());

                setupView();

                //多个尺码未选完保存的时候，自动开始新单
                if (!"BEGIN".equals(mOperationFlag)) {
                    mOperationFlag = "BEGIN";
                    start();
                }
            } else if (HttpHelper.saveBatchCutData.equals(url)) {
                isStarted = false;
                if (isFinish) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    initData();
                }
            } else if (HttpHelper.operationProduce.equals(url)) {
                isStarted = true;
                if ("BEGIN".equals(mOperationFlag) || "RESTART".equals(mOperationFlag)) {
                    mOperationFlag = "PAUSE";
                    mBtn_start.setText("暂停");
                    mBtn_start.setBackgroundResource(R.drawable.btn_yellow_round);
                } else {
                    mOperationFlag = "RESTART";
                    mBtn_start.setText("继续");
                    mBtn_start.setBackgroundResource(R.drawable.btn_green_round);
                }
            } else if (HttpHelper.markSelectedSize.equals(url)) {
                mSelectCheckBox.setChecked(!mSelectCheckBox.isChecked());
            }
        }
    }

    public static Intent getIntent(Context context, BatchCutRecordBo data, PositionInfoBo.OPERINFORBean operation) {
        Intent intent = new Intent(context, BatchCutWorkingActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("operation", operation);
        return intent;
    }
}
