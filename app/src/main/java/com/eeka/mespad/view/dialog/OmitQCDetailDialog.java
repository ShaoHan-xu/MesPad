package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eeka.mespad.R;
import com.eeka.mespad.activity.ImageBrowserActivity;
import com.eeka.mespad.bo.OmitQCBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

public class OmitQCDetailDialog extends BaseDialog {

    private LinearLayout mLayout_item;
    private List<OmitQCBo> mList;

    public OmitQCDetailDialog(@NonNull Context context, List<OmitQCBo> list) {
        super(context);
        mList = list;
        init();
    }

    @Override
    protected void init() {
        super.init();
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_omitqc_detail, null);
        setCancelable(false);
        setContentView(mView);

        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("漏检提示");

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mLayout_item = mView.findViewById(R.id.layout_omitqc_list);
        for (int i = 0; i < mList.size(); i++) {
            mLayout_item.addView(getItemView(mList.get(i)));
        }
    }

    private View getItemView(final OmitQCBo item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_omitqc_detail, null);
        TextView tv_location = view.findViewById(R.id.tv_omitqc_location);
        TextView tv_recordUser = view.findViewById(R.id.tv_omitqc_recordUser);
        TextView tv_item = view.findViewById(R.id.tv_omitqc_item);
        TextView tv_hangerId = view.findViewById(R.id.tv_omitqc_hangerId);
        TextView tv_sfc = view.findViewById(R.id.tv_omitqc_sfc);
        TextView tv_omitUser = view.findViewById(R.id.tv_omitqc_omitUser);
        TextView tv_omitOperate = view.findViewById(R.id.tv_omitqc_omitOperate);
        TextView tv_ncCodeDesc = view.findViewById(R.id.tv_omitqc_ncCodeDesc);

        tv_location.setText(item.getLineStation());
        tv_recordUser.setText(item.getRecordUserName());
        tv_item.setText(item.getItem());
        tv_hangerId.setText(item.getHangerId());
        tv_sfc.setText(item.getSfc());
        tv_omitUser.setText(item.getUserName());
        tv_omitOperate.setText(item.getOperationDesc());

        final String ncImageLocation = item.getNcImageLocation();
        if (isEmpty(ncImageLocation)) {
            tv_ncCodeDesc.setTextColor(mContext.getResources().getColor(R.color.text_gray_default));
        } else {
            tv_ncCodeDesc.setTextColor(mContext.getResources().getColor(R.color.text_blue_default));
            tv_ncCodeDesc.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        tv_ncCodeDesc.setText(item.getNcDesc());
        tv_ncCodeDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(ncImageLocation))
                    Toast.makeText(mContext, "该不良记录未拍照片", Toast.LENGTH_SHORT).show();
                else
                    mContext.startActivity(ImageBrowserActivity.getIntent(mContext, ncImageLocation));
            }
        });
        return view;
    }

    public void addItem(List<OmitQCBo> list) {
        int childCount = mLayout_item.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_item.getChildAt(i);
            childAt.findViewById(R.id.itemView).setBackgroundResource(R.color.white);
        }

        mList.addAll(0, list);
        for (OmitQCBo item : list) {
            View itemView = getItemView(item);
            itemView.findViewById(R.id.itemView).setBackgroundResource(R.color.text_green_default);
            mLayout_item.addView(itemView, 0);
        }
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.9), (int) (SystemUtils.getScreenHeight(mContext) * 0.7));
    }
}
