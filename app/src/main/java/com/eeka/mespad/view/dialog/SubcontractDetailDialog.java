package com.eeka.mespad.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonAdapter;
import com.eeka.mespad.adapter.ViewHolder;
import com.eeka.mespad.bo.SubcontractReceiveBo;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

/**
 * 查看外协收货详情dialog
 * Created by Lenovo on 2017/12/19.
 */

public class SubcontractDetailDialog extends BaseDialog {

    private List<SubcontractReceiveBo> mDta;

    public SubcontractDetailDialog(@NonNull Context context, @NonNull List<SubcontractReceiveBo> list) {
        super(context);
        mDta = list;
        init(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_subcontract_detail, null);
        setContentView(view);
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.lv_subcontract_receive);
        listView.setAdapter(new CommonAdapter<SubcontractReceiveBo>(mContext, mDta, R.layout.item_subcontract_receive) {
            @Override
            public void convert(ViewHolder holder, SubcontractReceiveBo item, int position) {
                EditText et_rfid = holder.getView(R.id.et_subcontractReceive_rfid);
                EditText et_order = holder.getView(R.id.et_subcontractReceive_orderNum);
                EditText et_sfc = holder.getView(R.id.et_subcontractReceive_sfc);
                EditText et_size = holder.getView(R.id.et_subcontractReceive_size);
                et_rfid.setText(item.getRfid());
                et_order.setText(item.getShopOrder());
                et_sfc.setText(item.getSfc());
                et_size.setText(item.getSize());

                holder.getView(R.id.tv_subcontractReceive_del).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.8), (int) (SystemUtils.getScreenHeight(mContext) * 0.9));
    }
}
