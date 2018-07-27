package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.bo.SewAttr;
import com.eeka.mespad.bo.SewDataBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 工序不合格点查看
 * Created by Lenovo on 2018/1/16.
 */

public class NCDetailDialog extends BaseDialog {

    private List<SewAttr> mItems;

    public NCDetailDialog(List<SewAttr> items, @NonNull Context context) {
        super(context);
        mItems = items;
        init();
    }

    @Override
    protected void init() {
        super.init();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_ncdetail, null);
        setContentView(view);

        LinearLayout layout_items = view.findViewById(R.id.layout_ncDetail);
        for (SewAttr item : mItems) {
            String ncDescription = item.getAttributes().getNC_DESCRIPTION();
            if (TextUtils.isEmpty(ncDescription)) {
                continue;
            }
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_loginuser, null);
            TextView tv_process = itemView.findViewById(R.id.tv_userName);
            TextView tv_ncDesc = itemView.findViewById(R.id.tv_userId);
            tv_process.setText(item.getDescription());
            tv_ncDesc.setText(ncDescription);
            layout_items.addView(itemView);
        }

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.8));
    }
}
