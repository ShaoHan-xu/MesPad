package com.eeka.mespad.view.dialog;

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

    public BatchSplitPackageDialog(@NonNull Context context, boolean editable, BatchCutRecordBo data, String sizeCode, int sizeTotal) {
        super(context);
        isEditable = editable;
        mData = data;
        mSizeCode = sizeCode;
        mSizeTotal = sizeTotal;
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

        tv_shopOrder.setText(mData.getShopOrder());
        tv_item.setText(mData.getItem());
        tv_workNo.setText(mData.getWorkNo());
        tv_layoutNo.setText(mData.getLayoutNo());
        tv_size.setText(mSizeCode);
        tv_qty.setText(mSizeTotal + "");

        mLayout_items = mView.findViewById(R.id.layout_itemList);

        mView.findViewById(R.id.btn_add).setOnClickListener(this);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mView.findViewById(R.id.btn_ok).setOnClickListener(this);

        LoadingDialog.show(mContext);
        HttpHelper.getBatchSplitItem(mData.getShopOrderRef(), mSizeCode, mSizeTotal, this);
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
                cancel();
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
                if (qty >= mSizeTotal) {
                    ErrorDialog.showAlert(mContext, "数量已分完，请减少其他分包数量后再增加新包");
                    return;
                }
                BatchSplitPackageItemBo item = new BatchSplitPackageItemBo();
                item.setSubPackageQty(mSizeTotal - qty);
                mLayout_items.addView(getItemView(item, mLayout_items.getChildCount() - 1), mLayout_items.getChildCount() - 1);
                refreshPrintSeq();
                break;
            case R.id.btn_ok:
                check();
                break;
            case R.id.tv_printSeq:
                print(v);
                break;
        }
    }

    private void print(View v) {
        int index = (int) v.getTag();
        BatchSplitPackagePrintBo printBo = mList_printData.get(index);
        printBo.setSizeCode(mSizeCode);
        new BatchSplitPackagePrintContentDialog(mContext, printBo).setParams(0.4f, 0.45f).show();
        BluetoothHelper.printSubPackageInfo(getOwnerActivity(), printBo);
        if (index > 0) {
            setPrintEnable(index - 1);
        }
    }

    private void check() {
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            View view = mLayout_items.getChildAt(i);
            EditText editText = view.findViewById(R.id.et_qty);
            if (isEmpty(editText.getText().toString())) {
                TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);
                ErrorDialog.showAlert(mContext, "分包号 " + tv_packageNum.getText().toString() + "，数量不能为空");
                return;
            }
        }
        new SplitPackageCheckDialog(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completed();
            }
        }).setParams(0.5f, 0.5f).show();
    }

    private void completed() {
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

        //删除新增按钮
        mLayout_items.removeViewAt(mLayout_items.getChildCount() - 1);

        mLayout_items.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);

        for (int i = 0; i < mLayout_items.getChildCount(); i++) {
            View view = mLayout_items.getChildAt(i);
            EditText editText = view.findViewById(R.id.et_qty);
            editText.setEnabled(false);
        }

        setPrintEnable(mLayout_items.getChildCount() - 1);
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
            tv_printSeq.setOnClickListener(this);

            if (i == index) {
                tv_printSeq.setBackgroundResource(R.drawable.btn_orange);
                tv_printSeq.setEnabled(true);
            }
        }
    }

    private View getItemView(BatchSplitPackageItemBo item, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_split_print_item, null);
        TextView tv_packageNum = view.findViewById(R.id.tv_packageNum);
        tv_packageNum.setText((position + 1) + "");

        TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
        tv_printSeq.setTag(position);
        tv_printSeq.setText("打印顺序" + (mList_item.size() - position));

        EditText editText = view.findViewById(R.id.et_qty);
        editText.setText(item.getSubPackageQty() + "");
        editText.setEnabled(isEditable);
        return view;
    }

    /**
     * 新增一行时刷新打印顺序
     */
    private void refreshPrintSeq() {
        for (int i = 0; i < mLayout_items.getChildCount() - 1; i++) {
            View view = mLayout_items.getChildAt(i);
            TextView tv_printSeq = view.findViewById(R.id.tv_printSeq);
            tv_printSeq.setText("打印顺序" + (mLayout_items.getChildCount() - 1 - i));
        }
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        LoadingDialog.dismiss();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBatchSplitItem.equals(url)) {
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
            } else if (HttpHelper.saveBatchSplitPackageData.equals(url)) {
                mList_printData = JSON.parseArray(resultJSON.getJSONArray("result").toString(), BatchSplitPackagePrintBo.class);
                refreshItemView();
                if (mOnSaveListener != null) {
                    mOnSaveListener.onClick(null);
                }
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
