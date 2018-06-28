package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;

public class SuspendAlertDialog extends BaseDialog {

    private String mShopOrder;
    private String mItemCode;
    private String mSize;
    private View.OnClickListener mListener;

    private int mSureCount;

    public SuspendAlertDialog(@NonNull Context context, String shopOrder, String itemCode, String size, View.OnClickListener listener) {
        super(context);
        mShopOrder = shopOrder;
        mItemCode = itemCode;
        mSize = size;
        mListener = listener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_suspendalert, null);
        setContentView(view);

        LinearLayout layout_items = view.findViewById(R.id.layout_suspendAlert_item);
        for (int i = 0; i < 3; i++) {
            View item = LayoutInflater.from(mContext).inflate(R.layout.item_suspendalert, null);
            TextView tv_item1 = item.findViewById(R.id.tv_suspendAlert_item1);
            TextView tv_item2 = item.findViewById(R.id.tv_suspendAlert_item2);
            TextView tv_item3 = item.findViewById(R.id.tv_suspendAlert_item3);
            TextView tv_item4 = item.findViewById(R.id.tv_suspendAlert_item4);
            String text = null;
            switch (i) {
                case 0:
                    text = mItemCode;
                    break;
                case 1:
                    text = mShopOrder;
                    break;
                case 2:
                    text = mSize;
                    TextView tv_item5 = item.findViewById(R.id.tv_suspendAlert_item5);
                    tv_item5.setText(text);
                    break;
            }
            tv_item1.setText(text);
            tv_item2.setText(text);
            tv_item3.setText(text);
            tv_item4.setText(text);

            final TextView sureBtn = item.findViewById(R.id.tv_suspendAlert_sureBtn);
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sureBtn.setEnabled(false);
                    sureBtn.setText("已确认");
                    mSureCount++;
                    if (mSureCount == 3) {
                        dismiss();
                        //全部确认完毕，执行回调
                        mListener.onClick(null);
                    }
                }
            });

            layout_items.addView(item);
        }
    }

}
