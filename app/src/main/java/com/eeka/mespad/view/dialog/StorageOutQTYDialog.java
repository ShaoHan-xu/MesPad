package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.callback.IntegerCallback;
import com.eeka.mespad.utils.SystemUtils;

public class StorageOutQTYDialog extends BaseDialog {

    private String mShopOrder;
    private String mItem;
    private String mSize;
    private int mLesQTY;
    private IntegerCallback mCallback;

    public StorageOutQTYDialog(@NonNull Context context, String shopOrder, String item, String size, String lessQTY, IntegerCallback callback) {
        super(context);
        mShopOrder = shopOrder;
        mItem = item;
        mSize = size;
        mLesQTY = Integer.valueOf(lessQTY);
        mCallback = callback;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_inputoutqty, null);
        setContentView(mView);

        TextView tv_shopOrder = mView.findViewById(R.id.tv_shopOrder);
        TextView tv_item = mView.findViewById(R.id.tv_item);
        TextView tv_size = mView.findViewById(R.id.tv_size);
        TextView tv_lessQTY = mView.findViewById(R.id.tv_lessQTY);
        tv_shopOrder.setText(mShopOrder);
        tv_item.setText(mItem);
        tv_size.setText(mSize);
        tv_lessQTY.setText(mLesQTY + "");

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mView.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.callback(mLesQTY);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.4), (int) (SystemUtils.getScreenHeight(mContext) * 0.6));

    }
}
