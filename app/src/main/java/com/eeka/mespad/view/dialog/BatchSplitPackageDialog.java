package com.eeka.mespad.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.BatchCutRecordBo;
import com.eeka.mespad.bo.BatchSplitPackageItemBo;
import com.eeka.mespad.bo.BatchSplitPackagePrintBo;
import com.eeka.mespad.bo.BatchSplitPackageSaveBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.FormatUtil;
import com.eeka.mespad.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

public class BatchSplitPackageDialog extends BaseDialog implements HttpCallback, View.OnClickListener {

    private BatchCutRecordBo mData;
    private String mSizeCode;
    private int mSizeTotal;

    private boolean isEditable;

    private LinearLayout mLayout_items;
    private List<BatchSplitPackageItemBo> mList_item;

    private List<BatchSplitPackagePrintBo> mList_printData;

    private boolean isOnlyPrint;

    public BatchSplitPackageDialog(@NonNull Context context, boolean editable, BatchCutRecordBo data, String sizeCode, int sizeTotal, boolean onlyPrint) {
        super(context);
        isEditable = editable;
        mData = data;
        mSizeCode = sizeCode;
        mSizeTotal = sizeTotal;
        isOnlyPrint = onlyPrint;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_splitpackage_batch, null);
        setContentView(mView);

        TextView tv_shopOrder = mView.findViewById(R.id.tv_shopOrder);
        TextView tv_item = mView.findViewById(R.id.tv_item);
        TextView tv_workNo = mView.findViewById(R.id.tv_workNo);
        TextView tv_layoutNo = mView.findViewById(R.id.tv_layoutNo);
        TextView tv_size = mView.findViewById(R.id.tv_size);
        TextView tv_qty = mView.findViewById(R.id.tv_qty);
        TextView tv_typeTitle = mView.findViewById(R.id.tv_splitCard_typeTitle);
        if (!isEditable) {
            tv_typeTitle.setText("按拉布单分包");
        }

        tv_shopOrder.setText(mData.getShopOrder());
        tv_item.setText(mData.getItem());
        tv_workNo.setText(mData.getWorkNo());
        tv_layoutNo.setText(mData.getLayoutNo());
        tv_size.setText(mSizeCode);
        tv_qty.setText(mSizeTotal + "");

        mLayout_items = mView.findViewById(R.id.layout_itemList);

        mView.findViewById(R.id.btn_add).setOnClickListener(this);
        mView.findViewById(R.id.btn_del).setOnClickListener(this);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mView.findViewById(R.id.btn_ok).setOnClickListener(this);

    }

    private View.OnClickListener mOnSaveListener;

    /**
     * 设置保存成功后的回调
     */
    public void setOnSaveListener(View.OnClickListener listener) {
        mOnSaveListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (isOnlyPrint) {
                    dismiss();
                } else {
                    if (findViewById(R.id.layout_button).getVisibility() == View.VISIBLE) {
                        dismiss();
                    } else {
                        cancel();
                    }
                }
                break;
            case R.id.btn_add:
                int qty = 0;
                for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
                    View view = mLayout_items.getChildAt(i);
                    EditText editText = view.findViewById(R.id.et_qty);
                    String valueStr = editText.getText().toString();
                    int value = FormatUtil.strToInt(valueStr);
                    qty += value;
                }
                BatchSplitPackageItemBo item = new BatchSplitPackageItemBo();
                item.setSubPackageQty(mSizeTotal - qty);
                mLayout_items.addView(getItemView(item, mLayout_items.getChildCount() - 1), mLayout_items.getChildCount() - 1);
                refreshPrintSeq();
                break;
            case R.id.btn_del:
                if (mLayout_items.getChildCount() > 1) {
                    mLayout_items.removeViewAt(mLayout_items.getChildCount() - 2);
                }
                break;
            case R.id.btn_ok:
                check();
                break;
            case R.id.tv_printSeq:
                mPrintingIndex = (int) v.getTag();
//                BatchSplitPackagePrintBo printBo = mList_printData.get(mPrintingIndex);
//                if (printBo.isPrinted() || !"M".equals(mData.getMaterialType())) {
                recordPrintState(v);
