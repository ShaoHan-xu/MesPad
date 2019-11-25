package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.BatchCutRecordBo;

import java.util.List;

public class CutRecordCheckDialog extends BaseDialog {

    private String mOperationDesc;

    private String mOrderNum;

    private List<BatchCutRecordBo.CutSizesBean> mSizes;

    private View.OnClickListener mListener;

    public CutRecordCheckDialog(@NonNull Context context, String operationDesc, String orderNum, List<BatchCutRecordBo.CutSizesBean> sizes, View.OnClickListener listener) {
        super(context);
        mOperationDesc = operationDesc;
        mOrderNum = orderNum;
        mSizes = sizes;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_cutrecord_check, null);
        setContentView(mView);

        TextView tv_operationDesc = mView.findViewById(R.id.tv_operationDesc);
        tv_operationDesc.setText("此次" + mOperationDesc + "作业为" + mOperationDesc + "作业单");

        TextView tv_orderNum = mView.findViewById(R.id.tv_orderNum);
        tv_orderNum.setText(mOrderNum);

        StringBuilder sb = new StringBuilder();
        if (mSizes != null) {
            for (BatchCutRecordBo.CutSizesBean item : mSizes) {
                sb.append(item.getSizeCode()).append("码").append(item.getSizeLeft()).append("件 ");
            }
            TextView tv_selectedSize = mView.findViewById(R.id.tv_selectedSize);
            tv_selectedSize.setText(sb.subSequence(0, sb.length() - 1));
        }

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onClick(v);
            }
        });
    }
}
