package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.DateUtil;
import com.eeka.mespad.utils.SystemUtils;

public class MaintenanceDialog extends BaseDialog {

    private static final String URL_DAY = "http://10.7.121.10/GST/mes/每日保养视频.mp4";
    private static final String URL_WEEK = "http://10.7.121.10/GST/mes/每周视频保养.mp4";

    private String mUrl;

    public MaintenanceDialog(@NonNull Context context, boolean isWeek) {
        super(context);
        if (isWeek) {
            mUrl = URL_WEEK;
        } else {
            mUrl = URL_DAY;
        }
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_maintenance, null);
        setContentView(mView);

        mView.findViewById(R.id.btn_lookVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.playVideo(mContext, mUrl, true);
            }
        });

        mView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.4));

    }
}