//                } else {
//                    showBindRfidDialog(v, printBo.getRfid());
//                }
                break;
        }
    }

    private int mPrintingIndex;

    private void completed() {
        String userId = SpUtil.getLoginUserId();
        if (isEmpty(userId)) {
            ErrorDialog.showAlert(mContext, "需要员工登录上岗才能操作");
            return;
        }

        LoadingDialog.show(mContext);
        mData.setCutSizes(null);
        BatchSplitPackagePrintBo printBo = mList_printData.get(mPrintingIndex);
        mData.setWorkNo(printBo.getWorkNo());
        HttpHelper.completedSplitPrint(userId, mData, this);
    }

    private void recordPrintState(View v) {
        BatchSplitPackagePrintBo printBo = mList_printData.get(mPrintingIndex);
        printBo.setSizeCode(mSizeCode);
        if (!printBo.isPrinted()) {
            LoadingDialog.show(mContext);
            HttpHelper.recordSubPackagePrintInfo(mData.getShopOrderRef(), mSizeCode, printBo.getSubPackageSeq(), printBo.getRfid(), printBo.getProcessLotRef(), this);
            if (mPrintingIndex == 0) {
                completed();
            }
        } else {
            print();
        }
    }

    private void print() {
        LoadingDialog.show(mContext);
        BatchSplitPackagePrintBo printBo = mList_printData.get(mPrintingIndex);
        printBo.setMatType(mData.getMaterialType());
        boolean success = BluetoothHelper.printSubPackageInfo((Activity) mContext, printBo);
        if (success) {
            View childAt = mLayout_items.getChildAt(mPrintingIndex);
            childAt.findViewById(R.id.tv_printSeq).setEnabled(false);
            new BatchSplitPackagePrintContentDialog(mContext, printBo).setParams(0.45f, 0.5f).show();
            if (!isOnlyPrint) {
                if (mPrintingIndex > 0) {
                    setPrintEnable(mPrintingIndex - 1);
                }
            }
        }
        LoadingDialog.dismiss();
    }

    private void check() {
        int qtyAll = 0;
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            View view = mLayout_items.getChildAt(i);
            EditText editText = view.findViewById(R.id.et_qty);
            if (isEmpty(editText.getText().toString())) {
                TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);
                ErrorDialog.showAlert(mContext, "分包号 " + tv_packageNum.getText().toString() + "，数量不能为空");
                return;
            }
            int qty = FormatUtil.strToInt(editText.getText().toString());
            qtyAll += qty;
        }
        if (qtyAll != mSizeTotal) {
            ErrorDialog.showAlert(mContext, "当前拉布单分包数量:" + qtyAll + " 与总数:" + mSizeTotal + " 不符,是否确定保存？", ErrorDialog.TYPE.ALERT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SplitPackageCheckDialog(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save();
                        }
                    }).setParams(0.5f, 0.55f).show();
                }
            }, false);
        } else {
            new SplitPackageCheckDialog(mContext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            }).setParams(0.5f, 0.55f).show();
        }
    }

    private void save() {
        List<BatchSplitPackageSaveBo.SubPackagesBean> list = new ArrayList<>();
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            View view = mLayout_items.getChildAt(i);
            EditText editText = view.findViewById(R.id.et_qty);
            TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);

            BatchSplitPackageSaveBo.SubPackagesBean item = new BatchSplitPackageSaveBo.SubPackagesBean();
            item.setSubPackageSeq(tv_packageNum.getText().toString());
            item.setSubPackageQty(editText.getText().toString());

            list.add(item);
        }

        BatchSplitPackageSaveBo data = new BatchSplitPackageSaveBo();
        data.setItem(mData.getItem());
        data.setLayOutRef(mData.getLayOutRef());
        data.setShopOrder(mData.getShopOrder());
        data.setShopOrderRef(mData.getShopOrderRef());
        data.setSizeCode(mSizeCode);
        data.setSubPackages(list);
        data.setSubSeq(mData.getWorkSeq());
        data.setSubOrder(mData.getWorkNo());
        data.setMaterialType(mData.getMaterialType());
        data.setCutNum(mData.getCutNum());

        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers == null || positionUsers.size() == 0) {
            ErrorDialog.showAlert(mContext, "请员工上岗再操作");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UserInfoBo user : positionUsers) {
            sb.append(user.getEMPLOYEE_NUMBER()).append(",");
        }
        String userId = sb.substring(0, sb.length() - 1);
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        LoadingDialog.show(mContext);
        HttpHelper.saveBatchSplitPackageData(userId, mData.getOperationBo(), resource.getRESOURCE_BO(), mData, data, this);
    }

    private void refreshItemView() {
        findViewById(R.id.btn_ok).setVisibility(View.GONE);
        TextView tv_cancel = findViewById(R.id.btn_cancel);
        tv_cancel.setText("关闭");
        TextView tv_tag = findViewById(R.id.tv_packageNum_tag);
        tv_tag.setText("包号");

        //删除新增按钮
        mLayout_items.removeViewAt(mLayout_items.getChildCount() - 1);

        mLayout_items.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);

        int index = mLayout_items.getChildCount() - 1;
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            BatchSplitPackagePrintBo printBo = mList_printData.get(i);
            View view = mLayout_items.getChildAt(i);
            TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);
            tv_packageNum.setText(printBo.getSubPackageSeq() + "");
            EditText editText = view.findViewById(R.id.et_qty);
            editText.setText(printBo.getSubPackageQty() + "");
            editText.setEnabled(false);

            if (isOnlyPrint) {
                TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
                tv_printSeq.setEnabled(true);
                if (printBo.isPrinted()) {
                    tv_printSeq.setBackgroundResource(R.drawable.btn_disable);
                }
            } else {
                if (index == mLayout_items.getChildCount() - 1 && printBo.isPrinted()) {
                    index = i - 1;
                }
            }
        }

        if (!isOnlyPrint) {
            setPrintEnable(index);
        }
    }

    /**
     * 设置打印按钮可以点击
     *
     * @param index 可以点击的按钮所在行下标
     */
    private void setPrintEnable(int index) {
        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View view = mLayout_items.getChildAt(i);
            TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
            tv_printSeq.setEnabled(false);

            if (isOnlyPrint || i == index) {
                tv_printSeq.setEnabled(true);
            }
        }
    }

    private <T> View getItemView(T data, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_split_print_item, null);
        TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);
        tv_packageNum.setText((position + 1) + "");

        TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
        tv_printSeq.setTag(position);
        tv_printSeq.setOnClickListener(this);

        EditText editText = view.findViewById(R.id.et_qty);
        editText.setEnabled(isEditable);

        if (data instanceof BatchSplitPackageItemBo) {
            BatchSplitPackageItemBo item = (BatchSplitPackageItemBo) data;
            editText.setText(item.getSubPackageQty() + "");
            tv_printSeq.setText("打印顺序" + (mList_item.size() - position));
        } else if (data instanceof BatchSplitPackagePrintBo) {
            BatchSplitPackagePrintBo item = (BatchSplitPackagePrintBo) data;
            editText.setText(item.getSubPackageQty() + "");
            tv_printSeq.setEnabled(true);
            tv_printSeq.setText("打印顺序" + (mList_printData.size() - position));
        }
        return view;
    }

    /**
     * 新增一行时刷新打印顺序
     */
    private void refreshPrintSeq() {
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            View view = mLayout_items.getChildAt(i);
            TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
            tv_printSeq.setTag(i);
            tv_printSeq.setText("打印顺序" + (mLayout_items.getChildCount() - 1 - i));
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBatchSplitItemByCustom.equals(url) || HttpHelper.getBatchSplitItemByRabRef.equals(url)) {
                mLayout_items.removeViews(0, mLayout_items.getChildCount() - 1);
                mList_item = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchSplitPackageItemBo.class);
                if (mList_item == null || mList_item.size() == 0) {
                    ErrorDialog.showAlert(mContext, "分包数据为空");
                    return;
                }
                for (int i = 0; i < mList_item.size(); i++) {
                    BatchSplitPackageItemBo itemBo = mList_item.get(i);
                    mLayout_items.addView(getItemView(itemBo, i), mLayout_items.getChildCount() - 1);
                }
                if (!isEditable) {
                    findViewById(R.id.layout_button).setVisibility(View.INVISIBLE);
                }
            } else if (HttpHelper.saveBatchSplitPackageData.equals(url)) {
                mList_printData = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchSplitPackagePrintBo.class);
                refreshItemView();
                if (mOnSaveListener != null) {
                    mOnSaveListener.onClick(null);
                }
            } else if (HttpHelper.getBatchSplitItemBySize.equals(url)) {
                mList_printData = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchSplitPackagePrintBo.class);
                for (int i = 0; i < mList_printData.size(); i++) {
                    BatchSplitPackagePrintBo itemBo = mList_printData.get(i);
                    mLayout_items.addView(getItemView(itemBo, i), mLayout_items.getChildCount() - 1);
                    if (i == 0) {
                        TextView tv_workNo = mView.findViewById(R.id.tv_workNo);
                        tv_workNo.setText(itemBo.getWorkNo());
                    }
                }
                refreshItemView();
            } else if (HttpHelper.recordSubPackagePrintInfo.equals(url)) {
                print();
            } else if (HttpHelper.completedSplitPrint.equals(url)) {

            }
        } else {
            ErrorDialog.showAlert(mContext, resultJSON.getString("message"));

            //获取数据失败时关闭弹框
            if (!HttpHelper.saveBatchSplitPackageData.equals(url)) {
                dismiss();
            }
        }
        LoadingDialog.dismiss();
    }

    @Override
    public void onFailure(String url, int code, String message) {
        LoadingDialog.dismiss();
        ErrorDialog.showAlert(mContext, message);
    }

    @Override
    public void show() {
        super.show();
        LoadingDialog.show(mContext);
        if (isOnlyPrint) {
            TextView tv_tag = findViewById(R.id.tv_packageNum_tag);
            tv_tag.setText("包号");
            HttpHelper.getBatchSplitItemBySize(mData.getOperation(), mSizeCode, mData.getRabRef(), mData.getCutNum(), this);
        } else {
            if (isEditable) {
                HttpHelper.getBatchSplitItemByCustom(mData.getShopOrderRef(), mSizeCode, mSizeTotal, this);
            } else {
                HttpHelper.getBatchSplitItemByRabRef(mData.getShopOrderRef(), mSizeCode, mData.getRabRef(), mData.getCutNum(), this);
            }
        }
    }
}
