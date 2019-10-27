package com.eeka.mespad.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BatchSplitPackagePrintBo;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.zxing.EncodingHandler;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;

public class BatchSplitPackagePrintContentDialog extends BaseDialog {

    private BatchSplitPackagePrintBo mData;

    private Handler mHandler;

    BatchSplitPackagePrintContentDialog(@NonNull Context context, BatchSplitPackagePrintBo printBo) {
        super(context);
        mData = printBo;
        init();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_batchsplitpackage_print, null);
        setContentView(mView);
        setCanceledOnTouchOutside(true);

        TextView tv_packageNum = mView.findViewById(R.id.tv_packageNum);
        TextView tv_shopOrder = mView.findViewById(R.id.tv_shopOrder);
        TextView tv_item = mView.findViewById(R.id.tv_item);
        TextView tv_sizeCode = mView.findViewById(R.id.tv_sizeCode);
        TextView tv_qty = mView.findViewById(R.id.tv_qty);

        String packageNum = String.valueOf(mData.getSubPackageSeq());
        String shopOrder = mData.getShopOrder();
        String item = mData.getItem();
        String sizeCode = mData.getSizeCode();
        String qty = String.valueOf(mData.getSubPackageQty());
        tv_packageNum.setText(packageNum);
        tv_shopOrder.setText(shopOrder);
        tv_item.setText(item);
        tv_sizeCode.setText(sizeCode);
        tv_qty.setText(qty);

        if ("M".equals(mData.getMatType())) {
            try {
                Bitmap code = EncodingHandler.create2Code(mData.getRfid(), UnitUtil.dip2px(mContext, 200));
                ImageView iv_qrCode = mView.findViewById(R.id.iv_qrCode);
                iv_qrCode.setImageBitmap(code);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeCallbacksAndMessages(null);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dismiss();
            }
        };
    }

    @Override
    public void show() {
        super.show();
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

}
