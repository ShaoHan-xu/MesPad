package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.R;
import com.eeka.mespad.bo.BatchLabuRecordBo;
import com.eeka.mespad.bo.BatchLabuRecordPrintBo;
import com.eeka.mespad.bo.PostBatchRecordLabuBo;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.zxing.EncodingHandler;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;

public class BatchLabuRecordPrintContentDialog extends BaseDialog {

    private BatchLabuRecordPrintBo mData;

    public BatchLabuRecordPrintContentDialog(@NonNull Context context, BatchLabuRecordPrintBo printBo) {
        super(context);
        mData = printBo;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_batchlaburecord_print, null);
        setContentView(mView);
        setCanceledOnTouchOutside(true);

        TextView tv_sizeCode = mView.findViewById(R.id.tv_sizeCode);
        TextView tv_layer = mView.findViewById(R.id.tv_layer);
        TextView tv_item = mView.findViewById(R.id.tv_item);
        TextView tv_shopOrder = mView.findViewById(R.id.tv_shopOrder);
        TextView tv_rabOrder = mView.findViewById(R.id.tv_rabOrder);

        StringBuilder sizeCode = new StringBuilder();
        StringBuilder layer = new StringBuilder();
        for (PostBatchRecordLabuBo.CutSizeBean item : mData.getSizeList()) {
            int lays = item.getLayers();
            String code = item.getSizeCode();
            sizeCode.append(code).append(" ");
            if (lays < 10) {
                layer.append("0");
            }
            layer.append(lays).append(" ");
        }
        tv_sizeCode.setText(sizeCode.toString());
        tv_layer.setText(layer.toString());
        tv_item.setText(mData.getItem());
        tv_shopOrder.setText(mData.getShopOrder());
        tv_rabOrder.setText(mData.getRabOrder());

        try {
            BatchLabuRecordPrintBo data = new BatchLabuRecordPrintBo();
            data.setMatType(mData.getMatType());
            data.setRabOrder(mData.getRabOrder());
            Bitmap code = EncodingHandler.create2Code(JSON.toJSONString(data), UnitUtil.dip2px(mContext, 200));
            ImageView iv_qrCode = mView.findViewById(R.id.iv_qrCode);
            iv_qrCode.setImageBitmap(code);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
