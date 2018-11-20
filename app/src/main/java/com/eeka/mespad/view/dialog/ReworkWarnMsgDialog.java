package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.ReworkItemBo;
import com.eeka.mespad.bo.ReworkWarnMsgBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 返修警告弹框
 * Created by Lenovo on 2018/1/16.
 */

public class ReworkWarnMsgDialog extends BaseDialog {

    private List<ReworkWarnMsgBo> mItems;

    public ReworkWarnMsgDialog(@NonNull Context context, List<ReworkWarnMsgBo> items) {
        super(context);
        mItems = items;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_reworkmsg, null);
        setContentView(view);

        LinearLayout layout_items = view.findViewById(R.id.layout_reworkMsg);
        for (ReworkWarnMsgBo item : mItems) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_remorkinfo, null);
            TextView tv_item1 = itemView.findViewById(R.id.tv_item1);
            TextView tv_item2 = itemView.findViewById(R.id.tv_item2);
            TextView tv_item3 = itemView.findViewById(R.id.tv_item3);
            TextView tv_item4 = itemView.findViewById(R.id.tv_item4);
            TextView tv_item5 = itemView.findViewById(R.id.tv_item5);
            tv_item1.setText(item.getSFC());
            tv_item2.setText(item.getHANGER_ID());
            tv_item3.setText(item.getOPER_DESC());
            tv_item4.setText(item.getNC_DESC());
            tv_item5.setText(item.getUSER_NAME());
            layout_items.addView(itemView);
        }

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.8));
    }
}
